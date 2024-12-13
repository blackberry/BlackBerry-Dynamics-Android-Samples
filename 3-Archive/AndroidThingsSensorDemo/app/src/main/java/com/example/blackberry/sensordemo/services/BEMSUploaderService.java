package com.example.blackberry.sensordemo.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.example.blackberry.sensordemo.models.BemsServer;
import com.example.blackberry.sensordemo.networking.GDHttpConnector;
import com.example.blackberry.sensordemo.networking.HttpRequestParams;
import com.good.gd.GDAndroid;
import com.good.gd.GDAppServer;
import com.good.gd.GDServiceDetail;
import com.good.gd.GDServiceProvider;
import com.good.gd.GDServiceType;
import com.good.gd.apache.http.entity.StringEntity;
import com.good.gd.apache.http.message.BasicHeader;
import com.good.gd.file.FileInputStream;
import com.good.gd.file.GDFileSystem;
import com.good.gd.utility.GDAuthTokenCallback;
import com.good.gd.utility.GDUtility;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;

public class BEMSUploaderService extends Service implements GDAuthTokenCallback {

    private static final String TAG = BEMSUploaderService.class.getSimpleName();
    public static final String ACTION_UPLOAD = "ACTION_UPLOAD";
    public static final String EXTRA_LOCAL_FILE = "EXTRA_LOCAL_FILE";
    public static final String EXTRA_REMOTE_FILE = "EXTRA_REMOTE_FILE";

    //The BEMS Docs service ID.
    private static final String DOCS_SERVICE = "com.good.gdservice.enterprise.docs";
    private boolean hasDocsService = false;

    private String bdAuthToken;

    private static final String THE_URL = "/docs/1/Local%20Share/AndroidThingsLogs/";

    private static final String SUCCESS_MESSAGE = "{\"Message\":\"Created\"}";

    //ArrayLists to hold the BEMS server details.
    ArrayList<BemsServer> docsServers = new ArrayList<>();

    private String localFile;
    private String remoteFile;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");

        if(intent.getAction() == ACTION_UPLOAD && intent.hasExtra(EXTRA_LOCAL_FILE) && intent.hasExtra(EXTRA_REMOTE_FILE)) {
            uploadFile(intent.getStringExtra(EXTRA_LOCAL_FILE), intent.getStringExtra(EXTRA_REMOTE_FILE));
        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    private void uploadFile(String localFile, String remoteFile)
    {
        //pass the file parameters to instance variables
        this.localFile = localFile;
        this.remoteFile = remoteFile;


        //Get Service Provider Info and Populate DocsServers ArrayList
        getServiceProviders();

        //Ensure there is a known server to connect to.
        if (!hasDocsService)
        {
            Log.e(TAG, "Error! BEMS Docs Service is not configured.");
        }
        else
        {
            //Upload the log file through BEMS.

            //Use the first BEMS Server found.
            String server = docsServers.get(0).getServer();

            //Request a GD Auth Token used for authentication with BEMS.
            GDUtility util = new GDUtility();
            util.getGDAuthToken("", server, this);

        }
    }

    @Override
    public void onGDAuthTokenSuccess(String token) {

        bdAuthToken = token;
        Log.i(TAG, "Received GD auth token.");

        //Token has been received, upload the file.
        doUpload();
    }

    @Override
    public void onGDAuthTokenFailure(int errorCode, String error) {

        Log.e(TAG, "Failed to receive GD auth token.  ErrorCode: " + errorCode + " Error: " + error);

    }

    //Triggers upload of the file.
    private void doUpload() {

        //displayOutput(true);

        ArrayList<BasicHeader> headers = new ArrayList<>();

        //Required - Set the GD auth token to authenticate with the docs server.
        headers.add(new BasicHeader("X-Good-GD-AuthToken", bdAuthToken));

        String uploadFilename = remoteFile;

        String urlWithServer = "https://" + docsServers.get(0).getServer() + THE_URL + uploadFilename;

        Log.d(TAG, "Requesting URL: " + urlWithServer);

        //Build the HttpRequestParams.
        HttpRequestParams params = new HttpRequestParams(urlWithServer, headers, null, HttpRequestParams.PUT);

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
                FileInputStream inputStream = GDFileSystem.openFileInput(localFile);

                if ( inputStream != null ) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString;
                    StringBuilder stringBuilder = new StringBuilder();

                    while ( (receiveString = bufferedReader.readLine()) != null ) {
                        stringBuilder.append(receiveString);
                    }

                    inputStream.close();
                    params[0].setPostBody(new StringEntity(stringBuilder.toString()));
                }

                GDHttpConnector http = new GDHttpConnector();
                return http.doRequest(params[0]);

            } catch (Exception e) {
                return e.toString();
            }
        }

        /**
         * Display the result returned from the network call.
         */
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "Upload Result: " + result);
            BEMSUploaderService.this.stopSelf();
        }
    }

    //Looks for a service provider that supports the BEMS Docs service.
    private void getServiceProviders()
    {
        Vector<GDServiceProvider> providers = GDAndroid.getInstance().getServiceProvidersFor(
                DOCS_SERVICE,
                "1.0.0.0", GDServiceType.GD_SERVICE_TYPE_SERVER);

        parseServiceDetails(providers);
    }

    //Extract the service and server details.
    private void parseServiceDetails(Vector<GDServiceProvider> providers)
    {
        //Holds the server details as we verify the services the server supports.
        ArrayList<BemsServer> serverCache = new ArrayList<>();

        hasDocsService = false;

        for (int count = 0; count < providers.size(); count++)
        {
            GDServiceProvider provider = providers.get(count);

            Vector<GDAppServer> servers = provider.getServerCluster();
            serverCache.clear();

            for (int serverCount = 0; serverCount < servers.size(); serverCount++)
            {
                GDAppServer server = servers.get(serverCount);

                BemsServer gserver = new BemsServer(server.server + ":" + server.port, server.priority);
                serverCache.add(gserver);
            }

            Vector<GDServiceDetail> serviceDetails = provider.getServices();

            for (int serviceCount = 0; serviceCount < serviceDetails.size(); serviceCount++)
            {
                GDServiceDetail serviceDetail = serviceDetails.get(serviceCount);

                if (serviceDetail.getIdentifier().contains(DOCS_SERVICE))
                {
                    hasDocsService = true;
                    docsServers = serverCache;
                }
            }
        }

    }

}
