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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blackberry.bbwebview.BBWebViewClient;
import com.blackberry.bbwebview.WebClientObserver;
import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.apache.http.client.CookieStore;
import com.good.gd.net.GDHttpClient;

import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class BrowserActivity extends AppCompatActivity implements GDStateListener {

    private static final String TAG = "APP_LOG" +  BrowserActivity.class.getSimpleName();

    public static final String EXTRA_URL = "extra url";
    private static final String EXTRA_PROGRESS_BAR = "progress bar";

    private WebView webView;
    private TextView urlField;
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
            // Load url which is retrieved from the intent
            if (url != null) {
                webView.loadUrl(url);
                urlField.setText(url);

                // Reset intent
                getIntent().replaceExtras(new Bundle());
                getIntent().setAction("");
                getIntent().setData(null);
                getIntent().setFlags(0);
            }
            else {
                // Looks like onCreate is called after idle unlocking App.
                // So, if we have saved instance, then restore web-view from it.
                if (savedInstanceState != null)
                    webView.restoreState(savedInstanceState);
            }
        }

        Log.i(TAG,"onCreate");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.clear_cookies:
                GDHttpClient httpClient = new GDHttpClient();
                CookieStore cookieStore = httpClient.getCookieStore();
                cookieStore.clear();
                CookieManager.getInstance().removeAllCookies(new ValueCallback<Boolean>() {
                    @Override
                    public void onReceiveValue(Boolean value) {
                        Toast.makeText(BrowserActivity.this, "Cookies are cleared", Toast.LENGTH_LONG).show();
                    }
                });
                return true;
            case R.id.stop_loading:
                webView.stopLoading();
                Toast.makeText(BrowserActivity.this, "Loading is stopped", Toast.LENGTH_LONG).show();
                return true;
            case R.id.clear_url_input:
                urlField.setText("");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i(TAG,"onBackPressed");
    }

    @Override
    public void onAuthorized() {
        Log.i(TAG,"onAuthorized");
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
