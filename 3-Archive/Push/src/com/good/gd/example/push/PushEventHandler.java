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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PatternMatcher;
import android.util.Log;

import com.good.gd.GDAndroid;
import com.good.gd.push.PushChannel;
/** 
 * PushEventHandler - a handler which is responsible for communicating in both directions
 * with the PushChannel. This example code demonstrates usage of a single
 * Push Channel but an application may open as many as required.
 */
class PushEventHandler {

	// Singleton so this can manage the relationship with the single connection and channel
	private static PushEventHandler _instance = null;
	static synchronized PushEventHandler getInstance() {
		if (_instance == null) {
			_instance = new PushEventHandler();
		}
		return _instance;
	}
	
	// Store members which represent the channel and the token
	// for the channel. It is possible for an app to open as many channels
	// as it requires, this example just shows one.
	private PushChannel    _channel    = null;
	private String         _token      = null;

	private PushEventHandler() {
	}

	private final BroadcastReceiver _localEventReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("PushEventHandler", "onReceive: event type = " + PushChannel.getEventType(intent));

			if (_channel != null) {
				//Channel state corresponds to the event type
				Log.i("PushEventHandler", "onReceive: channel state = " + _channel.getState());
			}

			switch (PushChannel.getEventType(intent)) {
				case Open:
					_token = PushChannel.getToken(intent);
					break;
				case Close:
					_token = null;

					// Unregister this receiver, since push channel is closed
					GDAndroid.getInstance().unregisterReceiver(_localEventReceiver);
					break;
			}
		}
	};
	
	/* Push Channel */

	private void registerLocalReceiver() {
		// IntentFilter can be set up manually by specifying Intent action, data scheme,
		// data authority and data path.
		IntentFilter intentFilter = new IntentFilter(PushChannel.GD_PUSH_CHANNEL_EVENT_ACTION);
		intentFilter.addDataScheme(_channel.getDataScheme());
		intentFilter.addDataAuthority(_channel.getDataAuthority(), null);
		intentFilter.addDataPath(_channel.getDataPath(), PatternMatcher.PATTERN_LITERAL);

		//Register PushEventHandler local receiver
		GDAndroid.getInstance().registerReceiver(_localEventReceiver, intentFilter);
	}
	
	/** connectChannel - open the channel, creating it if necessary */
	public PushChannel connectChannel() {
		if (_channel == null) {
			_channel = new PushChannel("com.good.sampleapp");
			registerLocalReceiver();
		}
		_channel.connect();

		return _channel;
	}
	
	/** disconnectChannel - close the channel if it exists, otherwise no-op */
	public void disconnectChannel() {
		if (_channel != null) {
			_channel.disconnect();
			_channel = null;
		}
	}
	
	/** isChannelConnected - returns whether a valid channel is open */
	public boolean isChannelConnected() {
		return (_channel != null && _token != null);
	}

	/** sendLoopbackMessage - triggers a message to be sent to the currently open
	 * Push Channel. THis function exists purely for demonstration purposes. In
	 * a normal scenario this message will be sent by an application server.
	 */
	public void sendLoopbackMessage(String message, String nocServerURL) {
		LoopbackMessage.send(message,  _token, nocServerURL);
	}
}