/* Copyright (c) 2023 BlackBerry Ltd.
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

package com.good.gd.example.appkinetics.saveeditclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.good.gd.GDAndroid;
import com.good.gd.GDServiceProvider;
import com.good.gd.GDServiceType;
import com.good.gd.GDStateListener;
import com.good.gd.file.File;
import com.good.gd.file.FileInputStream;
import com.good.gd.file.FileOutputStream;
import com.good.gd.icc.GDICCForegroundOptions;
import com.good.gd.icc.GDServiceClient;
import com.good.gd.icc.GDServiceException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SaveEditClient extends SampleAppActivity implements GDStateListener {

    private static final String TAG = SaveEditClient.class.getSimpleName();
    private static final String SERVICE_NAME = "com.good.gdservice.edit-file";
    private static final String SERVICE_VERSION = "1.0.0.0";
    private static final String SERVICE_METHOD = "editFile";
    private static final String FILE_NAME = "DataFile.txt";

    private TextView dataView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GDAndroid.getInstance().activityInit(this);
        GDSaveEditClientListener.getInstance().setOnReceiveAttachmentsEventListener(
                new GDSaveEditClientListener.OnReceiveAttachmentsEventListener() {
                    @Override
                    public void onReceiveAttachments(final String[] attachments) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (attachments != null) {
                                    refreshData(attachments[0]);
                                }
                            }
                        });
                    }
                }
        );
        setContentView(R.layout.activity_main);
        dataView = findViewById(R.id.data_view);

        findViewById(R.id.app_bar_action).setOnClickListener(view -> {
            processSendActionSelected();
        });

        setupAppBar(getString(R.string.app_name));

        View mainView = findViewById(R.id.bbd_appkinetics_save_edit_client_UI);

        adjustViewsIfEdgeToEdgeMode(mainView, null, dataView);
    }

    private void processSendActionSelected() {
        showDialog();
    }

    @Override
    public void onAuthorized() {
        if (GDSaveEditClientListener.getInstance().getPendingAttachments() != null) {
            refreshData(GDSaveEditClientListener.getInstance().getPendingAttachments()[0]);
        } else {
            final File file = getTextFile();

            final String text = readDataFromFile(file.getAbsolutePath());

            dataView.setText(text);
        }
    }

    private void showDialog() {
        final List<GDServiceProvider> services = getServiceProviders();
        final List<String> serviceNames = getServiceNames(services);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.send_to_label)
                .setAdapter(
                        new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, serviceNames),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog,
                                                final int which) {
                                sendFile(services.get(which).getAddress());
                            }
                        }
                )
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog,
                                                final int id) {
                                // User cancelled the dialog
                            }
                        }
                ).setCancelable(true);

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private List<GDServiceProvider> getServiceProviders() {
        final List<GDServiceProvider> serviceProviders = GDAndroid.getInstance()
                .getServiceProvidersFor(SERVICE_NAME, SERVICE_VERSION,
                        GDServiceType.GD_SERVICE_TYPE_APPLICATION);

        for (final GDServiceProvider serviceProvider : serviceProviders) {
            if (serviceProvider.getAddress().equals(getPackageName())) {
                serviceProviders.remove(serviceProvider);
                break;
            }
        }
        return serviceProviders;
    }

    private List<String> getServiceNames(final List<GDServiceProvider> serviceProviders) {
        final List<String> serviceNames = new ArrayList<String>();
        for (final GDServiceProvider serviceProvider : serviceProviders) {
            serviceNames.add(serviceProvider.getName());
        }
        return serviceNames;
    }

    private void sendFile(final String serviceId) {
        final File file = getTextFile();

        try {
            GDServiceClient.sendTo(serviceId,
                    SERVICE_NAME,
                    SERVICE_VERSION,
                    SERVICE_METHOD,
                    null,
                    new String[]{file.getAbsolutePath()},
                    GDICCForegroundOptions.PreferPeerInForeground);
        } catch (final GDServiceException gdServiceException) {
            Log.e(TAG, gdServiceException.getMessage());
        }
    }

    private File getTextFile() {
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

    private void refreshData(final String filePath) {
        final String editedText = readDataFromFile(filePath);
        dataView.setText(editedText);

        try {
            final File file = new File("/", FILE_NAME);

            final FileOutputStream overWritingFile = new FileOutputStream(file.getAbsolutePath());
            overWritingFile.write(editedText.getBytes());
            overWritingFile.flush();
            overWritingFile.close();
        } catch (final IOException ioException) {
            Log.e(TAG, ioException.getMessage());
        }
    }

    private String readDataFromFile(final String filePath) {
        String dataFromFile = "";
        byte data[];
        try {
            final InputStream inputStream = new FileInputStream(filePath);
            if (inputStream.available() > 0) {
                data = new byte[inputStream.available()];
                inputStream.read(data);
                dataFromFile = new String(data, "UTF-8");
                inputStream.close();
            }

        } catch (final IOException ioException) {
            dataFromFile = "File read error";
        }
        return dataFromFile;
    }

    @Override
    public void onLocked() {
        Log.d(TAG, "MainActivity.onLocked()");
    }

    @Override
    public void onWiped() {
        Log.d(TAG, "MainActivity.onWiped()");
    }

    @Override
    public void onUpdateConfig(final Map<String, Object> stringObjectMap) {
        Log.d(TAG, "MainActivity.onUpdateConfig()");
    }

    @Override
    public void onUpdatePolicy(final Map<String, Object> stringObjectMap) {
        Log.d(TAG, "MainActivity.onUpdatePolicy()");
    }

    @Override
    public void onUpdateServices() {
        Log.d(TAG, "MainActivity.onUpdateServices()");
    }

    @Override
    public void onUpdateEntitlements() {
        Log.d(TAG, "onUpdateEntitlements()");
    }
}