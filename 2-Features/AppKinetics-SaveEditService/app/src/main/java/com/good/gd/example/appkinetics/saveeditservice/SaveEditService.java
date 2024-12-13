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

package com.good.gd.example.appkinetics.saveeditservice;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.good.gd.GDAndroid;
import com.good.gd.GDServiceProvider;
import com.good.gd.GDServiceType;
import com.good.gd.GDStateListener;
import com.good.gd.file.File;
import com.good.gd.file.FileInputStream;
import com.good.gd.file.FileOutputStream;
import com.good.gd.icc.GDICCForegroundOptions;
import com.good.gd.icc.GDService;
import com.good.gd.icc.GDServiceException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class SaveEditService extends SampleAppActivity implements GDStateListener {

    private static final String TAG = SaveEditService.class.getSimpleName();
    private static final String FILE_NAME = "DataFile.txt";
    private static final String SERVICE_NAME = "com.good.gdservice.save-edited-file";
    private static final String SERVICE_VERSION = "1.0.0.0";
    private static final String SERVICE_METHOD = "saveEdit";

    private EditText dataEditText;

    private Dialog appDescriptionDialog;
    private static boolean attachmentReceived;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GDAndroid.getInstance().activityInit(this);

        setContentView(R.layout.activity_main);

        dataEditText = findViewById(R.id.data_view);

        setupAppBar(getString(R.string.app_name));

        View mainView = findViewById(R.id.bbd_appkinetics_save_edit_service_UI);
        adjustViewsIfEdgeToEdgeMode(mainView, null, dataEditText);

        if (GDSaveEditServiceListener.getInstance().getPendingAttachments() != null) {
            readDataFromFile(GDSaveEditServiceListener.getInstance().getPendingAttachments()[0]);
        } else {
            if (!attachmentReceived) {
                if (appDescriptionDialog == null) {
                    appDescriptionDialog = createAppDescriptionDialog();
                }
                appDescriptionDialog.show();
            }
        }

        GDSaveEditServiceListener.getInstance().setOnReceiveAttachmentsEventListener(
                new GDSaveEditServiceListener.OnReceiveAttachmentsEventListener() {
                    @Override
                    public void onReceiveAttachments(final String[] attachments) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                /*
                                 * Checking if dialog was created in case if
                                 * onAuthorized was called before
                                 * onReceiveAttachments(..).
                                 */
                                if (appDescriptionDialog != null) {
                                    appDescriptionDialog.dismiss();
                                }

                                readDataFromFile(attachments[0]);

                                attachmentReceived = true;
                            }
                        });
                    }
                }
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        GDSaveEditServiceListener.getInstance().clearListener();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                processDoneActionSelected();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void processDoneActionSelected() {
        doneEditing();
    }

    @Override
    public void onAuthorized() {
    	Log.d(TAG, "MainActivity.onAuthorized()");
    }

    private Dialog createAppDescriptionDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(false)
                .setTitle(R.string.description_dialog_title)
                .setMessage(R.string.description_dialog_message)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog,
                                                final int which) {
                                dialog.dismiss();

                                finish();
                            }
                        });
        return builder.create();
    }

    private void readDataFromFile(final String filePath) {
        String dataFromFile = "";
        byte data[];
        try {
            final File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }

            final InputStream inputStream = new FileInputStream(filePath);
            if (inputStream.available() > 0) {
                data = new byte[inputStream.available()];
                inputStream.read(data);
                dataFromFile = new String(data, "UTF-8");
            }

            inputStream.close();
        } catch (final IOException ioException) {
            Log.e(TAG, ioException.getMessage());
            dataFromFile = "File read error";
        }

        dataEditText.setText(dataFromFile);

        Log.d(TAG, "MainActivity.readDataFromFile() exit");
    }

    private void doneEditing() {
        try {
            final String requestedApplication = GDSaveEditServiceListener.getInstance()
                    .getRequestedApplication();
            if (TextUtils.isEmpty(requestedApplication)) {
                return;
            }

            if(!isImplementingSaveEditedFile(requestedApplication)) {
                showNotImplementingDialog(requestedApplication);
                return;
            }

            final String editedText = dataEditText.getText().toString();
            final File file = new File("/", FILE_NAME);
            file.delete();
            file.createNewFile();

            final FileOutputStream fileOutputStream = new FileOutputStream(file.getAbsolutePath());
            fileOutputStream.write(editedText.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();

            GDService.replyTo(requestedApplication,
                    null,
                    GDICCForegroundOptions.PreferPeerInForeground,
                    new String[]{file.getAbsolutePath()},
                    null);
        } catch (final IOException ioException) {
            Log.e(TAG, ioException.getMessage());
        } catch (final GDServiceException gdServiceException) {
            Log.e(TAG, gdServiceException.getMessage());
        }
    }

    private boolean isImplementingSaveEditedFile(final String requestedApplication) {
        final List<GDServiceProvider> serviceProviders = getSavedEditedFileServiceProviders();

        for(final GDServiceProvider provider : serviceProviders) {
            if(provider.getIdentifier().equals(requestedApplication)) {
                return true;
            }
        }
        return false;
    }

    private List<GDServiceProvider> getSavedEditedFileServiceProviders() {
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

    private void showNotImplementingDialog(final String requestedApplication) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final String formattedMessage = String.format(getResources()
                .getString(R.string.dialog_not_implementing_description), requestedApplication);

        builder.setCancelable(false)
                .setTitle("Error")
                .setMessage(formattedMessage)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog,
                                                final int which) {
                                dialog.dismiss();

                                finish();
                            }
                        })
                .create()
                .show();
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
        Log.d(TAG, "MainActivity.onUpdateEntitlements()");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	Log.d(TAG, "MainActivity.onSaveInstanceState()");

		super.onSaveInstanceState(savedInstanceState);
		final String dataFromFile = dataEditText.getText().toString();
		savedInstanceState.putString("dataEditText", dataFromFile);
	}

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.d(TAG, "MainActivity.onRestoreInstanceState()");

		super.onRestoreInstanceState(savedInstanceState);
		final String dataFromFile = savedInstanceState.getString("dataEditText");
		dataEditText.setText(dataFromFile);
    }
}
