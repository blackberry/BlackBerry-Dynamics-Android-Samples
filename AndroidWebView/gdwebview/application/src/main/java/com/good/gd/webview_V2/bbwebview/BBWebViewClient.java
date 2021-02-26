/*
 * Some comments and stub methods Copyright (C) 2008 The Android Open Source Project
 *
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
package com.good.gd.webview_V2.bbwebview;


import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.webkit.ClientCertRequest;
import android.webkit.DownloadListener;
import android.webkit.HttpAuthHandler;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SafeBrowsingResponse;
import android.webkit.ServiceWorkerController;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.good.gd.apache.http.HttpResponse;
import com.good.gd.apache.http.protocol.HttpContext;
import com.good.gd.webview_V2.R;
import com.good.gd.webview_V2.bbwebview.jsInterfaces.ClipboardEventListener;
import com.good.gd.webview_V2.bbwebview.jsInterfaces.DocumentCookieStore;
import com.good.gd.webview_V2.bbwebview.jsInterfaces.RequestBodyProvider;
import com.good.gd.webview_V2.bbwebview.tasks.http.GDHttpClientProvider;
import com.good.gd.webview_V2.bbwebview.tasks.http.RequestTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Future;

public class BBWebViewClient extends WebViewClient {

    private static final String TAG = "GDWebView-" +  BBWebViewClient.class.getSimpleName();
    private static final String TAG_LC = "GDWebView-" +  "BBWebViewClient_LC";//lifeCycle
    public static final String PIXEL_2XL_UA = "Mozilla/5.0 (Linux; Android 10; Pixel 2 XL) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.119 Mobile Safari/537.36";

    private static final DownloadListener DOWNLOAD_LISTENER = new BBDownloadListener();
    public static final String SHOULD_NOT_UPDATE_LAST_LOADED_URL = "should-update-last-loaded-url";

    private static RequestBodyProvider requestBodyProvider;
    private static ClipboardEventListener clipboardEventListener;
    private static DocumentCookieStore documentCookieStore = new DocumentCookieStore();

    private final WebClientObserver webClientObserver = new WebClientObserver();
    private final RequestInterceptor requestInterceptor = new RequestInterceptor();

    public static void init(WebView webView, BBWebViewClient wvClient){

        WebView.setWebContentsDebuggingEnabled(true);

        webView.setWebChromeClient(new BBChromeClient(wvClient.getObserver()));

        // service worker setup
        BBDServiceWorkerClient client = new BBDServiceWorkerClient(webView);
        ServiceWorkerController.getInstance().setServiceWorkerClient(client);
        ServiceWorkerController.getInstance().getServiceWorkerWebSettings().setBlockNetworkLoads(true);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDisplayZoomControls(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDomStorageEnabled(true);

        requestBodyProvider = new RequestBodyProvider(wvClient);

        webView.removeJavascriptInterface("RequestInterceptor");
        webView.addJavascriptInterface(requestBodyProvider, "RequestInterceptor");

        clipboardEventListener = new ClipboardEventListener(webView.getContext());

        webView.removeJavascriptInterface("AndroidClipboardListener");
        webView.addJavascriptInterface(clipboardEventListener,"AndroidClipboardListener");

        webView.removeJavascriptInterface("DocumentCookiesBridge");
        webView.addJavascriptInterface(documentCookieStore, "DocumentCookiesBridge");

        //disable webview networking, all the requests should go via GDHttpClient calls
        webView.getSettings().setBlockNetworkLoads(true);
        webView.getSettings().setUserAgentString(PIXEL_2XL_UA);

        webView.setDownloadListener(DOWNLOAD_LISTENER);

        GDHttpClientProvider.getInstance().setWebViewReference(webView);

    }

    public final void addRequestBody(String requestId, String body, String url, String browserContext) {
        synchronized (RequestTask.REQUESTS_BODIES) {
            Log.i(TAG, "addRequestBody ID: " + requestId + ", BODY:" + body);
            Log.i(TAG, "addRequestBody ID: " + requestId + ", URL:" + url);
            Log.i(TAG, "addRequestBody ID: " + requestId + ", browserContext:" + browserContext);

            // Remove old value
            if (RequestTask.REQUESTS_BODIES.containsKey(requestId)) {
                Log.i(TAG, "addRequestBody ID: remove old value with key " + requestId + ", browserContext:" + browserContext);
                RequestTask.REQUESTS_BODIES.remove(requestId);
            }

            RequestTask.REQUESTS_BODIES.put(requestId, new RequestTask.BrowserContext(body, url, browserContext));
        }
    }

    @Override
    public void onPageCommitVisible( WebView view,  String url) {
        Log.i(TAG_LC, "onPageCommitVisible " + url);
        super.onPageCommitVisible(view, url);

        webClientObserver.notifyPageContentVisible(view, url);
    }

    private void injectJsFromResources(WebView view, String url, int resId) throws IOException {
        Log.i(TAG_LC,"injectJsFromResources >> " + url);

        InputStream is = view.getResources().openRawResource(resId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = reader.readLine();
        StringBuilder script = new StringBuilder(line);
        while ((line = reader.readLine()) != null) {
            script.append(line).append("\n");
        }

        reader.close();
        is.close();

        view.loadUrl("javascript:" + script.toString());

        Log.i(TAG_LC,"injectJsFromResources << " + url);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(final WebView webView, final WebResourceRequest request) {

        Log.i(TAG_LC,"shouldInterceptRequest, url - " + request.getUrl().toString() + ", isRedirect " + request.isRedirect());

        if (request.getUrl().toString().startsWith("data")) {
            return super.shouldInterceptRequest(webView, request);
        }

        if (GDHttpClientProvider.getInstance().hasCachedResponse(request.getUrl().toString())) {
            return getResponseFromCache(request.getUrl().toString());
        }

        // Otherwise intercept the request and load it using GDHttpClient
        return requestInterceptor.invoke(webView, request);
    }

    @Override
    public void onReceivedError(WebView webView,  WebResourceRequest request,  WebResourceError error) {

        Log.i(TAG_LC, "onReceivedError " + request.getUrl() + " code: " + error.getErrorCode() + " desc: " + error.getDescription());

        // Clear WebView content
        webView.loadUrl("about:blank");
    }

    private WebResourceResponse getResponseFromCache(String url) {
        Log.e(TAG, "getResponseFromCache, url - " + url);

        Future<Pair<HttpResponse, HttpContext>> futureResponse = GDHttpClientProvider.getInstance().fetchCachedWebResponse(url);

        String mimeTypeFromExtension = RequestInterceptor.getMimeType(url);
        String connectionId = null;

        try {
            connectionId = (String) futureResponse.get().second.getAttribute("webview.connectionId");
        } catch (Exception e) {
            Log.e(TAG, "getResponseFromCache, failed to get connectionId");
        }

        return new BBWebResourceResponse(mimeTypeFromExtension, null,
                new BBResponseInputStream(futureResponse, connectionId), futureResponse, connectionId);
    }

    @Override
    public void onReceivedHttpError( WebView view,  WebResourceRequest request,  WebResourceResponse errorResponse) {
        Log.i(TAG_LC, "onReceivedHttpError " + request.getUrl() +" => " + errorResponse);
        super.onReceivedHttpError(view, request, errorResponse);
    }

    /**
     * As the host application if the browser should resend data as the
     * requested page was a objectCondition of a POST. The default is to not resend the
     * data.
     *
     * @param view       The WebView that is initiating the callback.
     * @param dontResend The message to send if the browser should not resend
     * @param resend     The message to send if the browser should resend data
     */
    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        Log.i(TAG_LC,"onFormResubmission");
        super.onFormResubmission(view, dontResend, resend);
    }

    /**
     * Notify the host application to update its visited links database.
     *
     * @param view     The WebView that is initiating the callback.
     * @param url      The url being visited.
     * @param isReload {@code true} if this url is being reloaded.
     */
    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        Log.i(TAG_LC,"doUpdateVisitedHistory " + url + " isReload: " + isReload);
        super.doUpdateVisitedHistory(view, url, isReload);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        Log.i(TAG_LC,"onReceivedSslError: " + error.toString() + ", priary error " + error.getPrimaryError());
        super.onReceivedSslError(view, handler, error);
    }

    @Override
    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        Log.i(TAG_LC,"onReceivedClientCertRequest");
        super.onReceivedClientCertRequest(view, request);
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        Log.i(TAG_LC,"onReceivedHttpAuthRequest");
        super.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    /**
     * Notify the host application that a key was not handled by the WebView.
     * Except system keys, WebView always consumes the keys in the normal flow
     * or if {@link #shouldOverrideKeyEvent} returns {@code true}. This is called asynchronously
     * from where the key is dispatched. It gives the host application a chance
     * to handle the unhandled key events.
     *
     * @param view  The WebView that is initiating the callback.
     * @param event The key event.
     */
    @Override
    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
        super.onUnhandledKeyEvent(view, event);
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        Log.i(TAG_LC,"onScaleChanged");
        super.onScaleChanged(view, oldScale, newScale);
    }

    /**
     * Notify the host application that a request to automatically log in the
     * user has been processed.
     *
     * @param view    The WebView requesting the login.
     * @param realm   The account realm used to look up accounts.
     * @param account An optional account. If not {@code null}, the account should be
     *                checked against accounts on the device. If it is a valid
     *                account, it should be used to log in the user.
     * @param args    Authenticator specific arguments used to log in the user.
     */
    @Override
    public void onReceivedLoginRequest(WebView view, String realm,  String account, String args) {
        Log.i(TAG_LC,"onReceivedLoginRequest");
        super.onReceivedLoginRequest(view, realm, account, args);
    }

    @Override
    public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
        Log.i(TAG_LC,"onRenderProcessGone");
        return super.onRenderProcessGone(view, detail);
    }

    @Override
    public boolean shouldOverrideUrlLoading( WebView view,  WebResourceRequest request) {
        Log.i(TAG_LC,"shouldOverrideUrlLoading, url - " + request.getUrl());

        // Continue loading
        return false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Log.i(TAG_LC,"onPageStarted >> " + url);

        try {
            injectJsFromResources(view, url, R.raw.request_interceptor);
            injectJsFromResources(view, url, R.raw.clipboard_interceptor);
            injectJsFromResources(view, url, R.raw.document_cookie_storage);
        } catch (IOException e) {
            Log.i(TAG_LC,"onPageStarted exception: " + e);
        }

        webClientObserver.notifyPageStarted(view, url);

        Log.i(TAG_LC,"onPageStarted << " + url);
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Log.i(TAG_LC,"onPageFinished " + url + " IN");

        url = url.replaceAll("#.*", "");
        super.onPageFinished(view, url);

        webClientObserver.notifyPageFinished(view, url);

        Log.i(TAG_LC,"onPageFinished " + url + " OUT");
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        Log.i(TAG_LC,"onLoadResource " + url);
        super.onLoadResource(view, url);
    }

    @Override
    public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponse callback) {
        Log.i(TAG_LC,"onSafeBrowsingHit");
        super.onSafeBrowsingHit(view, request, threatType, callback);
    }

    public WebClientObserver getObserver() { return webClientObserver; }
}
