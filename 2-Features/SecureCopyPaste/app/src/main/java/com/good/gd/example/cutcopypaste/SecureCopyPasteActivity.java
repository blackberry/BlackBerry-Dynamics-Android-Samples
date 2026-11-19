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

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.good.gd.GDAndroid;

import com.good.gd.example.cutcopypaste.databinding.ActivityMainBinding;

/**
 * Top-level Activity showing buttons for various functionality.
 */
public class SecureCopyPasteActivity extends SampleAppActivity {

    private static final String TAG = SecureCopyPasteActivity.class.getSimpleName();
    private static final int REQUEST_POST_NOTIFICATIONS = 1001;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GDAndroid.getInstance().activityInit(this);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupAppBar(getString(R.string.app_name));

        adjustViewsIfEdgeToEdgeMode(binding.bbdSecureCopyPasteUI, null, binding.topScreenContentLayout);

        // Request notification permission for Android 13+
        requestNotificationPermission();
    }

    private void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_POST_NOTIFICATIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "POST_NOTIFICATIONS permission granted");
            } else {
                Log.w(TAG, "POST_NOTIFICATIONS permission denied");
            }
        }
    }

    public void launchTextWidgetsGd(View aView) {
        startActivity(new Intent(this, TextWidgetsActivityGD.class));
    }

    public void launchTextWidgetsAppCompat(View aView) {
        startActivity(new Intent(this, TextWidgetsActivityAppCompat.class));
    }

    public void launchTextWidgetsAppCompatWithInflater(View aView) {
        startActivity(new Intent(this, TextWidgetsActivityAppCompatWithInflater.class));
    }

    public void launchPreferences(View aView) {
        startActivity(new Intent(this, PreferencesActivity.class));
    }

    public void launchRichText(View view) {
        startActivity(new Intent(this, RichTextActivity.class));
    }

    public void launchFingerprint(View aView) {
        GDAndroid.getInstance().openFingerprintSettingsUI();
    }
}
