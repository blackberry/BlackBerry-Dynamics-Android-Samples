/* Copyright (c) 2023 BlackBerry Ltd.
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

package com.good.gd.example.cutcopypaste;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

/**
    Regular view which uses system ClipboardManager.
    For comparision with SecureClipboardView implementation
 */

public class SystemClipboardView extends EditText {


    private ClipboardManager clipboardManager;

    public SystemClipboardView(Context context) {
        super(context);
        init();
    }

    public SystemClipboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SystemClipboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SystemClipboardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {

        switch (id) {
            case android.R.id.selectAll:
                return super.onTextContextMenuItem(id);

            case android.R.id.paste:

                if (clipboardManager.hasPrimaryClip()) {
                    CharSequence text = clipboardManager.getPrimaryClip().getItemAt(0).getText();

                    getText().replace(getSelectionStart(),getSelectionEnd(),text == null?"":text);
                    return true;
                }
                return false;

            case android.R.id.cut: {
                CharSequence selectedText = getSelectedText();

                getText().replace(getSelectionStart(), getSelectionEnd(), "");

                clipboardManager.setPrimaryClip(ClipData.newPlainText("text", selectedText));

                return true;
            }

            case android.R.id.copy: {
                CharSequence selectedText = getSelectedText();
                clipboardManager.setPrimaryClip(ClipData.newPlainText("text", selectedText));
                    /* Force the popup menu to disappear.
                     * Since stopTextActionMode is hidden,
                     * we can't call that other than through reflection */
                getText().replace(getSelectionStart(), getSelectionEnd(), selectedText);
                return true;
            }

            case android.R.id.shareText:
                return super.onTextContextMenuItem(id);
        }
        return super.onTextContextMenuItem(id);
    }

    private CharSequence getSelectedText() {
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();

        Log.i("SystemClipboardView", String.format("getSelectedText start:%d end:%d",selectionStart,selectionEnd));

        if (selectionStart < selectionEnd) {
            return getText().subSequence(selectionStart,selectionEnd);
        }
        return "";
    }
}
