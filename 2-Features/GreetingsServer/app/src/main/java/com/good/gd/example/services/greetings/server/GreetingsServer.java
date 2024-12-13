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

package com.good.gd.example.services.greetings.server;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.good.gd.GDAndroid;
import com.good.gd.icc.GDService;
import com.good.gd.icc.GDServiceException;

/**
 * This is the main application activity.
 */
public class GreetingsServer extends SampleAppActivity
                             implements BottomNavigationView.OnNavigationItemSelectedListener {

	private static final String TAG = GreetingsServer.class.getSimpleName();

    private static final int PERMISSION_REQUEST = 1988;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		GDAndroid.getInstance().activityInit(this);
		setContentView(R.layout.main);

        setupAppBar(getString(R.string.app_name));

		GreetingsServerApplication app = (GreetingsServerApplication)getApplicationContext();
		app.setCurrentActivity(this);

		// BottomNavigationView wants icons to be monochrome, turn that off
		BottomNavigationView bottomBar = findViewById(R.id.bottom_navigation);
		bottomBar.setItemIconTintList(null);

        // ... and add ourselves as the selection handler, so we can handle menu actions
		bottomBar.setOnNavigationItemSelectedListener(this);

        View mainView = findViewById(R.id.main_layout);
        View contentView = findViewById(R.id.bbd_greetings_server_UI);

        adjustViewsIfEdgeToEdgeMode(mainView, bottomBar, contentView);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		GreetingsServerApplication app = (GreetingsServerApplication) getApplicationContext();
		app.setCurrentActivity(null);
	}

	@Override
	public void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
	}

	void showAuthorizedUI() {
		TextView t = findViewById(R.id.authStatus);
		t.setText(R.string.authorized);
		t.setTextColor(getColor(R.color.auth_color));
	}

	void showNotAuthorizedUI() {
		TextView t = findViewById(R.id.authStatus);
		t.setText(R.string.not_authorized);
		t.setTextColor(getColor(R.color.not_auth_color));
	}

	private void displayMessage(String title, String message) {
		Log.d(TAG, "displayMessage title: " + title + " message:" + message);
		final Builder alert =
            new AlertDialog.Builder(this)
                .setTitle(title)
				.setMessage(message)
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int value) {
						// Just dismiss the dialog
					}
				});

		this.runOnUiThread(new Runnable() {
			public void run() {
				alert.show();
			}
		});
	}

    // BottomNavigationView.OnNavigationItemSelectedListener implementation
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_bring_client_to_front:
                Log.d(TAG, "+ bringToFront");
                try {
                    GDService.bringToFront("com.good.gd.example.services.greetings.client");
                    return true;
                } catch (GDServiceException e) {
                    displayMessage("Error", e.getMessage());
                }
                Log.d(TAG, "- bringToFront");
            break;
            case R.id.action_request_notifications:
                requestPermissions();
                break;
        }
        return false;
    }
    
    private void requestPermissions() {
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
}
