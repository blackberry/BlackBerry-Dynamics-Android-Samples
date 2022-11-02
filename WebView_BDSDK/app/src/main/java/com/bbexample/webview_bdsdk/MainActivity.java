/* Copyright (c) 2021 BlackBerry Limited
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

package com.bbexample.webview_bdsdk;

import static android.widget.Toast.LENGTH_SHORT;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blackberry.bbwebview.BBHttpAuthHandler;
import com.blackberry.bbwebview.BBWebChromeClient;
import com.blackberry.bbwebview.BBWebResourceError;
import com.blackberry.bbwebview.BBWebView;
import com.blackberry.bbwebview.BBWebViewClient;
import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;

import java.util.Map;

/*
    This sample demonstrates the use of com.blackberry.bbwebview.BBWebView, which extends
    android.webkit.WebView and provides BlackBerry Dynamics network connectivity.
 */

public class MainActivity extends AppCompatActivity implements GDStateListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String EXTRA_PROGRESS_BAR = "progress bar";

    private BBWebView webView;
    private TextView urlField;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GDAndroid.getInstance().activityInit(this);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        webView = findViewById(R.id.bb_web_view);

        urlField = findViewById(R.id.url_input);

        progressBar = findViewById(R.id.progress_bar);

        webView.setWebViewClient(new BBWebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                urlField.setText(url);
            }

            @Override
            public void onReceivedBBHttpAuthRequest(WebView view, BBHttpAuthHandler handler, String host, String realm) {
                Log.i(TAG,"onReceivedHttpAuthRequestCallback()");
                AuthDialog authDialog = new AuthDialog(host, realm, handler);
                authDialog.show(getSupportFragmentManager(), "AuthDialog");
            }

            @Override
            public void onReceivedBBError(WebView webView, WebResourceRequest request, BBWebResourceError error) {
                Log.i(TAG, "error for request: '" + request.getUrl() + "' code: " + error.getErrorCode() + " desc: " + error.getDescription());
            }
        });

        webView.setWebChromeClient(new BBWebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
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

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onDestroy() {
        disposeViews();
        super.onDestroy();
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);

        outState.putInt(EXTRA_PROGRESS_BAR, progressBar.getProgress());

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

            Toast.makeText(MainActivity.this, errMsg, LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i(TAG,"onBackPressed");
    }

    @Override
    public void onAuthorized() {
        initViews();
    }

    @Override
    public void onLocked() {

    }

    @Override
    public void onWiped() {

    }

    @Override
    public void onUpdateConfig(Map<String, Object> map) {

    }

    @Override
    public void onUpdatePolicy(Map<String, Object> map) {

    }

    @Override
    public void onUpdateServices() {

    }

    @Override
    public void onUpdateEntitlements() {

    }
}