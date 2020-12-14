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
package com.good.gd.webview_V2.bbwebview.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.good.gd.webview_V2.R;

public class JsDialogHelper {

    private static final String DIALOG_DEFAULT_TITLE = "A dialog on this page says";

    private final String message;
    private final JsResult jsResult;
    private final String defaultValue;

    public JsDialogHelper(String message, JsResult jsResult, String defaultValue) {
        this.message = message;
        this.jsResult = jsResult;
        this.defaultValue = defaultValue;
    }

    public void showDialog(Context context)
    {
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.prompt_dialog, null);

        EditText editText = dialogView.findViewById(R.id.prompt_edit_text);
        editText.setText(defaultValue);

        ((TextView) dialogView.findViewById(R.id.prompt_message)).setText(message);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
            .setTitle(DIALOG_DEFAULT_TITLE)
            .setPositiveButton(android.R.string.ok, new PositiveButton(editText, (JsPromptResult) jsResult))
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                jsResult.cancel();
            }
        })
            .setView(dialogView);

        builder.show();
    }

    private static class PositiveButton implements DialogInterface.OnClickListener {

        private EditText editText;
        private JsPromptResult jsPromptResult;

        public PositiveButton(EditText editText, JsPromptResult jsPromptResult) {
            this.editText = editText;
            this.jsPromptResult = jsPromptResult;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (jsPromptResult != null && editText != null) {
                jsPromptResult.confirm(editText.getText().toString());
            }
        }

    }

}
