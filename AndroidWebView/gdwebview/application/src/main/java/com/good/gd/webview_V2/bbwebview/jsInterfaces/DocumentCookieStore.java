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

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.good.gd.apache.http.Header;
import com.good.gd.apache.http.client.CookieStore;
import com.good.gd.apache.http.client.params.HttpClientParams;
import com.good.gd.apache.http.cookie.Cookie;
import com.good.gd.apache.http.cookie.CookieOrigin;
import com.good.gd.apache.http.cookie.CookieSpec;
import com.good.gd.apache.http.message.BasicHeader;
import com.good.gd.net.GDHttpClient;
import com.good.gd.webview_V2.bbwebview.tasks.http.InitHttpClient;

import java.util.ArrayList;
import java.util.List;

public class DocumentCookieStore {

    private static final String TAG = "GDWebView-" +  DocumentCookieStore.class.getSimpleName();

    private final CookieSpec cookieSpec;
    private final CookieStore cookieStore;

    public DocumentCookieStore() {
        GDHttpClient httpClient = InitHttpClient.createGDHttpClient();
        cookieStore = httpClient.getCookieStore();

        String cookiePolicy = HttpClientParams.getCookiePolicy(httpClient.getParams());
        cookieSpec = httpClient.getCookieSpecs().getCookieSpec(cookiePolicy);
    }

    @JavascriptInterface
    public String getDocumentCookie(String host, String path) {
        Log.d(TAG, "getDocumentCookie, host = " + host + ", path = " + path);

        CookieOrigin cookieOrigin = new CookieOrigin(host, 0, path, false);

        List<Cookie> cookies = cookieStore.getCookies();

        List<Cookie> matchedCookies = new ArrayList<Cookie>();
        for (Cookie cookie : cookies) {
            if (cookieSpec.match(cookie, cookieOrigin)) {
                matchedCookies.add(cookie);
            }
        }

        String cookie = "";

        if (!matchedCookies.isEmpty()) {
            List<Header> headers = cookieSpec.formatCookies(matchedCookies);
            if (!headers.isEmpty()) {
                cookie = headers.get(0).getValue();
                Log.d(TAG, "getDocumentCookie, found cookie in the store, size = " + matchedCookies.size() + ", cookie = " + cookie);
            }
        } else {
            Log.d(TAG, "getDocumentCookie, not found cookie in the store");
        }

        return cookie;
    }

    @JavascriptInterface
    public void setDocumentCookie(String cookie, String host, String path) {
        Log.d(TAG, String.format("setDocumentCookie(%s, %s, %s)", cookie, host, path));

        try {

            List<Cookie> cookies = cookieSpec.parse(new BasicHeader("Cookie", cookie), new CookieOrigin(host, 0, path, false));

            for (Cookie cookee : cookies) {
                cookieStore.addCookie(cookee);
                Log.d(TAG, "setDocumentCookie, saved cookie to secure store - " + cookee);
            }

        } catch (Exception e) {
            Log.e(TAG, "setDocumentCookie, exception", e);
            e.printStackTrace();
        }

    }

}
