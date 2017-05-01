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

package com.msohm.blackberry.samples.bemsdocsservicesample;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.apache.http.message.BasicHeader;
import com.good.gd.utility.GDAuthTokenCallback;
import com.good.gd.utility.GDUtility;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DocList extends AppCompatActivity implements GDStateListener,
        GDAuthTokenCallback
{

    private ArrayList<BemsServer> docsServers = new ArrayList<>();
    private String gdAuthToken;
    private ListView docListView;
    private boolean isAuthorized = false;
    private ProgressBar loadingIndicator;
    private ArrayList<HashMap<String, String>> docList;
    private String pathCrumbs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Initialize Good Dynamics.
        GDAndroid.getInstance().activityInit(this);

        docList = new ArrayList<>();

        docsServers = (ArrayList<BemsServer>)getIntent().getSerializableExtra("com.msohm.blackberry.samples.bemsdocsservicesample.DocServers");

        setContentView(R.layout.activity_doc_list);

        docListView = (ListView)findViewById(R.id.docListView);

        docListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {

                //Enable the loading indicator.
                loadingIndicator.setVisibility(ProgressBar.VISIBLE);

                TextView txtName = (TextView) view.findViewById(R.id.name);
                TextView txtSize = (TextView) view.findViewById(R.id.size);

                if (txtSize.getText().length() == 0)
                {
                    //If empty size, it's a directory.
                    listFiles(txtName.getText().toString());

                }
                else
                {
                    //If it has a size, it's a file.
                    listMetaData(txtName.getText().toString());
                }
            }
        });

        loadingIndicator = (ProgressBar) findViewById(R.id.loadingIndicator);

        Spinner serverListSpinner = (Spinner)findViewById(R.id.serverListSpinner);
        ArrayAdapter<BemsServer> serverListAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, docsServers);
        serverListSpinner.setAdapter(serverListAdapter);

    }


    //Creates the request to list files in a specified directory.
    private void listFiles(String path)
    {
        //Check if we are going into a new directory or back a level
        if (path.contentEquals(".."))
        {
            //Going up a directory.
            //Strip off the last directory.
            pathCrumbs = pathCrumbs.substring(0, pathCrumbs.lastIndexOf('/'));
        }
        else if (path.length() > 0)
        {
            //Going into a new directory.
            //Append the newly requested repo/directory to the end of the current path.
            pathCrumbs += "/" + Uri.encode(path);
        }

        //Add the current path to the end of the request URL.
        String url = "/docs/1" + pathCrumbs;

        //Load the URL.
        loadUrl(url);
    }

    //Creates the request to get metadata for a specified file.
    private void listMetaData(String path)
    {
        //Build the URL to request meta data for the current file.
        StringBuilder url = new StringBuilder();
        url.append("/docs/1");
        url.append(pathCrumbs);
        url.append('/');
        url.append(Uri.encode(path));
        url.append("?metadata");
        loadUrl(url.toString());
    }

    //Prepares the headers and parameters required to load the specified URL.
    private void loadUrl(String url)
    {
        ArrayList<BasicHeader> headers = new ArrayList<>();

        //Required - Set the GD auth token to authenticate with the docs server.
        headers.add(new BasicHeader("X-Good-GD-AuthToken", gdAuthToken));

        //Get the selected server:
        Spinner serverListSpinner = (Spinner)findViewById(R.id.serverListSpinner);
        int serverIndex = serverListSpinner.getSelectedItemPosition();

        String urlWithServer = "https://" + docsServers.get(serverIndex).getServer() + url;

        logOutput("Requesting URL: " + urlWithServer);

        //Build the HttpRequestParams.
        HttpRequestParams params = new HttpRequestParams(urlWithServer, headers, null, HttpRequestParams.GET);

        DownloadTask task = new DownloadTask();
        task.execute(params);
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
            parseList(result);
        }
    }

    //Parses the JSON returned from BEMS.
    private void parseList(String result)
    {
        if (result != null)
        {
            try {
                Object object = new JSONTokener(result).nextValue();

                if (object instanceof JSONArray)
                {
                    //We have a list of files and directories to parse.
                    JSONArray docs = (JSONArray)object;

                    //Clear out all existing files and documents.
                    docList.clear();

                    if (pathCrumbs.length() > 0)
                    {
                        //If not in the root directory, add a .. entry to go up a level.
                        HashMap<String, String> d = new HashMap<>();
                        d.put(AppConstants.TAG_NAME, "..");
                        d.put(AppConstants.TAG_SIZE, "");
                        d.put(AppConstants.TAG_ICON, Integer.toString(R.drawable.folder_32));

                        //Add the up level to the list.
                        docList.add(d);
                    }

                    int numDocs = docs.length();

                    //Iterate through each document and folder.
                    for (int count = 0; count < numDocs; count++) {
                        //Extract the individual contact.
                        JSONObject doc = docs.getJSONObject(count);

                        //Create a HashMap to store the individual doc.
                        HashMap<String, String> d = new HashMap<>();

                        //Extract the file/directory name values.
                        String name = doc.getString(AppConstants.TAG_NAME);
                        d.put(AppConstants.TAG_NAME, name);

                        //Size doesn't exist on the repository list, only file list.
                        //Optionally retrieve the size if it's there.
                        String size = "";
                        if (doc.has(AppConstants.TAG_SIZE)) {
                            size = "Size: " + doc.getString(AppConstants.TAG_SIZE);

                            //If there is a size, it's a file.  Use the file image
                            d.put(AppConstants.TAG_ICON, Integer.toString(R.drawable.file_32));
                        } else if (pathCrumbs.length() == 0) {
                            //If no pathCrumb, use the server icon.
                            d.put(AppConstants.TAG_ICON, Integer.toString(R.drawable.server_32));
                        } else {
                            //It's a folder, use the folder icon.
                            d.put(AppConstants.TAG_ICON, Integer.toString(R.drawable.folder_32));
                        }

                        d.put(AppConstants.TAG_SIZE, size);

                        //Add the doc to the list.
                        docList.add(d);
                    }

                    //Update parsed JSON data into ListView
                    ListAdapter adapter = new SimpleAdapter(
                            this, docList,
                            R.layout.doc_list_item, new String[]{AppConstants.TAG_ICON,
                            AppConstants.TAG_NAME, AppConstants.TAG_SIZE},
                            new int[]{R.id.imageIcon, R.id.name, R.id.size, });

                    docListView.setAdapter(adapter);
                }
                else if (object instanceof JSONObject)
                {
                    //We have file metadata to parse.
                    JSONObject file = (JSONObject)object;

                    StringBuilder sb = new StringBuilder();

                    sb.append("Name: ");
                    sb.append(file.getString(AppConstants.TAG_NAME));
                    sb.append('\n');
                    sb.append("Created: ");
                    sb.append(file.getString(AppConstants.TAG_CREATED));
                    sb.append('\n');
                    sb.append("Modified: ");
                    sb.append(file.getString(AppConstants.TAG_MODIFIED));
                    sb.append('\n');
                    sb.append("E-Tag: ");
                    sb.append(file.getString(AppConstants.TAG_ETAG));
                    sb.append('\n');
                    sb.append("Attributes: ");
                    sb.append(file.getString(AppConstants.TAG_ATTRIBUTES));

                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("File Metadata");
                    alert.setMessage(sb.toString());
                    alert.setNeutralButton ("Close", null);
                    alert.show();
                }
            }
            catch (Exception ex)
            {
                logOutput("Exception parsing result: " + ex.toString());

                //We have file metadata to parse.
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Error Parsing Output");
                alert.setMessage(result);
                alert.setNeutralButton ("Close", null);
                alert.show();
            }

            //Disable the loading indicator.
            loadingIndicator.setVisibility(ProgressBar.INVISIBLE);
        }
    }

    @Override
    public void onAuthorized()
    {
        isAuthorized = true;
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

    @Override
    public void onGDAuthTokenSuccess(String token)
    {
        gdAuthToken = token;
        logOutput("Received GD auth token.");

        //Reset the current path and list the document repositories.
        pathCrumbs = "";
        listFiles("");
    }

    @Override
    public void onGDAuthTokenFailure(int errorCode, String error)
    {
        logOutput("Failed to receive GD auth token.  ErrorCode: " + errorCode + " Error: " + error);
    }

    private void logOutput(String output)
    {
        Log.d("BEMS Docs Sample:", output);
    }
}
