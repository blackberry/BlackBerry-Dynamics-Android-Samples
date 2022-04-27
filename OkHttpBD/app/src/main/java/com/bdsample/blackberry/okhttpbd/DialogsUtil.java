/* Copyright (c) 2022 BlackBerry Limited
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

package com.bdsample.blackberry.okhttpbd;

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.good.gd.widget.GDEditText;

import java.util.HashMap;

public class DialogsUtil {

    private static final String TAG = DialogsUtil.class.getName();
    private static DialogData dialogData = new DialogData();

    public static final class CredentialsCache {
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public boolean getAllowDelegation() {
            return allowDelegation;
        }

        public void setAllowDelegation(boolean allowDelegation) {
            this.allowDelegation = allowDelegation;
        }

        public AuthType getAuthType() {
            return authType;
        }

        public void setAuthType(AuthType authType) {
            this.authType = authType;
        }

        private String username;
        private String password;
        private boolean allowDelegation = false;
        private AuthType authType;
        enum AuthType {NO_AUTH, KERB, NTLM, BASIC}
    }

    public static final class MediaTypeParams {
        private String type;
        private String value;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static final class DialogData {
        private String requestBody = "";
        private final HashMap<String, String> headers = new HashMap<>();
        private final MediaTypeParams mediaTypeParams = new MediaTypeParams();
        private CredentialsCache credsCache;

        public String getRequestBody() {
            return requestBody;
        }

        public void setRequestBody(String requestBody) {
            this.requestBody = requestBody;
        }

        public HashMap<String, String> getHeaders() {
            return headers;
        }

        public void addHeader(String key, String value)
        {
            this.headers.put(key, value);
        }

        public MediaTypeParams getMediaTypeParams() {
            return mediaTypeParams;
        }

        public void setMediaType(String type, String value) {
            mediaTypeParams.setType(type);
            mediaTypeParams.setValue(value);
        }

        public boolean isMediaTypeSet() {
            if (mediaTypeParams.type != null && mediaTypeParams.value != null) {
                return true;
            } else {
                return false;
            }
        }

        public CredentialsCache getCredsCache() {
            return credsCache;
        }

        public void setCredsCache(CredentialsCache credsCache) {
            this.credsCache = credsCache;
        }
    }

    public DialogData getDialogData() {
        return dialogData;
    }

    public void setDialogData(DialogData dialogData) {
        DialogsUtil.dialogData = dialogData;

    }

    public static final class DialogMaker {

        public AlertDialog getRequestBodyDialog(AppCompatActivity activity, DialogCallbackListener callbackListener) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View requestBodyView = inflater.inflate(R.layout.dialog_addrequestbody, null);

            builder.setView(requestBodyView)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            GDEditText requestBody = requestBodyView.findViewById(R.id.requestbodycontent);
                            dialogData.setRequestBody(requestBody.getText().toString());
                            callbackListener.onSubmittingDialogSuccessfully(dialogData);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d(TAG, "Cancel button was pressed");
                        }
                    });
            return builder.create();
        }

        public AlertDialog getHeadersDialog(AppCompatActivity activity, DialogCallbackListener callbackListener) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View addHeadersView = inflater.inflate(R.layout.dialog_addheaders, null);

            builder.setView(addHeadersView)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            GDEditText keditText = addHeadersView.findViewById(R.id.key1);
                            GDEditText veditText = addHeadersView.findViewById(R.id.value1);
                            dialogData.addHeader(keditText.getText().toString(), veditText.getText().toString());
                            keditText = addHeadersView.findViewById(R.id.key2);
                            veditText = addHeadersView.findViewById(R.id.value2);
                            dialogData.addHeader(keditText.getText().toString(), veditText.getText().toString());
                            keditText = addHeadersView.findViewById(R.id.key3);
                            veditText = addHeadersView.findViewById(R.id.value3);
                            dialogData.addHeader(keditText.getText().toString(), veditText.getText().toString());
                            keditText = addHeadersView.findViewById(R.id.key4);
                            veditText = addHeadersView.findViewById(R.id.value4);
                            dialogData.addHeader(keditText.getText().toString(), veditText.getText().toString());
                            callbackListener.onSubmittingDialogSuccessfully(dialogData);
                        }
                });
            return builder.create();
        }

        public AlertDialog getMediaTypeParamsDialog(AppCompatActivity activity, DialogCallbackListener callbackListener) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View mediaTypeView = inflater.inflate(R.layout.dialog_mediatypeparams, null);

            builder.setView(mediaTypeView)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            GDEditText keditText = mediaTypeView.findViewById(R.id.key1);
                            GDEditText veditText = mediaTypeView.findViewById(R.id.value1);
                            dialogData.setMediaType(keditText.getText().toString(), veditText.getText().toString());
                            callbackListener.onSubmittingDialogSuccessfully(dialogData);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d(TAG, "Cancel button was pressed");
                        }
                    });
            return builder.create();
        }

        public AlertDialog getCredsDialog(AppCompatActivity activity, DialogCallbackListener callbackListener,
                                          CredentialsCache.AuthType authType, int message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogCredsView = inflater.inflate(R.layout.dialog_creds, null);
            TextView userInstructions = dialogCredsView.findViewById(R.id.user_instructions);
            userInstructions.setText(message);

            // When there is a required authentication type already requested, initialize the
            // dialog popup with the radio button already selected.

            switch (authType){
                case KERB:
                    RadioButton kerberosRadioButton = dialogCredsView.findViewById(R.id.authtype_kerb);
                    kerberosRadioButton.setChecked(true);
                    break;
                case NTLM:
                    RadioButton ntlmRadioButton = dialogCredsView.findViewById(R.id.authtype_ntlm);
                    ntlmRadioButton.setChecked(true);
                    break;
                case BASIC:
                    RadioButton basicRadioButton = dialogCredsView.findViewById(R.id.authtype_basic);
                    basicRadioButton.setChecked(true);
                    break;
                case NO_AUTH:
                    //Nothing to do.
            }

            builder.setView(dialogCredsView)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            GDEditText userName = dialogCredsView.findViewById(R.id.username);
                            GDEditText password = dialogCredsView.findViewById(R.id.password);
                            CheckBox allowDelegation = dialogCredsView.findViewById(R.id.allowdelegation_checkbox);
                            RadioGroup authTypeRadioGroup = dialogCredsView.findViewById(R.id.authtypegroup);

                            //Find the AuthType selected.
                            CredentialsCache.AuthType authType;

                            int checkedID = authTypeRadioGroup.getCheckedRadioButtonId();

                            if (checkedID == R.id.authtype_basic) {
                                authType = CredentialsCache.AuthType.BASIC;
                            } else if (checkedID == R.id.authtype_kerb) {
                                authType = CredentialsCache.AuthType.KERB;
                            }else if (checkedID == R.id.authtype_ntlm) {
                                authType = CredentialsCache.AuthType.NTLM;
                            } else {
                                authType = CredentialsCache.AuthType.NO_AUTH;
                            }

                            CredentialsCache credsCache = new CredentialsCache();
                            credsCache.setUsername(userName.getText().toString());
                            credsCache.setPassword(password.getText().toString());
                            credsCache.setAllowDelegation(allowDelegation.isChecked());
                            credsCache.setAuthType(authType);
                            dialogData.setCredsCache(credsCache);
                            callbackListener.onSubmittingDialogSuccessfully(dialogData);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d(TAG, "Cancel button was pressed");
                        }
                    });
            return builder.create();
        }

        public void clearDialogData(){
            dialogData = new DialogData();
        }
    }
}
