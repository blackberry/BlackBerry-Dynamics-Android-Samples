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
import android.webkit.WebResourceResponse;

import com.good.gd.net.GDHttpClient;
import com.good.gd.webview_V2.bbwebview.utils.ClientsPool;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// Class to manage httpclient objects
// There is a dedicated thread per each GDHttpClient
public class GDHttpClientProvider {

    // Interface to execute operations on GDHttpClient on it's own thread
    public interface ClientCallback<R> {
        R doInClientThread(GDHttpClient httpClient);
    }

    public interface ClientCallback2<R,I> {
        R doInClientThread(I arg);
    }

    // pojo class containg related objects
    private static class Client {
        String id;// random UID
        // thread executor used to initialize the GDHttpClient and
        // execute the network requests
        ExecutorService httpExecutor;
        private GDHttpClient httpClient;
        private ClientsPool.Host host;
        WebResourceResponse response;// related WebResourceResponse, this is used for redirection cases

    }

    private static class Instance {
        private final static GDHttpClientProvider GD_HTTP_CLIENT_PROVIDER = new GDHttpClientProvider();
    }

    public static GDHttpClientProvider getInstance(){
        // thread-safe because of lazy initialization of nested classes in Java
        return Instance.GD_HTTP_CLIENT_PROVIDER;
    }

    private static final String TAG = "APP_LOG-" +  "GDHttpClientProvider";

    private GDHttpClientProvider(){}
    private final ClientsPool sharedPool = new ClientsPool();

    // total clients allocated
    // key: UUID | value: Client object
    private final Map<String, Client> clientsMap = new ConcurrentHashMap<>();


    private String prepareAsync(final Callable<GDHttpClient> objectSource) {

        Log.i(TAG,"-=prepareAsync");
        Client c = registerEmptyClient();

        execOnTargetObject(c.id, c, new ClientCallback2<GDHttpClient, Client>() {
            @Override
            public GDHttpClient doInClientThread(Client theClient) {
                try {
                    GDHttpClient httpClientInstance = objectSource.call();
                    theClient.httpClient = httpClientInstance;
                    return httpClientInstance;
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });

        Log.i(TAG,c.id + " prepareAsync=-");
        return c.id;
    }

    public String obtainPooledClient(String hostName) {
        Log.i("SHARED_Q","obtainClientId, host: " + hostName);

        ClientsPool.Host host = sharedPool.forHost(hostName);

        String reservedId = host.take();
        Client client = clientsMap.get(reservedId);
        client.host = host;

        Log.i("SHARED_Q","obtainClientId, host: " + hostName +" id: "+ reservedId);

        return reservedId;
    }

    public void releasePooledClient(String clientId) {
        Log.i("SHARED_Q","releaseClient = " + clientId);
        Client client = clientsMap.get(clientId);
        if(client != null) {
            client.host.release(clientId);
            Log.i("SHARED_Q","releaseClient = " + client.host.getName());
        } else {
            Log.w("SHARED_Q","releaseClient = " + null);
        }


    }

    private Client registerEmptyClient() {
        Client client = new Client();
        client.id = UUID.randomUUID().toString();
        client.httpExecutor = Executors.newFixedThreadPool(1);

        this.clientsMap.put(client.id, client);
        sharedPool.add(client.id);
        return client;
    }

    private void freeClient(Client client) {

        execGDHttpClientAsync(client.id, new ClientCallback<GDHttpClient>() {
            @Override
            public GDHttpClient doInClientThread(GDHttpClient httpClient) {
                try {
                    httpClient.getConnectionManager().shutdown();
                } catch (Exception e){

                }
                return null;
            }
        });

        this.clientsMap.remove(client.id);
    }

    public void initHttpClientsPool(List<? extends Callable<GDHttpClient>> clientsSource){
        for (Callable<GDHttpClient> httpClientProvider : clientsSource) {
            prepareAsync(httpClientProvider);
        }
    }

    public void disposeHttpClientsPool(){

        for (Client client : clientsMap.values()) {
            freeClient(client);
        }

        this.sharedPool.dispose();
    }

    /**
     *
     * @param clientId existing client id
     * @param gdHttpClientCallback callback to be executed on the related thread
     * @param <R> result type
     * @return future object providing the result
     */
    public <R> Future<R> execGDHttpClientAsync(final String clientId, final ClientCallback<R> gdHttpClientCallback) {

        final Client client = clientsMap.get(clientId);

        if(client == null) throw new RuntimeException("exec failed, no client associated with (" + clientId + ") ");
        Callable<R> clientTask = new Callable<R>() {
            @Override
            public R call() throws Exception {
                return gdHttpClientCallback.doInClientThread(client.httpClient);

            }
        };

        Future<R> res = client.httpExecutor.submit(clientTask);
        return res;

    }

    public <R,I> Future<R> execOnTargetObject(final String clientId, final I target, final ClientCallback2<R,I> gdHttpClientCallback) {

        final Client client = clientsMap.get(clientId);

        if(client == null) throw new RuntimeException("exec failed, no client associated with (" + clientId + ") ");

        Callable<R> clientTask = new Callable<R>() {
            @Override
            public R call() throws Exception {
                return gdHttpClientCallback.doInClientThread(target);

            }
        };

        Future<R> res = client.httpExecutor.submit(clientTask);

        return res;

    }

    // TODO: REFACTOR NEXT 2 methods
    //caches WebResourceResponse for a redirected page
    synchronized public void cacheResponseData(String connectionId, final WebResourceResponse wrResp) throws ExecutionException, InterruptedException {
        Log.i(TAG, "cacheResponseData " + connectionId + " " + wrResp);

        final Client client = clientsMap.get(connectionId);
        if(client != null) {
            client.response = wrResp;
            Log.i(TAG, "cacheResponseData " + wrResp);
        } else {
            throw new RuntimeException("no client for id: [" + connectionId + "]");
        }
    }

    // provides the WebResourceResponse for redirected url
    synchronized public WebResourceResponse fetchCachedWebResponse(String clientId){
        Log.i(TAG, "fetchCachedWebResponse " + clientId);
        final Client client = clientsMap.get(clientId);
        if(client != null) {
            WebResourceResponse response = client.response;

            Log.i(TAG, "fetchCachedWebResponse " + response);
            client.response = null;

            return response;
        }

        throw new RuntimeException("no client for id: [" + clientId + "]");
    }
}
