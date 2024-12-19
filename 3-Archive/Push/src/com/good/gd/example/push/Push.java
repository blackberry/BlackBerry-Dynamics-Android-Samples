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

package com.good.gd.example.push;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.net.GDConnectivityManager;
import com.good.gd.net.GDNetworkInfo;
import com.good.gd.push.PushChannel;
import com.good.gd.widget.GDEditText;

/**
 * Push activity; shows all incoming events, plus internal events like onPause,
 * onResume, and when buttons are clicked.
 */
public class Push extends SampleAppActivity implements
		GDStateListener, OnClickListener {

	private static final String DEFAULT_NOC_SERVER_URL = "gdmdc.good.com";
	private static final String NOC_SERVER_URL_FILE = "nocServerURL.txt";
	private TextView _statusTitle;
	private TextView _statusTextView;
	private ScrollView _scroller;
	private final DateFormat _timeFormatter;

	private PushEventHandler _pushEventHandler;

	private String statusText = null;
	private final Set<BroadcastReceiver> gdConnectivityReceivers = new HashSet<>();

	public Push() {
		super();
		_timeFormatter = new SimpleDateFormat("HH:mm:ss", Locale.US);
	}

	/** onCreate - called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		GDAndroid.getInstance().activityInit(this);
		setContentView(R.layout.main);

		setupAppBar(getString(R.string.app_name));

		if (savedInstanceState != null) {
			String STATE_STATUS_TEXT = "status_text";
			statusText = savedInstanceState.getString(STATE_STATUS_TEXT);
		} else {
			// link to the various elements of the main view
			_scroller = findViewById(R.id.status_scroller);
			_statusTitle = findViewById(R.id.status_title);
			_statusTextView = findViewById(R.id.status_view);
		}

		if (statusText != null) {
			_statusTextView.setText(statusText);
		}

		View mainView = findViewById(R.id.bbd_push_UI);
		View bottomBar = findViewById(R.id.action_view_menu);

		adjustViewsIfEdgeToEdgeMode(mainView, bottomBar, _statusTextView);
	}

	/** onDestroy - called when the activity is being destroyed */
	@Override
	public void onDestroy() {
		super.onDestroy();

		// clean up receivers
		removeReceivers();
	}

    @Override
    public void onResume() {
        super.onResume();
        updateTitleAndButtons();
        logConnectionStatus();
    }

	/**
	 * Register receiver to listen to GDConnectivityManager.GD_CONNECTIVITY_ACTION intent
	 *
	 * This method is to demonstrate how to set up receiver to listen to the broadcast
	 * intent when the push connection status is changed
	 */
	private BroadcastReceiver registerReceiver(final String name) {
		BroadcastReceiver receiver;

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				GDNetworkInfo networkInfo = GDConnectivityManager.getActiveNetworkInfo();
				updateTitleAndButtons();
				logConnectionStatus();
				logStatus(name + " received GD_CONNECTIVITY_ACTION intent.");
			}
		};

		GDAndroid.getInstance().registerReceiver(receiver,
				new IntentFilter(GDConnectivityManager.GD_CONNECTIVITY_ACTION));

		return receiver;
	}

	/**
	 * Add a broadcast receiver to listen to GDConnectivityManager.GD_CONNECTIVITY_ACTION intent
	 */
	private void addReceivers() {
		gdConnectivityReceivers.add(registerReceiver("Receiver 1"));
	}

	/**
	 * Remove all broadcast receivers listening to GDConnectivityManager.GD_CONNECTIVITY_ACTION intent
	 */
	private void removeReceivers() {
		for (BroadcastReceiver receiver : gdConnectivityReceivers) {
			GDAndroid.getInstance().unregisterReceiver(receiver);
		}
		gdConnectivityReceivers.clear();

		GDAndroid.getInstance().unregisterReceiver(_channelReceiver);
	}

	private boolean isOnline() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkCapabilities netCapabilities = connectivityManager.
				getNetworkCapabilities(connectivityManager.getActiveNetwork());

		if (netCapabilities != null) {
			if (netCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
				return true;
			} else if (netCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
				return true;
			} else if (netCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
				return true;
			}
		}

		return false;
	}

	private void showErrorDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		// Customize error dialog
		builder.setPositiveButton(R.string.push_error_dialog_button_ok, null);
		builder.setTitle(R.string.push_error_dialog_title);
		builder.setMessage(R.string.push_error_dialog_message);
		builder.setIcon(android.R.drawable.ic_dialog_alert);

		// Create the AlertDialog
		builder.create().show();
	}

	/**
	 * showPushMessageDialog - shows a dialog which will capture from the user a
	 * message to be sent
	 */
	private void showPushMessageDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.push_message);
		final GDEditText input = new GDEditText(this);
		alert.setView(input);
		input.setId(R.id.pushMessage);
		alert.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
                        sendLoopbackMessage(input.getText().toString());
					}
				});
		alert.setNegativeButton(android.R.string.cancel, null);
		alert.show();
	}

    private void sendLoopbackMessage(String message) {
        // check channel status before we actually try to send the message
        if (_pushEventHandler.isChannelConnected()) {
            logStatus("send loopback message: \"" + message + "\"");
            _pushEventHandler.sendLoopbackMessage(message, readNocServerURL());
        } else {
            Toast.makeText(this, "Push Channel Not Connected", Toast.LENGTH_SHORT).show();
            logStatus("send message: push channel is not connected");
        }
    }

	/** onChannelOpen - */
	private void onChannelOpen(String token, String pushChannelHost) {
		if (pushChannelHost == null) {
			logStatus("Channel open token = " + token);
		} else {
			logStatus("Channel open token = " + token + ", host = " + pushChannelHost);
		}
		updateTitleAndButtons();
	}

	/** onChannelClose - */
	private void onChannelClose(String token) {
		logStatus("Channel close token = " + token);
		updateTitleAndButtons();
	}

	/** onChannelError - */
	private void onChannelError(int error) {
		logStatus("Channel error error = " + error);
	}

	/** onChannelMessage - */
	private void onChannelMessage(String message) {
		logStatus("Channel message = " + message);
	}

	/** onChannelPingFail - */
	private void onChannelPingFail(int error) {
		logStatus("Channel ping fail error = " + error);
	}

	/** logConnectionStatus - logs an UP or DOWN message for the push connection */
	private void logConnectionStatus() {
		GDNetworkInfo networkInfo = GDConnectivityManager.getActiveNetworkInfo();
		logStatus(networkInfo.isPushChannelAvailable()
                    ? "Push connection available"
                    : "Push connection not available");
	}

	/**
	 * logStatus - write the specified message to the on-screen log (with
	 * timestamp), and also to the android log
	 */
	private void logStatus(String message) {
		if (_statusTextView == null) {
			_statusTextView = findViewById(R.id.status_view);
		}

		String outputmessage = (message.length() > 0) ? _timeFormatter
				.format(new Date()) + " " + message + "\n\n" : "\n";
		_statusTextView.append(outputmessage);
		if (_scroller == null) {
			_scroller = findViewById(R.id.status_scroller);
		}
		_scroller.post(new Runnable() {
			@Override
			public void run() {
				_scroller.fullScroll(View.FOCUS_DOWN);
			}
		});
		Log.v("Push", message + "\n");
	}

	/**
	 * updateTitleAndButtons - used to update the button states and title text
	 * based on state
	 */
	private void updateTitleAndButtons() {
        if (_pushEventHandler != null)
        {
			GDNetworkInfo networkInfo = GDConnectivityManager.getActiveNetworkInfo();
            boolean connected = networkInfo.isPushChannelAvailable();

            if (_statusTitle == null) {
                _statusTitle = findViewById(R.id.status_title);
            }

            _statusTitle.setText(connected ? R.string.push_connected_title
                                 : R.string.push_disconnected_title);
            _statusTitle.setTextColor(connected ? Color.GREEN : Color.RED);
        }
	}

	private void registerChannelReceiver(PushChannel channel) {
		// Register Broadcast receiver to receive intents only for particular PushChannel object.
		// For this purpose get IntentFilter instance from PushChannel object
		IntentFilter intentFilter = channel.prepareIntentFilter();

		//Register PushChannel receiver.
		GDAndroid.getInstance().registerReceiver(_channelReceiver, intentFilter);
	}

	@Override
	public void onAuthorized() {

		// set this as a listener for the push connection
		_pushEventHandler = PushEventHandler.getInstance();

        updateTitleAndButtons();
        logConnectionStatus();

		// Setup receivers to monitor push connection status change
		if (gdConnectivityReceivers.isEmpty()) {
			addReceivers();
		}
	}

	@Override
	public void onLocked() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onWiped() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUpdateConfig(Map<String, Object> settings) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUpdatePolicy(Map<String, Object> policyValues) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUpdateServices() {
		// TODO Auto-generated method stub
	}

    @Override
    public void onUpdateEntitlements() {
        // TODO Auto-generated method stub
    }

	/** Broadcast receiver to get Push Channel event notifications - */
	private final BroadcastReceiver _channelReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (PushChannel.getEventType(intent)) {
				case Open:
					onChannelOpen(PushChannel.getToken(intent), PushChannel.getPushChannelHost(intent));
					break;
				case Close:
					onChannelClose(PushChannel.getToken(intent));
					// Unregister this receiver, since push channel is closed
					GDAndroid.getInstance().unregisterReceiver(_channelReceiver);
					break;
				case Error:
					onChannelError(PushChannel.getErrorCode(intent, 0));
					break;
				case Message:
					onChannelMessage(PushChannel.getMessage(intent));
					break;
				case PingFail:
					onChannelPingFail(PushChannel.getPingFailCode(intent, 0));
					break;
			}
		}
	};

	@Override
	public void onClick(View v) {
		if (!isOnline()) {
			showErrorDialog();
		}

		switch (v.getId()) {
		case R.id.action_open_channel:
			openChannelClicked();
			break;
		case R.id.action_loopback_message:
			sendMessageClicked();
			break;
		case R.id.action_close_channel:
			closeChannelClicked();
			break;
		default:
			break;
		}
	}

	private void openChannelClicked() {
		GDNetworkInfo networkInfo = GDConnectivityManager.getActiveNetworkInfo();
		boolean pushConnected = networkInfo.isPushChannelAvailable();

		boolean channelConnected = _pushEventHandler.isChannelConnected();
		if (pushConnected) {
			if (channelConnected) {
				logStatus("openChannelClicked: channel already connected");
			} else {
				PushChannel channel = _pushEventHandler.connectChannel();
				registerChannelReceiver(channel);
			}
		} else {
			logStatus("openChannelClicked: push connection is not established");
		}
	}

	private void sendMessageClicked() {
		GDNetworkInfo networkInfo = GDConnectivityManager.getActiveNetworkInfo();
		boolean pushConnected = networkInfo.isPushChannelAvailable();

		boolean channelConnected = _pushEventHandler.isChannelConnected();
		if (pushConnected) {
			if (channelConnected) {
				showPushMessageDialog();
			} else {
                Toast.makeText(this, "Push Channel Not Connected", Toast.LENGTH_SHORT).show();
				logStatus("openChannelClicked: push channel is not connected");
			}
		} else {
            Toast.makeText(this, "No Push Connection", Toast.LENGTH_SHORT).show();
			logStatus("openChannelClicked: push connection is not established");
		}
	}

	private void closeChannelClicked() {
		GDNetworkInfo networkInfo = GDConnectivityManager.getActiveNetworkInfo();
		boolean pushConnected = networkInfo.isPushChannelAvailable();

		boolean channelConnected = _pushEventHandler.isChannelConnected();
		if(pushConnected) {
			if(channelConnected) {
				_pushEventHandler.disconnectChannel();
			} else {
				logStatus("openChannelClicked: push channel already closed");
			}
		} else {
			logStatus("openChannelClicked: push connection is not established");
		}
	}

	private String readNocServerURL() {
		BufferedReader reader;
		String nocServerURL;
		try {
			reader = new BufferedReader(
					new InputStreamReader(getAssets().open(NOC_SERVER_URL_FILE)));

			nocServerURL = reader.readLine();
			reader.close();

		} catch (IOException e) {
			Log.d("Push", "Can't read nocServerURL, will use default");
			return DEFAULT_NOC_SERVER_URL;

		}

		return nocServerURL;
	}

}
