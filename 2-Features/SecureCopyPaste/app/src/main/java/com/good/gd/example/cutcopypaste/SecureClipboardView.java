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
import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.DragEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.good.gd.content.ClipboardManager;
import com.good.gd.example.cutcopypaste.dlp.SecureActionModeCallback;

import java.nio.charset.Charset;

/**
 * Custom view which shows how Secure ClipboardManager can be used
 */
public class SecureClipboardView extends EditText {

    //private View.DragShadowBuilder shadowBuilder;
    private ClipboardManager clipboardManager;

    public SecureClipboardView(Context context) {
        super(context);
        init();
    }

    public SecureClipboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SecureClipboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SecureClipboardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        clipboardManager = ClipboardManager.getInstance(getContext());

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (getSelectedText().length() != 0) {

                    View shadowView = View.inflate(getContext(),R.layout.drag_shadow,null);
                    final int size = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    shadowView.measure(size, size);
                    shadowView.layout(0, 0, shadowView.getMeasuredWidth(), shadowView.getMeasuredHeight());
                    shadowView.invalidate();

                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(shadowView);

                    clipboardManager.startDragAndDrop(
                            ClipData.newPlainText("text", getSelectedText()),
                            SecureClipboardView.this,
                            shadowBuilder,//this paramenter will be used if Data Leakage Prevention is off
                            "Secure Drag and Drop");
                    return true;
                }
                return false;
            }
        });

        //set custom callback which removes custom menu items from selection context menu if DLP is on
        //in this view we support only cut,copy,paste,select_all operations
        ActionMode.Callback actionModeCallback = SecureActionModeCallback.newCallback();

        setCustomInsertionActionModeCallback(actionModeCallback);
        setCustomSelectionActionModeCallback(actionModeCallback);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {

        switch (id) {
            case android.R.id.selectAll:
                return super.onTextContextMenuItem(id);

            case android.R.id.paste: {
                if (clipboardManager.hasPrimaryClip()) {
                    CharSequence clipboardText = clipboardManager.getPrimaryClip().getItemAt(0).getText();
                    if (clipboardText != null) {
                        getText().replace(getSelectionStart(), getSelectionEnd(), clipboardText);
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }

            case android.R.id.cut: {
                CharSequence selectedText = getSelectedText();

                if (selectedText.toString().getBytes(Charset.defaultCharset()).length > ClipboardManager.CLIPDATA_TEXT_SIZE_LIMIT) {
                    Toast.makeText(getContext(),
                            "Text is too big, please select text up to " + ClipboardManager.CLIPDATA_TEXT_SIZE_LIMIT / 1024 + " kBytes", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    getText().replace(getSelectionStart(), getSelectionEnd(), "");
                    clipboardManager.setPrimaryClip(ClipData.newPlainText("text", selectedText));
                }
                return true;
            }

            case android.R.id.copy: {
                CharSequence selectedText = getSelectedText();

                if (selectedText.toString().getBytes(Charset.defaultCharset()).length > ClipboardManager.CLIPDATA_TEXT_SIZE_LIMIT) {
                    Toast.makeText(getContext(),
                            "Text is too big, please select text up to " + ClipboardManager.CLIPDATA_TEXT_SIZE_LIMIT / 1024 + " kBytes", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    clipboardManager.setPrimaryClip(ClipData.newPlainText("text", selectedText));
                    /* Force the popup menu to disappear.
                     * Since stopTextActionMode is hidden,
                     * we can't call that other than through reflection */
                    getText().replace(getSelectionStart(), getSelectionEnd(), selectedText);
                }
                return true;
            }
            default:
                return super.onTextContextMenuItem(id);
        }
    }

    private CharSequence getSelectedText() {
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();

        Log.i("SecureClipboardView", String.format("getSelectedText start:%d end:%d",selectionStart,selectionEnd));

        if (selectionStart < selectionEnd) {
            return getText().subSequence(selectionStart, selectionEnd);
        }
        return "";
    }

    @Override
    public boolean onDragEvent(DragEvent event) {

        Log.i("SecureClipboardView", "getClipData");

        switch (event.getAction()){

            case DragEvent.ACTION_DROP:

                //Check if secure clipboard can handle the event, currently it can handle only ACTION_DROP events
                ClipData clipData = clipboardManager.getClipData(event);

                if (clipData != null) {

                    CharSequence clipText = clipData.getItemAt(0).getText();
                    Log.d("SecureClipboardView", "getClipData: "+clipText);

                    int selectionEnd = getSelectionEnd();
                    Editable text = getText();

                    text.insert(selectionEnd, clipText);

                    Object localState = event.getLocalState();
                    if (localState instanceof String) {
                        Toast.makeText(getContext(),
                                "DragEvent local state: " + localState, Toast.LENGTH_SHORT)
                                .show();
                    }
                    return true;
                }
                return false;

            default:
                return super.onDragEvent(event);
        }
    }
}
