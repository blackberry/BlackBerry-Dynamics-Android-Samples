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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;

import com.good.gd.widget.GDWebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WebView extends GDWebView {
    private static final String TAG = WebView.class.getSimpleName();

    private static final String USER_INTERFACE = "assets:/UserInterface/index.html";

    public Boolean reloadIfUserInterface() {
        if (!this.getUrl().equals(USER_INTERFACE)) {
            return false;
        }
        ((Activity)this.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WebView.this.reload();
            }
        });
        return true;
    }

    private class JavaScriptBridge {
        @JavascriptInterface
        public String get() {
            return Settings.getInstance().toString();
        }

        @JavascriptInterface
        public String merge(String toMergeJSON) {
            try {
                return Settings.getInstance().mergeSettings(toMergeJSON, WebView.this);
            } catch (JSONException exception) {
                exception.printStackTrace();
                Map<String, String> map = new HashMap<>();
                map.put("error", exception.toString());
                return JSONObject.wrap(map).toString();
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void  defaultSettings(Context context) {
        WebSettings settings = this.getSettings();
        Log.d(TAG, "Base settings" +
            " BlockNetworkImage:" + settings.getBlockNetworkImage() +
            " LoadsImagesAutomatically:" + settings.getLoadsImagesAutomatically() +
            " CacheMode:" + settings.getCacheMode() +
            " DomStorageEnabled:" + settings.getDomStorageEnabled());

        settings.setBuiltInZoomControls(true);
        //
        // Without the following, the WebView won't request .js files. Also, the index.html UI in
        // the assets relies on executing the index.js code.
        settings.setJavaScriptEnabled(true);

        Settings.getInstance().applySettings(null, this);
        Lifecycle.getInstance().initialise(context);

        // The bridge to Settings is added here, as an inner class, so that it can get a reference
        // to the WebView object.
        this.addJavascriptInterface(new JavaScriptBridge(), "bridgeSettings");

        // register() sets some other JS bridges ... for now.
        new WebViewClient().register(this);

        // Minimal WebChromeClient to log alert() calls from JavaScript.
        this.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(android.webkit.WebView view,
                                     String url,
                                     String message,
                                     JsResult result
            ) {
                Log.d(TAG, "onJsAlert(" + message + ") thread:" +
                    Thread.currentThread().getName() + ".");
                result.cancel();
                return true;
            }
        });

        this.loadUrl(USER_INTERFACE);
    }

    public WebView(Context context) {
        super(context);
        this.defaultSettings(context);
    }

    public WebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.defaultSettings(context);
    }

    public WebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.defaultSettings(context);
    }

    public WebView(Context context, AttributeSet attributeSet, int i, int i1) {
        super(context, attributeSet, i, i1);
        this.defaultSettings(context);
    }

    public WebView(Context context, AttributeSet attributeSet, int i, boolean b) {
        super(context, attributeSet, i, b);
        this.defaultSettings(context);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK && this.canGoBack()) {
            this.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, keyEvent);
    }
}
