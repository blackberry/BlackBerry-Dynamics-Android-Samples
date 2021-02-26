/*
 * Copyright (c) 2020 BlackBerry Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.good.gd.webview_V2.bbwebview.tasks.http;

import android.util.Log;
import android.util.Pair;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.good.gd.apache.http.HttpResponse;
import com.good.gd.apache.http.protocol.HttpContext;
import com.good.gd.net.GDHttpClient;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

// Class to manage httpclient objects
// There is a dedicated thread per each GDHttpClient
public class GDHttpClientProvider {

    // Interface to execute operations on GDHttpClient on it's own thread
    public interface ClientCallback<R> {
        R doInClientThread(GDHttpClient httpClient, String id);
    }

    public interface ClientCallback2<R,I> {
        R doInClientThread(I arg);
    }

    // pojo class containing related objects
    private static class Client {
        private ConnectionPool.HostPortKey hostPort;
        private String id;// random UID
        // thread executor used to initialize the GDHttpClient and
        // execute the network requests
        private ExecutorService httpExecutor;
        private GDHttpClient httpClient;
        private WebResourceResponse response;// related WebResourceResponse, this is used for redirection cases
    }

    public static class Instance {
        private final static GDHttpClientProvider GD_HTTP_CLIENT_PROVIDER = new GDHttpClientProvider();
    }

    private static final String TAG = "GDWebView-" +  GDHttpClientProvider.class.getSimpleName();
    private static final String TAG_SHARED_Q = "GDWebView-" +  "SHARED_Q";

    public static GDHttpClientProvider getInstance(){
        // thread-safe because of lazy initialization of nested classes in Java
        return Instance.GD_HTTP_CLIENT_PROVIDER;
    }

    // total clients allocated
    // key: UUID | value: Client object
    private final Map<String, Client> clientsMap = new ConcurrentHashMap<>();
    private final ConnectionPool connectionPool = new ConnectionPool();
    private final Map<ConnectionPool.ResponseId, Future<Pair<HttpResponse, HttpContext>>> cachedResponses = new ConcurrentHashMap<>();

    private final AtomicBoolean intCalled = new AtomicBoolean(false);

    private WeakReference<WebView> webViewRef;

    private GDHttpClientProvider() {
    }

    private synchronized Client registerNewClient() {
        Client client = new Client();
        client.id = UUID.randomUUID().toString();
        client.httpExecutor = Executors.newFixedThreadPool(1);

        this.clientsMap.put(client.id, client);
        return client;
    }

    public void setWebViewReference(WebView webView) {
        if (webViewRef == null) {
            webViewRef = new WeakReference<>(webView);
        }
    }

    public String obtainPooledClient(String hostName) {

        // If connection pool is not initialized then return
        if (!intCalled.get()) {
            return null;
        }

        Log.i(TAG_SHARED_Q,"obtainClientId, host: " + hostName + " idle: " + connectionPool.pool.getNumIdle() + " active: " + connectionPool.pool.getNumActive());

        try {

            final ConnectionPool.HostPortKey key = new ConnectionPool.HostPortKey(hostName, 0/*todo: add actual port*/);

            final String id = connectionPool.pool.borrowObject(key, ConnectionPool.TIMEOUT_WAITING_FOR_CLIENT);

            Client client = clientsMap.get(id);

            client.hostPort = key;

            Log.i(TAG_SHARED_Q,"obtainClientId, " + id + " host = " + client.hostPort + " http client = " + clientsMap.get(id).httpClient.hashCode());
            return id;
        } catch (Exception e) {
            Log.e(TAG_SHARED_Q,"obtainClientId, exception " + e.getMessage() + " idle = " + connectionPool.pool.getNumIdle()
                    + " active = " + connectionPool.pool.getNumActive() + " clientMapSize = " + clientsMap.size());
            e.printStackTrace();
        }

        return null;
    }

    public void releasePooledClient(String clientId) {
        Log.i(TAG_SHARED_Q,"releasePooledClient, IN ");
        try {
            Client client = clientsMap.get(clientId);

            if (isClientValid(client)) {

                if (connectionPool.pool == null) {
                    Log.i(TAG_SHARED_Q,"release, pool is null, shutdown http client = " + client.httpClient.hashCode());

                    client.httpClient.getConnectionManager().shutdown();
                    client.httpClient = null;
                    return;
                }

                // Check if the response associated with clientId not in the cache
                for (Map.Entry<ConnectionPool.ResponseId, Future<Pair<HttpResponse, HttpContext>>>  resp : cachedResponses.entrySet()) {
                    if (resp.getKey().clientId.equals(clientId)) {
                        Log.i(TAG, "releasePooledClient, skip release of client with id " + clientId);
                        return;
                    }
                }

                Log.i(TAG_SHARED_Q,"releasePooledClient, release = " + clientId + " idle = " + connectionPool.pool.getNumIdle()
                        + " active = " + connectionPool.pool.getNumActive());

                connectionPool.pool.returnObject(client.hostPort, clientId);

                // Clear idle clients in the pool
                connectionPool.pool.clear();
            }

        } catch (Exception e) {
            Log.e(TAG_SHARED_Q,"releasePooledClient, unknown exception " + e);
            e.printStackTrace();
        }

        Log.i(TAG_SHARED_Q,"releasePooledClient, OUT release = " + clientId);
    }

    private boolean isClientValid(Client client) {
        return client != null && client.hostPort != null && client.httpClient != null;
    }

    public void initHttpClientsPool(){
        Log.i(TAG, "< initHttpClientsPool");

        if (intCalled.compareAndSet(false,true)) {
            connectionPool.init();
            Log.i(TAG, "initHttpClientsPool >> ");
        }

        Log.i(TAG, "initHttpClientsPool >");
    }

    public void disposeHttpClientsPool(){
        Log.i(TAG, "< disposeHttpClientsPool");
        if (intCalled.compareAndSet(true,false)) {
            connectionPool.releasePool();
            Log.i(TAG, "disposeHttpClientsPool >>");
        }
        Log.i(TAG, "disposeHttpClientsPool >");
    }

    /**
     *
     * @param clientId existing client id
     * @param gdHttpClientCallback callback to be executed on the related thread
     * @param <R> result type
     * @return future object providing the result
     */
    public <R> Future<R> execGDHttpClientAsync(final String clientId, final ClientCallback<R> gdHttpClientCallback) {

        Log.i(TAG, "execGDHttpClientAsync IN " + clientId);

        final Client client = clientsMap.get(clientId);

        if (client == null) throw new RuntimeException("exec failed, no client associated with (" + clientId + ") ");

        Callable<R> clientTask = new Callable<R>() {
            @Override
            public R call() {
                return gdHttpClientCallback.doInClientThread(client.httpClient, client.id);

            }
        };

        Future<R> res = client.httpExecutor.submit(clientTask);

        Log.i(TAG, "execGDHttpClientAsync OUT " + clientId);

        return res;

    }

    public <R,I> Future<R> execOnTargetObject(final String clientId, final I target, final ClientCallback2<R,I> userCallback) {

        Log.i(TAG, "execOnTargetObject IN " + clientId);

        final Client client = clientsMap.get(clientId);

        if(client == null) throw new RuntimeException("exec failed, no client associated with (" + clientId + ") ");

        final Callable<R> clientTask = new Callable<R>() {
            @Override
            public R call() {
                return userCallback.doInClientThread(target);

            }
        };

        Future<R> res = client.httpExecutor.submit(clientTask);

        Log.i(TAG, "execOnTargetObject OUT " + clientId);

        return res;

    }

    // Caches WebResourceResponse for a redirected page
    public synchronized void cacheResponseData(String locationUrl, String clientId, final Future<Pair<HttpResponse, HttpContext>> response) {
        Log.i(TAG, "cacheResponseData, locationUrl - " + locationUrl + ", clientId - " + clientId);

        for (Map.Entry<ConnectionPool.ResponseId, Future<Pair<HttpResponse, HttpContext>>>  resp : cachedResponses.entrySet()) {
            if (resp.getKey().locationUrl.equals(locationUrl)) {
                Log.e(TAG, "cacheResponseData, response is already in the cache");
                return;
            }
        }

        cachedResponses.put(new ConnectionPool.ResponseId(locationUrl, clientId), response);

        Log.i(TAG, "cacheResponseData, response is cached");
    }

    // Provides the WebResourceResponse for redirected url
    public synchronized Future<Pair<HttpResponse, HttpContext>> fetchCachedWebResponse(String locationUrl){
        Log.i(TAG, "fetchCachedWebResponse " + locationUrl);

        ConnectionPool.ResponseId key = null;
        Future<Pair<HttpResponse, HttpContext>> value = null;

        for (Map.Entry<ConnectionPool.ResponseId, Future<Pair<HttpResponse, HttpContext>>>  resp : cachedResponses.entrySet()) {
            if (resp.getKey().locationUrl.equals(locationUrl)) {

                key = resp.getKey();
                value = resp.getValue();

                Log.i(TAG, "cacheResponseData, response is found in the cache - " + key.clientId + " " + key.locationUrl);
                break;
            }
        }

        if (key != null) {
            // Clear cache
            cachedResponses.remove(key);
        } else {
            Log.e(TAG, "cacheResponseData, response is NOT found in the cache");
        }

        return value;
    }

    public synchronized boolean hasCachedResponse(String locationUrl) {
        Log.i(TAG, "hasCachedResponse " + locationUrl);

        for (Map.Entry<ConnectionPool.ResponseId, Future<Pair<HttpResponse, HttpContext>>>  resp : cachedResponses.entrySet()) {
            if (resp.getKey().locationUrl.equals(locationUrl)) {
                Log.i(TAG, "hasCachedResponse " + locationUrl + ", result true");
                return true;
            }
        }

        Log.i(TAG, "hasCachedResponse " + locationUrl + ", result false");
        return false;
    }

    private static class ConnectionPool {

        private static final int MAX_TOTAL_CLIENTS = 90;
        private static final int MAX_CLIENTS_PER_HOST = -1;
        private static final int MAX_IDLE_CLIENTS_PER_HOST = -1;
        private static final int MIN_IDLE_CLIENTS_PER_HOST = 0;
        private static final int TIMEOUT_WAITING_FOR_CLIENT = 5 * 1000; // milliseconds
        private static final int MAX_HTTP_CLIENTS_FOR_REUSE = 30;

        private ConnectionPool() {
        }

        private final GenericKeyedObjectPoolConfig<String> poolConfiguration = new GenericKeyedObjectPoolConfig<>();

        private GenericKeyedObjectPool<HostPortKey, String> pool;
        private KeyedPooledObjectFactory<HostPortKey, String> httpClientsFactory;

        private final Object setHttpClientLock = new Object();

        private final Set<GDHttpClient> httpClientIdlePool = ConcurrentHashMap.newKeySet();
        private final Set<GDHttpClient> httpClientActivePool = ConcurrentHashMap.newKeySet();

        private ExecutorService executor;

        public void init() {

            Log.i(TAG_SHARED_Q," < init ");

            if (pool != null) {
                Log.e(TAG_SHARED_Q,"init, pool is initialized, return");
                return;
            }

            if (executor == null) {
                executor = Executors.newSingleThreadExecutor();
            }

            httpClientsFactory = new BaseKeyedPooledObjectFactory<HostPortKey, String>() {
                @Override
                public String create(HostPortKey hostAndPort) {
                    Client client = GDHttpClientProvider.getInstance().registerNewClient();
                    Log.i(TAG_SHARED_Q,"create - client: " + client.id + " host: " + hostAndPort.host);
                    return client.id;
                }

                @Override
                public PooledObject<String> wrap(String httpClientId) {
                    Log.i(TAG_SHARED_Q,"wrap - " + httpClientId);
                    return new DefaultPooledObject<>(httpClientId);
                }

                @Override
                public void activateObject(HostPortKey key, PooledObject<String> p) throws Exception {
                    Log.i(TAG_SHARED_Q,"activateObject - " + key.host + " - " + p.getObject());
                    Client client = GDHttpClientProvider.getInstance().clientsMap.get(p.getObject());
                    if (client.httpClient == null) {
                        setHttpClient(client);
                    }
                }

                @Override
                public void passivateObject(HostPortKey key, PooledObject<String> p) {
                    Log.i(TAG_SHARED_Q,"passivate - " + key.host + " - " + p.getObject());
                    Client client = GDHttpClientProvider.getInstance().clientsMap.get(p.getObject());

                    // Check if can reuse
                    if (httpClientActivePool.size() > MAX_HTTP_CLIENTS_FOR_REUSE) {
                        // Schedule shutdowm operation on background
                        Log.i(TAG_SHARED_Q,"passivate, Schedule shutdown = " +  client.httpClient);
                        executor.submit(new ShutDownHttpClientTask(client.httpClient));
                    } else {
                        // Reuse client
                        Log.i(TAG_SHARED_Q,"passivate, add to pool = " +  client.httpClient);
                        httpClientIdlePool.add(client.httpClient);
                    }

                    client.hostPort = null;
                    client.httpClient = null;

                    Log.i(TAG_SHARED_Q,"passivate, client.id: " + client.id );
                }

                @Override
                public boolean validateObject(HostPortKey key, PooledObject<String> p) {
                    Log.i(TAG_SHARED_Q,"validate - " + key.host + " - " + p.getObject());
                    return true;
                }

                @Override
                public void destroyObject(HostPortKey key, PooledObject<String> p) {
                    Log.i(TAG_SHARED_Q,"destroy - " + key.host + " - " + p.getObject());

                    synchronized (GDHttpClientProvider.getInstance()) {
                        Client client = GDHttpClientProvider.getInstance().clientsMap.get(p.getObject());
                        client.httpExecutor.shutdownNow();
                        client.response = null;
                        client.httpExecutor = null;
                        GDHttpClientProvider.getInstance().clientsMap.remove(p.getObject());
                    }
                }
            };

            poolConfiguration.setMaxTotal(MAX_TOTAL_CLIENTS);
            poolConfiguration.setMaxTotalPerKey(MAX_CLIENTS_PER_HOST);
            poolConfiguration.setMaxIdlePerKey(MAX_IDLE_CLIENTS_PER_HOST);
            poolConfiguration.setMinIdlePerKey(MIN_IDLE_CLIENTS_PER_HOST);
            poolConfiguration.setJmxEnabled(false);

            pool = new GenericKeyedObjectPool<>(httpClientsFactory, poolConfiguration);

            Log.i(TAG_SHARED_Q," init > ");
        }

        public void releasePool() {
            Log.i(TAG_SHARED_Q,"releasePool, IN ");

            pool.close();
            pool.clear();
            pool = null;

            Log.i(TAG_SHARED_Q,"releasePool, active pool size = " + httpClientActivePool.size());

            ShutDownHttpClientTask task = new ShutDownHttpClientTask(httpClientActivePool);
            executor.submit(task);

            httpClientIdlePool.clear();
            httpClientActivePool.clear();

            // Shutdown executor
            executor.shutdown();
            executor = null;

            Log.i(TAG_SHARED_Q,"releasePool, OUT ");
        }

        /**
         * Retrieves first available http client from pool
         * @return
         */
        private GDHttpClient getIdleHttpClient() {
            Iterator<GDHttpClient> iterator = httpClientIdlePool.iterator();
            if (iterator.hasNext()) {
                return iterator.next();
            }
            return null;
        }

        private void setHttpClient(Client client) {
            Log.i(TAG_SHARED_Q,"< setHttpClient id: " + client.id);
            synchronized (setHttpClientLock) {
                GDHttpClient httpClient = getIdleHttpClient();
                if (httpClient != null) {
                    Log.i(TAG_SHARED_Q, "setHttpClient, retrieved from the pool, http client = " + httpClient.hashCode());

                    // Reuse existing http client
                    client.httpClient = httpClient;

                    httpClientIdlePool.remove(httpClient);
                } else {
                    // Create new http client
                    client.httpClient = InitHttpClient.createGDHttpClient();

                    httpClientActivePool.add(client.httpClient);

                    Log.i(TAG_SHARED_Q, "setHttpClient, created new http client = " + client.httpClient.hashCode());
                }
            }
            Log.i(TAG_SHARED_Q,"setHttpClient >");
        }

        private static class ShutDownHttpClientTask implements Runnable {

            private Set<GDHttpClient> clients;
            private GDHttpClient client;

            public ShutDownHttpClientTask(Set<GDHttpClient> clients) {
                this.clients = new HashSet<>(clients);
            }

            public ShutDownHttpClientTask(GDHttpClient client) {
                this.client = client;
            }

            @Override
            public void run() {

                Log.i(TAG_SHARED_Q, "shutDownHttpClientTask, IN shutDown "
                        + ( (client != null) ? " client = " + client.hashCode() : " clients = " + clients.size()));

                if (client != null) {

                    boolean isClientInActivePool = GDHttpClientProvider.getInstance()
                            .connectionPool.httpClientActivePool.contains(client);

                    // Client is already released
                    if (!isClientInActivePool) {
                        Log.e(TAG_SHARED_Q, "shutDownHttpClientTask, client - " + client.hashCode() + " is not in active pool, return");
                        return;
                    }

                    client.getConnectionManager().shutdown();

                    // Can safe remove from the pool
                    GDHttpClientProvider.getInstance().connectionPool.httpClientIdlePool.remove(client);
                    GDHttpClientProvider.getInstance().connectionPool.httpClientActivePool.remove(client);

                    client = null;

                    Log.e(TAG_SHARED_Q, "shutDownHttpClientTask, client is shut downed - " + client.hashCode());

                } else {
                    Log.i(TAG_SHARED_Q, "shutDownHttpClientTask, client size = " + clients.size());

                    for (GDHttpClient httpClient : clients) {
                        if (httpClient != null) {
                            Log.i(TAG_SHARED_Q, "shutDownHttpClientTask, shutdown client = " + httpClient.hashCode());
                            httpClient.getConnectionManager().shutdown();
                        }
                    }

                    clients.clear();
                }

                Log.i(TAG_SHARED_Q,"shutDownHttpClientTask, OUT");
            }

        }

        public static class HostPortKey {
            final String host;
            final int port;

            public HostPortKey(String host, int port) {
                this.host = host;
                this.port = port;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                HostPortKey that = (HostPortKey) o;
                return port == that.port &&
                        host.equals(that.host);
            }

            @Override
            public int hashCode() {
                return Objects.hash(host, port);
            }

            @NonNull
            @Override
            public String toString() {
                return "{" + host + " : " + port + "}";
            }
        }

        private static class ResponseId {
            private String locationUrl;
            private String clientId;

            public ResponseId(String locationUrl, String clientId) {
                this.locationUrl = locationUrl;
                this.clientId = clientId;
            }

        }

    }
}
