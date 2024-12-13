/* Copyright (c) 2018 BlackBerry Ltd.
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


package com.example.blackberry.sensordemo;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.blackberry.sensordemo.controllers.ServiceController;
import com.example.blackberry.sensordemo.helpers.CredentialsHelper;
import com.example.blackberry.sensordemo.models.AppPolicy;
import com.example.blackberry.sensordemo.models.DataModel;
import com.example.blackberry.sensordemo.services.SensorMonitorService;
import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;

import java.util.Map;


public class MainActivity extends Activity implements GDStateListener, ConnectivityManager.OnNetworkActiveListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ServiceController serviceController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //WakeLock - added to ensure that the sensor and services are alwaysOn and are not subject to Power Management
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "com.example.blackberry.sensordemo::WakeLock");
        wakeLock.acquire();


        //initialise DataModel and pass the context
        DataModel.sharedInstance().setContext(this);

        //Initialize the ServiceController and pass the context
        serviceController = ServiceController.sharedInstance();
        serviceController.setContext(this);


        //Verify the "thing" has a network connection, if not wait for one...
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        if(connectivityManager.isDefaultNetworkActive()) {
            Network defaultNetwork = connectivityManager.getActiveNetwork();
            NetworkInfo defaultNetworkInfo = connectivityManager.getNetworkInfo(defaultNetwork);

            if(defaultNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI && defaultNetworkInfo.isAvailable() && defaultNetworkInfo.isConnected()) {

                this.authorize();

            }
        } else {
            connectivityManager.addDefaultNetworkActiveListener(this);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        serviceController.stopMonitoringSensors();
    }


    //------------------------------------------------------------
    //OnNetworkActiveListener Implementation
    //------------------------------------------------------------

    @Override
    public void onNetworkActive() {
        Log.i(TAG, "onNetworkActive");
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if(connectivityManager.isDefaultNetworkActive()) {
            Network defaultNetwork = connectivityManager.getActiveNetwork();
            NetworkInfo defaultNetworkInfo = connectivityManager.getNetworkInfo(defaultNetwork);

            if(defaultNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI && defaultNetworkInfo.isAvailable() && defaultNetworkInfo.isConnected()) {

                connectivityManager.removeDefaultNetworkActiveListener(this);
                this.authorize();

            }
        }
    }

    //------------------------------------------------------------
    //BlackBerry Dynamics Authorization
    //------------------------------------------------------------

    private void authorize() {
        Log.i(TAG, "authorize");
        //BlackBerry Dynamics Indirect Authorization Initiation
        //If the Application is already activated then initialize, otherwise programmatically Activate with an access key
        if(GDAndroid.getInstance().isActivated(this)) {
            GDAndroid.getInstance().activityInit(this);
        }
        else {
            CredentialsHelper credentialsHelper = new CredentialsHelper(this);
            GDAndroid.getInstance().programmaticActivityInit(this,credentialsHelper.getActivationCredentials().getEmail(), credentialsHelper.getActivationCredentials().getAccessKey());
        }
    }

    //------------------------------------------------------------
    //BlackBerry Dynamics GDStateListener Implementation
    //------------------------------------------------------------
    @Override
    public void onAuthorized() {
        Log.i(TAG, "onAuthorized");
        AppPolicy.sharedInstance().setPolicy(GDAndroid.getInstance().getApplicationPolicy());

        serviceController.startMonitoringSensors();

        serviceController.configureAndStartMQTTService(AppPolicy.sharedInstance().getMQTTHost(),
                                                        AppPolicy.sharedInstance().getMqttPort());
    }

    @Override
    public void onLocked() {

    }

    @Override
    public void onWiped() {

    }

    @Override
    public void onUpdateConfig(Map<String, Object> map) {

    }

    @Override
    public void onUpdatePolicy(Map<String, Object> map) {
        Log.i(TAG, "onUpdatePolicy");
        AppPolicy.sharedInstance().setPolicy(GDAndroid.getInstance().getApplicationPolicy());

        //serviceController.startMonitoringSensors();
        serviceController.configureAndStartMQTTService(AppPolicy.sharedInstance().getMQTTHost(),
                                                                            AppPolicy.sharedInstance().getMqttPort());
    }

    @Override
    public void onUpdateServices() {

    }

    @Override
    public void onUpdateEntitlements() {

    }


    //------------------------------------------------------------
}
