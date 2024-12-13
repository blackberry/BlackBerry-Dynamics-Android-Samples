package com.example.blackberry.sensordemo.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


import com.good.gd.file.File;
import com.good.gd.file.FileOutputStream;
import com.good.gd.file.GDFileSystem;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogFileService extends Service {

    private static final String TAG = LogFileService.class.getSimpleName();

    public static final String ACTION_LOG = "ACTION_LOG";
    public static final String EXTRA_TEXT = "EXTRA_TEXT";
    public static final String EXTRA_LOCAL_FILE = "EXTRA_LOCAL_FILE";
    public static final String EXTRA_LOCAL_MAX_FILE_SIZE = "EXTRA_LOCAL_MAX_FILE_SIZE";

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

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");

        if(intent.getAction() == ACTION_LOG && intent.hasExtra(EXTRA_TEXT) && intent.hasExtra(EXTRA_LOCAL_FILE) && intent.hasExtra(EXTRA_LOCAL_MAX_FILE_SIZE)) {
            logText(intent.getStringExtra(EXTRA_TEXT), intent.getStringExtra(EXTRA_LOCAL_FILE), intent.getIntExtra(EXTRA_LOCAL_MAX_FILE_SIZE,1024));
        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    private void logText(String text, String filePath, int localMaxFileSize) {
        fileInfo(filePath);
        monitorLocalFileSize(filePath, localMaxFileSize);
        try
        {
            FileOutputStream outputStream = GDFileSystem.openFileOutput(filePath, Context.MODE_APPEND);
            outputStream.write(text.getBytes());
            outputStream.close();
            outputStream = null;
            String.format("Log File: %s, Data: %s", filePath, text);
            Log.i(TAG, "Log file written: ");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } finally {
            stopSelf();
        }
    }

    private void fileInfo(String filePath) {
        File localFile = new File(filePath);
        if( localFile.exists() ) {
            Log.i(TAG, "File Name: " + localFile.getName());
            Log.i(TAG, "File Size: " + localFile.length());

            Date lastModified = new Date( localFile.lastModified() );
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Log.i(TAG, "Last Modified: " + dateFormat.format(lastModified));

            Log.i(TAG, "File Name: " + localFile.getName());

            Log.i(TAG, "Total Space: " + localFile.getTotalSpace());
            Log.i(TAG, "Free Space: " + localFile.getFreeSpace());
            Log.i(TAG, "Usable Space: " + localFile.getUsableSpace());

        }
    }

    private void monitorLocalFileSize(String filePath, int maxFileSize) {
        File localFile = new File(filePath);
        if( localFile.exists() ) {
            //If local file size is greater than maxFileSize (KB)
            if (localFile.length() > maxFileSize * 1024) {
                localFile.delete();
            }
        }
    }

}
