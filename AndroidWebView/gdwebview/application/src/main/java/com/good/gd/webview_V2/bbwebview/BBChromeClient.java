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
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.good.gd.webview_V2.bbwebview.utils.JsDialogHelper;

class BBChromeClient extends WebChromeClient {

    private static final String TAG = "GDWebView-" + BBChromeClient.class.getSimpleName();

    @Override
    public boolean onConsoleMessage(android.webkit.ConsoleMessage consoleMessage) {

        Log.i(TAG, "JS console: " + consoleMessage.lineNumber() + " " + consoleMessage.messageLevel() + " " + consoleMessage.message() + " " + consoleMessage.sourceId());

        return super.onConsoleMessage(consoleMessage);
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {

        Log.i(TAG, "onJsPrompt");

        JsDialogHelper dialogHelper = new JsDialogHelper(message, result, defaultValue);
        dialogHelper.showDialog(view.getContext());

        return true;
    }

}
