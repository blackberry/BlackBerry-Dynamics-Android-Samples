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

package com.good.example.sdk.bypassunlock;

import java.util.Map;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import android.util.Log;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;

public class BypassUnlockApplication extends Application implements GDStateListener {

    private static final String LOG_TAG = "BypassUnlockApplication";
    private boolean isAuthorized = false;

    public boolean getAuthorized() {
        return isAuthorized;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_TAG, "onCreate\n");

        // turn off (suppress) night mode for this app
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        GDAndroid.getInstance().applicationInit(this);
        //Singleton AppEvent listener is set in Application class so it receives events independently of Activity lifecycle
        GDAndroid.getInstance().setGDStateListener(this);
    }

    @Override
    public void onAuthorized() {
        Log.i(LOG_TAG, "onAuthorized\n");
        isAuthorized = true;
    }

    @Override
    public void onLocked() {
        Log.i(LOG_TAG, "onLocked\n");
        isAuthorized = false;
    }

    @Override
    public void onWiped() {
        isAuthorized = false;
    }

    @Override
    public void onUpdateConfig(Map<String, Object> settings) {
    }

    @Override
    public void onUpdatePolicy(Map<String, Object> policyValues) {
    }

    @Override
    public void onUpdateServices() {
    }

    @Override
    public void onUpdateEntitlements() {
    }
}
