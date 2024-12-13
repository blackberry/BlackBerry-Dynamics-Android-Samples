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


 /*
 * Copyright 2023 BlackBerry Limited. All rights reserved.
 */

package com.good.gd.example.services.greetings.client;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.good.gd.GDAndroid;
import com.good.gd.GDAuthDelegateInfo;
import com.good.gd.GDStateListener;
import com.good.gd.file.File;
import com.good.gd.file.FileOutputStream;
import com.good.gd.icc.GDICCForegroundOptions;
import com.good.gd.icc.GDServiceClient;
import com.good.gd.icc.GDServiceException;
import com.good.gd.widget.GDTextView;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class GreetingsClient extends SampleAppActivity implements GDStateListener,
        OnClickListener {

    private static final String TAG = "GreetingsClient";

    private static final String _greetingServerId = "com.good.gd.example.services.greetings.server";
    
    private static final int PERMISSION_REQUEST = 1988;

    // test files
    private volatile String _file1;
    private volatile String _file2;

    public static long _startTime;
    GDTextView sendGreetingsView;
    private String sendGreetingsTextPreferMe;
    private String sendGreetingsTextPreferPeer;
    private String sendGreetingsTextNoOption;

    private GDICCForegroundOptions option = GDICCForegroundOptions.PreferMeInForeground;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,
                "######################## Greetings Client onCreate ########################\n");
        super.onCreate(savedInstanceState);

        GDAndroid.getInstance().activityInit(this);

        setContentView(R.layout.main);
        showNotAuthorizedUI();

        setupAppBar(getString(R.string.app_name));

        GreetingsClientGDServiceListener.getInstance().setCurrentContext(this);
        sendGreetingsView = findViewById(R.id.action_send_greetings);
        registerForContextMenu(sendGreetingsView);
        sendGreetingsTextPreferMe = getResources().getString(R.string.str_send_greetings_short) + " (" + getResources().getString(R.string.prefer_me_in_foreground) + ")";
        sendGreetingsTextPreferPeer = getResources().getString(R.string.str_send_greetings_short) + " (" + getResources().getString(R.string.prefer_peer_in_foreground) + ")";
        sendGreetingsTextNoOption = getResources().getString(R.string.str_send_greetings_short) + " (" + getResources().getString(R.string.no_foreground_option) + ")";

        View mainView = findViewById(R.id.main_layout);
        View contentView = findViewById(R.id.content_layout);
        View bottomBar = findViewById(R.id.bbd_greetings_client_UI);

        adjustViewsIfEdgeToEdgeMode(mainView, bottomBar, contentView);
    }

    @Override
    public void onResume() {
        super.onResume();
        logAuthDelegate();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);
        menu.setHeaderTitle("Select foreground option");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_peer:
                option = GDICCForegroundOptions.PreferPeerInForeground;
                sendGreetingsView.setText(sendGreetingsTextPreferPeer);
                break;
            case R.id.option_no:
                option = GDICCForegroundOptions.NoForegroundPreference;
                sendGreetingsView.setText(sendGreetingsTextNoOption);
                break;
            case R.id.option_me:
            default: {
                option = GDICCForegroundOptions.PreferMeInForeground;
                sendGreetingsView.setText(sendGreetingsTextPreferMe);
            }
        }
        return super.onContextItemSelected(item);
    }

    public void requestPermissions() {
        TextView t = findViewById(R.id.status);
        int TIRAMISU = 33; // the same value as Build.VERSION_CODES.TIRAMISU
        if (Build.VERSION.SDK_INT >= TIRAMISU) {
            String POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS"; // the same value as Manifest.permission.POST_NOTIFICATIONS
            if (checkSelfPermission(POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{POST_NOTIFICATIONS}, PERMISSION_REQUEST);
            } else {
                t.setText("Notification permission is granted");
            }
        } else {
            t.setText("No need to request notificaton permission for Android 12 or earlier");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        TextView t = findViewById(R.id.status);
        if (requestCode == PERMISSION_REQUEST && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                t.setText("Permission is granted");
            } else {
                t.setText("Permission is not granted");
            }
        } else {
            t.setText("No grant permission results, probably app does not have target Android 13");
        }
    }

    // Implementation of button actions
    private void _sendGreeting() {
        try {
            String customServiceMessage = "Hello from GreetingsClient";
            String requestID = GDServiceClient.sendTo(_greetingServerId,
                    "testService", "1.0.0", "testMethod", customServiceMessage,
                    null, option);
            Log.d(TAG, "sendGreeting requestID=" + requestID);
        } catch (GDServiceException e) {
            displayMessage("Error", e.getMessage());
        }
    }

    private void _sendGreetingWithAttachments() {
        try {
            String customServiceMessage = "Hello from GreetingsClient";

            String files[] = new String[2];
            files[0] = _file1;
            files[1] = _file2;

            String requestID = GDServiceClient.sendTo(_greetingServerId,
                    "testService", "1.0.0", "testMethod", customServiceMessage,
                    files, GDICCForegroundOptions.PreferMeInForeground);
            Log.d(TAG, "sendGreeting requestID=" + requestID);
        } catch (GDServiceException e) {
            displayMessage("Error", e.getMessage());
        }
    }

    private void _getAgeOfEmployeeWithName(String name) {
        try {
            String requestID = GDServiceClient.sendTo(_greetingServerId,
                    "search", "1.0.0", "age", name, null,
                    GDICCForegroundOptions.PreferMeInForeground);
            Log.d(TAG, "sendGreeting requestID=" + requestID);
        } catch (GDServiceException e) {
        	displayMessage("Error", e.getMessage());
        }
    }

    // UI
    private void showAuthorizedUI() {
        TextView t = findViewById(R.id.authStatus);
        t.setText(R.string.authorized);
        t.setTextColor(Color.rgb(0, 255, 0));
    }

    private void showNotAuthorizedUI() {
        TextView t = findViewById(R.id.authStatus);
        t.setText(R.string.not_authorized);
        t.setTextColor(Color.rgb(255, 0, 0));
    }

    private void setStatus(String status) {
        TextView t = findViewById(R.id.status);
        t.setText(status);
    }

    private void displayMessage(String title, String message) {
        Log.d(TAG, "displayMessage title: " + title + " message:" + message
                + "\n");

        FragmentManager fm = getSupportFragmentManager();
        GreetingsClientDialogFragment usermess = GreetingsClientDialogFragment.createInstance(title,message);
        usermess.show(fm, "usermessage");

    }

    // test method
    private String test_createFile(String filename, int size) {
        Log.d(TAG, "+ createFile: " + filename + "\n");
        File file = new File(filename);
        if (file.isDirectory()) {
            Log.d(TAG, "+ createFile deleting directory");
            boolean isDeleted = file.delete();
            if (!isDeleted){
                Log.d(TAG, "Cannot delete file" + filename);
            }
        }
        String parent = file.getParent();
        if (parent != null) {
            File parentfile = new File(parent);
            boolean created = parentfile.mkdirs();
            if (!created) {
                Log.d(TAG, "+ createFile: couldn't create directories");
            } else {
                Log.d(TAG, "+ createFile: created directories");
            }
        }

        byte[] data = new byte[size];
        for (int i = 0; i < data.length; ++i) {
            data[i] = 2; // just fill the file with a random value
        }

        OutputStream out;
        try {
            Log.d(TAG, "+ createFile" + filename + "\n");
            out = new BufferedOutputStream(new FileOutputStream(file));
            out.write(data);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- createFile - created\n");
        return filename;
    }

    @Override
    public void onAuthorized() {

        Log.d(TAG, "onAuthorized()");

        logAuthDelegate();

        showAuthorizedUI();

        // create two test files
        if (_file1 == null || _file2 == null) {
            int fileSize = 10000;
            _file1 = test_createFile("sendFile1.txt", fileSize);
            _file2 = test_createFile(
                    "/\u6c49\u8bed/\u6f22\u8a9e/sendfile2\u65e5\u672c\u56fd.txt",
                    fileSize);
        }
    }

    @Override
    public void onLocked() {
        Log.d(TAG, "onLocked( )");
        showNotAuthorizedUI();
    }

    @Override
    public void onWiped() {
        Log.d(TAG, "onWiped( )");
    }

    @Override
    public void onUpdateConfig(Map<String, Object> settings) {
        Log.d(TAG, "onUpdateConfig( )");
    }

    @Override
    public void onUpdatePolicy(Map<String, Object> policyValues) {
        Log.d(TAG, "onUpdatePolicy( )");
    }

    @Override
    public void onUpdateServices() {
        Log.d(TAG, "onUpdateServices( )");
    }

    @Override
    public void onUpdateEntitlements() {
        Log.d(TAG, "onUpdateEntitlements( )");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_bring_to_front:
                actionBringToFront();
                break;
            case R.id.action_send_greetings:
                actionSendGreetings();
                break;
            case R.id.action_send_greeting_with_attachments:
                actionSendGreetingWithAttachments();
                break;
            case R.id.action_bob:
                actionBob();
                break;
            case R.id.action_xavier:
                actionXavier();
                break;
            case R.id.action_open_fingerprint_ui:
                actionOpenFingerprintUI();
                break;
            case R.id.action_request_permissions:
                requestPermissions();
                break;
            default:
                break;
        }
    }

    private void actionBringToFront() {
        Log.d(TAG, "+ bringToFront");
        try {
            GDServiceClient.bringToFront(_greetingServerId);
        } catch (GDServiceException e) {
            displayMessage("Error", e.getMessage());
        }
        Log.d(TAG, "- bringToFront");
    }

    private void actionSendGreetings() {
        Log.d(TAG, "+ sendGreeting");
        _startTime = System.currentTimeMillis();
        _sendGreeting();
        setStatus("Waiting...");
        Log.d(TAG, "- sendGreeting");
    }

    private void actionSendGreetingWithAttachments() {
        Log.d(TAG, "+ sendGreetingWithAttachments");
        _startTime = System.currentTimeMillis();
        _sendGreetingWithAttachments();
        setStatus("Waiting...");
        Log.d(TAG, "- sendGreetingWithAttachments");
    }

    private void actionXavier() {
        Log.d(TAG, "+ getXavierAge");
        _startTime = System.currentTimeMillis();
        _getAgeOfEmployeeWithName("xavier");
        setStatus("Waiting...");
        Log.d(TAG, "- getXavierAge");
    }

    private void actionBob() {
        Log.d(TAG, "+ getBobsAge");
        _startTime = System.currentTimeMillis();
        _getAgeOfEmployeeWithName("bob");
        setStatus("Waiting...");
        Log.d(TAG, "- getBobsAge");
    }

    private void actionOpenFingerprintUI() {
        GDAndroid.getInstance().openFingerprintSettingsUI();
    }

    private void logAuthDelegate() {
        GDAuthDelegateInfo authDelInfo = GDAndroid.getInstance().getAuthDelegate();
        Log.d(TAG, "logAuthDelegate:" +
                   " delegated = " + authDelInfo.isAuthenticationDelegated() +
                   " delegate.name=" + authDelInfo.getName() +
                   " delegate.appId=" + authDelInfo.getApplicationId() +
                   " delegate.address=" + authDelInfo.getAddress());
    }
}
