package com.example.blackberry.sensordemo.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;

import com.google.android.things.contrib.driver.apa102.Apa102;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;

public class RainbowLEDService extends Service {

    private static final String TAG = RainbowLEDService.class.getSimpleName();

    public static final String ACTION_LEDS = "ACTION_LEDS";
    public static final String EXTRA_LED_COUNT = "EXTRA_LED_COUNT";
    public static final String EXTRA_LED_COLOR = "EXTRA_LED_COLOR";
    
    private int[] mLEDColors = new int[7];

    private Apa102 mLedStrip;

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
            mLedStrip = new Apa102("SPI0.0", Apa102.Mode.BGR);
            mLedStrip.setBrightness(1);
        } catch (IOException e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");

        if(intent.getAction() == ACTION_LEDS && intent.hasExtra(EXTRA_LED_COUNT) && intent.hasExtra(EXTRA_LED_COLOR)) {

            //Set LED Colors
            if(intent.getStringExtra(EXTRA_LED_COLOR).equals("RAINBOW")) {
                rainbow();
            } else {
                staticLEDColor(intent.getStringExtra(EXTRA_LED_COLOR));
            }

            //light the LEDs
            lightLEDS(intent.getIntExtra(EXTRA_LED_COUNT,0));

        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        mLEDColors = null;
    }


    private void rainbow() {
        for (int i = 0; i < mLEDColors.length; i++) {
            mLEDColors[i] = Color.HSVToColor(255, new float[]{i * 360.f / mLEDColors.length, 1.0f, 1.0f});
        }
        ArrayUtils.reverse(mLEDColors);
    }

    private void staticLEDColor(String color) {
        for (int i = 0; i < mLEDColors.length; i++) {
            mLEDColors[i] = Color.parseColor(color);
        }
    }

    private void lightLEDS(int ledCount) {
        
        int[] readingArray = Arrays.copyOf(mLEDColors,ledCount);

        ArrayUtils.reverse(readingArray);

        int[] zeroArray = new int[7];
        Arrays.fill(zeroArray,0);

        int[] allArray = ArrayUtils.insert(7-ledCount,   zeroArray,readingArray);

        try {

            mLedStrip.write(allArray);

        } catch (IOException e) {
            Log.d(TAG, e.getLocalizedMessage());
        } finally {
            //stopSelf();
        }
    }

}
