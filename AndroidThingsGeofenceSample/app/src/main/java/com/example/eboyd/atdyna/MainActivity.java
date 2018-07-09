/* Copyright (c) 2018 BlackBerry Ltd.
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

package com.example.eboyd.atdyna;

import android.util.Log;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.graphics.Color;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.apache.http.HttpResponse;
import com.good.gd.apache.http.client.methods.HttpGet;
import com.good.gd.net.GDHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Map;

import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;
import com.google.android.things.contrib.driver.apa102.Apa102;
import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay;

public class  MainActivity extends Activity implements GDStateListener
{
    private static final String TAG = "MainActivity";

    //Coordinates of the center of the geofence and the surrounding radius

    Double workLatitude = 37.285;
    Double workLongitude = -121.95;
    Integer radius = 1;

    String urlString = "https://ipapi.co/";
    String format = "/json/";
    TextView text_IP;
    TextView text_Distance;
    Button button_getMyPublicIP;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GDAndroid.getInstance().activityInit(this);

        text_IP = findViewById(R.id.textView_IP);
        text_Distance = findViewById(R.id.textView_Distance);


        button_getMyPublicIP = findViewById(R.id.button_getIP);
        button_getMyPublicIP.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                text_IP.setText("Loading public IP");
                new getIPAsyncTask().execute();
            }
        });
    }

    private class getIPAsyncTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... strings)
        {
            try
            {
                return getLocation();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            text_IP.setText("Your Location is : " +result);
        }
    }

    //Gets the longitude and latitude from the json, checks the distance from the center point and
    //activates the rainbowhat
    
    private String getLocation() throws IOException
    {
            Double latitude = getLatitude();
            Double longitude = getLongitude();
            String city = getCity();
            String region = getRegion();


            Double d = getDistance(latitude, workLatitude, longitude, workLongitude);
            geoCheck(d);

            return (latitude.toString()+", "+longitude.toString()+" in "+ city + ", "+ region);
    }

    //Dynamics GDHttP request to get the input stream

    private InputStream getInputStreamFromIpApi(String url) throws IOException
    {
        GDHttpClient httpClient = new GDHttpClient();
        final HttpGet request = new HttpGet(url);
        HttpResponse response = httpClient.execute(request);
        InputStream stream = response.getEntity().getContent();

        return stream;
    }

    //Converts the input stream to a JSON object
    
    private JSONObject streamToJson(InputStream stream) throws IOException
    {
        BufferedReader bfr = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuilder stb = new StringBuilder();

        String ips;

        while ((ips = bfr.readLine()) != null)
            stb.append(ips);

        try
        {
            JSONObject job = new JSONObject(stb.toString());
            return job;
        }
        catch (JSONException e)
        {
            Log.i(TAG, "StreamToJSON Failure");
            throw new RuntimeException(e);
        }
    }

    private double getLatitude() throws IOException
    {
        InputStream stream = getInputStreamFromIpApi(urlString + format);
        JSONObject test = streamToJson(stream);

        try
        {
             Double latitude = test.getDouble("latitude");
             return latitude;
        }
        catch (JSONException e)
        {
            throw new RuntimeException(e);
        }
    }

    private double getLongitude() throws IOException
    {
        InputStream stream = getInputStreamFromIpApi(urlString + format);
        JSONObject test = streamToJson(stream);

        try
        {
            Double longitude =test.getDouble("longitude");
            return longitude;
        }
        catch (JSONException e)
        {
            throw new RuntimeException(e);
        }
    }

    private String getCity() throws IOException
    {
        InputStream stream = getInputStreamFromIpApi(urlString + format);
        JSONObject test = streamToJson(stream);

        try
        {
            String city = test.getString("city");
            return city;
        }
        catch (JSONException e)
        {
            throw new RuntimeException(e);
        }
    }

    private String getRegion() throws IOException
    {
        InputStream stream = getInputStreamFromIpApi(urlString + format);
        JSONObject test = streamToJson(stream);

        try
        {
            String region = test.getString("region");
            return region;
        }
        catch (JSONException e)
        {
            throw new RuntimeException(e);
        }
    }

    private double getDistance(double lat1, double lat2, double long1, double long2)
    {
        int R = 6371;
        double rLat = degToRadians(lat2-lat1);
        double rLong = degToRadians(long2-long1);

        //Haversine formula for distance in km
        double a = Math.sin(rLat/2) * Math.sin(rLat/2) + Math.cos(degToRadians(lat1)) *
                Math.cos(degToRadians(lat2)) * Math.sin(rLong/2) * Math.sin(rLong/2);
        double b = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double c = R * b;

        return c;
    }

    private double degToRadians(double deg)
    {
        return deg * (Math.PI/180);
    }

    //Checks the distance from the center point compared to the radius of the field

    private void geoCheck(Double distance) throws IOException
    {
        if (distance < radius)
        {
            Apa102 ledstrip = RainbowHat.openLedStrip();
            ledstrip.setBrightness(31);
            int[] rainbow = new int[RainbowHat.LEDSTRIP_LENGTH];
            for (int i = 0; i < rainbow.length; i++)
            {
                rainbow[i] = Color.HSVToColor(255, new float[]
                        {
                                i * 360.f / rainbow.length, 1.0f, 1.0f
                        });
            }
            ledstrip.write(rainbow);
            ledstrip.close();
        }
        else
        {
            AlphanumericDisplay segment = RainbowHat.openDisplay();
            segment.setBrightness(15);
            segment.display("EROR");
            segment.setEnabled(true);
            segment.close();
        }

    }
    
    // Skeleton for functions for GDStateListener

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