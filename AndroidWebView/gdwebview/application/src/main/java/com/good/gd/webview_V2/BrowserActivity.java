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
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.ImageButton;
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
import com.good.gd.webview_V2.bbwebview.devtools.onPaneEditListener;

import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class BrowserActivity extends AppCompatActivity implements GDStateListener {

    private static final String TAG = "APP_LOG" +  BrowserActivity.class.getSimpleName();

    public static final String EXTRA_URL = "extra url";
    private static final String EXTRA_PROGRESS_BAR = "progress bar";

    private WebView webView;
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
        webView.onResume();
        Log.i(TAG,"onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
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

        super.onDestroy();

        Log.i(TAG,"onDestroy OUT");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);

        outState.putInt(EXTRA_PROGRESS_BAR, progressBar.getProgress());

        Log.i(TAG,"onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);

        progressBar.setProgress(savedInstanceState.getInt(EXTRA_PROGRESS_BAR, 0));

        Log.i(TAG,"onRestoreInstanceState");
    }

    private void disposeViews() {
        // Detach WebView from the parent layout
        // This ensure proper destruction of WebView
        ViewGroup viewGroup = (ViewGroup) webView.getParent();
        viewGroup.removeView(webView);

        webView.stopLoading();
        webView.destroy();
        webView = null;
        urlField = null;
    }

    private void initViews() {
        webView = findViewById(R.id.gd_web_view);

        urlField = findViewById(R.id.url_input);

        progressBar = findViewById(R.id.progress_bar);

        BBWebViewClient webViewClient = (BBWebViewClient) webView.getWebViewClient();

        webViewClient.getObserver().addProgressListener(new WebClientObserver.ProgressListener() {
            @Override
            public void progressChanged(int newProgress) {
                progressBar.setProgress(newProgress);
            }
        });

        webViewClient.getObserver().addOnPageStartedListener(new WebClientObserver.OnPageStarted() {
            @Override
            public void onPageStarted(WebView view, String url) {
                urlField.setText(url);
            }
        });

        setupPane(R.id.console, new OnJsEditListener());
        setupPane(R.id.cookies_list, new OnCookiesFilterEdit());

        ImageButton goBack = findViewById(R.id.back_btn);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webView.canGoBack()) {
                    webView.goBack();
                }
            }
        });

        ImageButton goForward = findViewById(R.id.forward_btn);
        goForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webView.canGoForward()) {
                    webView.goForward();
                }
            }
        });

        ImageButton reload = findViewById(R.id.reload_btn);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.reload();
            }
        });
    }

    private void setupPane(int paneId, onPaneEditListener editActionListener) {
        View pane = findViewById(paneId);
        final TextView consoleOut = pane.findViewById(R.id.eval_results);
        final TextView consoleEdit = pane.findViewById(R.id.eval_input);

        consoleOut.setMovementMethod(new ScrollingMovementMethod());

        editActionListener.activity = BrowserActivity.this;
        editActionListener.inputView = consoleEdit;
        editActionListener.outputView = consoleOut;
        editActionListener.targetWebView = webView;

        consoleEdit.setImeActionLabel("eval", KeyEvent.KEYCODE_ENTER);

        consoleEdit.setOnEditorActionListener(editActionListener);
    }

    public void onGo(View view) {
        String urlString = urlField.getText().toString();

        if (URLUtil.isValidUrl(urlString)) {

            Log.i(TAG, "loadUrl(" + urlString + ")");

            webView.stopLoading();
            webView.loadUrl(urlString);

        } else {
            String errMsg = "Invalid url input: " + urlString;
            Log.e(TAG, errMsg);

            Toast.makeText(BrowserActivity.this, errMsg, LENGTH_SHORT).show();
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

        // Load url which is retrieved from the intent
        if (passedUrl != null) {
            Log.i(TAG, "loadUrl(" + passedUrl + ")");
            webView.loadUrl(passedUrl);
            urlField.setText(passedUrl);
            
            // Reset passed url
            passedUrl = null;
        }

    }

    @Override
    public void onLocked() {}

    @Override
    public void onWiped() {}

    @Override
    public void onUpdateConfig(Map<String, Object> map) {}

    @Override
    public void onUpdatePolicy(Map<String, Object> map) {}

    @Override
    public void onUpdateServices() {}

    @Override
    public void onUpdateEntitlements() {}

}
