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

import android.util.Log;
import android.util.Pair;
import android.webkit.WebResourceResponse;

import com.good.gd.apache.http.Header;
import com.good.gd.apache.http.HttpResponse;
import com.good.gd.apache.http.protocol.HttpContext;
import com.good.gd.webview_V2.bbwebview.tasks.http.HttpResponseParser;
import com.good.gd.webview_V2.bbwebview.utils.Utils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class BBWebResourceResponse extends WebResourceResponse {

    private final String TAG = "GDWebView-" +  BBWebResourceResponse.class.getSimpleName() + "_" + hashCode();

    private Future<Pair<HttpResponse, HttpContext>> futureResp;
    private HttpResponse response;
    private String clientId;

    private final AtomicBoolean isResponseRetrieved;

    public BBWebResourceResponse(String mimeType, String encoding, InputStream inputStream, Future<Pair<HttpResponse, HttpContext>> futureResp, String clientId) {
        super(mimeType, encoding, inputStream);

        this.futureResp = futureResp;
        this.clientId = clientId;

        isResponseRetrieved = new AtomicBoolean(false);
    }

    @Override
    public String getReasonPhrase() {

        if (!isResponseRetrieved.get()) {
            waitForRequestToFinish();
        }

        Log.i(TAG,"getReasonPhrase: " + super.getReasonPhrase());

        return super.getReasonPhrase();
    }

    @Override
    public int getStatusCode() {

        if (!isResponseRetrieved.get()) {
            waitForRequestToFinish();
        }

        Log.i(TAG,"getStatusCode: " + super.getStatusCode());

        return super.getStatusCode();
    }

    @Override
    public String getEncoding() {
        Log.i(TAG,"getEncoding: >>");

        if (!isResponseRetrieved.get()) {
            waitForRequestToFinish();
        }

        if (super.getEncoding() == null) {
            retrieveEncoding();
        }

        String encoding = super.getEncoding();

        Log.i(TAG,"getEncoding: retVal = " + encoding);
        return encoding;
    }

    @Override
    public String getMimeType() {
        Log.i(TAG,"getMimeType >>");

        if (!isResponseRetrieved.get()) {
            waitForRequestToFinish();
        }

        if (super.getMimeType() == null) {
            retrieveMimeType();
        }

        String mimeType = super.getMimeType();

        Log.i(TAG,"getMimeType: retVal = " + mimeType);

        return mimeType;
    }

    // Entry point called from the chromium native
    @Override
    public Map<String, String> getResponseHeaders()
    {
        Log.i(TAG,"getResponseHeaders >>");

        HashMap<String, String> respHeaders = new LinkedHashMap<>();

        try {

            if (!isResponseRetrieved.get()) {
                waitForRequestToFinish();
            }

            if (response == null) {
                Log.e(TAG,"getResponseHeaders response = null");
                return respHeaders;
            }

            if (getEncoding() == null) {
                retrieveEncoding();
            }

            if (getMimeType() == null) {
                retrieveMimeType();
            }

            for (Header header : response.getAllHeaders()) {
                respHeaders.put(header.getName(), header.getValue());
            }

            Utils.debugLogHeaders(respHeaders);

        } catch (Exception e) {
            Log.i(TAG,"getResponseHeaders, exception " + e);
            e.printStackTrace();
        }

        Log.i(TAG,"getResponseHeaders <<");

        return respHeaders;
    }


    @Override
    public InputStream getData() {

        Log.i(TAG,"getData >>");

        InputStream inputStream = super.getData();

        Log.i(TAG,"getData " + inputStream);

        return super.getData();
    }

    private void waitForRequestToFinish() {
        Log.i(TAG,"waitForRequestToFinish, wait for http response, clientId " + clientId);
        try {
            response = futureResp.get().first;

            if (response != null && response.getEntity() != null) {
                Log.i(TAG, "waitForRequestToFinish, content " + response.getEntity().getContentLength());

                setStatusCodeAndReasonPhrase(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());

            } else {
                Log.e(TAG, "waitForRequestToFinish, no content ");
            }

        } catch (Exception e) {
            Log.e(TAG,"waitForRequestToFinish, failed to get response ", e);
        }

        isResponseRetrieved.set(true);

        Log.i(TAG,"waitForRequestToFinish, retrieved response");
    }

    private void retrieveMimeType() {
        if (response != null) {
            for (final Header header : response.getAllHeaders()) {
                if ("content-type".equalsIgnoreCase(header.getName())) {

                    String mimeType = header.getValue().replaceAll(";.*", "");
                    Log.i(TAG, "retrieveMimeType mimeType: " + mimeType);

                    setMimeType(mimeType);
                }
            }
        }
    }

    private void retrieveEncoding() {
        if (response != null && response.getEntity() != null) {
            String encoding = HttpResponseParser.parseContentEncoding(response.getAllHeaders(), response.getEntity());
            Log.i(TAG,"retrieveEncoding: " + encoding);
            setEncoding(encoding);
        }
    }

}
