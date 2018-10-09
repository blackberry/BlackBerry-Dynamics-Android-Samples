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


package com.example.blackberry.sensordemo.controllers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import com.example.blackberry.sensordemo.broadcastreceivers.DataModelBroadcastReceiver;
import com.example.blackberry.sensordemo.models.AppPolicy;
import com.example.blackberry.sensordemo.models.DataModel;
import com.example.blackberry.sensordemo.services.BEMSUploaderService;
import com.example.blackberry.sensordemo.services.DynamicsMqttService;
import com.example.blackberry.sensordemo.services.LogFileService;
import com.example.blackberry.sensordemo.services.SensorMonitorService;
import com.good.gd.GDAndroid;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


public class ServiceController implements AutoCloseable {

    private static final String TAG = ServiceController.class.getSimpleName();

    private MqttBroadcastReceiver mqttBroadCastReceiver;
    private DataModelBroadcastReceiver dataModelBroadcastReceiver;


    private Handler logFileServiceHandler;
    private Handler bemsUploaderServiceHandler;
    private Handler mqttPublishServiceHandler;


    private Context context;
    public void setContext(Context value) {
        context = value;
    }


    private static ServiceController instance;

    private ServiceController() {
        super();
    }

    public static ServiceController sharedInstance() {
        if(ServiceController.instance == null)
        {
            ServiceController.instance = new ServiceController();
        }
        return ServiceController.instance;
    }


    @Override
    public void close() throws Exception {
        stopMonitoringSensors();
        context.unregisterReceiver(mqttBroadCastReceiver);

        unscheduleLogFileService();
        unscheduleBEMSUploaderService();
    }


    //Service Controller Methods
    public void startMonitoringSensors() {
        Intent intent = new Intent(context, SensorMonitorService.class);
        intent.setAction(SensorMonitorService.ACTION_START_MONITORING);
        context.startForegroundService(intent);

        registerDataModelBroadcastReceiver();
        scheduleLogFileService();
        scheduleBEMSUploaderService();
    }

    public void stopMonitoringSensors() {
        Intent intent = new Intent(context, SensorMonitorService.class);
        intent.setAction(SensorMonitorService.ACTION_STOP_MONITORING);
        context.startForegroundService(intent);
    }

    public void configureAndStartMQTTService(String mqttHost, int mqttPort) {
        //validate that MQTT details are in the AppPolicy, and connect service
        if(mqttHost.length() > 0 && mqttPort > 0){
            registerMqttBroadcastReceiver();
            Intent intent = new Intent(context, DynamicsMqttService.class);
            intent.setAction(DynamicsMqttService.ACTION_CONNECT);
            intent.putExtra(DynamicsMqttService.EXTRA_HOST, mqttHost);
            intent.putExtra(DynamicsMqttService.EXTRA_PORT, mqttPort);
            context.startForegroundService(intent);
        }
    }


