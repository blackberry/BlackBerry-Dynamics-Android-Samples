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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.widget.GDTextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import android.net.Uri;
import android.provider.Settings;

import static com.good.example.sdk.bypassunlock.EventReceiver.BLOCK_ID;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements GDStateListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final long TEN_SECONDS = 10000;

    private final static int REQUEST_CODE = 10101;

    private void checkDrawOverlayPermission() {

        // Checks if app already has permission to draw overlays
        if (!Settings.canDrawOverlays(this)) {

            // If not, form up an Intent to launch the permission request
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));

            // Launch the Intent, with the supplied request code
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_bar_action: {
                Intent intent = new Intent();
                intent.setClass(this, ContactsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.action_block_for_10_seconds: {
                actionBlockFor10Seconds();
                break;
            }
            case R.id.action_block_until_broadcast: {
                actionBlockUntilBroadcast();
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GDAndroid.getInstance().activityInit(this);

        setContentView(R.layout.activity_main);

        GDTextView textView = findViewById(R.id.text_introduction);
        String styledText;
        try {
            InputStream is = getAssets().open("Introduction.html");

            int size = is.available();

            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            // Convert the buffer into a string.
            styledText = new String(buffer);

        } catch (IOException e) {
            e.printStackTrace();
            styledText = "Cannot load introduction HTML file!";
        }
        //fromHtml is deprecated from API level 24 and later. Retaining deprecated fromHtml() to support lower API levels.
        textView.setText(Html.fromHtml(styledText, Html.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE);

        View action = findViewById(R.id.app_bar_action);
        action.setOnClickListener(view -> {
            onClick(action);
        });

        View mainView = findViewById(R.id.bbd_bypass_unlock_UI);
        View contentView = findViewById(R.id.content_layout);

        SampleAppActivityUtils.setupAppBar(mainView, getString(R.string.app_name), true);
        SampleAppActivityUtils.adjustViewsIfEdgeToEdgeMode(mainView, null, contentView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!Settings.canDrawOverlays(this)) {
            // Check if user has granted permission, prompt otherwise
            checkDrawOverlayPermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        Log.i(TAG, "onActivityResult");

        super.onActivityResult(requestCode, resultCode, data);

        // Check if the received request code matches with what we have provided for the overlay draw request
        if (requestCode == REQUEST_CODE) {

            // Double-check that the user granted it (didn't just dismiss the request)
            if (!Settings.canDrawOverlays(this)) {
                Log.i(TAG, "Sorry. Can't draw overlays without permission...");
            }
        }
    }

    /*
     * Activity specific implementation of GDStateListener.
     *
     * If a singleton event Listener is set by the application (as it is in this case) then setting
     * Activity specific implementations of GDStateListener is optional
     */
    @Override
    public void onAuthorized() {
        //If Activity specific GDStateListener is set then its onAuthorized( ) method is called when
        //the activity is started if the App is already authorized
        Log.i(TAG, "onAuthorized()");

        GDTextView textPolicy = findViewById(R.id.text_policy);
        if(textPolicy != null) {
            textPolicy.setText(GDAndroid.getInstance().getApplicationPolicyString());
        }
    }

    @Override
    public void onLocked() {
        Log.i(TAG, "onLocked()");
    }

    @Override
    public void onWiped() {
        Log.i(TAG, "onWiped()");
    }

    @Override
    public void onUpdateConfig(Map<String, Object> settings) {
        Log.i(TAG, "onUpdateConfig()");
    }

    @Override
    public void onUpdatePolicy(Map<String, Object> policyValues) {
        GDTextView textPolicy = findViewById(R.id.text_policy);
        if(textPolicy != null) {
            textPolicy.setText(policyValues.toString());
        }
    }

    @Override
    public void onUpdateServices() {
        Log.i(TAG, "onUpdateServices()");
    }

    @Override
    public void onUpdateEntitlements() {
        Log.i(TAG, "onUpdateEntitlements()");
    }

    private void actionBlockUntilBroadcast() {
        GDAndroid.executeBlock(BLOCK_ID, "Local block", "Your application is blocked locally. Please use the same block id and send broadcast message with action INTENT_ACTION_UNBLOCK to unblock it.\n" +
                "You can run 'adb shell am broadcast -n com.good.example.sdk.bypassunlock/.EventReceiver -a INTENT_ACTION_UNBLOCK");
    }

    private void actionBlockFor10Seconds() {
        GDAndroid.executeBlock(BLOCK_ID, "Local block", "Your application is blocked locally. Please wait some time to unblock application.");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                GDAndroid.executeUnblock(BLOCK_ID);
            }
        }, TEN_SECONDS);
    }
}
