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

import android.util.Log;

import com.good.gd.apache.http.client.params.ClientPNames;
import com.good.gd.apache.http.client.params.CookiePolicy;
import com.good.gd.apache.http.client.params.HttpClientParams;
import com.good.gd.apache.http.params.BasicHttpParams;
import com.good.gd.apache.http.params.HttpConnectionParams;
import com.good.gd.apache.http.params.HttpParams;
import com.good.gd.net.GDHttpClient;
import com.good.gd.pki.Certificate;
import com.good.gd.pki.CertificateHandler;
import com.good.gd.pki.CertificateListener;

public class InitHttpClient {

    private static final String TAG = "GDWebView-" +  InitHttpClient.class.getSimpleName();

    private static final int TIMEOUT = 1000 * 90; // miliseconds

    private static CertificateListener GLOBAL_CERTIFICATE_STORE_LISTENER = new CertificateListener() {
        @Override
        public void onCertificatedAdded(Certificate certificate) {
            Log.i(TAG, "CertListener ADDED CN:" + certificate.getSubjectName() + " | issuer:" + certificate.getIssuer());
        }

        @Override
        public void onCertificateRemoved(Certificate certificate) {
            Log.i(TAG, "CertListener REMOVED CN:" + certificate.getSubjectName() + " | issuer:" + certificate.getIssuer());
        }
    };

    public static GDHttpClient createGDHttpClient() {

        Log.i(TAG, "GDHttpClient >>");

        final GDHttpClient httpClient = new GDHttpClient();

        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, TIMEOUT);
        params.setBooleanParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS,true);

        HttpClientParams.setCookiePolicy(params, CookiePolicy.BROWSER_COMPATIBILITY);

        httpClient.setParams(params);
        httpClient.setRedirectHandler(new BBRedirectHandler());

        CertificateHandler.getInstance().addCertificateListener(GLOBAL_CERTIFICATE_STORE_LISTENER);

        Log.i(TAG, "GDHttpClient <<");
        return httpClient;
    }
}
