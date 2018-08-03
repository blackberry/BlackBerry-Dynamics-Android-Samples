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

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.good.gd.GDAndroid;
import com.good.gd.GDAppServer;
import com.good.gd.GDStateAction;
import com.good.gd.GDStateListener;
import com.good.gd.icc.GDServiceClient;
import com.good.gd.icc.GDServiceClientListener;
import com.good.gd.icc.GDServiceException;

import java.util.List;
import java.util.Map;

public class WebViewSpike extends Application {
    private static final String TAG = WebViewSpike.class.getSimpleName();

    public Uri userInterface = null;
    boolean isAuthorized = false;

    MainActivity currentActivity = null;
    StreamWebViewClient webViewClient = null;
    WebChromeClient webChromeClient = null;

    private synchronized void finishWebView() {
        MainActivity activity = this.currentActivity;
        if (activity == null || !this.isAuthorized) {
            return;
        }
        final WebView webView = activity.getWebView();

        final String url = this.userInterface.toString();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WebViewSpike.this.webViewClient.register(webView);
                webView.setWebChromeClient(WebViewSpike.this.webChromeClient);
                Settings.getInstance().applySettings(null);
                Log.d(TAG, "Loading webview on thread:" + Thread.currentThread().getName() + ".");
                webView.loadUrl(url);
            }
        });
    }

    BroadcastReceiver broadcastReceiver = null;
    class StreamBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() == GDStateAction.GD_STATE_AUTHORIZED_ACTION) {
                WebViewSpike.this.isAuthorized = true;
                WebViewSpike.this.setLinks();
                WebViewSpike.this.finishWebView();
                return;
            }

            if (intent.getAction() == GDStateAction.GD_STATE_UPDATE_CONFIG_ACTION) {
                WebViewSpike.this.setLinks();
                final MainActivity activity = WebViewSpike.this.currentActivity;
                if (activity == null) {
                    return;
                }
                // Initiate reload, but only if the WebView is showing the UI.
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final WebView webView = activity.getWebView();
                        if (webView.getUrl().equals(WebViewSpike.this.userInterface.toString())) {
                            webView.reload();
                        }
                    }
                });
            }

        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.webViewClient = new StreamWebViewClient();
        this.webViewClient.setContext(this);

        this.webChromeClient = new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Log.d(TAG, "onJsAlert(" + message + ") thread:" +
                    Thread.currentThread().getName() + ".");
                result.cancel();
                return true;
            }
        };

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (activity.getClass() == MainActivity.class) {
                    WebViewSpike.this.currentActivity = (MainActivity) activity;
                    Settings.getInstance().activity = (MainActivity) activity;
                    WebViewSpike.this.finishWebView();

                    // https://stackoverflow.com/questions/37856407/can-activity-getintent-ever-return-null
                    final Intent intent = activity.getIntent();
                    if (intent != null) {
                        final Uri intentData = intent.getData();
                        if (intentData != null) {
                            Log.d(TAG,
                                "Activity started with data '" + intentData.toString() + "'.");
                        }
                    }
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {}

            @Override
            public void onActivityPaused(Activity activity) {}

            @Override
            public void onActivityStopped(Activity activity) {}

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {}

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (activity == WebViewSpike.this.currentActivity) {
                    WebViewSpike.this.currentActivity = null;
                    Settings.getInstance().activity = null;
                }
            }
        });

        Settings.getInstance().mergeDefaultSettings();
        this.userInterface = Uri.parse("http://localhost:1/");

        this.broadcastReceiver = new StreamBroadcastReceiver();

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

        final GDAndroid gdAndroid = GDAndroid.getInstance();
        gdAndroid.applicationInit(this);
        final IntentFilter filter = new IntentFilter(GDStateAction.GD_STATE_AUTHORIZED_ACTION);
        filter.addAction(GDStateAction.GD_STATE_UPDATE_CONFIG_ACTION);
        gdAndroid.registerReceiver(broadcastReceiver, filter);

        // Dummy GDStateListener.
        gdAndroid.setGDStateListener(new GDStateListener() {
            @Override
            public void onAuthorized() {}
            @Override
            public void onLocked() {}
            @Override
            public void onWiped() {}
            @Override
            public void onUpdateConfig(Map<String, Object> map) {}
            @Override
            public void onUpdatePolicy(Map<String, Object> map) {}
            @Override
            public void onUpdateServices() {}
            @Override
            public void onUpdateEntitlements() {}
        });
    }

    private void setLinks() {
        Settings settings = Settings.getInstance();
        settings.resetLinks();

        Map<String, Object> configMap = GDAndroid.getInstance().getApplicationConfig();

        List<GDAppServer> servers = (List<GDAppServer>) configMap.get(
            GDAndroid.GDAppConfigKeyServers);
        for (GDAppServer server : servers) {
            Uri.Builder link = new Uri.Builder();
            link.scheme("http");
            link.encodedAuthority(server.server + ":" + server.port);
            settings.addLinks(link.build().toString());
        }

        String config = (String) configMap.get(GDAndroid.GDAppConfigKeyConfig);
        settings.addLinks(config);
    }
}
