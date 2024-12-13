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

package com.good.gd.example.gdinteraction;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.good.gd.GDStateListener;
import java.util.Map;


import com.good.gd.GDAndroid;

public class GDAuthorizationService extends Service implements GDStateListener {
    private static final String TAG = GDAuthorizationService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 1;

    private boolean canRunGDInBackGround = false;
    private int mStartId;
    private ServiceHandler mServiceHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        canRunGDInBackGround = GDAndroid.getInstance().serviceInit(this);
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        Looper mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "GD_INTERACTION";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "GD INTERACTION",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("GD Interaction")
                    .setContentText("GD Interaction").build();

            startForeground(NOTIFICATION_ID, notification);
        } else {
            startForeground(NOTIFICATION_ID, new Notification());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy() called");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mStartId = startId;
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Handler that receives messages from the thread started by the Service.
    // This needs to be done so that long running operations are not run on
    // the Main thread
    private final class ServiceHandler extends Handler {
        ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onAuthorized() {
        Log.d(TAG, "onAuthorized() called");
        if (canRunGDInBackGround) {
            Log.d(TAG, "The Container can be accessed in the background");
            Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = mStartId;
            mServiceHandler.sendMessage(msg);
        }
    }

    @Override
    public void onLocked() {

    }

    @Override
    public void onWiped() {

    }

    @Override
    public void onUpdateConfig(Map<String, Object> settings) {

    }

    @Override
    public void onUpdatePolicy(Map<String, Object> policyValues) {

    }

    @Override
    public void onUpdateServices() {

    }

    @Override
    public void onUpdateEntitlements() {

    }
}
