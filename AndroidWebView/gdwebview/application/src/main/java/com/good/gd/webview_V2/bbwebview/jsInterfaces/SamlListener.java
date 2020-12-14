package com.good.gd.webview_V2.bbwebview.jsInterfaces;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class SamlListener {

    private static final String TAG = "GDWebView-" +  SamlListener.class.getSimpleName();

    private String lastResponse;

    @JavascriptInterface
    public void setContent(String samlResponse) {
        Log.i(TAG, "Saml response: " + samlResponse);
        lastResponse = samlResponse;
    }

    public String getResponse() {
        return lastResponse;
    }

    public void reset() {
        lastResponse = null;
    }

}
