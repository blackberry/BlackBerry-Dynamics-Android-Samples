/* Copyright 2024 BlackBerry Ltd.
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

package com.good.gd.example.gdinteraction;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.good.gd.GDAndroid;
import com.good.gd.GDAuthDelegateInfo;
import com.good.gd.GDStateListener;
import com.good.gd.error.GDError;

import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * GDInteraction activity; shows all incoming events, plus internal events like onPause,
 * onResume, and when buttons are clicked.
 */
public class GDInteraction extends SampleAppActivity implements GDStateListener, OnClickListener {

    public static final String TAG = "GDInteraction";

	private static final String fileReadheading = "Reading package manager log\n";
	private static final String fileContentCleared = "Package manager log cleared\n";
	private static final long TEN_SECONDS = 10000;
	public  static final String BLOCK_ID = "BLOCK_ID";
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 113;

	private TextView statusTextView;
	private ScrollView scroller;
	private final GDEventHandler gdEventHandler;
	private int numEventsLogged = 0;
	private boolean notificationPermissionRequested = false;

	public GDInteraction() {
		super();
		gdEventHandler = GDEventHandler.getInstance();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		setupAppBar(getString(R.string.app_name));

		// link to the various elements of the main view

		statusTextView = findViewById(R.id.gdinteraction_status_view);
		scroller = findViewById(R.id.gdinteraction_scroller);
		logStatus("onCreate: Calling canAuthorizeAutonomously before activityInit");
		if (GDAndroid.getInstance().canAuthorizeAutonomously(this)) {
			logStatus("canAuthorizeAutonomously TRUE");
		} else {
			logStatus("canAuthorizeAutonomously FALSE");
		}

		GDAndroid.getInstance().activityInit(this);
		GDEventHandler.getInstance().setGDInteractionActivity(this);

		View mainView = findViewById(R.id.main_layout);
		View bottomBar = findViewById(R.id.bottom_bar);

		adjustViewsIfEdgeToEdgeMode(mainView, bottomBar, statusTextView);
	}

	@Override
	protected void onPause() {
		super.onPause();

		logStatus("onPause()");
		logBlankLine();
	}

	@Override
	public void onResume() {
		super.onResume();

		logStatus("onResume()");
		logBlankLine();
		logAuthDelegate();

		updateFromEventHandler();

        checkNotificationPermission();
	}

