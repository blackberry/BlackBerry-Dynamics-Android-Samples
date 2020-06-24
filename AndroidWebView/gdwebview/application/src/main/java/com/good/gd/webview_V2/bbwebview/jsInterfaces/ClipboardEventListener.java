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

import android.content.ClipData;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.good.gd.content.ClipboardManager;

import java.nio.charset.Charset;

public class ClipboardEventListener {

    private Context context;

    public ClipboardEventListener(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public boolean onTextCopy(String text) {
        ClipboardManager clipboardManager = ClipboardManager.getInstance(context);

        if (text.getBytes(Charset.defaultCharset()).length > ClipboardManager.CLIPDATA_TEXT_SIZE_LIMIT) {
            Toast.makeText(context,
                    "Text is too big, please select text up to " + ClipboardManager.CLIPDATA_TEXT_SIZE_LIMIT / 1024 + " kBytes", Toast.LENGTH_SHORT)
                    .show();
        } else {
            clipboardManager.setPrimaryClip(ClipData.newPlainText("text", text));
        }
        return false;
    }

    @JavascriptInterface
    public boolean onTextCut(String text) {
        ClipboardManager clipboardManager = ClipboardManager.getInstance(context);

        if (text.getBytes(Charset.defaultCharset()).length > ClipboardManager.CLIPDATA_TEXT_SIZE_LIMIT) {
            Toast.makeText(context,
                    "Text is too big, please select text up to " + ClipboardManager.CLIPDATA_TEXT_SIZE_LIMIT / 1024 + " kBytes", Toast.LENGTH_SHORT)
                    .show();
        } else {
            clipboardManager.setPrimaryClip(ClipData.newPlainText("text", text));
        }
        return false;
    }

    @JavascriptInterface
    public String onTextPaste() {
        ClipboardManager clipboardManager = ClipboardManager.getInstance(context);
        if (clipboardManager.hasPrimaryClip()) {
            CharSequence clipboardText = clipboardManager
                    .getPrimaryClip().getItemAt(0).getText();
            return clipboardText.toString();
        }
        return null;
    }

}
