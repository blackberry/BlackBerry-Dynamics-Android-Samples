/* Copyright (c) 2021 BlackBerry Limited.
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

package com.example.blackberry.theconfigurator;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements GDStateListener
{
    private AppPolicy appPolicy;

    private TextView carTitle;
    private ImageView carImage;
    private TextView carDescription;
    private Button soundButton;
    private TextView policyVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Initialize Good Dynamics.
        GDAndroid.getInstance().activityInit(this);

        setContentView(R.layout.activity_main);

        carTitle = (TextView)findViewById(R.id.carTitle);
        carImage = (ImageView)findViewById(R.id.carImage);
        carDescription = (TextView)findViewById(R.id.carDescription);
        soundButton = (Button)findViewById(R.id.soundButton);
        policyVersion = (TextView)findViewById(R.id.policyVersion);

        soundButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playSound();
            }
        });

    }




    //Update the UI with the latest App Policy information.
    //Called after Good Dynamics has finished initializing and
    //when a new App Policy arrives.
    private void refreshUi()
    {
        //Auto play sound if enabled.
        if (appPolicy.enableAutoPlaySound())
        {
            //Play the sound.
            playSound();
        }

        //Get the car title and description from the app policy.
        carTitle.setText(appPolicy.getCarName());
        carDescription.setText(appPolicy.getCarDescription());

        //Change to the appropriate car image based on app policy setting.
        switch (appPolicy.getCarColor())
        {
            case AppPolicy.BLACK:
                    if (appPolicy.isConvertible())
                    {
                        carImage.setImageResource(R.drawable.black_convertible);
                    }
                    else
                    {
                        carImage.setImageResource(R.drawable.black_coupe);
                    }
                break;

            case AppPolicy.BLUE:
                if (appPolicy.isConvertible())
                {
                    carImage.setImageResource(R.drawable.blue_convertible);
                }
                else
                {
                    carImage.setImageResource(R.drawable.blue_coupe);
                }
                break;

            case AppPolicy.RED:
                if (appPolicy.isConvertible())
                {
                    carImage.setImageResource(R.drawable.red_convertible);
                }
                else
                {
                    carImage.setImageResource(R.drawable.red_coupe);
                }
                break;

            case AppPolicy.SILVER:
                if (appPolicy.isConvertible())
                {
                    carImage.setImageResource(R.drawable.silver_convertible);
                }
                else
                {
                    carImage.setImageResource(R.drawable.silver_coupe);
                }
                break;

            case AppPolicy.TURQUOISE:
                if (appPolicy.isConvertible())
                {
                    carImage.setImageResource(R.drawable.turquoise_convertible);
                }
                else
                {
                    carImage.setImageResource(R.drawable.turquoise_coupe);
                }
                break;

            case AppPolicy.YELLOW:
                if (appPolicy.isConvertible())
                {
                    carImage.setImageResource(R.drawable.yellow_convertible);
                }
                else
                {
                    carImage.setImageResource(R.drawable.yellow_coupe);
                }
                break;

            default:
                carImage.setImageResource(R.drawable.car_placeholder);
                break;
        }

        //Set visibility of the name, image and description.
        if (appPolicy.displayCarName())
        {
            carTitle.setVisibility(View.VISIBLE);
        }
        else
        {
            carTitle.setVisibility(View.INVISIBLE);
        }

        if (appPolicy.displayCarImage())
        {
            carImage.setVisibility(View.VISIBLE);
        }
        else
        {
            carImage.setVisibility(View.INVISIBLE);
        }

        if (appPolicy.displayCarDescription())
        {
            carDescription.setVisibility(View.VISIBLE);
        }
        else
        {
            carDescription.setVisibility(View.INVISIBLE);
        }

        //Enable or disable the sound button.
        soundButton.setEnabled(appPolicy.enableSound());

        //Display the app policy version.
        policyVersion.setText("Policy Version: " + appPolicy.getAppPolicyVersion());
    }

    //Play the car starting sound.
    private void playSound()
    {
        try
        {
            AssetFileDescriptor afd = getAssets().openFd("CarSound.mp3");
            MediaPlayer player = new MediaPlayer();
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.prepare();
            player.start();
        }
        catch (Exception ex)
        {
            Log.e("TheConfigurator", "Exception playing sound: " + ex.toString());
        }
    }

    //Triggered after Good Dynamics has initialized and the user has been authorized to use the app.
    @Override
    public void onAuthorized()
    {
        //Get the App Policy.
        appPolicy = new AppPolicy(GDAndroid.getInstance().getApplicationPolicy());
        //Update the UI based on the latest App Policy.
        refreshUi();
    }

    @Override
    public void onLocked()
    {

    }

    @Override
    public void onWiped()
    {

    }

    @Override
    public void onUpdateConfig(Map<String, Object> map)
    {

    }

    //Triggered when a new App Policy arrives.
    @Override
    public void onUpdatePolicy(Map<String, Object> map)
    {
        //Get the new App Policy.
        appPolicy.setPolicy(map);

        //Update the UI based on the latest App Policy.
        refreshUi();
    }

    @Override
    public void onUpdateServices()
    {

    }

    @Override
    public void onUpdateEntitlements()
    {

    }
}
