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

import java.io.UnsupportedEncodingException;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.good.gd.example.securestore.utils.FileUtils;

/** FileViewer - a basic viewer which will load content into an HTML webview.
 */
public class FileViewer extends SampleAppActivity {

    public static final String FILE_VIEWER_PATH = "path";

    private String mPath;

    /** onCreate - takes a path from the caller to get the file from
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.viewer);

        setupAppBar(getString(R.string.app_name));

        View mainView = findViewById(R.id.viewer_layout);
        adjustViewsIfEdgeToEdgeMode(mainView, null, mainView);

        Intent i = getIntent();
        if (i != null) {
            mPath = i.getStringExtra(FILE_VIEWER_PATH);
        }
    }

    /** onResume - sets up and loads data into the webview
     */
    public void onResume() {
        super.onResume();
        byte b[] = FileUtils.getInstance().getFileData(mPath);
        try {
        	if (b != null && b.length > 0) {
        		WebView wv = findViewById(R.id.webview);
        		wv.loadData(new String(b, "UTF-8"), "text/html", "UTF-8");
        	}
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