    private void registerMqttBroadcastReceiver() {
        mqttBroadCastReceiver = new MqttBroadcastReceiver();
        try
        {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(DynamicsMqttService.BROADCAST_ACTION_READY);
            context.registerReceiver(mqttBroadCastReceiver, intentFilter);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void registerDataModelBroadcastReceiver() {
        dataModelBroadcastReceiver = new DataModelBroadcastReceiver();
        try
        {
            IntentFilter intentFilter = new IntentFilter();
            //intentFilter.addAction(DataModel.BROADCAST_ACTION_BMX250_TEMPERATURE_UPDATED);
            intentFilter.addAction(DataModel.BROADCAST_ACTION_BMX250_PRESSURE_UPDATED);
            //intentFilter.addAction(DataModel.BROADCAST_ACTION_BCM2385_TEMPERATURE_UPDATED);
            intentFilter.addAction(DataModel.BROADCAST_ACTION_AIR_TEMPERATURE_UPDATED);
            context.registerReceiver(dataModelBroadcastReceiver, intentFilter);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void scheduleMqttPublish() {
        mqttPublishServiceHandler = new Handler();
        mqttPublishServiceHandler.postDelayed( mqttPublishService, AppPolicy.sharedInstance().getMqttPublishingInterval());
    }


    private void unscheduleMqttPublish() {
        if(mqttPublishServiceHandler != null) {
            mqttPublishServiceHandler.removeCallbacks(mqttPublishService);
        }
    }

    Runnable mqttPublishService = new Runnable() {
        @Override
        public void run() {
            try {

                Map<String, Object> applicationConfig = GDAndroid.getInstance().getApplicationConfig();
                String roomName = (applicationConfig.containsKey(GDAndroid.GDAppConfigKeyUserPrincipalName) ? applicationConfig.get(GDAndroid.GDAppConfigKeyUserPrincipalName).toString() : "");

                //remove the email domain from the string
                if(roomName.length() > 0 && roomName.contains("@")) {
                    int atPos = roomName.indexOf("@");
                    roomName = roomName.substring(0, atPos);
                }

                String airTemperatureTopic = AppPolicy.sharedInstance().getMqttTopic() + "/airTemperature/" + roomName;

                //Tidy up topic in case of any double //
                airTemperatureTopic = airTemperatureTopic.replaceAll("//", "/");

                //Publish the Air Temperature over MQTT + Dynamics
                Intent airTempIntent = new Intent(context, DynamicsMqttService.class);
                airTempIntent.setAction(DynamicsMqttService.ACTION_MESSAGE);
                airTempIntent.putExtra(DynamicsMqttService.EXTRA_TOPIC, airTemperatureTopic);
                airTempIntent.putExtra(DynamicsMqttService.EXTRA_VALUE, String.valueOf(DataModel.sharedInstance().getAirTemperature()));
                context.startForegroundService(airTempIntent);

                String airPressureTopic = AppPolicy.sharedInstance().getMqttTopic() + "/airPressure/" + roomName;

                //Tidy up topic in case of any double //
                airPressureTopic = airPressureTopic.replaceAll("//","/");

                //Publish the Air Pressure over MQTT + Dynamics
                Intent airPressureIntent = new Intent(context, DynamicsMqttService.class);
                airPressureIntent.setAction(DynamicsMqttService.ACTION_MESSAGE);
                airPressureIntent.putExtra(DynamicsMqttService.EXTRA_TOPIC, airPressureTopic);
                airPressureIntent.putExtra(DynamicsMqttService.EXTRA_VALUE, String.valueOf(DataModel.sharedInstance().getBmx250Pressure()));
                context.startForegroundService(airPressureIntent);




            } finally {
                mqttPublishServiceHandler.postDelayed( mqttPublishService, AppPolicy.sharedInstance().getMqttPublishingInterval());
            }
        }
    };

    private void scheduleLogFileService() {
        logFileServiceHandler = new Handler();
        logFileServiceHandler.postDelayed( logFileService, AppPolicy.sharedInstance().getLocalLoggingInterval());
    }

    private void unscheduleLogFileService() {
        logFileServiceHandler.removeCallbacks(logFileService);
    }

    Runnable logFileService = new Runnable() {
        @Override
        public void run() {
            try {
                Date timeStamp = new Date();
                DateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("timeStamp", timeStampFormat.format(timeStamp));
                jsonObject.put("cpuTemperature", DataModel.sharedInstance().getBcm2385Temperature());
                jsonObject.put("rainbowHatTemperature", DataModel.sharedInstance().getBmx250Temperature());
                jsonObject.put("airTemperature", DataModel.sharedInstance().getAirTemperature());
                jsonObject.put("rainbowHatPressure", DataModel.sharedInstance().getBmx250Pressure());

                String logText = jsonObject.toString() + "\r\n";

                Intent intent = new Intent(context, LogFileService.class);
                intent.setAction(LogFileService.ACTION_LOG);
                intent.putExtra(LogFileService.EXTRA_LOCAL_FILE, AppPolicy.sharedInstance().getLocalFilePath());
                intent.putExtra(LogFileService.EXTRA_TEXT, logText);
                intent.putExtra(LogFileService.EXTRA_LOCAL_MAX_FILE_SIZE, AppPolicy.sharedInstance().getLocalMaxFileSize());

                context.startService(intent);
            } catch( JSONException e) {

            } finally {
                logFileServiceHandler.postDelayed( logFileService, AppPolicy.sharedInstance().getLocalLoggingInterval());
            }
        }
    };

    private void scheduleBEMSUploaderService() {
        bemsUploaderServiceHandler = new Handler();
        bemsUploaderServiceHandler.postDelayed( bemsUploaderService, AppPolicy.sharedInstance().getRemoteUploadInterval());
    }

    private void unscheduleBEMSUploaderService() {
        bemsUploaderServiceHandler.removeCallbacks( bemsUploaderService );
    }

    Runnable bemsUploaderService = new Runnable() {
        @Override
        public void run() {
            try {
                Intent intent = new Intent(context, BEMSUploaderService.class);
                intent.setAction(BEMSUploaderService.ACTION_UPLOAD);
                intent.putExtra(BEMSUploaderService.EXTRA_LOCAL_FILE, AppPolicy.sharedInstance().getLocalFilePath());
                intent.putExtra(BEMSUploaderService.EXTRA_REMOTE_FILE, AppPolicy.sharedInstance().getRemoteFilePath());

                context.startService(intent);
            } finally {
                bemsUploaderServiceHandler.postDelayed( bemsUploaderService, AppPolicy.sharedInstance().getRemoteUploadInterval());
            }
        }
    };


    private class MqttBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            try
            {
                Log.d(TAG, "MqttBroadcastReceiver onReceive() called");

                if(intent.getAction() == DynamicsMqttService.BROADCAST_ACTION_READY) {
                  ServiceController.this.unscheduleMqttPublish();
                  ServiceController.this.scheduleMqttPublish();

                }

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

    }



}
