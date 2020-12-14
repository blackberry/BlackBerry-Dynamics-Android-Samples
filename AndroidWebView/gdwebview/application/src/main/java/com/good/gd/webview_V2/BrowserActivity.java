/*
 * Copyright (c) 2020 BlackBerry Limited.
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
package com.good.gd.webview_V2;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.webview_V2.bbwebview.BBWebViewClient;
import com.good.gd.webview_V2.bbwebview.WebClientObserver;
import com.good.gd.webview_V2.bbwebview.devtools.OnCookiesFilterEdit;
import com.good.gd.webview_V2.bbwebview.devtools.OnJsEditListener;
import com.good.gd.webview_V2.bbwebview.devtools.WebSettingsFragment;
import com.good.gd.webview_V2.bbwebview.devtools.onPaneEditListener;
import com.good.gd.webview_V2.bbwebview.tasks.http.GDHttpClientProvider;

import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class BrowserActivity extends AppCompatActivity implements GDStateListener {

    private static final String TAG = "APP_LOG" +  BrowserActivity.class.getSimpleName();

    public static final String EXTRA_URL = "extra url";

    private static final String EXTRA_PROGRESS_BAR = "progress bar";

    private BBWebViewClient bbWebViewClient;
    private WebView webview;
    private TextView urlField;
    private String passedUrl;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        GDAndroid.getInstance().activityInit(this);

        initViews();

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            String url = intent.getExtras().getString(EXTRA_URL);
            if (url != null) {
                passedUrl = url;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        webview.onResume();
        Log.i(TAG,"onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        webview.onPause();
        Log.i(TAG,"onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"onStop");
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG,"onDestroy IN");

        disposeViews();
        GDHttpClientProvider.getInstance().disposeHttpClientsPool();

        super.onDestroy();

        Log.i(TAG,"onDestroy OUT");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        webview.saveState(outState);

        outState.putInt(EXTRA_PROGRESS_BAR, progressBar.getProgress());

        Log.i(TAG,"onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webview.restoreState(savedInstanceState);

        progressBar.setProgress(savedInstanceState.getInt(EXTRA_PROGRESS_BAR, 0));

        Log.i(TAG,"onRestoreInstanceState");
    }

    private void disposeViews() {
        webview.stopLoading();
        webview.destroy();
        webview = null;
        urlField = null;
    }

    private void initViews() {
        webview = findViewById(R.id.gd_web_view);
        if (bbWebViewClient == null) {
            bbWebViewClient = new BBWebViewClient();
        }

        webview.setWebViewClient(bbWebViewClient);
        urlField = findViewById(R.id.url_input);

        progressBar = findViewById(R.id.progress_bar);

        bbWebViewClient.getObserver().addOnPageFinishedListener(new WebClientObserver.OnPageFinished() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setProgress(100);
            }
        });

        bbWebViewClient.getObserver().addOnPageStartedListener(new WebClientObserver.OnPageStarted() {
            @Override
            public void onPageStarted(WebView view, String url) {
                progressBar.setProgress(20);
            }
        });

        bbWebViewClient.getObserver().addOnContentVisibleListener(new WebClientObserver.OnPageContentVisible() {
            @Override
            public void onPageContentVisible(WebView view, String url) {
                progressBar.setProgress(70);
            }
        });

        setupPane(R.id.console, new OnJsEditListener());
        setupPane(R.id.cookies_list, new OnCookiesFilterEdit());
    }

    private void setupPane(int paneId, onPaneEditListener editActionListener) {
        View pane = findViewById(paneId);
        final TextView consoleOut = pane.findViewById(R.id.eval_results);
        final TextView consoleEdit = pane.findViewById(R.id.eval_input);

        consoleOut.setMovementMethod(new ScrollingMovementMethod());

        editActionListener.activity = BrowserActivity.this;
        editActionListener.inputView = consoleEdit;
        editActionListener.outputView = consoleOut;
        editActionListener.targetWebView = webview;

        consoleEdit.setImeActionLabel("eval", KeyEvent.KEYCODE_ENTER);

        consoleEdit.setOnEditorActionListener(editActionListener);
    }

    public void onGo(View view) {

        switch (view.getId()) {
            case R.id.go_btn: {
                String urlString = urlField.getText().toString();

                if (URLUtil.isValidUrl(urlString)) {

                    Log.i(TAG, "loadUrl(" + urlString + ")");

                    webview.stopLoading();
                    webview.loadUrl(urlString);

                } else {
                    String errMsg = "Invalid url input: " + urlString;
                    Log.e(TAG, errMsg);

                    Toast.makeText(BrowserActivity.this, errMsg, LENGTH_SHORT).show();
                }
            }
            break;

            case R.id.back_btn:
                webview.goBack();
            break;

            case  R.id.settings_btn:
                WebSettingsFragment webSettingsFragment = WebSettingsFragment.newInstance();
                webSettingsFragment.setWebSettings(webview.getSettings());

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container,webSettingsFragment)
                        .addToBackStack("s")
                        .commit();

                break;
        }
    }

    public void viewPaneInstance(View view) {
        switch (view.getId()){
            case R.id.console_btn:
                toggleViewPaneInstance(R.id.console,R.id.hide_js);
            break;
            case R.id.cookies_btn:
                toggleViewPaneInstance(R.id.cookies_list,R.id.hide_cookies);
            break;
        }

    }

    private final static SparseIntArray VISIBILITY_TOGGLE = new SparseIntArray(){{
        append(View.INVISIBLE,View.VISIBLE);
        append(View.VISIBLE,View.INVISIBLE);
        append(View.GONE,View.VISIBLE);
    }};

    private void toggleViewPaneInstance(int... viewIds) {
        for (int viewId : viewIds) {
            View view = findViewById(viewId);
            view.setVisibility(VISIBILITY_TOGGLE.get(view.getVisibility()));
        }
    }

    public void onHidePane(View view) {
        if(view.getId() == R.id.hide_cookies){
            toggleViewPaneInstance(R.id.cookies_list, view.getId());
        } else if(view.getId() == R.id.hide_js){
            toggleViewPaneInstance(R.id.console, view.getId());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i(TAG,"onBackPressed");
    }

    @Override
    public void onAuthorized() {
        Log.i(TAG,"onAuthorized");

        WebView webview = findViewById(R.id.gd_web_view);

        GDHttpClientProvider.getInstance().initHttpClientsPool();

        BBWebViewClient.init(webview,bbWebViewClient);

        if (passedUrl != null) {
            Log.i(TAG, "loadUrl(" + passedUrl + ")");
            webview.loadUrl(passedUrl);
            urlField.setText(passedUrl);
        }

    }

    @Override
    public void onLocked() {
        Log.i(TAG,"onLocked");
    }

    @Override
    public void onWiped() {
        //d
    }

    @Override
    public void onUpdateConfig(Map<String, Object> map) {
        //d
    }

    @Override
    public void onUpdatePolicy(Map<String, Object> map) {
        //d
    }

    @Override
    public void onUpdateServices() {
        //d
    }

    @Override
    public void onUpdateEntitlements() {
        //d
    }


}
