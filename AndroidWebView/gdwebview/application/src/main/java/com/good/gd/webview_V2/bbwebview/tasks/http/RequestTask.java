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
import com.good.gd.apache.http.conn.ConnectTimeoutException;
import com.good.gd.apache.http.entity.InputStreamEntity;
import com.good.gd.apache.http.entity.StringEntity;
import com.good.gd.apache.http.message.BasicHeader;
import com.good.gd.apache.http.message.BasicHttpResponse;
import com.good.gd.apache.http.message.BasicNameValuePair;
import com.good.gd.apache.http.protocol.BasicHttpContext;
import com.good.gd.apache.http.protocol.HttpContext;
import com.good.gd.net.GDHttpClient;
import com.good.gd.webview_V2.bbwebview.utils.Utils;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.good.gd.apache.http.protocol.HTTP.US_ASCII;
import static com.good.gd.apache.http.protocol.HTTP.UTF_8;
import static com.good.gd.webview_V2.bbwebview.BBWebViewClient.PIXEL_2XL_UA;

public class RequestTask implements GDHttpClientProvider.ClientCallback<Pair<HttpResponse,HttpContext>> {

    private static final String TAG = "GDWebView-" + RequestTask.class.getSimpleName();

    private String clientConnId;
    private WebResourceRequest request;
    private String targetUrl;
    private String originalWebViewUrl;
    private WebView webView;
    private BrowserContext browserContext;

    public static final String GD_INTERCEPT_TAG = "gdinterceptrequest";

    public static final ConcurrentHashMap<String, BrowserContext> REQUESTS_BODIES = new ConcurrentHashMap<>();

    public static class BrowserContext {

        private static final String NO_CORS = "no-cors";
        private static final String FETCH_MODE = "mode";

        private static final String XML_REQUEST = "XHR.prototype.send";
        private static final String FETCH_REQUEST = "window.fetch";
        private static final String REQUEST_CONTEXT_KEY = "this";

        public static final BrowserContext NULL = new BrowserContext("", "", "");

        public final String body;
        public final String url;

        private final String context;

        public BrowserContext(String body, String url, String context) {
            this.body = body == null ? "" : body;
            this.url = url == null ? "" : url;
            this.context = context == null ? "" : context;

            Log.i(TAG,"BrowserContext() context - " + context);
        }

        public boolean isNoCorsModeEnabled() {
            try {
                JSONObject object = new JSONObject(context);
                if (object.get(FETCH_MODE).equals(NO_CORS)) {
                    return true;
                }
            } catch (Exception e) {
                Log.i(TAG,"isNoCorsModeEnabled, exception: " + e);
            }
            return false;
        }

        public boolean isInterceptedFromJSRequest() {
            boolean result = false;

            try {
                JSONObject object = new JSONObject(context);
                if (object.get(REQUEST_CONTEXT_KEY).equals(XML_REQUEST)
                        || object.get(REQUEST_CONTEXT_KEY).equals(FETCH_REQUEST)) {

                    Log.i(TAG,"isInterceptedFromJSRequest, found Js context");

                    result = true;
                }
            } catch (Exception e) {
                Log.i(TAG,"isInterceptedFromJSRequest, exception: " + e);
            }

            Log.i(TAG,"isInterceptedFromJSRequest, result " + result);

            return result;
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
            String body = "";
            if (!TextUtils.isEmpty(requestID)) {
                if (REQUESTS_BODIES.containsKey(requestID)) {
                    body = REQUESTS_BODIES.get(requestID).body;

                    // Remove cached data after body is retried
                    REQUESTS_BODIES.remove(requestID);
                }
            }
            return body;
        }
    }

    private String getRequestBody(WebResourceRequest request) {
        String requestID = getInterceptedRequestID(request);
        return getRequestBodyByID(requestID);
    }

