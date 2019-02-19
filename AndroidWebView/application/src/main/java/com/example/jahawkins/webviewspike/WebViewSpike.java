/* Copyright (c) 2018 BlackBerry Ltd.
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

package com.example.jahawkins.webviewspike;

import android.app.Application;
import android.util.Log;

import com.good.gd.icc.GDServiceClient;
import com.good.gd.icc.GDServiceClientListener;
import com.good.gd.icc.GDServiceException;

import java.util.List;
import java.util.Map;

public class WebViewSpike extends Application {
    private static final String TAG = WebViewSpike.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        final GDServiceClientListener serviceClientListener = new GDServiceClientListener() {
            @Override
            public void onReceivingAttachments(String s, int i, String s1) {

            }

            @Override
            public void onReceivingAttachmentFile(String s, String s1, long l, String s2) {

            }

            @Override
            public void onReceiveMessage(String application,
                                         Object params,
                                         String[] attachments,
                                         String requestID)
            {
                Log.d(TAG,
                    "serviceClientListener onReceiveMessage '" + application + "' " + params + ".");
            }

            @Override
            public void onMessageSent(String application, String requestID, String[] attachments) {
                Log.d(TAG,
                    "serviceClientListener onMessageSent '" + application + "' '" + requestID +
                    "'.");
            }
        };

        try {
            GDServiceClient.setServiceClientListener(serviceClientListener);
        } catch (GDServiceException e) {
            e.printStackTrace();
        }
    }
}
