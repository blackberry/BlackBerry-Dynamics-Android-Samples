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

import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import com.good.gd.apache.http.Header;
import com.good.gd.apache.http.HttpResponse;
import com.good.gd.apache.http.NameValuePair;
import com.good.gd.apache.http.client.entity.UrlEncodedFormEntity;
import com.good.gd.apache.http.client.methods.HttpDelete;
import com.good.gd.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import com.good.gd.apache.http.client.methods.HttpGet;
import com.good.gd.apache.http.client.methods.HttpHead;
import com.good.gd.apache.http.client.methods.HttpOptions;
import com.good.gd.apache.http.client.methods.HttpPatch;
import com.good.gd.apache.http.client.methods.HttpPost;
import com.good.gd.apache.http.client.methods.HttpPut;
import com.good.gd.apache.http.client.methods.HttpRequestBase;
import com.good.gd.apache.http.client.protocol.ClientContext;
import com.good.gd.apache.http.cookie.Cookie;
import com.good.gd.apache.http.cookie.CookieOrigin;
import com.good.gd.apache.http.entity.StringEntity;
import com.good.gd.apache.http.impl.cookie.RFC2965Spec;
import com.good.gd.apache.http.message.BasicHeader;
import com.good.gd.apache.http.message.BasicNameValuePair;
import com.good.gd.apache.http.protocol.BasicHttpContext;
import com.good.gd.apache.http.protocol.HttpContext;
import com.good.gd.net.GDHttpClient;
import com.good.gd.webview_V2.bbwebview.BBWebViewClient;
import com.good.gd.webview_V2.bbwebview.utils.Utils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.good.gd.apache.http.protocol.HTTP.US_ASCII;
import static com.good.gd.apache.http.protocol.HTTP.UTF_8;
import static com.good.gd.webview_V2.bbwebview.BBWebViewClient.PIXEL_2XL_UA;

public class RequestTask implements GDHttpClientProvider.ClientCallback<Pair<HttpResponse,HttpContext>> {

    private static final String TAG = "GDWebView-" + RequestTask.class.getSimpleName();

    private String clientConnId;
    private WebResourceRequest request;
    private String targetUrl;
    private String originalWebViewUrl;
    private WebView vw;

    public static final String GD_INTERCEPT_TAG = "gdinterceptrequest";

    public static final ConcurrentHashMap<String, BrowserContext> REQUESTS_BODIES = new ConcurrentHashMap<>();

    public static class BrowserContext {

        private static final String NO_CORS = "no-cors";
        private static final String FETCH_MODE = "mode";

        public static final BrowserContext NULL = new BrowserContext("", "", "");

        public final String body;
        public final String url;

        private final String context;

        public BrowserContext(String body, String url, String context) {
            this.body = body == null ? "" : body;
            this.url = url == null ? "" : url;
            this.context = context == null ? "" : context;
        }

        public boolean isNoCorsModeEnabled() {
            try {
                JSONObject object = new JSONObject(context);
                if (object.get(FETCH_MODE).equals(NO_CORS)) {
                    return true;
                }
            } catch (Exception e) {
                Log.i(TAG,"isNoCorsModeEnabled, exception: " + e);
                e.printStackTrace();
            }
            return false;
        }

        public boolean isRequestContextUnknown() { return context.isEmpty(); }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BrowserContext that = (BrowserContext) o;
            return body.equals(that.body) &&
                    url.equals(that.url) &&
                    context.equals(that.context);
        }

