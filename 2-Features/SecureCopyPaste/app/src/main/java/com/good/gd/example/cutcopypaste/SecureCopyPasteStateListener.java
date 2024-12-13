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

import java.util.Map;

import android.util.Log;

import com.good.gd.GDStateListener;

/*
 * You have to have either a singleton GDStateListener, or make every Activity
 * implement it; often just having a singleton makes for cleaner code.
 */
public class SecureCopyPasteStateListener implements GDStateListener {

    private static final String TAG = SecureCopyPasteStateListener.class.getSimpleName();

    @Override
    public void onAuthorized() {
        Log.d(TAG, "onAuthorized()");
    }

    @Override
    public void onLocked() {
        Log.d(TAG, "onLocked()");
    }

    @Override
    public void onWiped() {
        Log.d(TAG, "onWiped()");
    }

    @Override
    public void onUpdateConfig(final Map<String, Object> settings) {
        Log.d(TAG, "onUpdateConfig()");
    }

    @Override
    public void onUpdatePolicy(final Map<String, Object> policyValues) {
        Log.d(TAG, "onUpdatePolicy()");
    }

    @Override
    public void onUpdateServices() {
        Log.d(TAG, "onUpdateServices()");
    }

    @Override
    public void onUpdateEntitlements() {
        Log.d(TAG, "onUpdateEntitlements()");
    }
}
