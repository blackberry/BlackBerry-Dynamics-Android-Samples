/* Copyright (c) 2018 BlackBerry Ltd.
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

package com.example.jahawkins.webviewspike;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;


public class MainActivity extends Activity {
    static final int WEB_VIEW_ID = View.generateViewId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout layout = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        WebView webView = new WebView(this);
        webView.setLayoutParams(params);
        webView.setId(WEB_VIEW_ID);
        layout.addView(webView);

        setContentView(layout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final WebView webView = findViewById(WEB_VIEW_ID);
        webView.reloadIfUserInterface();
    }
}
