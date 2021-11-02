/* Copyright (c) 2021  BlackBerry Limited.
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

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
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

    private final String FILENAME = "myVideo.mp4"; //Hard coded filename. Adjust as required.
    private Button copyButton;
    private Button playButton;
    MediaPlayer mp;
    boolean isStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize BlackBerry Dynamics.
        GDAndroid.getInstance().activityInit(this);
        setContentView(R.layout.activity_main);

        copyButton = (Button)findViewById(R.id.copyButton);

        //The copy button is used to copy an example video file included in this project to the
        //BlackBerry Dynamics file secure system.  This only needs to be run once.
        copyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                copyFile();
            }
        });

        playButton = (Button)findViewById(R.id.playButton);

        //Initializes the MediaPlayer.
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (!isStarted) {
                        BDMediaDataSource source = new BDMediaDataSource(FILENAME);
                        mp.setDataSource(source);
                        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.start();
                                isStarted = true;
                            }
                        });
                        mp.prepareAsync();
                    } else {
                        mp.start();
                    }
                }
                catch (IOException ioex) {}
            }
        });

        SurfaceView surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(this);
        mp = new MediaPlayer();
    }

    //Copies a sample video from the project into the BlackBerry Dynamics secure file system.
    private void copyFile()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Copy the sample video file and store it in the BlackBerry Dynamics file system.
                    AssetManager am = getApplicationContext().getAssets();
                    AssetFileDescriptor afDesc = null;

                    afDesc = am.openFd("SampleVideo.mp4");
                    FileInputStream in = afDesc.createInputStream();
                    FileOutputStream out =
                            GDFileSystem.openFileOutput(FILENAME, Context.MODE_PRIVATE);

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();

                    runOnUiThread(new Runnable() {
                        public void run() {
                            //Video file copied successfully.
                            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                            alert.setTitle("File Copied");
                            alert.setMessage("Success!");
                            alert.setNeutralButton("Close", null);
                            alert.show();
                        }
                    });
                }
                catch(final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //Failed to copy the video file
                            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                            alert.setTitle("File Copy Failed");
                            alert.setMessage(e.toString());
                            alert.setNeutralButton("Close", null);
                            alert.show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        try {
            mp.setDisplay(holder);
        }
        catch (IllegalStateException is){}
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mp.isPlaying()) {
            mp.pause();
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
