package com.example.blackberry.sensordemo.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.blackberry.sensordemo.models.DataModel;
import com.example.blackberry.sensordemo.sensordrivers.BCM2385TemperatureSensorDriver;
import com.google.android.things.contrib.driver.bmx280.Bmx280SensorDriver;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class SensorMonitorService extends Service implements SensorEventListener{

    private static final String TAG = SensorMonitorService.class.getSimpleName();

    public static final String ACTION_START_MONITORING = "ACTION_START_MONITORING";
    public static final String ACTION_STOP_MONITORING = "ACTION_STOP_MONITORING";

    private static final String I2C_BUS = "I2C1";

    private SensorManager mSensorManager;
    private SensorCallback mCallback = new SensorCallback();

    private Bmx280SensorDriver mBmx280SensorDriver;
    private BCM2385TemperatureSensorDriver mBcm2385TemperatureSensorDriver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        //promote the service to foreground in the hope this will run

        String CHANNEL_ID = "sensorMonitor";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "SensorMonitorService NotificationChannel",
                NotificationManager.IMPORTANCE_DEFAULT);

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                                                    .setContentTitle("Sensor Monitor Service")
                                                    .setContentText("This service monitors the various sensors in the project and broadcasts readings")
                                                    .setSmallIcon(com.good.gd.R.drawable.bbd_logo)
                                                    .build();


        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");

        if(intent.getAction() == ACTION_START_MONITORING ) {
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mSensorManager.registerDynamicSensorCallback(mCallback);
            setupSensors();
        } else if( intent.getAction() == ACTION_STOP_MONITORING ) {

        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        destroySensors();
        mSensorManager.unregisterListener(this);
        mSensorManager.unregisterDynamicSensorCallback(mCallback);
    }

    //--------------------------------------------------------
    //Sensor Event Listener Implementation
    //--------------------------------------------------------
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d(TAG, "onSensorChanged");
        if(sensorEvent.sensor.getName().equals("BMP280/BME280") && sensorEvent.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            //RainbowHat Temperature Sensor
            DataModel.sharedInstance().setBmx250Temperature(sensorEvent.values[0]);
        } else if(sensorEvent.sensor.getName().equals("BMP280/BME280") && sensorEvent.sensor.getType() == Sensor.TYPE_PRESSURE) {
            //RainbowHat Pressure Sensor
            DataModel.sharedInstance().setBmx250Pressure(sensorEvent.values[0]);
        } else if(sensorEvent.sensor.getName().equals(BCM2385TemperatureSensorDriver.DRIVER_NAME) && sensorEvent.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            //CPU Temperature Reading
            DataModel.sharedInstance().setBcm2385Temperature(sensorEvent.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d(TAG, "onAccuracyChange");
    }
    //--------------------------------------------------------

    //--------------------------------------------------------
    //Sensor Setup and Destroy
    //--------------------------------------------------------
    private void setupSensors() {
        try {
            //initialize and register BMX280 Temperature and Pressure
            mBmx280SensorDriver = new Bmx280SensorDriver(I2C_BUS);
            mBmx280SensorDriver.registerTemperatureSensor();
            mBmx280SensorDriver.registerPressureSensor();

            //Initialize and register BCM2385 CPU Temperature Sensor
            mBcm2385TemperatureSensorDriver = new BCM2385TemperatureSensorDriver();
            mBcm2385TemperatureSensorDriver.registerTemperatureSensor();
        } catch (IOException e) {
            Log.e(TAG, "Error configuring sensor", e);
        }
    }

    private void destroySensors() {
        if (mBmx280SensorDriver != null ) {
            mBmx280SensorDriver.unregisterTemperatureSensor();
            mBmx280SensorDriver.unregisterPressureSensor();
            mBcm2385TemperatureSensorDriver.unregisterTemperatureSensor();
            try {
                mBmx280SensorDriver.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing sensor", e);
            } finally {
                mBmx280SensorDriver = null;
            }
        }

        if(mBcm2385TemperatureSensorDriver != null) {
            mBcm2385TemperatureSensorDriver.unregisterTemperatureSensor();
            try {
                mBcm2385TemperatureSensorDriver.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing sensor", e);
            } finally {
                mBcm2385TemperatureSensorDriver = null;
            }
        }
    }


    // Listen for registration events from the sensor driver
    private class SensorCallback extends SensorManager.DynamicSensorCallback {
        @Override
        public void onDynamicSensorConnected(Sensor sensor) {
            // Begin listening for sensor readings
            mSensorManager.registerListener(SensorMonitorService.this, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        public void onDynamicSensorDisconnected(Sensor sensor) {
            // Stop receiving sensor readings
            mSensorManager.unregisterListener(SensorMonitorService.this, sensor);
        }
    }

}