    private void checkNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return;
        }

        if (!notificationPermissionRequested &&
			ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) != PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                        this,
                        new String[]{POST_NOTIFICATIONS},
                        REQUEST_CODE_POST_NOTIFICATIONS);

			notificationPermissionRequested = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean isGranted = grantResults.length > 0
                            && grantResults[0] == PERMISSION_GRANTED;

        Log.d(TAG, "onRequestPermissionsResult: isGranted = " + isGranted);
    }

	// Ask the GDEventHandler for all the events it has ever received, and log
	// all those
	// not already logged. This means that if the Activity is restarted, it will
	// correctly
	// log everything from when the app was started.
	@SuppressWarnings("unchecked")
	void updateFromEventHandler() {
		for (; numEventsLogged < gdEventHandler.eventCount(); numEventsLogged++) {
            Pair<String, List<String>> eventRecord = gdEventHandler.eventAtIndex(numEventsLogged);
            for (String s : eventRecord.second) {
                logStatus(eventRecord.first, s);
            }
			logBlankLine();
		}
	}

	// Write the specified message to the on-screen log (with timestamp), and
	// also to the android log
	private void logStatus(SpannableString message) {
		if (statusTextView != null) {
			statusTextView.append(message);
			scroller.post(new Runnable() {
				@Override
				public void run() {
					scroller.fullScroll(View.FOCUS_DOWN);
				}
			});
		}
		Log.v(TAG, message + "\n");
	}

	// Write the specified message to the on-screen log (with timestamp), and
	// also to the android log
	private void logStatus(String message) {
        if (message.length() > 0) {
            logStatus(gdEventHandler.timeNowString(), message);
        } else {
            logBlankLine();
        }
    }

	private void logStatus(String timestamp, String message) {
		if (statusTextView != null) {
			statusTextView.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.bbd_white));
			statusTextView.append(timestamp + " " + message + "\n");
			scroller.post(new Runnable() {
				@Override
				public void run() {
					scroller.fullScroll(View.FOCUS_DOWN);
				}
			});
		}
		Log.v(TAG, message + "\n");
	}

    private void logBlankLine() {
		if (statusTextView != null) {
            statusTextView.append("\n");
        }
    }

	@Override
	public void onAuthorized() {
		logStatus("GDStateListener.onAuthorized canAuthorizeAutonomously = " +
                    GDAndroid.getInstance().canAuthorizeAutonomously(this.getApplicationContext()));
	}

	@Override
	public void onLocked() {
		logStatus("GDStateListener.onLocked - GD APIs can be used but user interaction can't be used");
	}

	@Override
	public void onWiped() {
		logStatus("GDStateListener.onWiped - GD APIs can't be used");
	}

	@Override
	public void onUpdatePolicy(Map<String, Object> policyValues) {
		logStatus("GDStateListener.onUpdatePolicy - New Policy Settings received");
		for (String key : policyValues.keySet()) {
			logStatus("  " + key + "=" + policyValues.get(key));
		}
	}

	@Override
	public void onUpdateServices() {
		logStatus("GDStateListener.onUpdateServices");
	}

	@Override
	public void onUpdateConfig(Map<String, Object> settings) {
		logStatus("GDStateListener.onUpdateConfig - New App Config received");
		for (String key : settings.keySet()) {
			logStatus("  " + key + "=" + settings.get(key));
		}
	}

    @Override
    public void onUpdateEntitlements() {
        logStatus("GDStateListener.onUpdateEntitlements");
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.action_block_for_10_seconds:
			actionBlockFor10Seconds();
			break;
		case R.id.action_block_until_broadcast:
			actionBlockUntilBroadcast();
			break;
		case R.id.action_getfilecontent:
			actionBackgroundFileContent();
			break;
		case R.id.action_clearfilecontent:
			actionClearFileContent();
			break;
		case R.id.action_getappconfig:
			actionGetAppConfig();
			break;
		case R.id.action_change_password:
			actionChangePassword();
			break;
		case R.id.action_quit:
			logStatus("Quit pressed; Calling finish()");
			finish();
		default:
			break;
		}
	}

	private void actionBlockUntilBroadcast() {
		logStatus("Calling GDAndroid.executeBlock()");
		GDAndroid.executeBlock(BLOCK_ID, "Local block", "Your application is blocked locally. Please use the same block id and send broadcast message with action INTENT_ACTION_UNBLOCK to unblock it.\n" +
				"You can run 'adb shell am broadcast -n com.good.gd.example.gdinteraction/.EventReceiver -a INTENT_ACTION_UNBLOCK'");
	}

	private void actionBlockFor10Seconds() {
		logStatus("Calling GDAndroid.executeBlock()");
		GDAndroid.executeBlock(BLOCK_ID, "Local block", "Your application is blocked locally. Please wait some time to unblock application.");
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				logStatus("Calling GDAndroid.executeUnblock()");
				GDAndroid.executeUnblock(BLOCK_ID);
			}
		}, TEN_SECONDS);
	}

	private void actionGetAppConfig() {
		logStatus("Calling GDAndroid.getApplicationConfig()");
		try {
			Map<String, Object> settings = GDAndroid.getInstance()
					.getApplicationConfig();
			logStatus("Values returned by getApplicationConfig:");
			for (String key : settings.keySet()) {
				logStatus("  " + key + "=" + settings.get(key));
			}
            logBlankLine();

            logAuthDelegate();

		} catch (GDError e) {
			logStatus("Error thrown by getApplicationConfig() call: " + e);
		}
	}

    private void logAuthDelegate() {
        GDAuthDelegateInfo authDelegateInfo = GDAndroid.getInstance().getAuthDelegate();
        logStatus("Values returned by getAuthDelegate:");
        logStatus("  delegated=" + authDelegateInfo.isAuthenticationDelegated());
        logStatus("  name=" + authDelegateInfo.getName());
        logStatus("  appId=" + authDelegateInfo.getApplicationId());
        logStatus("  address=" + authDelegateInfo.getAddress());
        logBlankLine();
    }

	private void actionChangePassword() {
		logStatus("Calling GDAndroid.openChangePasswordUI()");
		try {
			if (!GDAndroid.getInstance().openChangePasswordUI()) {
                logStatus("openChangePasswordUI() returned false, auth delegated");
            }
            logAuthDelegate();
		} catch (GDError e) {
			logStatus("Error thrown by openChangePasswordUI() call: " + e);
		}
	}

	void actionBackgroundFileContent() {
		logBlankLine();
		logBlankLine();
		logStatus(prepareSpannableHeading(fileReadheading));
		logBlankLine();
		//Read data from the background task file.
		String data = FileUtils.readDataFromFile();
		if (data.length() == 0) {
			logStatus("File Empty \n");
		} else {
			SpannableString spanData = new SpannableString(data);
			spanData.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getBaseContext(), R.color.bbd_blue)),0,data.length(),0);
			logStatus(spanData);
		}
		logBlankLine();
	}

	private void actionClearFileContent() {
		FileUtils.clearFileData();
        statusTextView.setText("");
		logBlankLine();
		logStatus(prepareSpannableHeading(fileContentCleared));
	}

	private SpannableString prepareSpannableHeading (String st) {
		SpannableString heading = new SpannableString(st);
		heading.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getBaseContext(), R.color.bbd_button_green)),0,st.length(),0);
		heading.setSpan(new RelativeSizeSpan(1.25f),0,st.length(),0);
		return heading;
	}
}
