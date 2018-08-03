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
import android.webkit.WebView;

import com.good.gd.GDAndroid;


public class MainActivity extends Activity {

    public WebView getWebView() {return (WebView) findViewById(R.id.webView);}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GDAndroid.getInstance().activityInit(this);

        setContentView(R.layout.activity_main);
    }
//
//    @Override
//     protected void onSaveInstanceState(Bundle bundle) {
//        super.onSaveInstanceState(bundle);
//        WebViewSpike application = (WebViewSpike) getApplication();
//        if (application.isAuthorized) {
//            this.webView.saveState(bundle);
//        }
//    }
//
//    protected void onRestoreInstanceState(Bundle bundle) {
//        super.onRestoreInstanceState(bundle);
////        this.webView.restoreState(bundle);
//    }
}
