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
 * This file contains sample code that is licensed according to the BlackBerry Dynamics SDK terms and conditions.
 * Copyright 2019 BlackBerry Limited. All rights reserved.
 */

package com.good.gd.example.services.greetings.client;

import com.good.gd.GDAndroid;
import com.good.gd.icc.GDService;
import com.good.gd.icc.GDServiceException;
import com.good.gd.icc.GDServiceClient;
import com.good.gd.icc.GDServiceClientListener;
import com.good.gd.icc.GDServiceListener;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import android.util.Log;

public class GreetingsClientApplication extends Application {

	private static final String TAG = GreetingsClientApplication.class.getSimpleName();
	
	@Override
	public void onCreate() {
		super.onCreate();

        // turn off (suppress) night mode for this app
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

		GDAndroid.getInstance().applicationInit(this);
	
		GDServiceClientListener client = GreetingsClientGDServiceListener.getInstance();
		GDServiceListener server = GreetingsClientGDServiceListener.getInstance();
		
		Log.e(TAG , "GreetingsClientApplication::onCreate() service client Listener = " + client + "\n");
		
		if (client != null) {
    		//Set the Client Service Listener to get responses from server
    		try {
				GDServiceClient.setServiceClientListener(client);
				GDService.setServiceListener(server);
    		} catch (GDServiceException e) {
    			Log.e(TAG , "GreetingsClientApplication::onCreate()  Error Setting GDServiceClientListener --" + e.getMessage()  + "\n");
    		}
    	}
	}
}
