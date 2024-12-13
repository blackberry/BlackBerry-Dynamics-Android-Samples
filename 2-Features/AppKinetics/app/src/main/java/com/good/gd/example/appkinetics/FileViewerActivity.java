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
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.good.gd.GDAndroid;
import com.good.gd.GDServiceProvider;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * File viewer screen
 */
public class FileViewerActivity extends SampleAppActivity implements OnClickListener {

    // Constants --------------------------------------------------------------
    public static final String FILE_VIEWER_PATH = "path";

    // Static Variables -------------------------------------------------------
    private static final Map<String, String> mimeTypes;

    private static final int ITEM_MAIN_ACTION_BAR_CONTAINER = 0;

    // Static Initializer -----------------------------------------------------
    static {
        mimeTypes = new HashMap<String, String>();
        mimeTypes.put(".txt", "text/plain");
        mimeTypes.put(".xml", "text/xml");
        mimeTypes.put(".html", "text/html");
        mimeTypes.put(".htm", "text/html");
        mimeTypes.put(".jpg", "image/jpeg");
        mimeTypes.put(".jpeg", "image/jpeg");
        mimeTypes.put(".png", "image/png");
    }

    // Instance Variables -----------------------------------------------------
    private String _filePath;

    // Public Methods ---------------------------------------------------------

    /**
     * onCreate - takes a path from the caller to get the file from
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(AppKineticsHelpers.LOGTAG, "FileViewerActivity.onCreate()\n");
        super.onCreate(savedInstanceState);

        AppKineticsModel.getInstance().setContext(this.getApplicationContext());

        GDAndroid.getInstance().activityInit(this);

        setContentView(R.layout.viewer);

        Intent i = getIntent();
        if (i != null) {
            _filePath = i.getStringExtra(FILE_VIEWER_PATH);
        }

        setupAppBar("File Viewer Activity");

        ViewGroup mainView = findViewById(R.id.viewr_main_layout);
        ViewGroup contentView = findViewById(R.id.file_viewer_layout);

        adjustViewsIfEdgeToEdgeMode(mainView, null, contentView);
    }

    /**
     * onResume - sets up and loads data into the webview
     */
    public void onResume() {
        Log.d(AppKineticsHelpers.LOGTAG, "FileViewerActivity.onResume()\n");
        super.onResume();

        AppKineticsModel.getInstance().onResume();

        WebView wv = findViewById(R.id.webview);

        String fileExt = _filePath.substring(_filePath.lastIndexOf('.'));
        fileExt = fileExt.toLowerCase();
        if (mimeTypes.containsKey(fileExt)) {
            String mimeType = mimeTypes.get(fileExt);

            byte b[] = AppKineticsModel.getInstance().getFileData(_filePath);
            try {
                if (b != null && b.length > 0) {
                    if (fileExt.equals(".jpg") || fileExt.equals(".jpeg")
                            || fileExt.equals(".png")) {
                        String b64Data = Base64.encodeToString(b,
                                Base64.DEFAULT);
                        wv.loadData(b64Data, mimeType, "base64");
                    } else
                        wv.loadData(new String(b, "UTF-8"), mimeType, "UTF-8");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            // Unsupported file type
            String html = "<h1>Unsupported File Type<h1><p/><h2>%1$s</h2><p/><h4>Use external apps to view this file</h4>";
            String html2 = String.format(html, _filePath);
            wv.loadData(html2, "text/html", "utf-8");
        }

        if (AppKineticsModel.getInstance().isAuthorized()
				&& ! AppKineticsModel.getInstance().getPendingFileList()
						.isEmpty()) {
			Intent i = new Intent(getApplicationContext(),
					AppKinetics.class);
			i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(i);
		}
    }

    @Override
    protected void onPause() {
        AppKineticsModel.getInstance().onPause();
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        Log.d(AppKineticsHelpers.LOGTAG, "FileViewerActivity.onPostResume IN\n");
        super.onPostResume();

        if (AppKineticsModel.getInstance().isAuthorized() ) {
            AppKineticsModel.getInstance().setCurrentActivity(this);
            AppKineticsModel.getInstance().processPendingDialog();
        }
        Log.d(AppKineticsHelpers.LOGTAG, "FileViewerActivity.onPostResume OUT\n");
    }

    public void showSendToDialog() {
        final FileTransferService fts = new FileTransferService(this);
        final List<GDServiceProvider> svcLst = fts.getList();
        // Use the Builder class for convenient dialog construction

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.send_to)
                .setAdapter(
                        new IconAndTextListAdapter(this,
                        		svcLst),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                            	GDServiceProvider svc = svcLst.get(which);
                                String svcAddrs = fts.addressLookup(svc.getName());
                                List<String> files = new ArrayList<String>();
                                files.add(_filePath);
                                AppKineticsModel.getInstance().sendFiles(
                                        svcAddrs, files);
                            }
                        }
                )
                .setNegativeButton(R.string.cancel,
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_send_to:
                showSendToDialog();
                break;
            case R.id.action_delete:
                AppKineticsModel.getInstance().deleteFile(_filePath);
                finish();
                break;
        }
    }
}