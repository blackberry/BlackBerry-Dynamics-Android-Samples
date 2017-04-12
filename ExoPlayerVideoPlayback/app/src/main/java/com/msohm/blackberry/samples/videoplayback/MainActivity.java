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

package com.msohm.blackberry.samples.videoplayback;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.file.File;
import com.good.gd.file.FileOutputStream;
import com.good.gd.file.GDFileSystem;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

import java.io.FileInputStream;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GDStateListener {

    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 45;
    private Button playButton;
    private Button copyButton;
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private final String FILENAME = "myVideo.mp4"; //Hard coded filename. Adjust extension as required.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize BlackBerry Dynamics.
        GDAndroid.getInstance().activityInit(this);
        setContentView(R.layout.activity_main);

        simpleExoPlayerView = (SimpleExoPlayerView)findViewById(R.id.exoPlayer);

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

        //Initializes the ExoPlayer.
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            //Release the existing player so we don't initialize 2 instances.
            if (player != null)
            {
                player.release();
            }

            // 1. Create a default TrackSelector
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

            // 2. Create a default LoadControl
            LoadControl loadControl = new DefaultLoadControl();

            // 3. Create the player
            player = ExoPlayerFactory.newSimpleInstance(v.getContext(), trackSelector, loadControl);

            // Bind the player to the view.
            simpleExoPlayerView.setPlayer(player);

            // Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new BDDataSourceFactory();

            // Produces Extractor instances for parsing the media data.
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

            // This is the MediaSource representing the media to be played.
            Uri uri = Uri.fromFile(new File(FILENAME));

            MediaSource videoSource = new ExtractorMediaSource(uri,
                    dataSourceFactory, extractorsFactory, null, null);

            // Prepare the player with the source.
            player.prepare(videoSource);
            }
        });
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
    public void onUpdateDataPlan() {

    }

    @Override
    public void onUpdateEntitlements() {

    }
}
