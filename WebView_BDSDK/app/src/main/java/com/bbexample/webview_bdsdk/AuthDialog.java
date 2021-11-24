/* Copyright (c) 2021 BlackBerry Limited
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
package com.bbexample.webview_bdsdk;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.blackberry.bbwebview.BBHttpAuthHandler;

public class AuthDialog extends DialogFragment {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button okButton;
    private EditText password;
    private EditText username;

    private String host;
    private String realm;

    private BBHttpAuthHandler authHandler;

    public AuthDialog(String host, String realm, BBHttpAuthHandler authHandler) {
        this.host = host;
        this.realm = realm;
        this.authHandler = authHandler;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        ViewGroup viewGroup = initView(getActivity().getLayoutInflater());

        builder.setView(viewGroup);

        return builder.create();
    }

    private ViewGroup initView(LayoutInflater inflater) {
        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.auth_dialog, null);

        TextView title = dialogView.findViewById(R.id.title_dialog);
        title.setText("Host: " + host + ", realm: " + realm);

        username = dialogView.findViewById(R.id.username);
        username.requestFocus();

        password = dialogView.findViewById(R.id.password);

        okButton = dialogView.findViewById(R.id.btn_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (authHandler != null) {

                    String usernameString = username.getText().toString();
                    String passwordString = password.getText().toString();

                    Log.i(TAG,"AuthDialog.onClick() username " + username + " password " + password);

                    authHandler.proceed(usernameString, passwordString);
                }

                dismiss();
            }
        });

        Button cancelButton = dialogView.findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authHandler != null) {

                    Log.i(TAG,"AuthDialog.onClick() cancel ");

                    authHandler.cancel();
                }
                dismiss();
            }
        });

        return dialogView;
    }

}
