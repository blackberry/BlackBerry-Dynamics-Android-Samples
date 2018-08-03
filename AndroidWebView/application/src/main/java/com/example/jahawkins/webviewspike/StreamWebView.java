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
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebSettings;

import com.good.gd.widget.GDWebView;

public class StreamWebView extends GDWebView {
    private static final String TAG = StreamWebView.class.getSimpleName();

    @SuppressLint("SetJavaScriptEnabled")
    private void  defaultSettings() {
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
    }

    public StreamWebView(Context context) {
        super(context);
        this.defaultSettings();
    }

    public StreamWebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.defaultSettings();
    }

    public StreamWebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.defaultSettings();
    }

    public StreamWebView(Context context, AttributeSet attributeSet, int i, int i1) {
        super(context, attributeSet, i, i1);
        this.defaultSettings();
    }

    public StreamWebView(Context context, AttributeSet attributeSet, int i, boolean b) {
        super(context, attributeSet, i, b);
        this.defaultSettings();
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
