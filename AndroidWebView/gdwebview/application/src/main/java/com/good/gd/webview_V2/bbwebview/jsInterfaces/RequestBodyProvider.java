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
package com.good.gd.webview_V2.bbwebview.jsInterfaces;

import android.webkit.JavascriptInterface;

import com.good.gd.webview_V2.bbwebview.BBWebViewClient;

public class RequestBodyProvider {

    private BBWebViewClient webViewClient;

    public RequestBodyProvider(BBWebViewClient webViewClient) {
        this.webViewClient = webViewClient;
    }

    @JavascriptInterface
    public void addRequestBody(String requestId, String body, String url, String browserContext) {
        webViewClient.addRequestBody(requestId, body, url, browserContext);
    }

    public void setWebViewClient(BBWebViewClient webViewClient) {
        this.webViewClient = webViewClient;
    }
}
