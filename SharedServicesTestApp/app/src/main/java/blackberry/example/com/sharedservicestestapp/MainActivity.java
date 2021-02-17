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

package blackberry.example.com.sharedservicestestapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.good.gd.GDAndroid;
import com.good.gd.GDServiceProvider;
import com.good.gd.GDServiceType;
import com.good.gd.GDStateListener;
import com.good.gd.file.File;
import com.good.gd.file.FileOutputStream;
import com.good.gd.file.GDFileSystem;
import com.good.gd.icc.GDICCForegroundOptions;
import com.good.gd.icc.GDServiceClient;
import com.good.gd.icc.GDServiceException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements GDStateListener {

    //Transfer File Service https://marketplace.blackberry.com/services/273065
    private static final String TRANSFER_FILE_SERVICE = "com.good.gdservice.transfer-file";
    private static final String TRANSFER_FILE_SERVICE_VERSION = "1.0.0.0";

    //Edit File Service https://marketplace.blackberry.com/services/292114
    private static final String EDIT_FILE_SERVICE = "com.good.gdservice.edit-file";
    private static final String EDIT_FILE_SERVICE_VERSION = "1.0.0.0";

    //Send Email Service https://marketplace.blackberry.com/services/855115
    private static final String SEND_EMAIL_SERVICE = "com.good.gfeservice.send-email";
    private static final String SEND_EMAIL_SERVICE_VERSION = "1.0.0.0";

    //Create Calendar Item Service https://marketplace.blackberry.com/services/299461271
    private static final String CREATE_CALENDAR_ITEM_SERVICE = "com.good.gdservice.create-calendar-item";
    private static final String CREATE_CALENDAR_ITEM_SERVICE_VERSION = "1.0.0.0";

    private static final String WORD_DOC_FILE_NAME = "word.docx";

    //Open HTTP URL Service https://marketplace.blackberry.com/services/795018
    private static final String OPEN_HTTP_URL_SERVICE = "com.good.gdservice.open-url.http";
    private static final String OPEN_HTTP_URL_SERVICE_VERSION = "1.0.0.0";

    //Open UEM App Catalog Service https://marketplace.blackberry.com/services/1971285451
    private static final String OPEN_UEM_APP_CATALOG_SERVICE = "com.blackberry.gdservice.open-catalog";
    private static final String OPEN_UEM_APP_CATALOG_SERVICE_VERSION = "1.0.0.0";

    private Vector<GDServiceProvider> appDetails = null;
    private String appPackageName = null;

    private boolean openHTTPURLFullScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GDAndroid.getInstance().activityInit(this);
        setContentView(R.layout.activity_main);

        this.appPackageName = getPackageName();
    }


    public void onTransferClick(View view)
    {
        final List<GDServiceProvider> serviceProviders =
                getServiceProviders(TRANSFER_FILE_SERVICE, TRANSFER_FILE_SERVICE_VERSION);
        showChoicesAlert(serviceProviders, TRANSFER_FILE_SERVICE);
    }

    public void onEditClick(View view)
    {
        final List<GDServiceProvider> serviceProviders =
                getServiceProviders(EDIT_FILE_SERVICE, EDIT_FILE_SERVICE_VERSION);
        showChoicesAlert(serviceProviders, EDIT_FILE_SERVICE);
    }

    public void onSendEmailClick(View view)
    {
        final List<GDServiceProvider> serviceProviders =
                getServiceProviders(SEND_EMAIL_SERVICE, SEND_EMAIL_SERVICE_VERSION);
        showChoicesAlert(serviceProviders, SEND_EMAIL_SERVICE);
    }

    public void onOpenHttpURLClick(View view)
    {
        openHTTPURLFullScreen = false;
        final List<GDServiceProvider> serviceProviders =
                getServiceProviders(OPEN_HTTP_URL_SERVICE, OPEN_HTTP_URL_SERVICE_VERSION);
        showChoicesAlert(serviceProviders, OPEN_HTTP_URL_SERVICE);
    }

    public void onOpenHttpURLFSClick(View view)
    {
        openHTTPURLFullScreen = true;
        final List<GDServiceProvider> serviceProviders =
                getServiceProviders(OPEN_HTTP_URL_SERVICE, OPEN_HTTP_URL_SERVICE_VERSION);
        showChoicesAlert(serviceProviders, OPEN_HTTP_URL_SERVICE);
    }

    public void onOpenUEMAppCatalogClick(View view)
    {
        final List<GDServiceProvider> serviceProviders =
                getServiceProviders(OPEN_UEM_APP_CATALOG_SERVICE, OPEN_UEM_APP_CATALOG_SERVICE_VERSION);
        showChoicesAlert(serviceProviders, OPEN_UEM_APP_CATALOG_SERVICE);
    }

    public void onCreateCalendarItemClick(View view)
    {
        final List<GDServiceProvider> serviceProviders =
                getServiceProviders(CREATE_CALENDAR_ITEM_SERVICE, CREATE_CALENDAR_ITEM_SERVICE_VERSION);
        showChoicesAlert(serviceProviders, CREATE_CALENDAR_ITEM_SERVICE);
    }

    //BlackBerry Dynamics can register to be service providers.  This method searches for apps that are
    // service providers for the specified service ID and version.
    public List<GDServiceProvider> getServiceProviders(String serviceID, String serviceVersion) {

        final List<GDServiceProvider> options = new ArrayList<GDServiceProvider>();
        this.appDetails = GDAndroid.getInstance().getServiceProvidersFor(serviceID,
                serviceVersion,
                GDServiceType.GD_SERVICE_TYPE_APPLICATION);

        for (GDServiceProvider detail : this.appDetails) {
            final String servicePackage = detail.getAddress();

            if ( otherApplication(servicePackage))
            {
                final String serviceName = detail.getName();
                if (!TextUtils.isEmpty(serviceName))
                {
                    options.add(detail);
                }
            }
        }
        return options;
    }


    //There may be more than one application on the device that is a service provider a particular
    //service.  This prompts the user to choose which app they wish to use.
    private void showChoicesAlert(final List<GDServiceProvider> serviceProviders, final String serviceID)
    {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (serviceProviders.isEmpty()){
            builder.setTitle("Error").setMessage("No providers found for this service.");
        } else {
            builder.setTitle("Send To")
                    .setAdapter(
                            new IconAndTextListAdapter(this, serviceProviders),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    GDServiceProvider svc = serviceProviders.get(which);
                                    String serviceAddress = addressLookup(svc.getName());

                                    switch (serviceID)
                                    {
                                        case TRANSFER_FILE_SERVICE:
                                            doTransferFile(serviceAddress);
                                            break;

                                        case EDIT_FILE_SERVICE:
                                            doEditFile(serviceAddress);
                                            break;

                                        case SEND_EMAIL_SERVICE:
                                            doSendEmail(serviceAddress);
                                            break;

                                        case OPEN_HTTP_URL_SERVICE:
                                            doOpenHttpUrl(serviceAddress);
                                            break;

                                        case OPEN_UEM_APP_CATALOG_SERVICE:
                                            doOpenUEMAppCatalog(serviceAddress);
                                            break;

                                        case CREATE_CALENDAR_ITEM_SERVICE:
                                            doCreateCalendarItem(serviceAddress);
                                            break;

                                        default:
                                            TextView output = findViewById(R.id.outputTextView);
                                            output.setText("No action method defined for this service: "
                                                    + serviceAddress);
                                            break;
                                    }
                                }
                            }
                    );
        }
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                }
        ).setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void doTransferFile(String serviceAddress)
    {
        if (copyFileIfNeeded(WORD_DOC_FILE_NAME))
        {
            try
            {
                final List<String> filesToSend = new ArrayList<String>();
                filesToSend.add("/" + WORD_DOC_FILE_NAME);

                GDServiceClient.sendTo(serviceAddress,
                        TRANSFER_FILE_SERVICE,
                        TRANSFER_FILE_SERVICE_VERSION,
                        "transferFile", null,
                        filesToSend.toArray(new String[filesToSend.size()]),
                        GDICCForegroundOptions.PreferPeerInForeground);
            } catch (final GDServiceException gdServiceException)
            {
                TextView output = findViewById(R.id.outputTextView);
                output.setText("AppKineticsModel.sendFiles: unable to transfer file: "
                        + gdServiceException.toString());
            }
        }
    }

    private void doEditFile(String serviceAddress)
    {
        if (copyFileIfNeeded(WORD_DOC_FILE_NAME))
        {
            try
            {
                File file = new File("/", WORD_DOC_FILE_NAME);

                GDServiceClient.sendTo(serviceAddress,
                        EDIT_FILE_SERVICE,
                        EDIT_FILE_SERVICE_VERSION,
                        "editFile", null,
                        new String[]{file.getAbsolutePath()},
                        GDICCForegroundOptions.PreferPeerInForeground);
            } catch (final GDServiceException gdServiceException)
            {
                TextView output = findViewById(R.id.outputTextView);
                output.setText("AppKineticsModel.sendFiles: unable to transfer file: "
                        + gdServiceException.toString());
            }
        }
    }

    private void doSendEmail(String serviceAddress)
    {
        if (copyFileIfNeeded(WORD_DOC_FILE_NAME))
        {
            try
            {
                Map<String, Object> params = new HashMap<>();

                List<String> recipients =  new ArrayList<>(1);
                recipients.add("someone@blackberry.com");
                params.put("to", recipients);

                params.put("subject", "Send Email Service");

                params.put("body",
                "This email was created using the BlackBerry Dynamics Send Email Service");

                //Add attachments
                File file = new File(WORD_DOC_FILE_NAME);
                String[] attachments = {file.getAbsolutePath()};

                GDServiceClient.sendTo(serviceAddress,
                        SEND_EMAIL_SERVICE,
                        SEND_EMAIL_SERVICE_VERSION,
                        "sendEmail",
                        params,
                        attachments,
                        GDICCForegroundOptions.PreferPeerInForeground);


            } catch (final GDServiceException gdServiceException)
            {
                TextView output = findViewById(R.id.outputTextView);
                output.setText("AppKineticsModel.sendFiles: unable to transfer file: "
                        + gdServiceException.toString());
            }
        }
    }

    private void doOpenHttpUrl(String serviceAddress)
    {
        try
        {
            Map<String, Object> params = new HashMap<>();

            params.put("url", "https://www.blackberry.com");

            if (openHTTPURLFullScreen)
            {
                params.put("fullscreen", true);
            }

            GDServiceClient.sendTo(serviceAddress,
                    OPEN_HTTP_URL_SERVICE,
                    OPEN_HTTP_URL_SERVICE_VERSION,
                    "open",
                    params,
                    null,
                    GDICCForegroundOptions.PreferPeerInForeground);


        } catch (final GDServiceException gdServiceException)
        {
            TextView output = findViewById(R.id.outputTextView);
            output.setText("AppKineticsModel.doOpenHttpUrl: unable to open url: "
                    + gdServiceException.toString());
        }
    }

    private void doOpenUEMAppCatalog(String serviceAddress)
    {
        try
        {
            Map<String, Object> params = new HashMap<>();

            params.put("url", "uemappstore://openAppDetails?androidId=com.blackberry.docstogo.gdapp");

            GDServiceClient.sendTo(serviceAddress,
                    OPEN_UEM_APP_CATALOG_SERVICE,
                    OPEN_UEM_APP_CATALOG_SERVICE_VERSION,
                    "open",
                    params,
                    null,
                    GDICCForegroundOptions.PreferPeerInForeground);


        } catch (final GDServiceException gdServiceException)
        {
            TextView output = findViewById(R.id.outputTextView);
            output.setText("AppKineticsModel.doOpenUEMAppCatalog: unable to open url: "
                    + gdServiceException.toString());
        }
    }

    private void doCreateCalendarItem(String serviceAddress)
    {
        try
        {
            Map<String,Object> params = new HashMap<>();
            params.put("title","Calendar Title");
            params.put("description","Some content");
            params.put("location","The location");
            params.put("startTime","2020-12-01T09:00:00+00:00");
            params.put("endTime","2020-12-01T10:00:00+00:00");
            params.put("allDay",false);

            GDServiceClient.sendTo(serviceAddress,
                    CREATE_CALENDAR_ITEM_SERVICE,
                    CREATE_CALENDAR_ITEM_SERVICE_VERSION,
                    "createCalendarItem",
                    params,
                    null,
                    GDICCForegroundOptions.PreferPeerInForeground);

        } catch (final GDServiceException gdServiceException)
        {
            TextView output = findViewById(R.id.outputTextView);
            output.setText("AppKineticsModel.sendFiles: unable to transfer file: "
                    + gdServiceException.toString());
        }
    }

    //Copies the bundled word.docx to BlackBerry Dynamics secure storage.
    private boolean copyFileIfNeeded(String filename)
    {
        try
        {
            File theFile = new File(filename);

            if (!theFile.exists())
            {

                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open(filename);
                FileOutputStream out =
                        GDFileSystem.openFileOutput(filename, Context.MODE_PRIVATE);

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();

            }
            return true;
        }
        catch(Exception e)
        {
            //Failed to copy the video file
            TextView output = findViewById(R.id.outputTextView);
            output.setText("File Copy Failed. " + e.toString());
            return false;
        }
    }

    /**
     * otherApplication - checks if string refers to self (this package)
     *
     * @param packageName  The package name
     * @return True if the package name refers to self, false otherwise.
     */
    private boolean otherApplication(final String packageName) {
        return ! appPackageName.equalsIgnoreCase(packageName);
    }


    private String addressLookup(String name)
    {
        if (TextUtils.isEmpty(name) || (null == this.appDetails)) {
            throw new NoSuchElementException(name);
        }

        for (GDServiceProvider detail : this.appDetails) {
            if (name.equals(detail.getName())) {
                return detail.getAddress();
            }
        }
        throw new NoSuchElementException(name);
    }

    @Override
    public void onAuthorized() {    }

    @Override
    public void onLocked() {    }

    @Override
    public void onWiped() {    }

    @Override
    public void onUpdateConfig(Map<String, Object> map) {    }

    @Override
    public void onUpdatePolicy(Map<String, Object> map) {    }

    @Override
    public void onUpdateServices() {    }

    @Override
    public void onUpdateEntitlements() {    }
}