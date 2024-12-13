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

package com.good.gd.example.appkinetics;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.good.gd.GDAndroid;
import com.good.gd.GDServiceProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * File list screen
 */
public class AppKinetics extends SampleAppActivity implements OnClickListener, AppKineticsModelListener{

    // Instance Variables -----------------------------------------------------
    private FileListAdapter adapter;

    // Public Methods ---------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(AppKineticsHelpers.LOGTAG, "AppKinetics.onCreate()\n");

        AppKineticsModel.getInstance().setContext(this.getApplicationContext());
        AppKineticsModel.getInstance().setModelListener(this);

        GDAndroid.getInstance().activityInit(this);

        setContentView(R.layout.main);

        // setup the click handler
        final ListView listview = findViewById(R.id.listview);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String filePath = ((FileModel) parent
                        .getItemAtPosition(position)).getName();
                final Intent intent = new Intent(getApplicationContext(),
                        FileViewerActivity.class);
                intent.putExtra(FileViewerActivity.FILE_VIEWER_PATH, filePath);

                startActivity(intent);
            }
        });
        initList();

        setupAppBar(getString(R.string.app_name));

        ViewGroup mainView = findViewById(R.id.bbd_appkinetics_UI);
        ViewGroup bottomBar = findViewById(R.id.action_view_menu);

        adjustViewsIfEdgeToEdgeMode(mainView, bottomBar, listview);
    }

    private void initList() {
        final ListView listView = findViewById(R.id.listview);

        final List<FileModel> data =
                new ArrayList<FileModel>(AppKineticsModel.getInstance().getFiles());

        Collections.sort(data, new FileModel.NameComparator());

        adapter = new FileListAdapter(this, data);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        AppKineticsModel.getInstance().onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d(AppKineticsHelpers.LOGTAG, "AppKinetics.onResume() IN\n");
        super.onResume();

        AppKineticsModel.getInstance().onResume();

        if (AppKineticsModel.getInstance().isAuthorized()) {
            updateFileList();
        }
        Log.d(AppKineticsHelpers.LOGTAG, "AppKinetics.onResume() OUT\n");
    }

	@Override
	protected void onPostResume() {
		Log.d(AppKineticsHelpers.LOGTAG, "AppKinetics.onPostResume IN\n");
		super.onPostResume();

		if (AppKineticsModel.getInstance().isAuthorized() ) {
		    AppKineticsModel.getInstance().setAppKineticsActivity(this);
			AppKineticsModel.getInstance().setCurrentActivity(this);
			this.runOnUiThread(new Thread(new Runnable() {
			    public void run() {
		 			AppKineticsModel.getInstance().savePendingFiles();
					updateFileList();
			    }
			}));
			AppKineticsModel.getInstance().processPendingDialog();
		}
		Log.d(AppKineticsHelpers.LOGTAG, "AppKinetics.onPostResume OUT\n");
	}

    @Override
    public void onStart() {
        super.onStart();
        Log.d(AppKineticsHelpers.LOGTAG, "AppKinetics.onStart()\n");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(AppKineticsHelpers.LOGTAG, "AppKinetics.onStop()\n");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppKineticsModel.getInstance().setModelListener(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_send_to: {
                sendFiles();
                break;
            }
            case R.id.action_delete: {
                deleteSelectedFiles();
                updateFileList();
                break;
            }
            case R.id.action_reset: {
                resetFileList();
                updateFileList();
                break;
            }
        }
    }

    // Private Methods --------------------------------------------------------

    private void updateFileList() {
        if(adapter != null) {
            Log.d(AppKineticsHelpers.LOGTAG, "AppKinetics.displayList()\n");

            final List<FileModel> data =
                    new ArrayList<FileModel>(AppKineticsModel.getInstance().getFiles());

            Collections.sort(data, new FileModel.NameComparator());

            adapter.notifyDataSetChanged(data);
        }
    }

    private void deleteSelectedFiles() {
        for (FileModel file : AppKineticsModel.getInstance().getFiles()) {
            if (file.isSelected())
                AppKineticsModel.getInstance().deleteFile(file.getName());
        }
    }

    private void resetFileList() {
        AppKineticsModel.getInstance().resetFileList();
    }

    private void sendFiles() {
        List<String> selectedFiles = new ArrayList<String>();
        for (FileModel file : AppKineticsModel.getInstance().getFiles()) {
            if (file.isSelected()) {
                selectedFiles.add(file.getName());
            }
        }
        if (selectedFiles.size() > 0)
            showSendToDialog(selectedFiles);
    }

    private void showSendToDialog(final List<String> files) {
        final FileTransferService fileTransferService = new FileTransferService(
                this);
        final List<GDServiceProvider> transferServices = fileTransferService.getList();

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (transferServices.isEmpty()){
            builder.setTitle(R.string.error).setMessage(R.string.no_providers);
        } else {
            builder.setTitle(R.string.send_to)
                .setAdapter(
                    new IconAndTextListAdapter(this, transferServices),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            GDServiceProvider svc = transferServices.get(which);
                            String svcAddrs = fileTransferService
                                    .addressLookup(svc.getName());
                            AppKineticsModel.getInstance().sendFiles(
                                    svcAddrs, files);
                        }
                    }
                );
        }
        builder.setNegativeButton(R.string.cancel,
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

	 void showOverwriteDialog(final String receivedFileName
                              , final String outFileName
                              , final InputStream inputStream) {
         Log.d(AppKineticsHelpers.LOGTAG, "AppKinetics.showOverwriteDialog IN\n");

         AlertDialog.Builder builder = new AlertDialog.Builder(this);

         builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int id) {
                 AppKineticsModel.getInstance().actualSaveFile(outFileName, inputStream);
                 try {
                     inputStream.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
                 updateFileList();
             }
         });
         builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int id) {
             }
         });
         builder.setCancelable(true);

         final ScrollView scrollView = new ScrollView(getApplicationContext());
         final TextView textView = new TextView(getApplicationContext());

         //set scrollable message with custom font size
         final String dialogMessageSubstr1 = getString(R.string.overwrite_dialog_substr1);
         final String dialogMessageSubstr2 = getString(R.string.overwrite_dialog_substr2);
         final String dialogMessage = dialogMessageSubstr1 + receivedFileName + dialogMessageSubstr2;
         textView.setText(dialogMessage);
         final Resources res = getResources();
         final float dialogFontSize = res.getDimension(R.dimen.overwrite_dilog_font_size);
         textView.setTextSize(dialogFontSize);
         textView.setBackgroundColor(ContextCompat.getColor(this,R.color.overwrite_dilog_background));
         textView.setTextColor(ContextCompat.getColor(this,R.color.overwrite_dilog_text_color));
         scrollView.addView(textView);

         builder.setCustomTitle(scrollView);

         AlertDialog overwriteDialog = builder.create();
         overwriteDialog.show();
     }

    @Override
    public void onFilesListChanged() {
        updateFileList();
    }
}
