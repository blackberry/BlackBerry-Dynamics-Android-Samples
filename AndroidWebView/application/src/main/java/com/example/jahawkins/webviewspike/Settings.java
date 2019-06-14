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
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class Settings {
    private static final String LINKS = "links";
    private static final String DEBUG_ENABLED = "debugEnabled";
    private static final String ALLOW_CACHE = "allowCache";
    private static final Pattern eolPattern = Pattern.compile("$", Pattern.MULTILINE);

    private static final String DEFAULT_SETTINGS = "{" +
        "'intercept': true, 'injectHTML': true, 'nslookup':false, 'apacheRedirect':true, " +
        "'retrieve':false, 'debugEnabled': true, 'allowCache':true, " +
        "'stripContentSecurityPolicy':false, 'appKinetics':false, 'chunked':true" +
        "}";
    /*
        intercept:false
        Would switch off the whole HTTP interception in WebViewClient, except for the UI HTML.

        injectHTML:true
        Switches on injection of the inject.js asset file into suitable responses.

        nslookup:true
        Would switch on a GDNetUtility nslookup() call for every server address. The result is only
        logged, not used for anything.

        apacheRedirect:false
        Would switch off redirection in the BlackBerry Dynamics Apache HTTP layer. The application
        code then handles HTTP redirects instead.

        retrieve:true
        Would switch on retrieve mode, in which HTTP requests are executed but their responses
        aren't passed back to the WebView. This is just a diagnostic mode.

        debugEnabled:true
        Makes the WebView in the application appear in the chrome://inspect and hence in the Chrome
        developer tools.

        allowCache:false
        Would switch off use of caching by the WebView, which probably should be done in production
        but which makes it run even slower.

        stripContentSecurityPolicy:true
        Strips the Content-Security-Policy (CSP) header from any responses in which it is present. It's
        used by google.com login, for example, to block inline JS insertion, on which this program
        relies. This is now false by default because stripping the header has been replaced by
        N-once generation and manipulation of the CSP, which is superior.

        appKinetics:true
        Would open every website other than the application UI HTML by sending an AppKinetics
        service request to "com.good.gdservice.open-url.http". Only use this option if you want to
        open the ADAL login page in BlackBerry Access, so you can get called back by Intent to
        com.blackberry.work, which is registered in the Android manifest.

    */

    private Map<String, Object> map = null;

    private static final Settings sharedInstance = new Settings();
    public static Settings getInstance() {
        return sharedInstance;
    }
    private Settings() {
        super();
        this.map = new HashMap<String, Object>();
        try {
            this.updateSettings(DEFAULT_SETTINGS);
        } catch (JSONException exception) {
            throw new AssertionError(
                "JSON exception in default settings:" + exception.toString() +
                    " " + DEFAULT_SETTINGS);
        }
        this.resetLinks();
    }

    @Override
    public String toString() {
        return JSONObject.wrap(this.map).toString();
    }

    public Boolean getSetting(String key) {
        return (Boolean) this.map.get(key);
    }


    public void applySettings(Map<String, Object> oldSettings, final WebView webView) {
        // Changes that are made here can only be run on the UI thread, which means we need the
        // Activity.
        Activity activity = (Activity) webView.getContext();

        // After setWebContentsDebuggingEnabled(true), the WebView will appear on the
        // chrome://inspect page in the Chrome browser on an attached computer. From that page,
        // the WebView can be opened in theChrome Developer Tools, which is awesome.
        // Note that the set... is a class method and applies at the application level.
        Boolean nowDebugging = (Boolean) this.map.get(Settings.DEBUG_ENABLED);
        if (oldSettings != null) {
            final Boolean oldDebugging = (Boolean) oldSettings.get(Settings.DEBUG_ENABLED);
            if (nowDebugging.equals(oldDebugging)) {
                nowDebugging = null;
            }
        }
        if (nowDebugging != null) {
            final Boolean finalNowDebugging = nowDebugging;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WebView.setWebContentsDebuggingEnabled(finalNowDebugging);
                }
            });
        }

        final int cacheMode = ((boolean)this.map.get(Settings.ALLOW_CACHE)) ?
            WebSettings.LOAD_DEFAULT : WebSettings.LOAD_NO_CACHE;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // There's a restriction on which threads WebView methods can be called, even
                // innocuous methods like getSettings().
                webView.getSettings().setCacheMode(cacheMode);
            }
        });
    }

    private HashMap<String, Object> updateSettings(String toMergeJSON) throws JSONException {
        final HashMap<String, Object> oldSettings = (
            this.map.size() > 0 ? new HashMap<String, Object>(this.map) : null);
        final JSONObject toMerge = new JSONObject(toMergeJSON);
        final Iterator<String> keyIterator = toMerge.keys();
        while (keyIterator != null && keyIterator.hasNext()) {
            String key = keyIterator.next();
            this.map.put(key, toMerge.opt(key));
        }
        return oldSettings;
    }

    public String mergeSettings(String toMergeJSON, WebView webView) throws JSONException {
        this.applySettings(this.updateSettings(toMergeJSON), webView);
        return this.toString();
    }

    public void resetLinks() {
        this.map.put(Settings.LINKS, new ArrayList<String>());
    }

    public void addLinks(String linksString) {
        if (linksString == null) {
            return;
        }
        ArrayList<String> links = (ArrayList<String>) this.map.get(Settings.LINKS);
        String[] linkLines = eolPattern.split(linksString);
        for (String link : linkLines) {
            links.add(link.trim());
        }
    }
}
