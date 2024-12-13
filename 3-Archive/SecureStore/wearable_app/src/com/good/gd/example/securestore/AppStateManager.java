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

package com.good.gd.example.securestore;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.WearableExtender;
import androidx.core.app.NotificationManagerCompat;

import com.good.gd.example.securestore.common_lib.AppGDStateControl;
import com.good.gd.example.securestore.common_lib.AppGDStateControlListener;
import com.good.gd.example.securestore.common_lib.ConnectedApplicationControl;
import com.good.gd.example.securestore.common_lib.ConnectedApplicationListener;
import com.good.gd.example.securestore.common_lib.ConnectedApplicationState;
import com.good.gd.example.securestore.common_lib.FileTransferControl;
import com.good.gd.example.securestore.common_lib.FileTransferListener;
import com.good.gd.example.securestore.common_lib.ServicesControl;


import static com.good.gd.example.securestore.common_lib.utils.AppLogUtils.DEBUG_LOG;
import static com.good.gd.example.securestore.common_lib.utils.AppLogUtils.ERROR_LOG;

import com.good.gdwearable.GDAndroid;

import java.lang.ref.WeakReference;

/**
 * Secure Store Wearable App Manager, initialize all other singletons and manage overall app
 * state including adding notification into wearable stream when file is received
 */
class AppStateManager implements AppGDStateControlListener, ConnectedApplicationListener,
        FileTransferListener{

    private static AppStateManager _instance;

    private final WeakReference<Context> mWeakContext;

    final static int FILE_RECEIVED_ID = 0; // ID used to denote File Received Notification
    private final static int SHOW_FILES_ID = 1; // ID used to denote File list UI Notification

    final static String FILE_RECEIVED_EXTRA = "SecureStore_FileReceived";

    private final static String FILE_RECEIVED_NOTIFICATIONS_GROUP = "Group_file_received";
    private final static String FILE_LIST_NOTIFICATIONS_GROUP = "Group_file_list";

    private int fileReceivedID = FILE_RECEIVED_ID;

    static AppStateManager createInstance(Context aContext) {

        if (_instance == null) {
            _instance = new AppStateManager(aContext);
        }

        return _instance;

    }

    private AppStateManager(Context aContext) {

        mWeakContext = new WeakReference<>(aContext);

        //We first do GD Application Init which will load the basic GD functionality
        GDAndroid.getInstance().applicationInit(mWeakContext.get());

        //Next we create Singleton State Listener and set on GD
        AppGDStateControl listener = AppGDStateControl.createInstance();
        GDAndroid.getInstance().setGDStateListener(listener);

        // Next we set Singletons which handle our InterDevice Communication
        ServicesControl.createInstance();
        FileTransferControl.createInstance();
        ConnectedApplicationControl.createInstance(mWeakContext.get());

        AppGDStateControl.getInstance().addAppStateListener(this);
        ConnectedApplicationControl.getInstance().addConnectedAppStateListener(this);

        FileTransferControl.getInstance().addFileTransferListener(this);


    }

    @Override
    public void onAppGDStateChanged(AppGDStateControl.State aNewState) {

        if(aNewState == AppGDStateControl.State.GD_Authorized){

            // Place Notification in Wearable Stream which allows easy access to file list once app has started
            showFileList();

            // We are now authorized so we check if there are any Connected Applications
            ConnectedApplicationControl.getInstance().updateConnectedApplications();

        }

    }

    @Override
    public void onConnectedApplicationStateChanged(ConnectedApplicationState aState) {

    }


    private void showNotification(String aNotificationTag, int aNotificationID, String aNotificationMessage,
                                  int aNotificationPriority, String aNotificationGroup, Intent aIntentToLaunch) {
        if (mWeakContext.get() == null) {
            return;
        }
        Context context = mWeakContext.get();
        //Next we need to get the App Label to display
        String appLabel;

        try {
            appLabel = context.getString(context.getApplicationInfo().labelRes);
        } catch(Resources.NotFoundException e){
            //If the app hasn't set app name as a resource then this will be thrown
            appLabel = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
        }

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.mipmap.ic_attachment, "Open",
                PendingIntent.getActivity(context, aNotificationID, aIntentToLaunch, 0)).build();


        Notification notification = new NotificationCompat.Builder(context, "channel_01")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(appLabel)
                .setContentText(aNotificationMessage)
                .setGroup(aNotificationGroup)
                .setPriority(aNotificationPriority)
                .extend(new WearableExtender().addAction(action))
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(aNotificationTag,aNotificationID,notification);

    }

    @Override
    public void fileReceived(String aFilePath) {

        DEBUG_LOG("File received from remote application file = " + aFilePath);

        //Setup Intent to show received new file
        Intent i = new Intent();
        i.putExtra(FileViewer.FILE_VIEWER_PATH, aFilePath);
        if (mWeakContext.get() != null) {
            i.setClass(mWeakContext.get(), FileViewer.class);
        }
        i.putExtra(FILE_RECEIVED_EXTRA, FILE_RECEIVED_ID);

        //A combination of these flags will ensure any existing activities from the app are stopped and new file is shown at top of stack
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        String fileName = aFilePath.substring(aFilePath.lastIndexOf("/")+1, aFilePath.length());

        fileReceivedID++;

        showNotification(FILE_RECEIVED_EXTRA, fileReceivedID, fileName + " Received", Notification.PRIORITY_MAX,
                FILE_RECEIVED_NOTIFICATIONS_GROUP, i);

    }

    private void showFileList() {

        DEBUG_LOG("showFileList( ) ");

        //Setup Intent to show received new file
        Intent i = new Intent();
        if (mWeakContext.get() != null) {
            i.setClass(mWeakContext.get(), SecureStore.class);
        }

        //A combination of these flags will ensure any existing activities from the app are stopped and new file is shown at top of stack
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        showNotification(FILE_RECEIVED_EXTRA, SHOW_FILES_ID, "Local Files", Notification.PRIORITY_LOW,
                FILE_LIST_NOTIFICATIONS_GROUP, i);

    }

    @Override
    public void fileSentSuccess() {

        DEBUG_LOG("file Sent To remote application Success  ");
    }

    @Override
    public void fileSentError(String aErrorMessage) {

        ERROR_LOG("File Sent to remote application failure  " + aErrorMessage);

    }

    @Override
    public void numberFilesReceived(int aNumberFiles) {

        DEBUG_LOG("number of files received =  " + aNumberFiles);

    }
}