    public static BrowserContext getRequestContext(WebResourceRequest request) {
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

    public static String getInterceptedRequestID(WebResourceRequest request) {
        String requestID = getUrlSegments(request, GD_INTERCEPT_TAG)[1];
        if (requestID.contains("/")) {
            requestID = requestID.substring(0, requestID.lastIndexOf("/"));
        }
        return requestID.trim();
    }

    private HttpRequestBase getHttpRequestBase(WebResourceRequest request, URI uri) {

        HttpRequestBase method;

        String body = null;

        if (isInterceptedRequest(request)) {
            body = getRequestBody(request);
            Log.i(TAG,"request body retrieved: " + body);
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

    public static void setGETRequestHeaders(final URI targetUri, final HttpRequestBase httpMethod,final WebResourceRequest webResourceRequest,
                                             final String originWebViewUrl, BrowserContext browserContext) {

        Log.i(TAG, "setGETRequestHeaders IN ");

        Log.i(TAG, "setGETRequestHeaders " + "origin:" + originWebViewUrl);
        Log.i(TAG, "setGETRequestHeaders " + "request:" + targetUri);

        final Map<String, String> webViewHeaders = webResourceRequest.getRequestHeaders();

        // Basically, we do not need to override or add headers if the request context is unknown.
        // Also check for 'Accept' and 'Origin' headers, they are required for a response to be successful.
        if (webResourceRequest.getRequestHeaders().containsKey("Accept")
                && webResourceRequest.getRequestHeaders().containsKey("Origin")
                && !webResourceRequest.getRequestHeaders().get("Origin").equals("null")
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
                try {
                    originURI = URI.create(originWebViewUrl);
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "setGETRequestHeaders - failed to create originURI from - " + originWebViewUrl);
                }
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
            String value = webViewHeaders.get(headerKey);

            if (headerKey.equals("Referer")) {
                value = webViewHeaders.get(headerKey).split(GD_INTERCEPT_TAG)[0];
            }

            // Content cache settings are disabled. Do not add the header for images to prevent '304 Not Modified' response with no content.
            boolean shouldSkip = headerKey.equals("If-Modified-Since");

            if (!shouldSkip) {
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
        }else if(originURI.getAuthority() != null && originURI.getAuthority().equalsIgnoreCase(targetUri.getAuthority())){
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
            put("Content-Type", "application/x-www-form-urlencoded");

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


    public RequestTask(String clientConnId, WebResourceRequest webReq, String urlIntercepted,
                       String originalWebViewUrl, WebView webView, BrowserContext browserContext) {
        this.clientConnId = clientConnId;
        request = webReq;
        this.targetUrl = urlIntercepted;
        this.originalWebViewUrl = originalWebViewUrl;
        this.webView = webView;
        this.browserContext = browserContext;
    }

    @Override
        public Pair<HttpResponse,HttpContext> doInClientThread(GDHttpClient httpClient, String id) {

        Log.i(TAG, "doInClientThread IN, targetUrl: " + targetUrl + " clientId: " + id + " requestUrl: " + request.getUrl());

        Utils.debugLogHeaders(request.getRequestHeaders());

        HttpResponse response = null;
        HttpContext httpContext = null;
        HttpRequestBase method = null;

        try {

            if (targetUrl.contains("#")) {
                targetUrl = targetUrl.substring(0, targetUrl.lastIndexOf('#'));
            }

            URI targetUri = URI.create(targetUrl);

            method = getHttpRequestBase(request, targetUri);
            long nano;

            Log.i(TAG, "HTTP_EXEC " + httpClient.hashCode() + " >> " + "[" + Process.myTid() + "] <" + TimeUnit.NANOSECONDS.toMillis(nano = System.nanoTime()) + "> " + method.getURI().toString());

            httpContext = createHttpContext(httpClient);
            httpContext.setAttribute("webview.http.method",method.getMethod());
            httpContext.setAttribute("webview.http.isForMainFrame",request.isForMainFrame());
            httpContext.setAttribute("webview.connectionId",clientConnId);

            response = httpClient.execute(method, httpContext);

            Log.i(TAG, "HTTP_EXEC " + httpClient.hashCode() + " << " + "[" + Process.myTid() + "] <" + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - nano) + "> " + method.getURI().toString() +" " + response.getStatusLine());
        } catch (ConnectTimeoutException e) {
            Log.e(TAG, "HTTP_EXEC execute ERROR: ConnectTimeoutException, resend request");
            try {
                response = httpClient.execute(method, httpContext);
            } catch (Exception exception) {
                Log.e(TAG, "HTTP_EXEC execute ERROR: request failed after resend " + exception);
            }
        } catch (Exception e) {
            Log.e(TAG, "HTTP_EXEC execute ERROR: " + e);
            e.printStackTrace();
        }

        // Check if redirect is needed
        if (response != null && response.getStatusLine().getStatusCode() == 200
                && isRedirectionRequested(httpContext, method, response)
                && !browserContext.isInterceptedFromJSRequest()) {
            Log.i(TAG, "doInClientThread, process http redirection");
            return handleRedirect(httpContext, response);
        }

        Log.i(TAG, "doInClientThread OUT, targetUrl: " + targetUrl);

        return Pair.create(response,httpContext);
    }

    public static HttpContext createHttpContext(GDHttpClient httpClient) {
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

    private boolean isRedirectionRequested(HttpContext context, HttpRequestBase method, HttpResponse response) {

        boolean isContentPage = false;
        if (response.containsHeader("Content-Type")) {
            isContentPage = response.getFirstHeader("Content-Type").getValue().contains("text/html");
        }

        boolean isMoved301Redirect = context.getAttribute("webview.redirect.moved.permanently") != null;
        boolean isMoved302Redirect = context.getAttribute("webview.redirect.moved.temprorary") != null;
        boolean hasLocationUrl = context.getAttribute("webview.redirect.url") != null;
        boolean isGetMethod = method.getMethod().equals("GET");

        Log.i(TAG, "isRedirectionRequested, isContentPage " + isContentPage + ", isRedirect " + (isMoved301Redirect || isMoved302Redirect)
                + ", hasLocationUrl " + hasLocationUrl + ", isGetMethod " + isGetMethod);

        return isContentPage && (isMoved301Redirect || isMoved302Redirect) && hasLocationUrl && isGetMethod;
    }

    private Pair<HttpResponse,HttpContext> handleRedirect(HttpContext context, HttpResponse response) {
        Object locationUrl = context.getAttribute("webview.redirect.url");
        String clientId = (String) context.getAttribute("webview.connectionId");

        Log.i(TAG, "handleRedirect, cache request, locationUrl - " + locationUrl + ", clientId " + clientId);

        // Cache response
        Future<Pair<HttpResponse, HttpContext>> futureResponse = CompletableFuture.completedFuture(Pair.create(response, context));
        GDHttpClientProvider.getInstance().cacheResponseData((String) locationUrl, clientId, futureResponse);

        // Form html redirection page
        String redirectPage = "<html><head><meta http-equiv=\"Refresh\" content=\"0; URL=" + locationUrl +
                "\"></head></html>";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(redirectPage.getBytes());

        HttpResponse redirectResponse = new BasicHttpResponse(response.getStatusLine());
        redirectResponse.setHeader(new BasicHeader("Content-Length", String.valueOf(redirectPage.length())));
        redirectResponse.setHeader(new BasicHeader("Content-Type", "text/html; charset=UTF-8"));

        redirectResponse.setEntity(new InputStreamEntity(inputStream, redirectPage.length()));

        // Return new response
        return Pair.create(redirectResponse, context);
    }

}
