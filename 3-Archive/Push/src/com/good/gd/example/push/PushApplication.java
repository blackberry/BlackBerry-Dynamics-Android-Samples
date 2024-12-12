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

import java.util.Map;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

public class PushApplication extends Application implements GDStateListener {

	private PushEventHandler _pushEventHandler;
	private PushActivityCallback _currentActivity;
	private boolean authorized = false;
	
	//Application singleton. holds the singleton Push Handler and receives GD state updates. Manages sending updates to Activities
	
	@Override
	public void onCreate() {
		super.onCreate();

        // turn off (suppress) night mode for this app
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

		GDAndroid.getInstance().applicationInit(this);
		
		GDAndroid.getInstance().setGDStateListener(this);
	}

	public void registerActivity(PushActivityCallback callback) {
	
		_currentActivity = callback;
		
		if (authorized) {
			_currentActivity.onAuthorized(_pushEventHandler);
		}
		
	}
	
	public void unregisterActivity(PushActivityCallback callback){
		
		if (_currentActivity==callback) {
			_currentActivity = null;
		}
	}
	
	@Override
	public void onAuthorized() {

		authorized = true;
		_pushEventHandler = PushEventHandler.getInstance();
		
		if (_currentActivity!=null) {
			_currentActivity.onAuthorized(_pushEventHandler);
		}
	}

	@Override
	public void onLocked() {
	}

	@Override
	public void onUpdateConfig(Map<String, Object> arg0) {
	}

	@Override
	public void onUpdatePolicy(Map<String, Object> arg0) {
	}

	@Override
	public void onUpdateServices() {
	}

    @Override
    public void onUpdateEntitlements() {
    }

	@Override
	public void onWiped() {
		authorized = false;
		if (_currentActivity!=null) {
			_currentActivity.onNotAuthorized(_pushEventHandler);
		}
	}
}
