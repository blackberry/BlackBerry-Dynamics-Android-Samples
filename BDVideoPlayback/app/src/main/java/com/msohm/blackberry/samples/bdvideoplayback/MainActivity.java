/* Copyright (c) 2017  BlackBerry Limited.
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

package com.msohm.blackberry.samples.bdvideoplayback;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.file.FileOutputStream;
import com.good.gd.file.GDFileSystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GDStateListener,
        SurfaceHolder.Callback {

    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 45;
    private final String FILENAME = "myVideo.mp4"; //Hard coded filename. Adjust extension as required.
    private Button copyButton;
    private Button playButton;
    MediaPlayer mp;
    boolean isPaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize BlackBerry Dynamics.
        GDAndroid.getInstance().activityInit(this);
        setContentView(R.layout.activity_main);

        copyButton = (Button)findViewById(R.id.copyButton);

        //The copy button is used to copy an existing video file you have into the secure
        //BlackBerry Dynamics file system.  This only needs to be run once.
        copyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Ensure we have permission to read the file being copied.
                int permissionCheck = ContextCompat.checkSelfPermission(v.getContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE);

                if (permissionCheck != PackageManager.PERMISSION_GRANTED)
                {
                    //Permission isn't set.  Request it.
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                else
                {
                    copyFile();
                }
            }
        });

        playButton = (Button)findViewById(R.id.playButton);

        //Initializes the MediaPlayer.
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            try {
                if (!isPaused) {
                    BDMediaDataSource source = new BDMediaDataSource(FILENAME);
                    mp.setDataSource(source);
                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    });
                    mp.prepareAsync();
                } else {
                    mp.start();
                }
                isPaused = false;
            }
            catch (IOException ioex) {}

            }
        });

        SurfaceView surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(this);
        mp = new MediaPlayer();
        isPaused = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
        {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //File permission request was granted, yay!
                //Copy the file.
                copyFile();
            }
            else
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("File Not Copied");
                alert.setMessage("Permission Denied");
                alert.setNeutralButton("Close", null);
                alert.show();
            }
        }
    }

    //Copies an existing video on your device into the BlackBerry Dynamics secure file system.
    //For simplicity this method is not thread safe for copying large video files.
    //If you need to copy large files use an AsyncTask.
    private void copyFile()
    {
        try
        {
            //Copy a video file and store it in the BD file system
            //TODO  Update the path below to point to a video on your device.
            FileInputStream in =
                    new FileInputStream("/storage/emulated/0/Download/YourVideoFile.mp4");
            FileOutputStream out =
                    GDFileSystem.openFileOutput(FILENAME, Context.MODE_PRIVATE);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
            {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

            //Video file copied successfully.
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("File Copied");
            alert.setMessage("Success!");
            alert.setNeutralButton("Close", null);
            alert.show();

        }
        catch(Exception e)
        {
            //Failed to copy the video file
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("File Copy Failed");
            alert.setMessage(e.toString());
            alert.setNeutralButton("Close", null);
            alert.show();
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        try
        {
            mp.setDisplay(holder);
        }
        catch (IllegalStateException is){}
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mp.isPlaying() && !isPaused) {
            mp.pause();
            isPaused = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mp != null) {
            mp.stop();
            mp.release();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {  }

    @Override
    public void onAuthorized() {

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

    }

    @Override
    public void onUpdateServices() {

    }

    @Override
    public void onUpdateEntitlements() {

    }
}
