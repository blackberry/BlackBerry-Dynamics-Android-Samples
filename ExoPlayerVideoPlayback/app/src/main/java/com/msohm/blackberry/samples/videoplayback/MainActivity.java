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

package com.msohm.blackberry.samples.videoplayback;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.file.FileOutputStream;
import com.good.gd.file.GDFileSystem;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;

import java.io.FileInputStream;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GDStateListener {

    private Button playButton;
    private Button copyButton;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private final String FILENAME = "myVideo.mp4"; //Hard coded filename. Adjust as required.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize BlackBerry Dynamics.
        GDAndroid.getInstance().activityInit(this);
        setContentView(R.layout.activity_main);

        playerView = (PlayerView)findViewById(R.id.exoPlayer);
        copyButton = (Button)findViewById(R.id.copyButton);

        //The copy button is used to copy an example video file included in this project to the
        //BlackBerry Dynamics file secure system.  This only needs to be run once.
        copyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                copyFile();
            }
        });

        playButton = (Button)findViewById(R.id.playButton);

        //Initializes ExoPlayer.
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            //Release the existing player so we don't initialize 2 instances.
            if (player != null) {
                player.release();
            }

                //Create the player
                player = new SimpleExoPlayer.Builder(getApplicationContext())
                        .build();

                // Bind the player to the view.
                playerView.setPlayer(player);

                // Produces DataSource instances through which media data is loaded.
                DataSource.Factory dataSourceFactory = new BDDataSource.Factory();

                // Produces Extractor instances for parsing the media data.
                DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

                // This is the MediaSource representing the media to be played.
                MediaItem mediaItem = MediaItem.fromUri("file:///" + FILENAME);

                //Use a ProgressiveMediaSource to play an mp4 file.
                ProgressiveMediaSource mediaSource =
                        new ProgressiveMediaSource.Factory(dataSourceFactory, extractorsFactory)
                                .createMediaSource(mediaItem);

                //Add the MediaSource.
                player.setMediaSource(mediaSource);

                // Prepare the player.
                player.prepare();
            }
        });
    }

    //Copies a sample video from the project into the BlackBerry Dynamics secure file system.
    private void copyFile()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
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
