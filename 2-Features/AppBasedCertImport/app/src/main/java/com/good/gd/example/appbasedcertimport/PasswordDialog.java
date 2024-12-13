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

package com.good.gd.example.appbasedcertimport;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;


public class PasswordDialog extends Dialog implements View.OnClickListener {
    private Button mBtn_cancel, mBtn_ok;

    private PasswordDialogListener mResultListener;
    private EditText mPassword;

    public PasswordDialog(@NonNull Context context, PasswordDialogListener resultListener) {
        super(context);
        mResultListener = resultListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        setContentView(R.layout.password_dialog_layout);
        mBtn_cancel = findViewById(R.id.btn_cancel);
        mBtn_ok = findViewById(R.id.btn_ok);
        mBtn_cancel.setOnClickListener(this);
        mBtn_ok.setOnClickListener(this);
        mPassword = findViewById(R.id.ed_password);
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mBtn_ok.setEnabled(true); // Allow empty password ("")
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                String password = mPassword.getText().toString();
                dismiss();
                mResultListener.onResult(password);
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }

    public interface PasswordDialogListener {
        void onResult(String result);
    }
}