        @Override
        public int hashCode() {
            return Objects.hash(body, url, context);
        }
    }

    private boolean isInterceptedRequest(WebResourceRequest request) {
        return request.getUrl().toString().contains(GD_INTERCEPT_TAG);
    }

    private String getRequestBodyByID(String requestID) {
        synchronized (REQUESTS_BODIES) {
            String body = null;
            if (!TextUtils.isEmpty(requestID)) {
                body = REQUESTS_BODIES.getOrDefault(requestID, BrowserContext.NULL).body;
            }
            return body;
        }
    }

    private String getRequestBody(WebResourceRequest request) {
        String requestID = getInterceptedRequestID(request);
        return getRequestBodyByID(requestID);
    }

    private BrowserContext getRequestContext(WebResourceRequest request) {
        BrowserContext context = BrowserContext.NULL;
        String requestID = getInterceptedRequestID(request);
        synchronized (REQUESTS_BODIES) {
            if (!TextUtils.isEmpty(requestID)) {
                context = REQUESTS_BODIES.getOrDefault(requestID, BrowserContext.NULL);
            }
        }
        return context;
    }

    static public String[] getUrlSegments(WebResourceRequest request, String divider) {
        String urlString = request.getUrl().toString();
        return urlString.split(divider);
    }

    private String getInterceptedRequestID(WebResourceRequest request) {
        return getUrlSegments(request, GD_INTERCEPT_TAG)[1];
    }

    private HttpRequestBase getHttpRequestBase(WebResourceRequest request, URI uri) {

        HttpRequestBase method;

        String body = null;
        BrowserContext browserContext = BrowserContext.NULL;

        if (isInterceptedRequest(request)) {
            body = getRequestBody(request);//"Amount=10&B2=Submit"

            browserContext = getRequestContext(request);

            Log.i(TAG,"request body: " + body);
        }

        switch (request.getMethod()) {

            case "GET": {
                    method = new HttpGet(uri);
                    setGETRequestHeaders(uri,method,request,originalWebViewUrl, browserContext);
                }
                    break;
            case "HEAD": {
                method = new HttpHead(uri);
                setGETRequestHeaders(uri,method,request,originalWebViewUrl, null);
            }
            break;
            case "POST": {
                HttpPost httpPost = new HttpPost(uri);

                if (body != null) {
                    formRequestEntity(request, uri, body, httpPost);
                } else {
                    Log.i(TAG,"empty POST request body");
                }

                setPOSTRequestHeaders(uri, httpPost, request,originalWebViewUrl, browserContext);

                if(body != null && httpPost.containsHeader("Content-Type")){
                    Header header = httpPost.getHeaders("Content-Type")[0];

                    if(header.getValue().contains("multipart/form-data")){
                        String boundaryFromBody = body.replaceAll("(\\r\\n|\\n)+.*", "").replaceAll("^--","");
                        httpPost.setHeader("Content-Type",header.getValue().replaceAll("boundary.*","boundary=" + boundaryFromBody));
                    }
                }

                method = httpPost;
                }
                break;
            case "PUT": {
                    HttpPut httpPut = new HttpPut(uri);
                    setPOSTRequestHeaders(uri, httpPut, request, originalWebViewUrl, null);

                    if (body != null) {
                        formRequestEntity(request, uri, body, httpPut);
                    }

                    method = httpPut;
                }
                break;
            case "OPTIONS":
                method = new HttpOptions(uri);
                setGETRequestHeaders(uri,method,request,originalWebViewUrl, null);
                break;
            case "DELETE":
                method = new HttpDelete(uri);
                setGETRequestHeaders(uri,method,request,originalWebViewUrl, null);
                break;
            case "PATCH": {
                    HttpPatch httpPatch = new HttpPatch(uri);
                    setPOSTRequestHeaders(uri, httpPatch, request, originalWebViewUrl, null);


                    if (body != null) {
                        formRequestEntity(request, uri, body, httpPatch);
                    }
                    method = httpPatch;
                }
                break;
            default:
                method = null;
                break;
        }

        Utils.debugLogHeaders(method.getAllHeaders());

        Log.i(TAG,"request created: " + request.getMethod() + " " + uri);

        return method;
    }

    private static void setGETRequestHeaders(final URI targetUri, final HttpRequestBase httpMethod,final WebResourceRequest webResourceRequest,
                                             final String originWebViewUrl, BrowserContext browserContext) {

        Log.i(TAG, "setGETRequestHeaders IN ");

        Log.i(TAG, "setGETRequestHeaders " + "origin:" + originWebViewUrl);
        Log.i(TAG, "setGETRequestHeaders " + "request:" + targetUri);

        final Map<String, String> webViewHeaders = webResourceRequest.getRequestHeaders();

        // Basically, we do not need to override or add headers if the request context is unknown.
        // Also check for 'Accept' and 'Origin' headers, they are required for a response to be successful.
        if (webResourceRequest.getRequestHeaders().containsKey("Accept")
                && webResourceRequest.getRequestHeaders().containsKey("Origin")
                && browserContext != null && browserContext.isRequestContextUnknown()) {

                Log.i(TAG, "setGETRequestHeaders - unknown context, return");

                // Add WebView provided headers to the request
                for (Map.Entry<String, String> header : webViewHeaders.entrySet()) {
                    httpMethod.addHeader(header.getKey(), header.getValue());
                }

                return;
        }

        Map<String, String> completeRequestHeaders = new TreeMap<String, String>() {{

            URI originURI = null;
            URI refererURI = null;

            if(originWebViewUrl != null){
                originURI = URI.create(originWebViewUrl);
            }

            String refererUrl = webResourceRequest.getRequestHeaders().get("Referer");
            if(refererUrl != null){
                refererURI = URI.create(refererUrl);
            }

            put("Host", targetUri.getAuthority());
            put("Connection", "keep-alive");
            put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            put("Accept-Encoding", "gzip, deflate, br");
            put("Accept-Language", "en-US,en;q=0.9");
            put("Cache-Control", "no-store");

            if(originURI != null) {
                // no Origin for GET,HEAD requests
                // put("Origin", originURI.getScheme() + "://" + originURI.getAuthority());
            }

            put("Pragma", "no-cache");
            //put("Referer", "");
            put("Save-Data", "on");

            String secFetchDest = secFetchDestHeader(webResourceRequest, webViewHeaders);
            put("Sec-Fetch-Dest", secFetchDest);

            String secFetchMode = secFetchModeHeader(webResourceRequest, webViewHeaders, targetUri);
            put("Sec-Fetch-Mode", secFetchMode);

            String secFetchSite = secFetchSiteHeader(originURI, refererURI, targetUri);
            put("Sec-Fetch-Site", secFetchSite);

            put("Sec-Fetch-User", "?1");

            put("Upgrade-Insecure-Requests", "1");
            put("User-Agent", PIXEL_2XL_UA);

        }};

        Log.i(TAG, "setGETRequestHeaders webViewHeaders " + webViewHeaders);

        httpMethod.setHeaders(new BasicHeader[]{});
        httpMethod.addHeader("Host", completeRequestHeaders.get("Host"));

        for (String headerKey : webViewHeaders.keySet()) {
            String value = webViewHeaders.get(headerKey).split(GD_INTERCEPT_TAG)[0];

            // Content cache settings are disabled. Do not add the header for images to prevent '304 Not Modified' response with no content.
            if (!headerKey.equals("If-Modified-Since") && Utils.isImage(targetUri.toString())) {
                httpMethod.addHeader(headerKey, value);
            }
        }

        for (String key : completeRequestHeaders.keySet()) {
            if(!httpMethod.containsHeader(key)){
                httpMethod.addHeader(key,completeRequestHeaders.get(key));
            }
        }

        Log.i(TAG, "setGETRequestHeaders OUT ");
    }

    private static String secFetchDestHeader(final WebResourceRequest webResourceRequest,final Map<String, String> webViewHeaders) {
        String secFetchDest = "empty";
        if(webResourceRequest.isForMainFrame()){
            secFetchDest = "document";
        } else {
            if(webViewHeaders.containsKey("Accept")){
                String acceptHeader = webViewHeaders.get("Accept");

                if(acceptHeader.startsWith("image")){
                    secFetchDest = "image";
                }
                if(acceptHeader.endsWith("css")){
                    secFetchDest = "style";
                }
                if(acceptHeader.endsWith("script")){
                    secFetchDest = "script";
                }
                if(acceptHeader.startsWith("video")){
                    secFetchDest = "video";
                }
            }
        }
        return secFetchDest;
    }

    private static String secFetchModeHeader(WebResourceRequest webResourceRequest, Map<String, String> webViewHeaders, URI targetUri) {
        String secFetchMode = "cors";
        if(webResourceRequest.isForMainFrame()){
            secFetchMode = "navigate";
        } else {
            if(webViewHeaders.containsKey("Authority")){
                if(URI.create(webViewHeaders.get("Authority")).getAuthority().equalsIgnoreCase(targetUri.getAuthority())){
                    secFetchMode = "no-cors";
                }
            }
        }
        return secFetchMode;
    }

    private static String secFetchSiteHeader(final URI originURI,final URI refererURI,final URI targetUri) {
        Log.i(TAG, "secFetchSiteHeader O:" + originURI);
        Log.i(TAG, "secFetchSiteHeader R:" + refererURI);
        Log.i(TAG, "secFetchSiteHeader T:" + targetUri);

        String secFetchSite = null;
        if(originURI == null){
            secFetchSite = "none";
            if(refererURI != null){
                if(!refererURI.getAuthority().equalsIgnoreCase(targetUri.getAuthority())){
                    secFetchSite = "cross-site";
                } else {
                    secFetchSite = "same-origin";
                }
            }
        }else if(originURI.getAuthority().equalsIgnoreCase(targetUri.getAuthority())){
            secFetchSite = "none";
            if(refererURI != null){
                secFetchSite = "same-origin";
            }
        } else {
            secFetchSite = "cross-site";
        }

        Log.i(TAG, "secFetchSiteHeader " + secFetchSite);

        return secFetchSite;
    }

    private static void setPOSTRequestHeaders(final URI targetUri,final  HttpRequestBase httpMethod,final  WebResourceRequest webResourceRequest,
                                              final  String originWebViewUrl, final BrowserContext browserContext) {

        Log.i(TAG, "setPOSTRequestHeaders IN ");

        final URI originURI = URI.create(originWebViewUrl);

        Log.i(TAG, "setPOSTRequestHeaders " + "origin:" + originURI);
        Log.i(TAG, "setPOSTRequestHeaders " + "request:" + targetUri);

        final Map<String, String> webViewHeaders = webResourceRequest.getRequestHeaders();

        Map<String, String> completeRequestHeaders = new TreeMap<String, String>() {{

            URI originURI = null;
            URI refererURI = null;

            if(originWebViewUrl != null){
                originURI = URI.create(originWebViewUrl);
            }

            String refererUrl = webResourceRequest.getRequestHeaders().get("Referer");
            if(refererUrl != null){
                refererURI = URI.create(refererUrl);
            }

            put("Host", targetUri.getAuthority());
            //put("Authority", targetUri.getAuthority());
            put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            put("Accept-Encoding", "gzip, deflate, br");
            put("Accept-Language", "en-US,en;q=0.9,uk-UA;q=0.8,uk;q=0.7,ru;q=0.6");
            put("Cache-Control", "no-cache");
            // put("Connection", "keep-alive");
            // put("Content-Length", "" + contentLength);//set by GDApache internally
            put("Content-Type", "aplication/x-www-form-urlencoded");

            put("Origin", originURI.getScheme() + "://" + originURI.getAuthority());
            put("Pragma", "no-cache");
            //put("Referer", "");
            put("Save-Data", "on");
            String secFetchDest = secFetchDestHeader(webResourceRequest, webViewHeaders);
            put("Sec-Fetch-Dest", secFetchDest);

            if (browserContext != null && browserContext.isNoCorsModeEnabled()) {
                put("Sec-Fetch-Mode", "no-cors");
            } else {
                String secFetchMode = secFetchModeHeader(webResourceRequest, webViewHeaders, targetUri);
                put("Sec-Fetch-Mode", secFetchMode);
            }

            String secFetchSite = secFetchSiteHeader(originURI, refererURI, targetUri);
            put("Sec-Fetch-Site", secFetchSite);

            put("Sec-Fetch-User", "?1");

            put("Upgrade-Insecure-Requests", "1");
            put("User-Agent", PIXEL_2XL_UA);

        }};

        Log.i(TAG, "setPOSTRequestHeaders webViewHeaders " + webViewHeaders);

        httpMethod.setHeaders(new BasicHeader[]{});
        httpMethod.addHeader("Host", completeRequestHeaders.get("Host"));

        for (String headerKey : webViewHeaders.keySet()) {
            String value = webViewHeaders.get(headerKey).split(GD_INTERCEPT_TAG)[0];
            httpMethod.addHeader(headerKey, value.trim());
        }

        for (String key : completeRequestHeaders.keySet()) {
            if(!httpMethod.containsHeader(key)){
                httpMethod.addHeader(key,completeRequestHeaders.get(key).trim());
            }
        }

        Log.i(TAG, "setPOSTRequestHeaders OUT ");
    }


    static private void formRequestEntity(final WebResourceRequest request,final URI uri,final String body,final HttpEntityEnclosingRequestBase entityRequest) {
        try {

            if(request.getRequestHeaders().containsValue("application/x-www-form-urlencoded") && false){//TODO: no worky

                List<NameValuePair> list = new ArrayList<>();

                Log.i(TAG,"request FormEntity >>");

                String[] params = body.split("&");

                Log.i(TAG,String.format("request FormEntity params size(%s)",params.length));
                for (int i = 0; i < params.length; i++) {
                    String param = params[i];
                    Log.i(TAG,String.format("request FormEntity param[%s]=(%s)",i,param));
                    String[] keyVal = param.split("=");
                    list.add(new BasicNameValuePair(keyVal[0], keyVal[1]));
                }

                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(list, US_ASCII);

                entityRequest.setEntity(urlEncodedFormEntity);

                Log.i(TAG,String.format("request FormEntity values size(%s) <<",list.size()));
            } else {
                Log.i(TAG,"request StringEntity >>");
                StringEntity entity = new StringEntity(body, UTF_8);
                entity.setContentType(request.getRequestHeaders().get("Content-Type"));

                entityRequest.setEntity(entity);

                Log.i(TAG,"request StringEntity <<");
            }

        } catch (Exception e) {
            Log.e(TAG,String.format("request (%s) (%s)",request.getMethod(),uri),e);
        }
    }


    public RequestTask(String clientConnId, WebResourceRequest webReq, String urlIntercepted, String originalWebViewUrl, WebView vw) {
        this.clientConnId = clientConnId;
        request = webReq;
        this.targetUrl = urlIntercepted;
        this.originalWebViewUrl = originalWebViewUrl;
        this.vw = vw;
    }

    @Override
        public Pair<HttpResponse,HttpContext> doInClientThread(GDHttpClient httpClient, String id) {

        Log.i(TAG, "doInClientThread IN, targetUrl: " + targetUrl + " clientId: " + id + " requestUrl: " + request.getUrl());

        Utils.debugLogHeaders(request.getRequestHeaders());

        HttpResponse response = null;
        HttpContext httpContext = null;
        try {

            if(request.getMethod().equalsIgnoreCase("post")){
                targetUrl = targetUrl.replaceAll("#.*","/");
            }
            URI targetUri = URI.create(targetUrl);


            HttpRequestBase method = getHttpRequestBase(request, targetUri);
            long nano;

            // Sync with document.cookies set by js
            final CountDownLatch awaitCookies = new CountDownLatch(1);
            final AtomicReference<String> docCookies = new AtomicReference<>();

            vw.post(new Runnable() {
                @Override
                public void run() {
                    vw.evaluateJavascript("document.cookie", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            Log.i(TAG, "document.cookie -> " + value);
                            docCookies.set(value);
                            awaitCookies.countDown();
                        }
                    });
                }
            });

            boolean asyncCookies = awaitCookies.await(3, TimeUnit.SECONDS);
            Log.i(TAG, "document.cookie async " + asyncCookies);

            String cookiesIncludingSecure = CookieManager.getInstance().getCookie("https://" + targetUri.getHost());

            if(cookiesIncludingSecure != null) {

                final List<Cookie> currentHttpCookies = httpClient.getCookieStore().getCookies();
                final Set<String> cookiesId = new HashSet<>();
                for (Cookie currentHttpCookie : currentHttpCookies) {
                    cookiesId.add(currentHttpCookie.getName()+currentHttpCookie.getValue());
                }

                Log.i(TAG, String.format("cookiesStore.size(%s) cookiesIdSet.size(%s)",currentHttpCookies.size(),cookiesId.size()));


                String[] splittedCookies = cookiesIncludingSecure.split(";");
                for (int i = 0; i < splittedCookies.length; i++) {
                    String splittedCookie = splittedCookies[i];
                    splittedCookie = splittedCookie.replaceAll("expires=[A-Za-z0-9,\\s:-]+;", "");

                    RFC2965Spec rfc2965Spec = new RFC2965Spec();

                    try {

                        List<Cookie> cookies = rfc2965Spec.parse(new BasicHeader("Set-Cookie", splittedCookie),
                                new CookieOrigin(targetUri.getHost(), 0, "/", true ));

                        for (Cookie cookee : cookies) {
                            if(!cookiesId.contains(cookee.getName()+cookee.getValue())) {
                                httpClient.getCookieStore().addCookie(cookee);
                                Log.i(TAG, String.format("set cookie gd(%s)", cookee));

                            } else {

                                Log.i(TAG, String.format("skipping cookie gd(%s)", cookee));

                            }

                        }

                    } catch (Exception e) {
                        Log.e(TAG, String.format("set cookie"), e);
                    }
                }

            }

            Log.i(TAG, "HTTP_EXEC " + httpClient.hashCode() + " >> " + "[" + Process.myTid() + "] <" + TimeUnit.NANOSECONDS.toMillis(nano = System.nanoTime()) + "> " + method.getURI().toString());

            httpContext = createHttpContext(httpClient);
            httpContext.setAttribute("webview.http.method",method.getMethod());
            httpContext.setAttribute("webview.http.isForMainFrame",request.isForMainFrame());
            httpContext.setAttribute("webview.connectionId",clientConnId);

            response = httpClient.execute(method, httpContext);

            List<Cookie> cookies = httpClient.getCookieStore().getCookies();

            String targetHost = targetUri.getAuthority();

            URI locationURI = (URI) httpContext.getAttribute("webview.redirect.url");

            //sync cookies from the http response to the webview
            for (Cookie cookie : cookies) {
                if(cookie.getValue() != null) {
                    if(cookie.getDomain() != null &&
                            (cookie.getDomain().equalsIgnoreCase(targetHost) ||
                            (locationURI != null && cookie.getDomain().equalsIgnoreCase(locationURI.getAuthority())))){

                        String cookieValue = cookie.getName() + "=" + cookie.getValue() + "; path=" + cookie.getPath() + "; " +
                                ((cookie.getExpiryDate() != null) ? ("expires=" + cookie.getExpiryDate()) : "") +
                                (cookie.isSecure() ? "; Secure;" : "");

                        CookieManager.getInstance().setCookie((cookie.isSecure() ? "https://" : "") + cookie.getDomain(),
                                cookieValue);

                        Log.d(TAG, String.format("set cookie(%s) to webview(%s)",cookie.getName(), cookieValue));
                    }

                }
            }

            CookieManager.getInstance().flush();

            Log.i(TAG, "HTTP_EXEC " + httpClient.hashCode() + " << " + "[" + Process.myTid() + "] <" + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - nano) + "> " + method.getURI().toString() +" " + response.getStatusLine());
        } catch (Exception e) {
            Log.e(TAG, "HTTP_EXEC execute ERROR: " + e);
            e.printStackTrace();
        }

        Log.i(TAG, "doInClientThread OUT, targetUrl: " + targetUrl);

        return Pair.create(response,httpContext);
    }

    private static HttpContext createHttpContext(GDHttpClient httpClient) {
        HttpContext context = new BasicHttpContext();
        context.setAttribute(
                ClientContext.AUTHSCHEME_REGISTRY,
                httpClient.getAuthSchemes());
        context.setAttribute(
                ClientContext.COOKIESPEC_REGISTRY,
                httpClient.getCookieSpecs());
        context.setAttribute(
                ClientContext.COOKIE_STORE,
                httpClient.getCookieStore());
        context.setAttribute(
                ClientContext.CREDS_PROVIDER,
                httpClient.getCredentialsProvider());
        return context;
    }
}
