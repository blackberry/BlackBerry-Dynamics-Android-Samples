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

package com.msohm.sample.crossthebridge;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.good.gd.GDAndroid;
import com.good.gd.GDServiceDetail;
import com.good.gd.GDServiceProvider;
import com.good.gd.GDServiceType;
import com.good.gd.GDStateListener;
import com.good.gd.file.File;
import com.good.gd.file.FileOutputStream;
import com.good.gd.icc.GDICCForegroundOptions;
import com.good.gd.icc.GDServiceClient;
import com.good.gd.icc.GDServiceException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements GDStateListener
{
    //The service IDs.
    private static final String BLACKBERRY_BRIDGE_APP_ID = "com.blackberry.intune.bridge";
    private static final String EDIT_FILE_SERVICE = "com.good.gdservice.edit-file";
    private static final String SERVICE_VERSION = "1.0.0.0";
    private static final String SERVICE_METHOD = "editFile";

    private static final String FILE_NAME = "WordDoc.docx";

    private Button editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Initialize BlackBerry Dynamics.
        GDAndroid.getInstance().activityInit(this);

        setContentView(R.layout.activity_main);

        editButton = findViewById(R.id.editWordButton);
    }

    //Looks for a service provider that supports the directory lookup service.
    public void onFindBridge(View view)
    {
        Vector<GDServiceProvider> providers = GDAndroid.getInstance().getServiceProvidersFor(
                EDIT_FILE_SERVICE,
                "1.0.0.0", GDServiceType.GD_SERVICE_TYPE_APPLICATION);

        String serviceDetails = parseServiceDetails(providers);

        TextView output = findViewById(R.id.outputTextView);
        output.setText(serviceDetails);
    }

    //Searches the service providers for the BlackBerry Bridge edit file service.
    private String parseServiceDetails(Vector<GDServiceProvider> providers)
    {
        //Creates the string to display on screen.
        StringBuffer sb = new StringBuffer();

        for (int count = 0; count < providers.size(); count++)
        {
            GDServiceProvider provider = providers.get(count);

            if (BLACKBERRY_BRIDGE_APP_ID.equalsIgnoreCase(provider.getIdentifier()))
            {
                sb.append("✔ BlackBerry Bridge detected.\n");

                Vector<GDServiceDetail> serviceDetails = provider.getServices();

                for (int serviceCount = 0; serviceCount < serviceDetails.size(); serviceCount++) {
                    GDServiceDetail serviceDetail = serviceDetails.get(serviceCount);

                    if (EDIT_FILE_SERVICE.equalsIgnoreCase(serviceDetail.getIdentifier()))
                    {
                        sb.append("✔ BlackBerry Bridge edit file service detected.\n");
                        sb.append("✔ Ready to edit document.\n");

                        //Enable the edit button.
                        editButton.setEnabled(true);
                    }
                }
            }
        }

        if (sb.length() == 0)
        {
            sb.append("❌ BlackBerry Bridge not found.");
        }

        return sb.toString();
    }

    //Uses BlackBerry Dynamics AppKinetics service to securely send the file to BlackBerry Bridge.
    public void onEditDoc(View view)
    {
        final File file = getDocFile();

        try {
            GDServiceClient.sendTo(BLACKBERRY_BRIDGE_APP_ID,
                    EDIT_FILE_SERVICE,
                    SERVICE_VERSION,
                    SERVICE_METHOD,
                    null,
                    new String[]{file.getAbsolutePath()},
                    GDICCForegroundOptions.PreferPeerInForeground);
        }
        catch (final GDServiceException gdServiceException)
        {
            TextView output = view.findViewById(R.id.outputTextView);
            output.setText("Exception: " + gdServiceException.getMessage());
        }
    }

    //Copies a sample Word document from the application's asset folder into the BlackBerry
    //Dynamics secure file system.
    @Nullable
    private File getDocFile() {
        try {
            final InputStream inputStream = getAssets().open(FILE_NAME);
            final File file = new File("/", FILE_NAME);

            if (!file.exists()) {
                file.createNewFile();
            } else {
                return file;
            }

            final FileOutputStream fileOutputStream = new FileOutputStream(file);

            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                fileOutputStream.write(buf, 0, len);
            }
            inputStream.close();
            fileOutputStream.close();

            return file;
        } catch (final IOException ioException) {
            return null;
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
    public void onUpdateEntitlements() {

    }
}
