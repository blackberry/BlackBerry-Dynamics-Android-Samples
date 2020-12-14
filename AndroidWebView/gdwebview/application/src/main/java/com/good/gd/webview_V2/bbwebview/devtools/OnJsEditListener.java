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
package com.good.gd.webview_V2.bbwebview.devtools;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.webkit.ValueCallback;
import android.widget.TextView;

import static android.graphics.Typeface.ITALIC;

public class OnJsEditListener extends onPaneEditListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                final String command = inputView.getText().toString();

                SpannableString text = new SpannableString(command + "\n");
                text.setSpan(
                        new ForegroundColorSpan(Color.MAGENTA), 0, text.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                text.setSpan(
                        new StyleSpan(ITALIC), 0, text.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                text.setSpan(
                        new BulletSpan(12,0xff_ff_ff_ff), 0, text.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                outputView.append(text);

                inputView.setText("");
                handled = true;

                targetWebView.evaluateJavascript(command, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        if (!activity.isFinishing()) {
                            SpannableString text = new SpannableString(value + "\n");
                            text.setSpan(
                                    new ForegroundColorSpan(Color.rgb(0,254,0)), 0, text.length(),
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            outputView.append(text);
                        }
                    }
                });
            }
            return handled;
        }
}
