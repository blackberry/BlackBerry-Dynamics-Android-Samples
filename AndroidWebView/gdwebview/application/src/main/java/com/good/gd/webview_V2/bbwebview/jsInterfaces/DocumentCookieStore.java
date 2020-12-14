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
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;

import com.good.gd.apache.http.cookie.Cookie;
import com.good.gd.apache.http.cookie.CookieOrigin;
import com.good.gd.apache.http.impl.cookie.RFC2965Spec;
import com.good.gd.apache.http.message.BasicHeader;
import com.good.gd.net.GDHttpClient;

import java.util.List;

public class DocumentCookieStore {

    private static final String TAG = "GDWebView-" +  DocumentCookieStore.class.getSimpleName();

    private static class InstanceProvider {
        private static GDHttpClient httpClient = new GDHttpClient();
        private static RFC2965Spec rfc2965Spec = new RFC2965Spec();
    }

    @JavascriptInterface
    public String getDocumentCookie(String host, String path) {
        Log.d(TAG, String.format("getDocumentCookie(%s,%s)", host, path));
        return CookieManager.getInstance().getCookie("https://" + host + path);
    }

    @JavascriptInterface
    public void setDocumentCookie(String cookie, String host, String path) {
        Log.d(TAG, String.format("setDocumentCookie(%s,%s,%s)", cookie, host, path));

        cookie = cookie.replaceAll("expires=[A-Za-z0-9,\\s:-]+;","");

        try {

            List<Cookie> cookies = InstanceProvider.rfc2965Spec.parse(new BasicHeader("Cookie", cookie), new CookieOrigin(host, 0, "/", false));

            for (Cookie cookee : cookies) {
                InstanceProvider.httpClient.getCookieStore().addCookie(cookee);
                Log.d(TAG, String.format("setDocumentCookie gd(%s)", cookee));
            }

        } catch (Exception e) {
            Log.e(TAG, String.format("setDocumentCookie(%s,%s,%s)", cookie, host, path), e);
        }

        CookieManager.getInstance().setCookie("https://" + host + path,cookie);
        Log.d(TAG, String.format("setDocumentCookie(%s,%s,%s)", cookie, host, path));
    }

}
