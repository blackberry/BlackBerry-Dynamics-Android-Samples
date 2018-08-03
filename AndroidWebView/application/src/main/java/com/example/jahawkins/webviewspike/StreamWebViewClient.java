/* Copyright (c) 2018 BlackBerry Ltd.
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

package com.example.jahawkins.webviewspike;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.good.gd.GDAndroid;
import com.good.gd.GDServiceProvider;
import com.good.gd.GDServiceProviderType;
import com.good.gd.GDServiceType;
import com.good.gd.apache.http.Header;
import com.good.gd.apache.http.HttpEntity;
import com.good.gd.apache.http.HttpRequest;
import com.good.gd.apache.http.HttpResponse;
import com.good.gd.apache.http.client.CookieStore;
import com.good.gd.apache.http.client.methods.HttpGet;
import com.good.gd.apache.http.client.methods.HttpOptions;
import com.good.gd.apache.http.client.methods.HttpPost;
import com.good.gd.apache.http.client.params.HttpClientParams;
import com.good.gd.apache.http.cookie.Cookie;
import com.good.gd.apache.http.entity.EntityTemplate;
import com.good.gd.apache.http.entity.StringEntity;
import com.good.gd.apache.http.params.HttpConnectionParams;
import com.good.gd.apache.http.params.HttpParams;
import com.good.gd.icc.GDICCForegroundOptions;
import com.good.gd.icc.GDServiceClient;
import com.good.gd.icc.GDServiceException;
import com.good.gd.net.GDHttpClient;
import com.good.gd.net.GDNetUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.good.gd.apache.http.entity.ContentProducer;

public class StreamWebViewClient extends WebViewClient {
    private static final String TAG = StreamWebViewClient.class.getSimpleName();
    private Context context = null;

    private long datePageStart = -1;
    private long datePageFinish = -1;

    public void setContext(Context context) {
        this.context = context;
    }

    private String logStr(String value) {
        if (value == null) {
            return " null";
        }
        return " \"" + value + "\"";
    }
    private String logURI(Uri value) {
        return logStr(value.toString());
    }
    private String logThread() {
        return logStr(Thread.currentThread().getName());
    }

    private boolean getSetting(String setting) {
        return Settings.getInstance().getSetting(setting);
    }

    private Map<String, Map<String, Object>> requestCache = null;

    public void addRequestCache(String uuid, HashMap<String, Object> map) {
        if (this.requestCache == null) {
            this.requestCache = new HashMap<String, Map<String, Object>>();
        }
        this.requestCache.put(uuid, map);
    }

    class ReaderContentProducer implements ContentProducer {
        Reader reader = null;

        ReaderContentProducer(Reader reader) {
            super();
            this.reader = reader;
        }

        ReaderContentProducer(String string) {
            super();
            this.reader = new StringReader(string);
        }

        @Override
        public void writeTo(OutputStream outputStream) throws IOException {
            OutputStream outputBuffer = new BufferedOutputStream(outputStream);
            for (;;) {
                int data = this.reader.read();
                if (data == -1) {
                    break;
                }
                outputBuffer.write(data);
            }
            this.reader.close();
            outputBuffer.close();
        }
    }

    private JavaScriptBridge bridge = null;
    class JavaScriptBridge {
        @JavascriptInterface
        public String getSettings() {
            return Settings.getInstance().toString();
        }

        @JavascriptInterface
        public String mergeSettings(String toMergeJSON) {
            try {
                return Settings.getInstance().mergeSettings(toMergeJSON);
            } catch (JSONException exception) {
                exception.printStackTrace();
                Map<String, String> map = new HashMap<>();
                map.put("error", exception.toString());
                return JSONObject.wrap(map).toString();
            }
        }

        @JavascriptInterface
        public String getTitle() {return "Android WebView";}

        @JavascriptInterface
        public String getStr() {
            return this.getClass().getSimpleName();
        }

        @JavascriptInterface
        public String alert(String message) {
            Log.d(TAG, "alert(" + message + ")");
            return message;
        }

        static final String UNIQUE_ACTION = "uniqueAction";

        @JavascriptInterface
        public String postMessage(String messageJSON) {
            JSONObject message = null;
            Iterator<String> keyIterator = null;
            String return_ = "";
            Log.d(TAG, "postMessage(" + messageJSON + ")");
            try {
                message = new JSONObject(messageJSON);
                keyIterator = message.keys();
            } catch (JSONException exception) {
                keyIterator = null;
                return_ = exception.toString();
            }
            HashMap<String, Object> messageMap = new HashMap<String, Object>();

            while (keyIterator != null && keyIterator.hasNext()) {
                String key = keyIterator.next();
                messageMap.put(key, message.opt(key));
            }

            if (messageMap.containsKey(UNIQUE_ACTION)) {
                Uri uri = Uri.parse((String) messageMap.get(UNIQUE_ACTION));
                String uuid = StreamWebViewClient.getUuidParameter(uri);
                StreamWebViewClient.this.addRequestCache(uuid, messageMap);
            }

            return return_;
        }
    }

    public void register(WebView webView) {
        webView.setWebViewClient(this);
        this.bridge = new JavaScriptBridge();
        webView.addJavascriptInterface(this.bridge, "bridge");
    }

    private void lookup(String host) {
        if (Character.isDigit(host.charAt(0))) {
            Log.d(TAG, "Not sending nslookup for digits" + logStr(host) + ".");
            return;
        }
        Log.d(TAG, "Sending nslookup for" + logStr(host) + " ...");
        GDNetUtility.getInstance().nslookup(
            host,
            GDNetUtility.GDNslookupType.GDNslookupARECORD,
            new GDNetUtility.GDNslookupCallback() {
                @Override
                public void onNslookupResponseSuccess(JSONObject jsonObject) {
                    Log.d(TAG,
                        "... nslookup success " + Thread.currentThread().getName() + " " +
                            jsonObject.toString());
                }

                @Override
                public void onNslookupResponseFailure(
                    GDNetUtility.GDNetUtilityErr gdNetUtilityErr
                ) {
                    Log.d(TAG,
                        "... nslookup failure " + Thread.currentThread().getName() + " " +
                            gdNetUtilityErr.toString());
                }
            }
        );
    }

    private Uri uriForRedirect(Uri uri, ResponseBuilder responseBuilder) {
        final Uri location = Uri.parse(responseBuilder.headers.get("Location"));
        Uri.Builder builder = location.buildUpon();

        // Next statements support the Location being a value like "index302.html" that doesn't have
        // a scheme nor server address.
        if (location.getScheme() == null) {
            builder.scheme(uri.getScheme());
        }
        if (location.getEncodedAuthority() == null) {
            builder.encodedAuthority(uri.getEncodedAuthority());
        }
        if (location.isRelative()) {
            builder.path(uri.getPath());
            for (String segment : location.getPathSegments()) {
                builder.appendPath(segment);
            }
        }
        Uri redirect = builder.build();
        Log.d(TAG,
            "uriForRedirect" + logURI(redirect) +
            " uri:" + uri.getPathSegments().size() +
            " loc:" + location.getPathSegments().size() +
            " abs:" + location.isAbsolute() + " rel:" + location.isRelative()
        );

        return redirect;
    }

    private String logHeaders(final HttpRequest httpRequest) {
        return logHeaders(httpRequest.getAllHeaders());
    }
    private String logHeaders(final HttpResponse httpResponse) {
        return logHeaders(httpResponse.getAllHeaders());
    }
    private String logHeaders(final Header[] headers) {
        if (headers.length <= 0) {
            return " None";
        }
        StringBuilder builder = new StringBuilder();
        for (Header header : headers) {
            builder.append("\n'").append(header.getName()).append("': \"")
                .append(header.getValue()).append("\"");
        }
        return builder.toString();
    }
    private String logHeaders(final WebResourceRequest request) {
        return logHeaders(request.getRequestHeaders());
    }
    private String logHeaders(final WebResourceResponse response) {
        return logHeaders(response.getResponseHeaders());
    }
    private String logHeaders(final Map<String, String> headers) {
        if (headers == null || headers.size() <= 0) {
            return " None";
        }
        final StringBuilder builder = new StringBuilder();
        for (String key : headers.keySet()) {
            builder.append("\n'").append(key).append("': \"").append(headers.get(key)).append("\"");
        }
        return builder.toString();
    }

    private String logProviders(final Vector<GDServiceProvider> providers) {
        if (providers == null) {return " None";}
        final StringBuilder builder = new StringBuilder(" Providers:" + providers.size());
        for (int index=0; index < providers.size(); index++) {
            final GDServiceProvider provider = providers.elementAt(index);
            builder.append("\n[").append(index).append("]").append(logStr(provider.getAddress()))
            .append(logStr(provider.getName()));
        }
        return builder.toString();
    }

    static final String UUID_PARAMETER = "gd_uuid_gd";
    static String getUuidParameter(Uri uri) {
        Set<String> names;
        try {
            names = uri.getQueryParameterNames();
        }
        catch (UnsupportedOperationException exception) {
            names = new HashSet<String>();
        }
        String uuid = null;
        if (names.contains(UUID_PARAMETER)) {
            uuid = uri.getQueryParameter(UUID_PARAMETER);
        }
        return uuid;
    }

    class StreamCookieStore implements CookieStore {
        private List<Cookie> list = new ArrayList<Cookie>();

        @Override
        public void addCookie(Cookie cookie) {
            list.add(cookie);
            Log.d(TAG, "Adding cookie" + logStr(cookie.toString()) + ".");
            return;
        }

        @Override
        public List<Cookie> getCookies() {
            return this.list;
        }

        @Override
        public boolean clearExpired(Date date) {
            return false;
        }

        @Override
        public void clear() {
            return;
        }
    }
    StreamCookieStore cookieStore = new StreamCookieStore();

    static final String UUID_HEADER = "X-GD-UUID";
    static final String REQUEST_BODY = "requestBody";
    static final String ORIGINAL_ACTION = "origAction";
    private ResponseBuilder executeHTTP(final WebResourceRequest resourceRequest,
                                        Uri uri,
                                        final GDHttpClient httpClient)
    {
        final String method = resourceRequest.getMethod().toLowerCase();
        HttpRequest httpRequest = null;

        String uuid = getUuidParameter(uri);
        if (uuid != null) {
            uri = Uri.parse((String) this.requestCache.get(uuid).get(ORIGINAL_ACTION));
        }

        Map<String, String> headers = resourceRequest.getRequestHeaders();
        if (headers.containsKey(UUID_HEADER)) {
            Log.d(TAG, "Request has header" + logStr(UUID_HEADER) + " with value" +
                logStr(headers.get(UUID_HEADER)));
            if (uuid == null) {
                uuid = getUuidParameter(Uri.parse(headers.get(UUID_HEADER)));
            }
        }
        HttpEntity bodyEntity = null;
        if (uuid != null) {
            final String bodyData = (String) this.requestCache.get(uuid).get(REQUEST_BODY);
            if (getSetting("chunked")) {
                ContentProducer contentProducer = new ReaderContentProducer(bodyData);
                bodyEntity = new EntityTemplate(contentProducer);
            } else {
                try {
                    bodyEntity = new StringEntity(bodyData, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    Log.d(TAG,
                            "UnsupportedEncodingException" + logURI(uri) + " " + exception.toString() + ".");
                    return new ResponseBuilder(404,
                            "UnsupportedEncodingException" + " " + exception.toString() + ".");
                }
            }
        }

        if (method.equals("get")) {
            httpRequest = new HttpGet(uri.toString());
        }
        else if (method.equals("post")) {
            HttpPost post = new HttpPost(uri.toString());
            if (bodyEntity != null) {
                post.setEntity(bodyEntity);
            }
            httpRequest = post;
        }
        else if (method.equals("options")) {
            httpRequest = new HttpOptions(uri.toString());
            if (bodyEntity != null) {
                Log.e(TAG, "Sorry, HTTP OPTIONS with body isn't supported.");
            }
        }

        if (httpRequest == null) {
            return new ResponseBuilder(404,  "Unsupported method" + logStr(method));
        }

        final HttpParams params = httpRequest.getParams();
        final int timeOutSeconds = 20;
        HttpConnectionParams.setConnectionTimeout(params, 1000 * timeOutSeconds);
        HttpClientParams.setRedirecting(params, getSetting("apacheRedirect"));


//        HttpClientParams.setCookiePolicy(params, CookiePolicy.BROWSER_COMPATIBILITY);
//        HttpClientParams.setCookiePolicy(params, CookiePolicy.RFC_2965);
//        HttpClientParams.setCookiePolicy(params, CookiePolicy.RFC_2109);
//        HttpClientParams.setCookiePolicy(params, CookiePolicy.NETSCAPE);

//        httpClient.setCookieStore(this.cookieStore);


        Log.d(TAG, "Cookie policy" + logStr(HttpClientParams.getCookiePolicy(params)) +
            " Default headers for" + logURI(uri) + " " + logHeaders(httpRequest));
        for (String header : headers.keySet()) {
            if (!header.equalsIgnoreCase(UUID_HEADER)) {
                httpRequest.setHeader(header, headers.get(header));
            }
        }
        Log.d(TAG,
            "Web resource request headers for" + logURI(uri) + " " + logHeaders(httpRequest));

        ResponseBuilder responseBuilder = null;

        HttpResponse httpResponse = null;
        final long start = SystemClock.elapsedRealtime();
        try {
            if (method.equals("get")) {
                httpResponse = httpClient.execute((HttpGet) httpRequest);
            }
            else if (method.equals("post")) {
                httpResponse = httpClient.execute((HttpPost) httpRequest);
            }
            else if (method.equals("options")) {
                httpResponse = httpClient.execute((HttpOptions) httpRequest);
            }
            else {
                throw new AssertionError("No case to execute method" + logStr(method) + ".");
            }
        }
        catch (IOException exception) {
            // An IOException could be raised if the SSL/TLS connection fails.
            Log.d(TAG,
                "IOException executing HTTP" + logURI(uri) + " " + exception.toString() + ".");
            responseBuilder = new ResponseBuilder(404, exception);
            httpResponse = null;
        }
        catch (IllegalStateException exception) {
            Log.d(TAG,
                    "IllegalStateException executing HTTP" + logURI(uri) + " " + exception.toString() + ".");
            final String message = exception.getMessage();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            });
            responseBuilder = new ResponseBuilder(406, exception);
            httpResponse = null;
        }
        double seconds = ((float)(SystemClock.elapsedRealtime() - start)) / 1000.0;
        Log.d(TAG,
            "Elapsed time for" + logURI(uri) +
                " redirecting:" + HttpClientParams.isRedirecting(params) +
                String.format(" %.02f", seconds) + logThread());

        if (responseBuilder == null) {
            responseBuilder = new ResponseBuilder();
            responseBuilder.context = this.context;
            if (getSetting("injectHTML")) {
                responseBuilder.setInjectAsset("inject.html");
            }
            else {
                responseBuilder.setInjectAsset(null);
            }
            responseBuilder.build(resourceRequest, httpResponse, httpClient);
        }

        return responseBuilder;
    }

    private ResponseBuilder getByHTTP(final WebResourceRequest request) {
        Uri uri = request.getUrl();

        ResponseBuilder responseBuilder = null;
        // Code will go around the loop once for each redirection.
        final int max3xx = 10;
        for (int tries=0; responseBuilder == null && tries < max3xx; tries++) {
            if (getSetting("nslookup")) {
                // Informational only. The code only logs the result.
                this.lookup(uri.getHost());
            }

            GDHttpClient httpClient = new GDHttpClient();
            responseBuilder = executeHTTP(request, uri, httpClient);

            Log.d(TAG, "Executed HTTP for" + logURI(uri) + logThread());

            int statusCode = responseBuilder.statusCode;

            if (statusCode >= 300 && statusCode < 400) {
                final Uri redirect = uriForRedirect(uri, responseBuilder);
                if (redirect.equals(uri)) {
                    throw new AssertionError("Redirect to same location" + logURI(uri) +
                        " " + responseBuilder.headers.toString());
                }
                // If there was any content with the redirect, a WebInputStream will have been
                // created and passed the httpClient. It is then correct to drain the content and
                // close the stream.
                if (responseBuilder.getGDHttpClient() != null) {
                    Log.d(TAG, "Connection will be drained for " + statusCode +
                        logURI(uri) + logURI(redirect) + " " + responseBuilder.headers.toString());
                    int drainage = responseBuilder.stream.drain();
                    responseBuilder.stream.close();
                    Log.d(TAG, "Drain " + drainage + logURI(uri) + logThread());

                    httpClient = null;
                }
                // Set the uri for the next go-around the loop.
                uri = redirect;
                responseBuilder = null;
            }

            if (httpClient != null &&
                responseBuilder != null &&
                responseBuilder.getGDHttpClient() == null)
            {
                Log.d(TAG, "Shutting down connection in get loop for" + logURI(uri));
                httpClient.getConnectionManager().shutdown();
                // Could set httpClient to null here but it goes out of scope in every iteration.
            }
        }
        if (responseBuilder == null) {
            // If the code gets here, then there were too many redirects.
            responseBuilder = new ResponseBuilder(
                404, "Maximum redirects exceeded:" + max3xx + ".");
        }
        return responseBuilder;
    }

    private WebResourceResponse getAsset(WebResourceRequest request) {
        final Uri uri = request.getUrl();
        final String host = uri.getHost();
        final int port = uri.getPort();
        if (!( host != null && host.equals("localhost") && port == 1 )) {
            return null;
        }

        String name = "index.html";
        String extension;
        // If contentType isn't set, the WebView won't run the JS code. It appears that the
        // MimeTypeMap doesn't map .js to anything, so there's a special case for that.

        if (uri.getPathSegments().size() > 0) {
            name = uri.getLastPathSegment();
            extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        }
        else {
            extension = MimeTypeMap.getFileExtensionFromUrl(name);
        }
        String contentType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (contentType == null && extension.equals("js")) {
            contentType = "application/javascript";
        }

        int statusCode = 200;
        String reasonPhrase = "OK";
        WebInputStream stream = null;

        try {
            InputStream assetStream = this.context.getAssets().open(name);
            stream = new WebInputStream(assetStream, name);
        } catch (IOException exception) {
            Log.d(TAG, "Asset exception " + exception.toString());
            stream = new WebInputStream();
            statusCode = 404;
            reasonPhrase = exception.toString();
        }
        return new WebResourceResponse(contentType, null, statusCode, reasonPhrase, null, stream);
    }

    private static final String serviceID = "com.good.gdservice.open-url.http";
    private static final String serviceVersion = "1.0.0.0";
    private static final String serviceMethod = "open";
    private static final String serviceAttachments[] = new String[0];
    private Boolean openByAppKinetics(Uri uri) {
        if (!Settings.getInstance().getSetting("appKinetics")) {
            return false;
        }

        final String host = uri.getHost();
        final int port = uri.getPort();
        if (host != null && host.equals("localhost") && port == 1) {
            return false;
        }

        final Vector<GDServiceProvider> providers = GDAndroid.getInstance().getServiceProvidersFor(
            serviceID, serviceVersion, GDServiceType.GD_SERVICE_TYPE_APPLICATION);
        Log.d(TAG,
            "openByAppKinetics service discovery" + logStr(serviceID) + logStr(serviceVersion) +
            logProviders(providers));
        if (providers.size() < 1) {
            Log.e(TAG,
                "No providers for service" + logStr(serviceID) + logStr(serviceVersion) + ".");
            // Possibly should raise an exception here although it'd be nicer to create a web page
            // on the fly and show it in the WebView.
        }
        else {
            final Map<String, String> params = new HashMap<>();
            params.put("url", uri.toString());
            // Select the first provider. It'd be better to give the user an option to select one
            // but this is only a PoC.
            final String address = providers.elementAt(0).getAddress();
            try {
                final String requestID = GDServiceClient.sendTo(address, serviceID, serviceVersion,
                    serviceMethod, params, serviceAttachments,
                    GDICCForegroundOptions.PreferPeerInForeground);
                Log.d(TAG, "Sent service request" + logStr(address) + logStr(requestID));
            } catch (GDServiceException exception) {
                Log.e(TAG, "Service exception " + exception.toString());
            }
        }

        return true;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        final Uri uri = request.getUrl();
        final String method = request.getMethod();
        final CookieManager cookieManager = CookieManager.getInstance();

        Log.d(TAG,
            "shouldInterceptRequest" +
            " main:" + request.isForMainFrame() + " intercept:" + getSetting("intercept") +
            logStr(method) + logURI(uri)  + logStr(cookieManager.getCookie(uri.toString())) +
            logThread() + logHeaders(request));

        WebResourceResponse response = getAsset(request);
        if (response == null) {
            ResponseBuilder responseBuilder = null;
            if (getSetting("intercept") || getSetting("retrieve")) {
                responseBuilder = getByHTTP(request);
                if (getSetting("retrieve")) {
                    Log.d(TAG, "Retrieve mode, connection will be drained " +
                        logURI(uri) + " " + responseBuilder.statusCode +
                        logStr(responseBuilder.reasonPhrase) +
                        " headers:" + logHeaders(responseBuilder.headers));
                    int drainage = responseBuilder.stream.drain();
                    responseBuilder.stream.close();
                    Log.d(TAG, "Retrieval drain " + drainage + logThread());

                    responseBuilder = new ResponseBuilder(404, "Retrieve mode.");
                }
                response = responseBuilder.toWebResponse();
            }
            // else leave response as null, which tells the WebView to handle it itself.
        }

        Log.d(TAG, "shouldInterceptRequest response:" + (response == null ?
            "null" :
            ("" + response.getStatusCode() + logStr(response.getReasonPhrase()) + " headers:" +
                logHeaders(response))
        ));

        return response;
    }

    // These don't get called if the URL loading was initiated by loadUrl(). Returning true stops
    // the WebView from doing anything and means the code should handle it itself, which it might
    // do by calling loadUrl, which explains the first statement. The implementation here only logs
    // the thread.
    @Override
    public boolean shouldOverrideUrlLoading (WebView view, String url) {
        Log.d(TAG, "shouldOverrideUrlLoading url" + logStr(url) + logThread());
        return this.openByAppKinetics(Uri.parse(url));
    }
    //
    // The WebResourceRequest variant doesn't get called, which might be because of the API level
    // settings in the project. It's left in for completeness.
    @Override
    public boolean shouldOverrideUrlLoading (WebView view, WebResourceRequest request) {
        Log.d(TAG, "shouldOverrideUrlLoading request" + logURI(request.getUrl()) + " " +
            request.isForMainFrame() + logThread());
        return this.openByAppKinetics(request.getUrl());
    }

    // These are only used to log time taken to load the page and see which thread was running.
    @Override
    public void onPageStarted(WebView webView, String url, Bitmap favicon) {
        this.datePageStart = SystemClock.elapsedRealtime();
        Log.d(TAG, "onPageStarted(," + url + ",)" + logThread());
        super.onPageStarted(webView, url, favicon);
    }
    @Override
    public void onPageFinished(WebView webView, String url) {
        this.datePageFinish = SystemClock.elapsedRealtime();
        double seconds = ((float)(this.datePageFinish - this.datePageStart)) / 1000.0;
        Log.d(TAG, "onPageFinished(," + url + ") " + String.format("%.02f", seconds) + logThread());
        super.onPageFinished(webView, url);
    }
}
