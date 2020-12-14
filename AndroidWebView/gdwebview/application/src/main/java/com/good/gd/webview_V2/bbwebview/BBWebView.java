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

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import com.good.gd.webview_V2.R;
import com.good.gd.webview_V2.bbwebview.utils.DLPPolicy;
import com.good.gd.webview_V2.bbwebview.utils.Utils;

import java.util.Map;

public class BBWebView extends WebView {

    private static final String TAG = "GDWebView-" +  BBWebView.class.getSimpleName();

    private String lastSelectedText = "";

    /**
     * Construct a new WebView with a Context object.
     *
     * @param context A Context object used to access application assets.
     */
    public BBWebView(Context context) {
        super(context);

        init();
    }

    /**
     * Construct a new WebView with layout parameters.
     *
     * @param context A Context object used to access application assets.
     * @param attrs   An AttributeSet passed to our parent.
     */
    public BBWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    /**
     * Construct a new WebView with layout parameters and a default style.
     *
     * @param context      A Context object used to access application assets.
     * @param attrs        An AttributeSet passed to our parent.
     * @param defStyleAttr
     */
    public BBWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public BBWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    public void init() {

        setOnDragListener(new OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                Log.i(TAG, "onDrag: requestSelectedText");
                updateTextSelection();
                return false;
            }
        });

    }

    @Override
    public boolean onDragEvent(DragEvent event) {

        Log.i(TAG, "onDragEvent: received drag event ");

        switch (event.getAction()) {
            case DragEvent.ACTION_DROP: {
                Log.i(TAG, "onDragEvent: Received ACTION_DROP");

                // There could be 2 cases:
                //
                // 1. Received a drag with drop action which was started from WebView.
                // Then lastSelectedText won't be empty and the condition will be false.
                //
                // 2. Received a drag with drop action which was started from non-GD app (or GD app).
                // Then if DLP is enabled we should prohibit this drop event.
                //
                // Note: The drag-n-drop text from GD app is not handled as it requires SDK support for WebView.
                if (DLPPolicy.isInboundDlpEnabled() && lastSelectedText.isEmpty()) {
                    Log.i(TAG, "onDragEvent: Prevented drag-n-drop text from non-GD app to GDWebView");
                    return true;
                }

                break;
            }
        }

        lastSelectedText = "";

        return super.onDragEvent(event);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {

        InputConnection connection = super.onCreateInputConnection(outAttrs);

        if (DLPPolicy.isDictationPreventionEnabled()) {
            Log.i(TAG, "onCreateInputConnection: Dictation policy ON");
            outAttrs.privateImeOptions = "nm";
        }

        if (DLPPolicy.isKeyboardRestrictionModeEnabled()) {
            Log.i(TAG, "onCreateInputConnection: Incognito policy ON");
            outAttrs.imeOptions = outAttrs.imeOptions |  EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING;
        }

        return connection;
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        super.loadUrl(url, additionalHttpHeaders);

        ((BBWebViewClient) getWebViewClient()).getObserver().notifyLoadUrl(url);
    }

    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);

        ((BBWebViewClient) getWebViewClient()).getObserver().notifyLoadUrl(url);
    }

    private void updateTextSelection() {
        try {
            String script = Utils.getFileContent(R.raw.selection_interceptor, getContext());
            evaluateJavascript(script, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    // For example, if an user selects 'hello' text in WebView
                    // Then here the received value will be ""hello"".
                    // Remove redundant quotes and save new value.
                    lastSelectedText = value.substring(value.indexOf('"'), value.lastIndexOf('"') - 1);
                }
            });
        } catch (Exception e) {
            Log.i(TAG, "requestSelectedText: exception " + e);
        }
    }

}
