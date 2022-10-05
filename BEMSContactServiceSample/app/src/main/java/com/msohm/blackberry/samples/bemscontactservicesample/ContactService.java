/* Copyright (c) 2021 BlackBerry Ltd.
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

package com.msohm.blackberry.samples.bemscontactservicesample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.apache.http.entity.StringEntity;
import com.good.gd.apache.http.message.BasicHeader;
import com.good.gd.utility.GDAuthTokenCallback;
import com.good.gd.utility.GDUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContactService extends AppCompatActivity implements GDStateListener,
        GDAuthTokenCallback
{

    private ArrayList<BemsServer> bemsServers = new ArrayList<>();
    private String gdAuthToken;
    private ListView resultsListView;
    private boolean isAuthorized = false;
    private ProgressBar loadingIndicator;
    private Button getContactsButton;
    private Button createContactsButton;
    private String usersEmail;
    private ArrayList<HashMap<String, String>> contactList;
    private TextView numResultsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Initialize Good Dynamics.
        GDAndroid.getInstance().activityInit(this);

        contactList = new ArrayList<>();

        bemsServers = (ArrayList<BemsServer>)getIntent().getSerializableExtra
                ("com.msohm.blackberry.samples.bemscontactservicesample.BEMSServers");

        setContentView(R.layout.activity_contact_listing);

        resultsListView = findViewById(R.id.resultsListView);

        loadingIndicator = findViewById(R.id.loadingIndicator);

        getContactsButton = findViewById(R.id.getContactsButton);

        createContactsButton = findViewById(R.id.createContactButton);

        numResultsTextView = findViewById(R.id.numResultsTextView);

        Spinner serverListSpinner = findViewById(R.id.serverListSpinner);
        ArrayAdapter<BemsServer> serverListAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, bemsServers);
        serverListSpinner.setAdapter(serverListAdapter);
    }

    //Requests a GD Auth Token used for authentication with BEMS.
    public void onGetAuthToken(View view)
    {
        //Enable the loading indicator.
        loadingIndicator.setVisibility(ProgressBar.VISIBLE);

        //Ensure GD Authorization is complete.  Cannot request a GD Auth Token until it is.
        if (isAuthorized)
        {
            Spinner serverListSpinner = findViewById(R.id.serverListSpinner);
            int serverIndex = serverListSpinner.getSelectedItemPosition();
            String server = bemsServers.get(serverIndex).getServer();

            logOutput("Requesting GD Auth Token for: " + server);
            GDUtility util = new GDUtility();
            util.getGDAuthToken("", server, this);
        } else
        {
            logOutput("Not yet authorized to request GD Auth Token. Try again later.");
        }
    }

    //Called when the Get Contacts button is pressed.
    public void onGetContacts(View view)
    {
        //Enable the loading indicator.
        loadingIndicator.setVisibility(ProgressBar.VISIBLE);

        ArrayList<BasicHeader> headers = new ArrayList<>();

        //Required - Set the GD auth token to authenticate with the BEMS service.
        headers.add(new BasicHeader("X-Good-GD-AuthToken", gdAuthToken));
        headers.add(new BasicHeader("Content-Type", "application/json"));
        headers.add(new BasicHeader("Accept", "application/json"));

        //Get the selected server:
        Spinner serverListSpinner = (Spinner)findViewById(R.id.serverListSpinner);
        int serverIndex = serverListSpinner.getSelectedItemPosition();

        //Build the URL, adding the BEMS contact service REST endpoint.
        String urlWithServer = "https://" + bemsServers.get(serverIndex).getServer() + "/api/contact";

        logOutput("Requesting URL: " + urlWithServer);

        //Create the search criteria JSON that is sent to the contact service.
        JSONObject searchJson = new JSONObject();
        StringEntity json = null;

        try
        {
            searchJson.put(AppConstants.TAG_ACCOUNT, usersEmail);
            searchJson.put(AppConstants.TAG_MAX_NUMBER_RESULTS, 50);
            searchJson.put(AppConstants.TAG_OFFSET, 0);

            ArrayList<String> shape = new ArrayList<String>();
            shape.add(AppConstants.TAG_FULL_NAME);
            shape.add(AppConstants.TAG_EMAIL_ADDRESS);

            searchJson.put(AppConstants.TAG_USER_SHAPE, new JSONArray(shape));

            json = new StringEntity(searchJson.toString());

            logOutput("Sending JSON: " + searchJson.toString());
        }
        catch (Exception jsonex)
        {
            logOutput("Exception generating JSON: " + jsonex);
        }

        //Build the HttpRequestParams.
        HttpRequestParams params = new HttpRequestParams(urlWithServer, headers, json, HttpRequestParams.POST);

        DownloadTask task = new DownloadTask();
        task.execute(params);
    }

    //Called when the Create Contact button is pressed.
    public void onCreateContact(View view)
    {
        //Enable the loading indicator.
        loadingIndicator.setVisibility(ProgressBar.VISIBLE);

        ArrayList<BasicHeader> headers = new ArrayList<>();

        //Required - Set the GD auth token to authenticate with the BEMS service.
        headers.add(new BasicHeader("X-Good-GD-AuthToken", gdAuthToken));
        headers.add(new BasicHeader("Content-Type", "application/json"));
        headers.add(new BasicHeader("Accept", "application/json"));

        //Get the selected server:
        Spinner serverListSpinner = (Spinner)findViewById(R.id.serverListSpinner);
        int serverIndex = serverListSpinner.getSelectedItemPosition();

        //Build the URL, adding the BEMS contact service REST endpoint.
        String urlWithServer = "https://" + bemsServers.get(serverIndex).getServer() + "/api/contact/create";

        logOutput("Requesting URL: " + urlWithServer);

        //Create the contact that will be created.
        JSONObject contactJson = new JSONObject();
        StringEntity json = null;

        //Using hard coded values to create a new contact.
        String firstName = "Fred";
        String lastName = "Smith";
        String email = "fred@fredsemail.com";

        try
        {
            contactJson.put(AppConstants.TAG_FIRST_NAME, firstName);
            contactJson.put(AppConstants.TAG_LAST_NAME, lastName);
            contactJson.put(AppConstants.TAG_EMAIL_ADDRESS1, email);

            json = new StringEntity(contactJson.toString());

            logOutput("Sending JSON: " + contactJson.toString());
        }
        catch (Exception jsonex)
        {
            logOutput("Exception generating JSON: " + jsonex);
        }

        //Build the HttpRequestParams.
        HttpRequestParams params = new HttpRequestParams(urlWithServer, headers, json, HttpRequestParams.POST);

        DownloadTask task = new DownloadTask();
        task.execute(params);
    }

    /**
     * Implementation of AsyncTask, to fetch the data in the background away from
     * the UI thread.
     */
    private class DownloadTask extends AsyncTask<HttpRequestParams, Void, String>
    {

        private static final int REQUEST_SEARCH = 100;
        private static final int REQUEST_CREATE = 200;
        private int requestType = 0;

        @Override
        protected String doInBackground(HttpRequestParams... params)
        {
            if (params[0].getUrl().contains("create")) {
                requestType = REQUEST_CREATE;
            }
            else {
                requestType = REQUEST_SEARCH;
            }

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
        protected void onPostExecute(String result) {
            logOutput("JSON Result: " + result);

            if (REQUEST_CREATE == requestType) {
                parseCreateResults(result);
            } else if (REQUEST_SEARCH == requestType){
                parseSearchResults(result);
            } else {
                logOutput("Unknown request type.");
            }

        }
    }

    //Parses the JSON returned from BEMS that acknowledges whether the contact creation was successful.
    private void parseCreateResults(String result)
    {
        String alertTitle;
        String alertMessage;

        if (result == null) {
            alertTitle = "Error!";
            alertMessage = "BEMS failed to create the new contact.";
        } else {
            alertTitle = "Contact Creation Result";
            alertMessage = result;
        }
        //Disable the loading indicator.
        loadingIndicator.setVisibility(ProgressBar.INVISIBLE);

        AlertDialog alertDialog = new AlertDialog.Builder(ContactService.this).create();
        alertDialog.setTitle(alertTitle);
        alertDialog.setMessage(alertMessage);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    //Parses the JSON returned from BEMS that contains the list of contacts
    //returned from the contacts service.
    private void parseSearchResults(String result)
    {

        if (result != null)
        {
            //Clear out all existing contacts.
            contactList.clear();

            try {
                //Get a JSONArray of the result.
                JSONObject resultJSON = new JSONObject(result);
                JSONObject lookupInfo = resultJSON.getJSONObject(AppConstants.TAG_LOOKUP_INFO);


                JSONArray contacts = resultJSON.getJSONArray(AppConstants.TAG_CONTACT_ARRAY);

                int numContacts = lookupInfo.getInt(AppConstants.TAG_LOOKUP_SIZE);
                int totalCount = lookupInfo.getInt(AppConstants.TAG_LOOKUP_TOTAL_COUNT);

                numResultsTextView.setText("Showing: " + numContacts + " of " + totalCount + " Contacts");

                //Iterate through each contact.
                for (int count = 0; count < numContacts; count++)
                {
                    //Extract the individual contact.
                    JSONObject contact = contacts.getJSONObject(count);

                    //Extract the contact name and email address.
                    String displayName;
                    String email;
                    if (contact.has(AppConstants.TAG_DISPLAY_NAME))
                    {
                        displayName = contact.getString(AppConstants.TAG_DISPLAY_NAME);
                    } else
                    {
                        displayName = "Contact has no display name";
                    }

                    if (contact.has(AppConstants.TAG_EMAIL_ADDRESS))
                    {
                        email = contact.getString(AppConstants.TAG_EMAIL_ADDRESS);
                    } else
                    {
                        email = "Contact has no email";
                    }

                    //Create a HashMap to store the individual contact.
                    HashMap<String, String> c = new HashMap<>();
                    c.put(AppConstants.TAG_DISPLAY_NAME, displayName);
                    c.put(AppConstants.TAG_EMAIL_ADDRESS, email);

                    //Add the contact to the list.
                    contactList.add(c);
                }
            }
            catch (Exception ex)
            {
                logOutput("Exception parsing result: " + ex.toString());
            }

            //Update parsed JSON data into ListView
            ListAdapter adapter = new SimpleAdapter(
                    this, contactList,
                    R.layout.contact_list_item, new String[]{AppConstants.TAG_DISPLAY_NAME,
                    AppConstants.TAG_EMAIL_ADDRESS},
                    new int[]{R.id.name, R.id.email});

            resultsListView.setAdapter(adapter);
        }

        //Disable the loading indicator.
        loadingIndicator.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    public void onAuthorized()
    {
        isAuthorized = true;

        //Get the application configuration, which will contain the user's email address.
        //The user's email address is sent to the BEMS Contact Service to identify them.
        Map<String, Object> config = GDAndroid.getInstance().getApplicationConfig();

        usersEmail = (String)config.get("userId");

        logOutput("User's email address is: " + usersEmail);
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
    public void onUpdateServices() { }

    @Override
    public void onUpdateEntitlements() { }

    @Override
    public void onGDAuthTokenSuccess(String token)
    {
        gdAuthToken = token;
        logOutput("Received GD auth token.");

        //Enable the search and create buttons.
        getContactsButton.setEnabled(true);
        createContactsButton.setEnabled(true);

        //Disable the loading indicator.
        loadingIndicator.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    public void onGDAuthTokenFailure(int errorCode, String error)
    {
        logOutput("Failed to receive GD auth token.  ErrorCode: " + errorCode + " Error: " + error);

        //Disable the loading indicator.
        loadingIndicator.setVisibility(ProgressBar.INVISIBLE);
    }

    private void logOutput(String output)
    {
        Log.d("BEMS Dir Sample:", output);
    }
}
