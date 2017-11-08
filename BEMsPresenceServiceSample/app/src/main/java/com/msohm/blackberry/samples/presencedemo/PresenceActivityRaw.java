/* Copyright (c) 2017 BlackBerry Ltd.
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

package com.msohm.blackberry.samples.presencedemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.apache.http.entity.StringEntity;
import com.good.gd.apache.http.message.BasicHeader;
import com.good.gd.net.GDConnectivityManager;
import com.good.gd.net.GDNetworkInfo;
import com.good.gd.push.PushChannel;
import com.good.gd.push.PushChannelListener;
import com.good.gd.utility.GDAuthTokenCallback;
import com.good.gd.utility.GDUtility;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

public class PresenceActivityRaw extends AppCompatActivity implements GDStateListener,
        GDAuthTokenCallback, PushChannelListener
{

    private ArrayList<BemsServer> presenceServers = new ArrayList<>();
    private String gdAuthToken;
    private String pushToken;
    private String sequence;
    private TextView outputTextView;
    private boolean isAuthorized = false;
    private Button subscribeButton;
    private Button unSubscribeButton;
    private PushChannel pushChannel;
    private Runnable updateRunnable;
    private Handler updateHandler;
    private BroadcastReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Initialize Good Dynamics.
        GDAndroid.getInstance().activityInit(this);

        presenceServers = (ArrayList<BemsServer>)getIntent().getSerializableExtra("com.msohm.blackberry.samples.bemsdemo.PresenceServers");

        //Define the Runnable that will be used to poll for updates every AppConstants.TIMER_DELAY
        //milliseconds in the future.
        updateRunnable = new Runnable()
        {
            public void run()
            {
                getContactUpdates(false);
            }
        };

        setContentView(R.layout.activity_presence_raw);

        updateHandler = new Handler();

        outputTextView = (TextView)findViewById(R.id.outputTextView);
        subscribeButton = (Button)findViewById(R.id.subscribeButton);
        unSubscribeButton = (Button)findViewById(R.id.unsubscribeButton);

        Spinner serverListSpinner = (Spinner)findViewById(R.id.serverListSpinner);
        ArrayAdapter<BemsServer> serverListAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, presenceServers);
        serverListSpinner.setAdapter(serverListAdapter);
    }

    @Override
    protected void onDestroy ()
    {
        //Close the push channel when the activity is destroyed.
        if (receiver != null)
        {
            GDAndroid.getInstance().unregisterReceiver(receiver);
        }

        super.onDestroy();
    }

    //Called when the get auth token button is pressed.
    public void onGetAuthToken(View view)
    {
        //Ensure GD Authorization is complete.  Cannot request a GD Auth Token until it is.
        if (isAuthorized)
        {
            Spinner serverListSpinner = (Spinner)findViewById(R.id.serverListSpinner);
            int serverIndex = serverListSpinner.getSelectedItemPosition();
            String server = presenceServers.get(serverIndex).getServer();

            logOutput("Requesting GD Auth Token for: " + server);
            GDUtility util = new GDUtility();
            util.getGDAuthToken("", server, this);
        } else
        {
            logOutput("Not yet authorized to request GD Auth Token. Try again later.");
        }
    }

    //Called when the subscribe button is pressed.
    public void onSubscribe(View view)
    {
        //Get the headers and URL to make the BEMS request.
        ArrayList<BasicHeader> headers = prepareHeaders();
        String url = buildURL();

        StringEntity postBody = null;

        try
        {
            postBody = new StringEntity(
                    "{\n" +
                    "    \"contacts\" : [" + AppConstants.CONTACT_ADDRESSES + "],\n" +
                    "    \"notify\" : \"" + AppConstants.NOTIFY_KEY + "\"\n" +
                    "}");
        }
        catch (UnsupportedEncodingException uex)
        {
            logOutput("Error building post body: " + uex.toString());
        }

        //Build the HttpRequestParams.
        HttpRequestParams params = new HttpRequestParams(url, headers, postBody, HttpRequestParams.POST);

        logOutput("Initiating subscription request.");

        DownloadTask task = new DownloadTask();
        task.execute(params);

    }

    //Called when the unsubscribe button is pressed.
    public void onUnSubscribe(View view)
    {
        //Get the headers and URL to make the BEMS request.
        ArrayList<BasicHeader> headers = prepareHeaders();
        String url = buildURL();

        StringEntity postBody = null;

        //Unsubscribe to stop the flow of notifications can accomplished by subscribing
        // with an empty list of contacts.
        try
        {
            postBody = new StringEntity(
                    "{\n" +
                    "    \"contacts\" : [],\n" +
                    "    \"notify\" : \"" + AppConstants.NOTIFY_KEY + "\"\n" +
                    "}");
        }
        catch (UnsupportedEncodingException uex)
        {
            logOutput("Error building post body: " + uex.toString());
        }

        //Build the HttpRequestParams.
        HttpRequestParams params = new HttpRequestParams(url, headers, postBody, HttpRequestParams.POST);

        logOutput("Initiating unsubscribe request.");

        //Remove all scheduled polls requests.
        sequence = null;
        updateHandler.removeCallbacks(updateRunnable);

        DownloadTask task = new DownloadTask();
        task.execute(params);
    }

    private void getContactUpdates(boolean isPushTriggered)
    {
        //Get the headers and URL to make the BEMS request.
        ArrayList<BasicHeader> headers = prepareHeaders();

        //Start with the base URL and append the target and sequence values.
        StringBuffer url = new StringBuffer();
        url.append(buildURL());
        url.append('/');
        url.append(AppConstants.NOTIFY_KEY);

        //If this was triggered by a push notification append the sequence from the push message
        //to the URL.  This informs BEMS to only send the contact that changed.
        if (isPushTriggered && sequence != null && sequence.length() > 0)
        {
            url.append("?sequence=");
            url.append(sequence);
        }

        //Build the HttpRequestParams.
        HttpRequestParams params = new
                HttpRequestParams(url.toString(), headers, null, HttpRequestParams.GET);

        logOutput("Sending update request.");

        DownloadTask task = new DownloadTask();
        task.execute(params);
    }

    //Cancels future timer schedules and schedules a new one AppConstants.TIMER_DELAY millseconds
    //in the future.
    private void reSchedulePollUpdate()
    {
        updateHandler.removeCallbacks(updateRunnable);
        updateHandler.postDelayed(updateRunnable, AppConstants.TIMER_DELAY);
        logOutput("Poll update scheduled in (milliseconds): " + AppConstants.TIMER_DELAY);
    }

    //Builds the URL used to make the BEMS request.
    private String buildURL()
    {
        //Build the URL to submit the subscription:
        Spinner serverListSpinner = (Spinner) findViewById(R.id.serverListSpinner);
        int serverIndex = serverListSpinner.getSelectedItemPosition();
        return "https://" + presenceServers.get(serverIndex).getServer() + "/presence/subscriptions";
    }

    //Creates the headers for the request sent to the BEMS server.
    private ArrayList<BasicHeader> prepareHeaders()
    {
        ArrayList<BasicHeader> headers =  new ArrayList<>();

        //Optional - Set the presence server version we want to use.
        headers.add(new BasicHeader("X-Good-Presence-Version", "1.0.0"));

        if (pushToken != null)
        {
            //Optional - Set the GNP token to support push updates for subscriptions if push
            //is ready.
            headers.add(new BasicHeader("X-Good-GNP-Token", pushToken));
        }

        //Required - Set the GD auth token to authenticate with the presence server.
        headers.add(new BasicHeader("X-Good-GD-AuthToken", gdAuthToken));

        //Use the device's IMEI as its unique identifier.
        TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        headers.add(new BasicHeader("X-Good-DeviceId", telManager.getDeviceId()));

        //Set the accept headers
        headers.add(new BasicHeader("Accept", "application/json"));
        headers.add(new BasicHeader("Content-Type", "application/json"));

        return headers;
    }

    //Extract the sequence number from the JSON response.
    private String parseSequence(String json)
    {
        if (json != null)
        {
            try {
                //Get a JSONObject of the result.
                JSONObject jsonObj = new JSONObject(json);
                sequence = jsonObj.getString(AppConstants.TAG_SEQUENCE);
            }
            catch (Exception ex)
            {
                logOutput("Exception parsing result: " + ex.toString());
            }
        }

        return null;
    }

    /**
     * Implementation of AsyncTask, to fetch the data in the background away from
     * the UI thread.
     */
    private class DownloadTask extends AsyncTask<HttpRequestParams, Void, String>
    {

        @Override
        protected String doInBackground(HttpRequestParams... params)
        {
            try {
                GDHttpConnector http = new GDHttpConnector();
                return http.doRequest(params[0]);

            } catch (IOException e) {
                return e.toString();
            }
        }

        /**
         * Display the result returned from the network call.
         */
        @Override
        protected void onPostExecute(String result)
        {
            reSchedulePollUpdate();
            logOutput(result);
            parseSequence(result);
        }
    }

    /**
     * Register receiver to listen to GDConnectivityManager.GD_CONNECTIVITY_ACTION intent
     *
     * This method is to demonstrate how to set up receiver to listen to the broadcast
     * intent when the push connection status is changed
     */
    private void registerReceiver()
    {
        if (receiver == null)
        {
            receiver = new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context context, Intent intent)
                {
                    logOutput("Received GD_CONNECTIVITY_ACTION intent.");
                    setupPushChannel();
                }
            };

            GDAndroid.getInstance().registerReceiver(receiver,
                    new IntentFilter(GDConnectivityManager.GD_CONNECTIVITY_ACTION));
        }
    }

    //Creates the Push Channel if it's available and hasn't already been created.
    private void setupPushChannel()
    {
        logOutput("Setup Push Channel.");

        GDNetworkInfo networkInfo = GDConnectivityManager.getActiveNetworkInfo();
        logOutput("isPushChannelAvailable = " + networkInfo.isPushChannelAvailable());

        if (pushChannel == null && networkInfo.isPushChannelAvailable())
        {
            pushChannel = new PushChannel();
            pushChannel.setListener(this);
            pushChannel.connect();

            logOutput("Push Channel Created.");
        }
    }

    @Override
    public void onChannelOpen(String token)
    {
        pushToken = token;
    }

    @Override
    public void onChannelMessage(String message)
    {
        logOutput("Update received via push:");
        logOutput(message);

        //Trigger a contact update poll.
        getContactUpdates(true);
    }

    @Override
    public void onChannelClose(String token)
    {
        logOutput("Push Channel Connection Closed.");
        pushToken = null;
    }

    @Override
    public void onChannelError(int i)
    {
        logOutput("Push Channel Error.");
    }

    @Override
    public void onChannelPingFail(int i)
    {

    }

    @Override
    public void onGDAuthTokenSuccess(String token)
    {
        gdAuthToken = token;
        logOutput("Received GD auth token.");
        subscribeButton.setEnabled(true);
        unSubscribeButton.setEnabled(true);
    }

    @Override
    public void onGDAuthTokenFailure(int errorCode, String error)
    {
        logOutput("Failed to receive GD auth token.  ErrorCode: " + errorCode + " Error: " + error);
    }

    private void logOutput(String output)
    {
        outputTextView.setText(outputTextView.getText() + "\n" + output);
    }

    @Override
    public void onAuthorized()
    {
        isAuthorized = true;
        registerReceiver();
        setupPushChannel();
    }

    @Override
    public void onLocked() {  }

    @Override
    public void onWiped() {  }

    @Override
    public void onUpdateConfig(Map<String, Object> map) {  }

    @Override
    public void onUpdatePolicy(Map<String, Object> map) {  }

    @Override
    public void onUpdateServices() {  }

    @Override
    public void onUpdateEntitlements() {  }
}
