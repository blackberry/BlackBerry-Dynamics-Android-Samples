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

package com.good.gd.example.keymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.pki.CredentialsProfile;
import com.good.gd.pki.ui.CredentialManagerUI;

import java.util.Map;

public class MainActivity extends SampleAppActivity implements GDStateListener, View.OnClickListener {

    @Override //Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.main);

        GDAndroid.getInstance().activityInit(this);

        setupAppBar(getString(R.string.app_title));

        View mainView = findViewById(R.id.bbd_keymanager_UI);

        adjustViewsIfEdgeToEdgeMode(mainView, null, null);
    }

    @Override //GDStateListener
    public void onAuthorized() {
        // Register User Credential Profile types that will show up
        // in the Credental Manager UI if assigned to the user.
        CredentialsProfile.register(CredentialsProfile.Type.Appbased);
        CredentialsProfile.register(CredentialsProfile.Type.DeviceKeystore);
        CredentialsProfile.register(CredentialsProfile.Type.UserCertificate);
        CredentialsProfile.register(CredentialsProfile.Type.AssistedSCEP);
        CredentialsProfile.register(CredentialsProfile.Type.Entrust);
        CredentialsProfile.register(CredentialsProfile.Type.PKIConnector);

        // Register a click listener to detect a click on the
        // 'Show Credential Manager' button.
        Button btn = findViewById(R.id.ShowCredentialManagerButton);
        btn.setOnClickListener(this);
    }

    @Override //View.OnClickListener
    public void onClick(View v) {
        if (v.getId() == R.id.ShowCredentialManagerButton) {
            // You have clicked the 'Show Credential Manager' button, so
            // show the Credential Manager UI. This Actvity becomes the parent
            // of the Credential Manager Activity so when the user navigates
            // back, the main view with 'Show Credential Manager' button is
            // shown again.
            CredentialManagerUI.showCredentials(this);
        }
    }

    @Override //GDStateListener
    public void onLocked() {

    }

    @Override //GDStateListener
    public void onWiped() {

    }

    @Override //GDStateListener
    public void onUpdateConfig(Map<String, Object> settings) {

    }

    @Override //GDStateListener
    public void onUpdatePolicy(Map<String, Object> policyValues) {

    }

    @Override //GDStateListener
    public void onUpdateServices() {

    }

    @Override //GDStateListener
    public void onUpdateEntitlements() {

    }
}
