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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.good.gd.GDAndroid;

import com.good.gd.example.cutcopypaste.databinding.ActivityMainBinding;

/**
 * Top-level Activity showing buttons for various functionality.
 */
public class SecureCopyPasteActivity extends SampleAppActivity {

    private static final String TAG = SecureCopyPasteActivity.class.getSimpleName();

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GDAndroid.getInstance().activityInit(this);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupAppBar(getString(R.string.app_name));

        View mainView = findViewById(R.id.main_layout);
        View mainContent = findViewById(R.id.content_layout);

        adjustViewsIfEdgeToEdgeMode(mainView, null, mainContent);
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
