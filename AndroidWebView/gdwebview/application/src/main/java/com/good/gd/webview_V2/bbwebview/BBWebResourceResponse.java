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

import android.os.Process;
import android.util.Log;
import android.util.Pair;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.good.gd.apache.http.Header;
import com.good.gd.apache.http.HttpResponse;
import com.good.gd.apache.http.protocol.HttpContext;
import com.good.gd.webview_V2.bbwebview.tasks.http.GDHttpClientProvider;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class BBWebResourceResponse extends WebResourceResponse {
    private final String TAG = "GDWebView-" +  BBWebResourceResponse.class.getSimpleName() + "_" + hashCode();
    private final String clientId;
    private Future<Pair<HttpResponse, HttpContext>> futureResp;
    private BBWebView webView;
    private WebResourceResponse redirectingResponse;

    public BBWebResourceResponse(String mimeType, String encoding, InputStream data, Future<Pair<HttpResponse,HttpContext>> futureResp, String clientId, WebView webView) {

        super(mimeType, encoding, data);
        this.clientId = clientId;
        Log.i(TAG,String.format("BBWebResourceResponse mimeType=%s, encoding=%s",mimeType,encoding));
        this.futureResp = futureResp;

        this.webView = (BBWebView) webView;

    }

    public BBWebResourceResponse(String mimeType, String encoding, int statusCode,  String reasonPhrase, Map<String, String> responseHeaders, InputStream data) {
        super(mimeType, encoding, statusCode, reasonPhrase, responseHeaders, data);
        this.clientId = null;
    }

    @Override
    public String getEncoding() {
        Log.i(TAG,"getEncoding [" + Process.myTid() + "]");
        String encoding = redirectingResponse == null?super.getEncoding():redirectingResponse.getEncoding();
        Log.i(TAG,"getEncoding [" + Process.myTid() + "] retVal = " + encoding);
        return encoding;
    }

    @Override
    public String getMimeType() {
        Log.i(TAG,"getMimeType [" + Process.myTid() + "]");
        String mimeType = redirectingResponse == null?super.getMimeType():redirectingResponse.getMimeType();
        Log.i(TAG,"getMimeType [" + Process.myTid() + "] retVal = " + mimeType);

        return mimeType;
    }

    @Override
    public int getStatusCode() {
        if(redirectingResponse != null){
            return redirectingResponse.getStatusCode();
        }

        return super.getStatusCode();
    }

    // Entry point called from the chromium native
    @Override
    public Map<String, String> getResponseHeaders()
    {
        Log.i(TAG,"getResponseHeaders [" + Process.myTid() + "] IN");

        redirectingResponse = null;

        HttpResponse response;
        HashMap<String, String> respHeaders = new LinkedHashMap<>();
        try {
            Log.w(TAG,"getResponseHeaders Blocked [" + Process.myTid() + "]" );
            response = futureResp.get().first;
            Log.w(TAG,"getResponseHeaders retrieved response [" + Process.myTid() + "]" );

            if(response == null) {
                Log.w(TAG,"getResponseHeaders RESPONSE NULL [" + Process.myTid() + "]" );
                return respHeaders;
            }

            HttpContext httpContext = futureResp.get().second;

            final Object locationURL = httpContext.getAttribute("webview.redirect.url");
            if(locationURL instanceof URI && Boolean.TRUE.equals(httpContext.getAttribute("webview.http.isForMainFrame"))){

                //there was a redirect to another url
                //we return fake response to the webview for the requested url,
                //schedule new loadUrl with the location url and
                //cache the actual response which is then immediately returned in the shouldInterceptRequest method
                return createTemporaryRedirectResponse(httpContext, locationURL);
            }


            for (final Header header : response.getAllHeaders()) {
                respHeaders.put(header.getName(), header.getValue());
                if("content-type".equalsIgnoreCase(header.getName())){

                    String mimeType = header.getValue().replaceAll(";.*", "");
                    Log.i(TAG,"getResponseHeaders [" + Process.myTid() + "] set mimeType: " + mimeType);

                    setMimeType(mimeType);
                }
            }

            Log.i(TAG,"getResponseHeaders [" + Process.myTid() + "] map: " + respHeaders);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG,"getResponseHeaders [" + Process.myTid() + "] OUT");

        return respHeaders;
    }


    private Map<String, String> createTemporaryRedirectResponse(final HttpContext httpContext, final Object locationURL) throws ExecutionException, InterruptedException {
        Log.i(TAG,"getResponseHeaders: navigate to " + locationURL);

        //reset location uri for this webResponse
        httpContext.removeAttribute("webview.redirect.url");

        final String connId = (String) httpContext.getAttribute("webview.connectionId");
        GDHttpClientProvider.getInstance().cacheResponseData(connId,new BBWebResourceResponse("text/html","utf-8",
                new BBResponseInputStream(futureResp, this.clientId,webView),futureResp,this.clientId, webView));

        webView.post(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG,">> redirect: setOnPageFinishedAction for " + webView.getOriginalUrl());

                final BBWebViewClient webViewClient = (BBWebViewClient) webView.getWebViewClient();
                webViewClient.getObserver().addOnPageFinishedListener(new WebClientObserver.OnPageFinished() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        Log.i(TAG,String.format(">> redirect: webview(%s) new url(%s)", url, locationURL));

                        HashMap<String, String> additionalHttpHeaders = new HashMap<>();
                        additionalHttpHeaders.put(BBWebViewClient.X_REDIRECT_REPONSE_ID, connId);

                        // Unregister to do not receive a notification about redirect url loading
                        ((BBWebViewClient) webView.getWebViewClient()).getObserver().removeLoadUrlListener(GDHttpClientProvider.getInstance());

                        webView.loadUrl(locationURL.toString(), additionalHttpHeaders);

                        // Register to get a notification about next url loading
                        ((BBWebViewClient) webView.getWebViewClient()).getObserver().addLoadUrlListener(GDHttpClientProvider.getInstance());

                        // Unregister listener
                        webViewClient.getObserver().removeOnPageFinishedListener(this);
                        Log.i(TAG, "<< redirect: loaded location url into webview " + locationURL);
                    }
                });

                Log.i(TAG,"<> redirect: setOnPageFinishedAction for " + webView.getOriginalUrl());
                Log.i(TAG,"<< redirect: setOnPageFinishedAction for " + locationURL);
            }
        });

        final String responseBody = "<html>" +
                "<body>" +
                "<h3>Redirecting to <span style=\"color:blue\">" + locationURL.toString() + "</span></h3>" +
                "</body>" +
                "</html>";

        HashMap<String, String> basicResponseHeaders = new HashMap<String, String>() {{
            put("Content-Type", "text/html; charset=utf-8");
            put("Content-Length", responseBody.getBytes().length + "");
            put("Cache-Control", "no-store");

        }};

        redirectingResponse = buildCustomResponse(responseBody,basicResponseHeaders);

        setData(redirectingResponse.getData());
        setMimeType("text/html");
        setEncoding("UTF-8");

        setStatusCodeAndReasonPhrase(200,"OK");

        return basicResponseHeaders;
    }

    @Override
    public InputStream getData() {
        Log.i(TAG,"getData [" + Process.myTid() + "] IN");
        if(redirectingResponse != null){
            return redirectingResponse.getData();
        }
        return super.getData();
    }

    public static WebResourceResponse buildCustomResponse(String message, Map<String, String> headers) {
        try {
            return new WebResourceResponse(
                    "text/html",
                    "UTF-8", 200,
                    "OK", headers,
                    new ByteArrayInputStream(message.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
