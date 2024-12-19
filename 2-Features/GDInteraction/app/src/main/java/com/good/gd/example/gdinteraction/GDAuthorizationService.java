/* Copyright 2024 BlackBerry Ltd.
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
 */

package com.good.gd.example.gdinteraction;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Map;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;

public class GDAuthorizationService extends JobService implements GDStateListener {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "GD_INTERACTION";

    private NotificationManager notificationManager;
    private boolean canRunGDInBackGround = false;
    private boolean isAuthorized = false;
    private JobParameters jobParameters;

    static void scheduleJob(Context context) {

        final JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);

        final JobInfo jobInfo =
                new JobInfo.Builder(1, new ComponentName(context, GDAuthorizationService.class))
                           .setExpedited(true)
                           .build();

        jobScheduler.cancelAll();
        jobScheduler.schedule(jobInfo);
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        this.jobParameters = jobParameters;

        notificationManager = getSystemService(NotificationManager.class);

        Log.d(GDInteraction.TAG, "GDAuthorizationService.onStartJob: notificationManager = " + notificationManager);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                                            "GD INTERACTION",
                                            NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);

        canRunGDInBackGround = GDAndroid.getInstance().serviceInit(this);
        if (canRunGDInBackGround) {

            sendNotification("Authorizing");
            Log.d(GDInteraction.TAG, "GDAuthorizationService.onStartJob: Authorizing");

            return true;

        } else {
            sendNotification("Cannot authorize in background");
            Log.w(GDInteraction.TAG, "GDAuthorizationService.onStartJob: cannot run in background, not authorizing");

            return false;
        }
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(GDInteraction.TAG, "GDAuthorizationService.onStopJob: params = " + params);
        return false;
    }

    private void sendNotification(String text) {
        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(
                NOTIFICATION_ID,
                new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.gcs_launcher_foreground)
                    .setContentTitle("GD Interaction")
                    .setContentText(text)
                    .build());
        } else {
            Log.w(GDInteraction.TAG, "GDAuthorizationService.sendNotification: notifications not allowed!" +
                                     " Not sending: \"" + text + "\"");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(GDInteraction.TAG, "GDAuthorizationService.onDestroy() called");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onAuthorized() {

        isAuthorized = true;

        sendNotification("Authorized");

        Log.d(GDInteraction.TAG,
            "onAuthorized: canRunGDInBackGround=" + canRunGDInBackGround +
            " canAuthorizeAutonomously=" + GDAndroid.getInstance().canAuthorizeAutonomously(this));

        jobFinished(jobParameters, false);
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
