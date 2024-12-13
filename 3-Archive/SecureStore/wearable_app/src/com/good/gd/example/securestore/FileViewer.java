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

package com.good.gd.example.securestore;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.good.gdwearable.GDAndroid;

import com.good.gd.example.securestore.utils.FileUtils;

import static com.good.gd.example.securestore.common_lib.utils.AppLogUtils.DEBUG_LOG;

import java.io.UnsupportedEncodingException;

/**
 * FileViewer - Based on the handheld version to view file types. However Wearables don't support
 * Webview so use basic TextVuew instead
 */
public class FileViewer extends Activity {

    public static final String FILE_VIEWER_PATH = "path";

    private String mPath;

    /** onCreate - takes a path from the caller to get the file from
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GDAndroid.getInstance().activityInit(this);

        setContentView(R.layout.viewer);

        Intent i = getIntent();
        if (i != null) {
            mPath = i.getStringExtra(FILE_VIEWER_PATH);

            //Check if FileViewer was started by a notification. If it was then cancel the notification
            int notification_id = i.getIntExtra(AppStateManager.FILE_RECEIVED_EXTRA, 0);

            if(notification_id == AppStateManager.FILE_RECEIVED_ID){

                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                        .cancel(AppStateManager.FILE_RECEIVED_EXTRA, AppStateManager.FILE_RECEIVED_ID);

            }

        }

        DEBUG_LOG("FileViewer started file path = " + mPath);
    }

    /** onResume - sets up and loads data into the webview
     */
    public void onResume() {
        super.onResume();
        byte b[] = FileUtils.getInstance().getFileData(mPath);
        try {
        	if (b != null && b.length > 0) {
                DEBUG_LOG("fileviewer t length = " + b.length);
                TextView t = findViewById(R.id.file_textview);
                t.setText(new String(b, "UTF-8"));
        	}
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
