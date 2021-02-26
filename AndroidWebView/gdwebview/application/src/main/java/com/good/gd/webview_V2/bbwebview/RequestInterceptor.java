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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

// delegate class for WebViewClient's and ServiceWorkerClient's shouldInterceptRequest method
public class RequestInterceptor {

    private static final String TAG = "GDWebView-" + RequestInterceptor.class.getSimpleName();
    private final List<String> HTTP_VERBS = Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS");

    public WebResourceResponse invoke(final WebView webView, final WebResourceRequest request) {

        Log.i(TAG,"shouldInterceptRequest IN " + request.getUrl() + " method: " + request.getMethod() + " redirect:" + request.isRedirect()
                + " hasGesture: " + request.hasGesture()  + " isForMainFrame: " + request.isForMainFrame());

        Utils.debugLogHeaders(request.getRequestHeaders());

        try {

            RequestTask.BrowserContext browserContext = RequestTask.BrowserContext.NULL;

            // Retrieve information about the request intercepted by js
            if (request.getUrl().toString().contains(RequestTask.GD_INTERCEPT_TAG)) {
                browserContext = RequestTask.getRequestContext(request);
            }

            String requestUrl = getOriginalRequestUri(request, RequestTask.GD_INTERCEPT_TAG);
            String requestId = getRequestId(request, RequestTask.GD_INTERCEPT_TAG);

            Log.i(TAG, "-shouldInterceptRequest GD-marker-stripped URL: " + requestUrl);
            Log.i(TAG, "-shouldInterceptRequest URL requestId: " + requestId);

            if (HTTP_VERBS.contains(request.getMethod())) {

                final AtomicReference<String> origUrlRef = getOriginOnWebViewThread(webView);

                String clientId = GDHttpClientProvider.getInstance().obtainPooledClient(request.getUrl().getHost());

                if (clientId == null) {
                    Log.e(TAG, "-shouldInterceptRequest clientId == null ");
                    return new WebResourceResponse(null, null, null);
                }

                Future<Pair<HttpResponse, HttpContext>> futureResponse =
                        GDHttpClientProvider.getInstance().execGDHttpClientAsync(clientId,
                                new RequestTask(clientId, request, requestUrl, origUrlRef.get(), webView, browserContext));

                String mimeTypeFromExtension = getMimeType(requestUrl);

                return new BBWebResourceResponse(mimeTypeFromExtension, null,
                        new BBResponseInputStream(futureResponse, clientId), futureResponse, clientId);

            } else {
                Log.e(TAG, "-shouldInterceptRequest no method: " + request.getMethod());
            }

        } catch (Exception e) {
            Log.e(TAG, "-shouldInterceptRequest: " + request.getMethod() + " " + request.getUrl() + " " + e);
        }

        Log.e(TAG, "-shouldInterceptRequest return empty response ");

        return new WebResourceResponse(null, null,null);
    }

    public static AtomicReference<String> getOriginOnWebViewThread(final WebView vw) throws InterruptedException {
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

    public static String getMimeType(String url) {
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(url);
        String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);

        Log.i(TAG,"getMimeType, file extension: " + fileExtension + " mimeType: " + mimeTypeFromExtension);

        return mimeTypeFromExtension;
    }

}