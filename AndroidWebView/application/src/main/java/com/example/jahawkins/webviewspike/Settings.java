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
    static final String LINKS = "links";
    static final String DEBUG_ENABLED = "debugEnabled";
    static final String ALLOW_CACHE = "allowCache";

    Map<String, Object> map = null;
    Pattern eolPattern = null;

    private static final Settings sharedInstance = new Settings();
    public static Settings getInstance() {
        return sharedInstance;
    }
    private Settings() {
        super();
        this.map = new HashMap<String, Object>();
        this.eolPattern = Pattern.compile("$", Pattern.MULTILINE);
    }

    @Override
    public String toString() {
        return JSONObject.wrap(this.map).toString();
    }

    /* A reference to the current Activity is needed for two reasons.
     *
     * -   Settings are applied by this class to the WebView instance, which it obtains from the
     *     Activity.
     * -   Some settings can only be applied on the UI thread, which requires this class to call
     *     Activity runOnUIThread().
     */
    MainActivity activity = null;

    public Boolean getSetting(String key) {
        return (Boolean) this.map.get(key);
    }


    public void applySettings(Map<String, Object>oldSettings) {
        // Changes that are made here can only be run on the UI thread, which means we need the
        // Activity.
        if (this.activity == null) {
            return;
        }

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
        final WebView webView = activity.getWebView();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final WebSettings webSettings = webView.getSettings();
                webSettings.setCacheMode(cacheMode);
            }
        });
    }

    public String mergeSettings(String toMergeJSON) throws JSONException {
        JSONObject toMerge = null;
        Iterator<String> keyIterator = null;
        HashMap<String, Object> oldSettings = (
            this.map.size() > 0 ? new HashMap<String, Object>(this.map) : null);
        toMerge = new JSONObject(toMergeJSON);
        keyIterator = toMerge.keys();
        while (keyIterator != null && keyIterator.hasNext()) {
            String key = keyIterator.next();
            this.map.put(key, toMerge.opt(key));
        }
        this.applySettings(oldSettings);
        return this.toString();
    }

    public String mergeDefaultSettings() {
        try {
            return this.mergeSettings("{" +
                "'intercept': true, 'injectHTML': true, 'nslookup':false, 'apacheRedirect':true, " +
                "'retrieve':false, 'debugEnabled': true, 'allowCache':true, " +
                "'stripContentSecurityPolicy':false, 'appKinetics':false, 'chunked':true" +
            "}");
        } catch (JSONException e) {
            throw new AssertionError("JSON exception in default settings.");
        }
        /*
        intercept:false
        Would switch off the whole HTTP interception in StreamWebViewClient, except for the UI HTML.

        injectHTML:true
        Switches on injection of the inject.html asset file into suitable responses.

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
        Strips the Content-Security-Policy header from any responses in which it is present. Its
        used by google.com login, for example, to block inline JS insertion, on which this program
        relies.

        appKinetics:true
        Would open every website other than the application UI HTML by sending an AppKinetics
        service request to "com.good.gdservice.open-url.http". Only use this option if you want to
        open the ADAL login page in BlackBerry Access, so you can get called back by Intent to
        com.blackberry.work, which is registered in the Android manifest.

         */
    }

    public void resetLinks() {
        this.map.put(Settings.LINKS, new ArrayList<String>());
    }

    public void addLinks(String linksString) {
        if (linksString == null) {
            return;
        }
        ArrayList<String> links = (ArrayList<String>) this.map.get(Settings.LINKS);
        String[] linkLines = this.eolPattern.split(linksString);
        for (String link : linkLines) {
            links.add(link.trim());
        }
    }
}
