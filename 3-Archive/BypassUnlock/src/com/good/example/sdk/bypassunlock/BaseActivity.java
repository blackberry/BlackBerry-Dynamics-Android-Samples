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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;

import java.util.Map;

public class BaseActivity extends AppCompatActivity implements
        View.OnClickListener, GDStateListener {

    private boolean isAuthorized = false;
    private Switch switchView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GDAndroid.getInstance().activityInit(this);
        BypassUnlockApplication app = (BypassUnlockApplication)getApplication();
        isAuthorized = app.getAuthorized();
    }

    void setSwitchView(int id) {
        switchView  = findViewById(id);
        if(switchView != null) {
            if (isAuthorized) {
                switchView.setChecked(true);
            } else {
                switchView.setChecked(false);
            }
        }
    }

    /**
     * onCreateOptionsMenu - called once to create the options menu structure
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_bar_action: {
                contactsClicked();
                break;
            }
            default: {
            }
        }
    }

    private void contactsClicked() {
        if(isAuthorized) {
            Intent intent = new Intent();
            intent.setClass(this, ContactsActivity.class);
            startActivity(intent);
        }else {
            new AlertDialog.Builder(this)
                    .setTitle("Application is Locked")
                    .setMessage("Are you sure you want to open Contacts and enter password?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setClass(getApplicationContext(), ContactsActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    @Override
    public void onAuthorized() {
        isAuthorized = true;
        if(switchView != null) {
            switchView.setChecked(true);
        }
    }

    @Override
    public void onLocked() {
        isAuthorized = false;
        if(switchView != null) {
            switchView.setChecked(false);
        }
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
