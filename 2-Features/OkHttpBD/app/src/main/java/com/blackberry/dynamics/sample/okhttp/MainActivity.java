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

package com.blackberry.dynamics.sample.okhttp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.blackberry.okhttpsupport.interceptor.BBCustomInterceptor;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*
This sample demonstrates the use of the com.blackberry.okhttpsupport.interceptor.BBCustomInterceptor,
which allows the OkHttp library to be used for network communication using BlackBerry Dynamics
enterprise network connectivity.

Basic authentication is currently supported in this sample. There are some UI place holders
for KCD and NTLM authentication, but the ability to provide credentials for these authentication
methods is not supported by BBCustomInterceptor at the time of creating/updating this sample.
Kerberos authentication using PKINIT or KCD is supported when correctly configured in
BlackBerry UEM and should be handled automatically by the API without the need to provide
credentials.

Refer to the BlackBerry Dynamics SDK Release Notes for the version of BlackBerry Dynamics SDK you
are using to verify supported features.
https://docs.blackberry.com/en/development-tools/blackberry-dynamics-sdk-android/

 */

public class MainActivity extends AppCompatActivity implements GDStateListener,
        DialogCallbackListener, AuthenticationRequiredListener {

    private static final String TAG = MainActivity.class.getName();

    private ReactiveBasicAuthInterceptor reactiveAuthInterceptor;
    private DialogsUtil.CredentialsCache.AuthType requiredAuthType = DialogsUtil.CredentialsCache.AuthType.NO_AUTH;

    private EditText urlTextEditField;
    private Button goButton;
    private RadioButton getMethodRadioButton;
    private RadioButton headMethodRadioButton;
    private RadioButton postMethodRadioButton;
    private RadioButton putMethodRadioButton;
    private RadioButton disableHostVerificationButton;
    private RadioButton disablePeerVerificationButton;
    private RadioGroup httpMethodsRadioGroup;
    private TextView httpResponse;

    private BBCustomInterceptor bbCustomInterceptor;
    private OkHttpClient httpClient;

    private DialogsUtil.DialogMaker dialogMaker;
    private DialogsUtil dialogsUtil = new DialogsUtil();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GDAndroid.getInstance().activityInit(this);
        setContentView(R.layout.activity_main);

        dialogMaker = new DialogsUtil.DialogMaker();

        urlTextEditField = findViewById(R.id.url_textEdit);
        goButton = findViewById(R.id.urlGoButton);

        httpMethodsRadioGroup = findViewById(R.id.httpmethods);
        getMethodRadioButton = findViewById(R.id.httpgetmethod);
        headMethodRadioButton = findViewById(R.id.httpheadmethod);
        postMethodRadioButton = findViewById(R.id.httppostmethod);
        putMethodRadioButton = findViewById(R.id.httpputmethod);

        disablePeerVerificationButton = findViewById(R.id.disablepeerverification);
        disableHostVerificationButton = findViewById(R.id.disablehostverification);

        getMethodRadioButton.setChecked(true);
        httpResponse = findViewById(R.id.httpresponse);

        initUIButtonListeners();

        bbCustomInterceptor = new BBCustomInterceptor();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.okhttp_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog dialog;

        int itemID = item.getItemId();

        if (itemID == R.id.runasyncrequests) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(this, AsyncUseCaseActivity.class);
            startActivity(intent);
        } else if (itemID == R.id.addrequestbody) {
            dialog = dialogMaker.getRequestBodyDialog(this, this);
            dialog.show();
        } else if (itemID == R.id.addheaders) {
            dialog = dialogMaker.getHeadersDialog(this, this);
            dialog.show();
        } else if (itemID == R.id.addmediatype) {
            dialog = dialogMaker.getMediaTypeParamsDialog(this, this);
            dialog.show();
        } else if (itemID == R.id.refreshbutton) {
            httpResponse.setText("");
            urlTextEditField.setText("");
            getMethodRadioButton.setChecked(true);
            headMethodRadioButton.setChecked(false);
            postMethodRadioButton.setChecked(false);
            putMethodRadioButton.setChecked(false);
            disableHostVerificationButton.setChecked(false);
            disablePeerVerificationButton.setChecked(false);
            dialogMaker.clearDialogData();
            bbCustomInterceptor = new BBCustomInterceptor();
            initHttpClient();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initHttpClient() {
        reactiveAuthInterceptor = new ReactiveBasicAuthInterceptor(this);
        bbCustomInterceptor = new BBCustomInterceptor();

        OkHttpClient.Builder okBuilder = new OkHttpClient().newBuilder();

        httpClient = okBuilder.addInterceptor(reactiveAuthInterceptor).addInterceptor(bbCustomInterceptor).build();
    }

    private void initUIButtonListeners() {
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processGoRequest();
            }
        });
    }

    private void processGoRequest() {
        String urlStr = urlTextEditField.getText().toString();

        //Ensure the URL is valid.
        try {
            URL url = new URL(urlStr);
        } catch (MalformedURLException e) {
            Toast.makeText(
                    this, "Invalid URL.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        requiredAuthType = DialogsUtil.CredentialsCache.AuthType.NO_AUTH;

        int selectedHttpMethodId = httpMethodsRadioGroup.getCheckedRadioButtonId();

        if (disableHostVerificationButton.isChecked()) {
            bbCustomInterceptor.disableHostVerification();
        }

        if (disablePeerVerificationButton.isChecked()) {
            bbCustomInterceptor.disablePeerVerification();
        }

        if (selectedHttpMethodId == R.id.httpgetmethod){
            Request request = new Request.Builder()
                    .url(urlStr)
                    .build();
            enqueueHttpCall(request);
        }else if (selectedHttpMethodId == R.id.httpheadmethod) {
            processHEADRequest();
        } else if (selectedHttpMethodId == R.id.httppostmethod) {
            processPOSTRequest();
        } else if (selectedHttpMethodId == R.id.httpputmethod) {
            processPUTRequest();
        }
    }

    private void processHEADRequest() {
        String urlStr = urlTextEditField.getText().toString();
        Request request = new Request.Builder()
                .url(urlStr)
                .head()
                .build();
        enqueueHttpCall(request);
    }

    private void processPUTRequest() {
        Request request;
        String urlStr = urlTextEditField.getText().toString();
        Request.Builder requestBuilder = new Request.Builder();
        RequestBody requestBody = processRequestBody();

        if (requestBody != null) {
            request = requestBuilder.url(urlStr).put(requestBody).build();
            enqueueHttpCall(request);
        }
    }

    private void processPOSTRequest() {
        Request request;
        String urlStr = urlTextEditField.getText().toString();
        Request.Builder requestBuilder = new Request.Builder();
        RequestBody requestBody = processRequestBody();

        if (requestBody != null) {
            request = requestBuilder.url(urlStr).post(requestBody).build();
            enqueueHttpCall(request);
        }
    }

    private RequestBody processRequestBody() {
        boolean formDataAvailable;
        RequestBody requestBody;

        // Make sure we have request body to post.
        if (dialogsUtil.getDialogData().getRequestBody() == null) {
            Toast.makeText(
                    this, "Missing Request Body. Use the menu to add it.",
                    Toast.LENGTH_LONG
            ).show();
            return null;
        }

        Request.Builder requestBuilder = new Request.Builder();
        formDataAvailable = addHeadersToRequestBuilder(requestBuilder);

        //Posting form data.
        if (formDataAvailable) {
            requestBody = getRequestBodyFromFormBuilder();
        } else {
            // Default mediaType if user didn't set one.
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

            if (dialogsUtil.getDialogData().isMediaTypeSet()) {
                DialogsUtil.MediaTypeParams mediaTypeParams = dialogsUtil.getDialogData().getMediaTypeParams();
                mediaType = MediaType.parse(mediaTypeParams.getType() + " " + mediaTypeParams.getValue());
            }
            requestBody = RequestBody.create(dialogsUtil.getDialogData().getRequestBody().getBytes(), mediaType);
        }

        return requestBody;
    }

    private boolean addHeadersToRequestBuilder(Request.Builder requestBuilder) {
        boolean formDataAvailable = false;


        for (Map.Entry<String, String> header : (Iterable<Map.Entry<String, String>>) dialogsUtil.getDialogData().getHeaders().entrySet()) {
            if (header.getValue().equalsIgnoreCase("application/x-www-form-urlencoded")) {
                formDataAvailable = true;
            }
            requestBuilder.addHeader(header.getKey(), header.getValue());
        }

        return formDataAvailable;
    }

    private RequestBody getRequestBodyFromFormBuilder() {
        RequestBody requestBody;
        FormBody.Builder formBuilder = new FormBody.Builder();

        if (dialogsUtil.getDialogData().getRequestBody().contains("&")) {
            String[] args = dialogsUtil.getDialogData().getRequestBody().split("&");

            int argLen = args.length;
            for (int count = 0; count < argLen; count++ )
            {
                if (args[count].contains("=")) {
                    String[] kvPairs = args[count].split("=");
                    formBuilder.add(kvPairs[0], kvPairs[1]);
                }
            }
        }
        requestBody = formBuilder.build();
        return requestBody;
    }

    private void enqueueHttpCall(Request request) {
        Toast.makeText(this, "Please Wait", Toast.LENGTH_LONG).show();

        httpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (request.method().equalsIgnoreCase("HEAD")) {
                            httpResponse.setText(response.headers().toString());
                        } else {
                            if (response.body() != null) {
                                try {
                                    httpResponse.setText(response.body().string());
                                } catch (IOException ioException) {
                                    Log.e(TAG, "Exception while rendering the response of the http call. " +  ioException.toString());
                                }
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (call.isCanceled()) {
                    Log.d(TAG, "Call was cancelled. Ignore failure call.");
                } else {
                    Log.e(TAG, "enqueueHttpCall - OnFailure invoked. " +  e.toString());
                }

            }
        });
    }

    @Override
    public void onAuthorized() {
        bbCustomInterceptor = new BBCustomInterceptor();
        initHttpClient();

    }

    @Override
    public void onLocked() {

    }

    @Override
    public void onWiped() {

    }

    @Override
    public void onUpdateConfig(Map<String, Object> map) {

    }

    @Override
    public void onUpdatePolicy(Map<String, Object> map) {

    }

    @Override
    public void onUpdateServices() {

    }

    @Override
    public void onUpdateEntitlements() {

    }

    @Override
    public void onSubmittingDialogSuccessfully(DialogsUtil.DialogData dialogData) {
        Log.d(TAG,"onSubmittingDialogSuccessfully");

        this.dialogsUtil.setDialogData(dialogData);

        reactiveAuthInterceptor.onReceivingCreds(this.dialogsUtil.getDialogData().getCredsCache().getUsername(),
                this.dialogsUtil.getDialogData().getCredsCache().getPassword());

        // Upon submission of the required credentials, we process the Go request.
        if (requiredAuthType != DialogsUtil.CredentialsCache.AuthType.NO_AUTH &&
            dialogData.getCredsCache() != null) {
            processGoRequest();
        }
    }

    @Override
    public void showCredsDialogPopup(DialogsUtil.CredentialsCache.AuthType authType) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (authType != null) {
                    requiredAuthType = authType;
                }

                AlertDialog dialog = dialogMaker.getCredsDialog(MainActivity.this, MainActivity.this,
                        authType, R.string.appserver_creds_user_instructions);
                dialog.show();
            }
        });
    }


}