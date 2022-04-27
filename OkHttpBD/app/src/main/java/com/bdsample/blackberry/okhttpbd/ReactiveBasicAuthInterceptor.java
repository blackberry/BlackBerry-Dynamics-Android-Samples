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

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ReactiveBasicAuthInterceptor implements Interceptor, CredentialsReceivedCallBack {

    private final AuthenticationRequiredListener authenticationListener;
    private String credentials;

    private static final String TAG = ReactiveBasicAuthInterceptor.class.getName();

    public ReactiveBasicAuthInterceptor(AuthenticationRequiredListener authenticationListener) {
        this.authenticationListener = authenticationListener;
    }


    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response response = chain.proceed((chain.request()));

        if (response.code() == 401) {

            if (credentials == null) {
                authenticationListener.showCredsDialogPopup(DialogsUtil.CredentialsCache.AuthType.BASIC);
                chain.call().cancel();
                return chain.proceed(chain.request());
            }

            Request request = chain.request();
            Request authenticatedRequest = request.newBuilder().header("Authorization", credentials).build();
            return chain.proceed(authenticatedRequest);
        }

        return response;
    }

    @Override
    public void onReceivingCreds(String username, String password) {
        Log.d(TAG, "onReceivingCreds invoked - Credentials received : " + (username));

        if (username != null && password != null) {
                this.credentials = Credentials.basic(username, password);
        }
        else {
            this.credentials = null;
        }
    }
}
