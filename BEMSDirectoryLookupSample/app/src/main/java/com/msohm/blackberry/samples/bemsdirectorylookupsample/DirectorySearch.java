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

package com.msohm.blackberry.samples.bemsdirectorylookupsample;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.apache.http.entity.StringEntity;
import com.good.gd.apache.http.message.BasicHeader;
import com.good.gd.utility.GDAuthTokenCallback;
import com.good.gd.utility.GDUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DirectorySearch extends AppCompatActivity implements GDStateListener,
        GDAuthTokenCallback
{

    private ArrayList<BemsServer> docsServers = new ArrayList<>();
    private String gdAuthToken;
    private ListView resultsListView;
    private boolean isAuthorized = false;
    private ProgressBar loadingIndicator;
    private Button searchButton;
    private CheckBox includePersonalCheckBox;
    private EditText searchText;
    private String usersEmail;
    private ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Initialize Good Dynamics.
        GDAndroid.getInstance().activityInit(this);

        contactList = new ArrayList<>();

        docsServers = (ArrayList<BemsServer>)getIntent().getSerializableExtra
                ("com.msohm.blackberry.samples.bemsdocsservicesample.DirectoryServers");

        setContentView(R.layout.activity_directory_search);

        resultsListView = (ListView)findViewById(R.id.resultsListView);

        loadingIndicator = (ProgressBar) findViewById(R.id.loadingIndicator);

        searchButton = (Button) findViewById(R.id.searchButton);

        searchText = (EditText) findViewById(R.id.searchText);

        includePersonalCheckBox = (CheckBox) findViewById(R.id.includePersonalCheckBox);

        Spinner serverListSpinner = (Spinner)findViewById(R.id.serverListSpinner);
        ArrayAdapter<BemsServer> serverListAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, docsServers);
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
            Spinner serverListSpinner = (Spinner)findViewById(R.id.serverListSpinner);
            int serverIndex = serverListSpinner.getSelectedItemPosition();
            String server = docsServers.get(serverIndex).getServer();

            logOutput("Requesting GD Auth Token for: " + server);
            GDUtility util = new GDUtility();
            util.getGDAuthToken("", server, this);
        } else
        {
            logOutput("Not yet authorized to request GD Auth Token. Try again later.");
        }
    }

    //Called when the Search button is pressed.
    public void onSearch(View view)
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

        //Build the URL, adding the BEMS directory lookup service REST endpoint.
        String urlWithServer = "https://" + docsServers.get(serverIndex).getServer() + "/api/lookupuser";

        logOutput("Requesting URL: " + urlWithServer);

        //Create the search criteria JSON that is sent to the directory lookup service.
        JSONObject searchJson = new JSONObject();
        StringEntity json = null;

        try
        {
            searchJson.put(AppConstants.TAG_ACCOUNT, usersEmail);
            searchJson.put(AppConstants.TAG_SEARCH_KEY, searchText.getText());
            searchJson.put(AppConstants.TAG_MAX_NUMBER_RESULTS, 10);

            if (includePersonalCheckBox.isChecked())
            {
                searchJson.put(AppConstants.TAG_SEARCH_PERSONAL_CONTACTS, true);
            }
            else
            {
                searchJson.put(AppConstants.TAG_SEARCH_PERSONAL_CONTACTS, false);
            }

            ArrayList<String> shape = new ArrayList<String>();

            shape.add("FullName");
            shape.add("EmailAddress");

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
        protected void onPostExecute(String result) {
            logOutput("JSON Result: " + result);

            parseSearchResults(result);
        }
    }

    //Parses the JSON returned from BEMS that contains the list of contacts
    //returned from the directory lookup.
    private void parseSearchResults(String result)
    {

        if (result != null)
        {
            //Clear out all existing contacts.
            contactList.clear();

            try {
                //Get a JSONArray of all contacts.
                JSONArray contacts = new JSONArray(result);

                int numContacts = contacts.length();

                //Iterate through each contact.
                for (int count = 0; count < numContacts; count++)
                {
                    //Extract the individual contact.
                    JSONObject contact = contacts.getJSONObject(count);

                    //Extract the contact name and email address.
                    String fullName = contact.getString(AppConstants.TAG_FULL_NAME);
                    String email = contact.getString(AppConstants.TAG_EMAIL_ADDRESS);

                    //Create a HashMap to store the individual contact.
                    HashMap<String, String> c = new HashMap<>();
                    c.put(AppConstants.TAG_FULL_NAME, fullName);
                    c.put(AppConstants.TAG_EMAIL_ADDRESS, email);

                    //Add the contact to the list.
                    contactList.add(c);
                }
            }
            catch (Exception ex)
            {
                logOutput("Exception parsing result: " + ex.toString());
            }

        }

        //Update parsed JSON data into ListView
        ListAdapter adapter = new SimpleAdapter(
                this, contactList,
                R.layout.dir_list_item, new String[]{AppConstants.TAG_FULL_NAME,
                AppConstants.TAG_EMAIL_ADDRESS},
                new int[]{R.id.name, R.id.email});

        resultsListView.setAdapter(adapter);

        //Disable the loading indicator.
        loadingIndicator.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    public void onAuthorized()
    {
        isAuthorized = true;

        //Get the application configuration, which will contain the user's email address.
        //The user's email address is sent to the BEMS Directory Lookup Service to identify them.
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

        //Enable the search button.
        searchButton.setEnabled(true);

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
