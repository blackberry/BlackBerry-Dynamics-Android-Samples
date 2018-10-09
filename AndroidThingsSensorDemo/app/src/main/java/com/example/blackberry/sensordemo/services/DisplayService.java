package com.example.blackberry.sensordemo.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class DisplayService extends Service {

    private static final String TAG = DisplayService.class.getSimpleName();
    public static final String ACTION_DISPLAY = "ACTION_DISPLAY";
    public static final String EXTRA_TEXT = "EXTRA_TEXT";

    private AlphanumericDisplay mDisplay;

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
        try {
            mDisplay = new AlphanumericDisplay("I2C1");
            mDisplay.setEnabled(true);
        } catch(IOException e) {
            Log.d(TAG, "display error");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");

        if(intent.getAction() == ACTION_DISPLAY && intent.hasExtra(EXTRA_TEXT)) {
            setText(intent.getStringExtra(EXTRA_TEXT));
        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    private void setText(String text) {
        try {

            mDisplay.display(text);

        } catch (IOException e) {
            Log.d(TAG, "display error");
        } finally {
            //stopSelf();
        }
    }

}
