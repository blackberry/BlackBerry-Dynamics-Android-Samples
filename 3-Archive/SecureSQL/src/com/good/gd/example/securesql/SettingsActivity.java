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

package com.good.gd.example.securesql;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;

import com.good.gd.example.utils.Settings;

import java.util.Map;

import static com.good.gd.example.utils.Settings.SECURE_SQL_SP;

public class SettingsActivity extends SampleAppActivity implements
                                        GDStateListener,
                                        View.OnClickListener,
                                        CompoundButton.OnCheckedChangeListener {

    public static final String TAG = "SettingsActivity";

    private CheckBox reauthCheckbox;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GDAndroid.getInstance().activityInit(this);

        setContentView(R.layout.activity_settings);

        setupAppBarAndEnabledBackButton(getString(R.string.app_name));

        settings = getSettings(savedInstanceState);

        initViews();

        ViewGroup mainView = findViewById(R.id.main_layout);
        ViewGroup contentView = findViewById(R.id.content_layout);
        ViewGroup bottomBar = findViewById(R.id.action_view_menu);

        adjustViewsIfEdgeToEdgeMode(mainView, bottomBar, contentView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        cacheSettings();
        outState.putParcelable(SECURE_SQL_SP, settings);
    }

    private Settings getSettings(Bundle bundle) {

        if(bundle != null && bundle.containsKey(SECURE_SQL_SP)) {
            Settings s = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    ? bundle.getParcelable(SECURE_SQL_SP, Settings.class)
                    : bundle.getParcelable(SECURE_SQL_SP);
            return s;
        }
        return new Settings().loadFromPreferences();
    }

    private void initViews() {
        reauthCheckbox = findViewById(R.id.reauthCheckbox);

        updateStates();

        reauthCheckbox.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        cacheSettings();
        updateStates();
    }

    private void updateStates() {
        boolean reauthenticateEnabled = settings.isReauthenticateEnabled();

        // set values
        reauthCheckbox.setChecked(reauthenticateEnabled);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_save:
                actionSave();
                break;
            case R.id.action_cancel:
                finish();
                break;
        }
    }

    private void cacheSettings() {
        settings.setReauthenticate(reauthCheckbox.isChecked());
    }

    private void actionSave() {
        settings.setReauthenticate(reauthCheckbox.isChecked());
        settings.saveToPreferences();

        finish();
    }

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
