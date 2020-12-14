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
package com.good.gd.webview_V2.bbwebview;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.good.gd.apache.http.HttpResponse;
import com.good.gd.apache.http.protocol.HttpContext;
import com.good.gd.webview_V2.bbwebview.tasks.http.GDHttpClientProvider;
import com.good.gd.webview_V2.bbwebview.tasks.http.RequestTask;
import com.good.gd.webview_V2.bbwebview.utils.Utils;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.good.gd.webview_V2.bbwebview.BBWebViewClient.X_REDIRECT_REPONSE_ID;

// delegate class for WebViewClient's and ServiceWorkerClient's shouldInterceptRequest method
class RequestInterceptor {

    private static final String TAG = "GDWebView-" + RequestInterceptor.class.getSimpleName();
    private final List<String> HTTP_VERBS = Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS");

    public RequestInterceptor() {
    }

    public WebResourceResponse invoke(WebResourceRequest request, final WebView vw) {
        String requestUrl = request.getUrl().toString();

        Log.i(TAG,"shouldInterceptRequest IN " + request.getUrl() + " method: " + request.getMethod() + " redirect:" + request.isRedirect()
                + " hasGesture: " + request.hasGesture()  + " isForMainFrame: " + request.isForMainFrame());

        Utils.debugLogHeaders(request.getRequestHeaders());

        if(request.getRequestHeaders().containsKey(X_REDIRECT_REPONSE_ID) ){
            Log.i(TAG, "-shouldInterceptRequest REDIRECT path: " + request.getUrl());
            String clientId = request.getRequestHeaders().get(X_REDIRECT_REPONSE_ID);
            if(!TextUtils.isEmpty(clientId)) {
                WebResourceResponse cachedWebResponse = GDHttpClientProvider.getInstance().fetchCachedWebResponse(clientId);
                if (cachedWebResponse == null) {
                    Log.i(TAG, "cachedWebResponse == null");
                }
                return cachedWebResponse;
            }
        }
        try {

            requestUrl = getOriginalRequestUri(request, RequestTask.GD_INTERCEPT_TAG);
            String requestId = getRequestId(request, RequestTask.GD_INTERCEPT_TAG);

            Log.i(TAG, "-shouldInterceptRequest GD-marker-stripped URL: " + requestUrl);
            Log.i(TAG, "-shouldInterceptRequest URL requestId: " + requestId);

            requestUrl = Utils.encodeUrl(requestUrl);

            Log.i(TAG, "-shouldInterceptRequest URL encoded: " + requestUrl);

            if (HTTP_VERBS.contains(request.getMethod())) {

                String host = request.getUrl().getHost();
                if (host == null) {
                    throw new IllegalArgumentException("null host for " + requestUrl);
                }

                final AtomicReference<String> origUrlRef = getOriginOnWebViewThread(vw);

                String clientId = GDHttpClientProvider.getInstance().obtainPooledClient(request.getUrl().getHost());

                if (clientId == null) {
                    return new WebResourceResponse(null, null, null);
                }

                Future<Pair<HttpResponse, HttpContext>> futureResponse =
                        GDHttpClientProvider.getInstance().execGDHttpClientAsync(clientId,
                                new RequestTask(clientId, request, requestUrl, origUrlRef.get(), vw));

                String resExtention = MimeTypeMap.getFileExtensionFromUrl(requestUrl);
                String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(resExtention);
                Log.i(TAG,"shouldInterceptRequest:getFileExtensionFromUrl" + resExtention + " mimeType guess is " + mimeTypeFromExtension);

                WebResourceResponse webResourceResponse = new BBWebResourceResponse(
                        mimeTypeFromExtension,
                        null,
                        new BBResponseInputStream(futureResponse,clientId, vw ),
                        futureResponse,clientId,vw);

                return webResourceResponse;

            } else {
                Log.w(TAG, "shouldInterceptRequest " + request.getMethod());
            }

        } catch (Exception e) {
            Log.e(TAG, "shouldInterceptRequest: " + request.getMethod() + " " + requestUrl,e);
        }

        Log.w(TAG, "shouldInterceptRequest SKIPPING " + request.getMethod()+" " + requestUrl);

        return new WebResourceResponse(null, null,null);
    }

    private AtomicReference<String> getOriginOnWebViewThread(final WebView vw) throws InterruptedException {
        final AtomicReference<String> origUrlRef = new AtomicReference<>();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                origUrlRef.set(vw.getOriginalUrl());
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(1, TimeUnit.SECONDS);

        return origUrlRef;
    }

    private String getOriginalRequestUri(WebResourceRequest request, String marker) {
        return RequestTask.getUrlSegments(request, marker)[0];
    }

    private String getRequestId(WebResourceRequest request, String marker) {
        if (!request.getUrl().toString().contains(RequestTask.GD_INTERCEPT_TAG))
            return "not found";
        return RequestTask.getUrlSegments(request, marker)[1];
    }

}