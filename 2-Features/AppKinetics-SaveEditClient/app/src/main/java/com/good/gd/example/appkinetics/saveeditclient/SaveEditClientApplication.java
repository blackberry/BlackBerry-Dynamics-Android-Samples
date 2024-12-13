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

package com.good.gd.example.appkinetics.saveeditclient;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import android.util.Log;

import com.good.gd.GDAndroid;
import com.good.gd.icc.GDService;
import com.good.gd.icc.GDServiceClient;
import com.good.gd.icc.GDServiceClientListener;
import com.good.gd.icc.GDServiceException;
import com.good.gd.icc.GDServiceListener;

public class SaveEditClientApplication extends Application {

    private static final String TAG = SaveEditClientApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        // turn off (suppress) night mode for this app
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        GDAndroid.getInstance().applicationInit(this);

        final GDServiceClientListener clientListener = GDSaveEditClientListener.getInstance();
        final GDServiceListener serviceListener = GDSaveEditClientListener.getInstance();

        try {
            GDServiceClient.setServiceClientListener(clientListener);
            GDService.setServiceListener(serviceListener);
        } catch (final GDServiceException exception) {
            Log.e(TAG, "SaveEditClientApplication::onCreate()  " +
                    "Error Setting GDServiceClientListener -- " + exception.getMessage() + "\n");
        }
    }
}
