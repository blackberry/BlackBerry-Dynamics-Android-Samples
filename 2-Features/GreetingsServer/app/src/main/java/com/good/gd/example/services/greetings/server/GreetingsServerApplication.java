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

import com.good.gd.GDAndroid;
import com.good.gd.GDStateAction;
import com.good.gd.icc.GDService;
import com.good.gd.icc.GDServiceException;
import com.good.gd.icc.GDServiceListener;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.appcompat.app.AppCompatDelegate;
import android.util.Log;

public class GreetingsServerApplication extends Application {

    private static final String TAG = GreetingsServerApplication.class.getSimpleName();
    private GreetingsServer _currentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        
        // Register broadcast receiver to get GD state action updates.
        // running this before applicationInit() as a way to test the fix to GD-66335/66339
        // and because it's a good idea to mirror what a lot of applications will do
        registerGDStateReceiver();

        GDAndroid.getInstance().applicationInit(this);

        GDServiceListener serv = GreetingsServerGDServiceListener.getInstance();

        Log.e(TAG , "GreetingsServerApplication::onCreate() service Listener = " + serv + "\n");

        if(serv!=null){
            //Set the Service Listener to get requests from clients
            try {
                GDService.setServiceListener(serv);
            } catch (GDServiceException e) {
                Log.e(TAG , "GreetingsServerApplication::onCreate()  Error Setting GDServiceListener --" + e.getMessage()  + "\n");
            }
        }
    }

    void setCurrentActivity(GreetingsServer activity) {
        _currentActivity = activity;
    }

    private BroadcastReceiver _gdStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String stateAction = intent.getAction();

            // We are only interested in 2 GD state events. All other will be just logged to logcat.
            Log.d(TAG, "######################## Greetings Server onReceive " + stateAction + "\n");
            switch (stateAction) {
                case GDStateAction.GD_STATE_AUTHORIZED_ACTION :
                    if(_currentActivity != null) {
                        _currentActivity.showAuthorizedUI();
                    }
                    break;
                case GDStateAction.GD_STATE_LOCKED_ACTION:
                    if(_currentActivity != null) {
                        _currentActivity.showNotAuthorizedUI();
                    }
                    break;
            }
        }
    };

    private void registerGDStateReceiver() {
        IntentFilter intentFilter = new IntentFilter();

        // Register either all state actions or only particular one per one Broadcast receiver.
        // State action can be then received from the Broadcast Intent.
        intentFilter.addAction(GDStateAction.GD_STATE_AUTHORIZED_ACTION);
        intentFilter.addAction(GDStateAction.GD_STATE_LOCKED_ACTION);
        intentFilter.addAction(GDStateAction.GD_STATE_WIPED_ACTION);
        intentFilter.addAction(GDStateAction.GD_STATE_UPDATE_POLICY_ACTION);
        intentFilter.addAction(GDStateAction.GD_STATE_UPDATE_SERVICES_ACTION);
        intentFilter.addAction(GDStateAction.GD_STATE_UPDATE_CONFIG_ACTION);
        intentFilter.addAction(GDStateAction.GD_STATE_UPDATE_ENTITLEMENTS_ACTION);

        GDAndroid.getInstance().registerReceiver(_gdStateReceiver, intentFilter);
    }
}
