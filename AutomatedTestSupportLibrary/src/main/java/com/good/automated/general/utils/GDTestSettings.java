/* Copyright (c) 2017 - 2020 BlackBerry Limited.
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

package com.good.automated.general.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class GDTestSettings {

    private static final String GD_TEST_PROVISION_EMAIL = "GD_TEST_PROVISION_EMAIL";
    private static final String GD_TEST_PROVISION_ACCESS_KEY = "GD_TEST_PROVISION_ACCESS_KEY";
    private static final String GD_TEST_PROVISION_PASSWORD = "GD_TEST_PROVISION_PASSWORD";
    private static final String GD_TEST_UNLOCK_KEY = "GD_TEST_UNLOCK_KEY";
    private static final String GD_TEST_PROVISION_CONFIG_NAME = "GD_TEST_PROVISION_CONFIG_NAME";

    private static final String GD_TESTSETTINGS_FILENAME = "com.good.gd.test.json";

    private static GDTestSettings instance = null;

    private Map<String, JSONObject> gdTestSettingsJsonObjects = null;
    private String defaultPackageName = null;

    public static synchronized GDTestSettings getInstance() {
        if (instance == null) {
            instance = new GDTestSettings();
        }
        return instance;
    }

    private GDTestSettings() {
    }

    /**
     * Initializes {@link GDTestSettings} for specific application.
     * Reads credentials from files named by convention:
     * {@literal <package-name>.json}
     *
     * @param testContext        context of application under test
     * @param defaultPackageName package name of application under test
     */
    public void initialize(Context testContext, String defaultPackageName) {
        this.defaultPackageName = defaultPackageName;

        gdTestSettingsJsonObjects = new HashMap<>();

        AssetManager assetManager = testContext.getAssets();

        try {
            for (String filename : assetManager.list("")) {
                if (!isJsonFile(filename)) {
                    continue;
                }

                JSONObject testSettingsObject = parseJson(assetManager, filename);
                processJson(defaultPackageName, filename, testSettingsObject);
            }
        } catch (IOException e) {
            Assert.fail("GD::TestSettings - Error parsing data " + e.toString());
        }
    }

    /**
     * Return provision email for default package
     */
    public String getAppProvisionEmail() {
        return getAppProvisionEmail(defaultPackageName);
    }

    /**
     * Return provision email for specified package
     */
    public String getAppProvisionEmail(String packageName) {
        return getStringAttribute(packageName, GD_TEST_PROVISION_EMAIL);
    }

    /**
     * Return provision access key for default package
     */
    public String getAppProvisionAccessKey() {
        return getAppProvisionAccessKey(defaultPackageName);
    }

    /**
     * Return provision access key for specified package
     */
    public String getAppProvisionAccessKey(String packageName) {
        return getStringAttribute(packageName, GD_TEST_PROVISION_ACCESS_KEY);
    }

    /**
     * Return provision password for default package
     */
    public String getAppProvisionPassword() {
        return getAppProvisionPassword(defaultPackageName);
    }

    /**
     * Return provision password for specified package
     */
    public String getAppProvisionPassword(String packageName) {
        return getStringAttribute(packageName, GD_TEST_PROVISION_PASSWORD);
    }

    /**
     * Overrides default credentials.
     */
    public void overrideActivationCredentials(JSONArray credentials) throws JSONException{
        for (int i = 0; i < credentials.length(); i++) {

            JSONObject jsonObject = credentials.getJSONObject(i);

            String packageName = jsonObject.getString(GD_TEST_PROVISION_CONFIG_NAME);

            // Put or replace credentials for package name
            gdTestSettingsJsonObjects.put(packageName, jsonObject);
        }
    }

    /**
     * Return unlock key for default package
     *
     * @throws IllegalArgumentException if json file does not unlock key entry
     * @see GDTestSettings#GD_TEST_UNLOCK_KEY
     */
    public String getAppUnlockKey() {
        return getAppUnlockKey(defaultPackageName);
    }

    /**
     * Return unlock key for specific package
     *
     * @throws IllegalArgumentException if json file does not unlock key entry
     * @see GDTestSettings#GD_TEST_UNLOCK_KEY
     */
    public String getAppUnlockKey(String packageName) {
        String unlockKey = getStringAttribute(packageName, GD_TEST_UNLOCK_KEY);

        if (unlockKey == null) {
            throw new IllegalArgumentException("No unlock key provided in json file for " + packageName + " package");
        }

        return unlockKey;
    }

    private boolean validateSettingsFile(JSONObject object) {
        boolean result = false;
        try {
            result = object.getString(GD_TEST_PROVISION_EMAIL) != null && object.getString(GD_TEST_PROVISION_EMAIL).length() > 0 &&
                    object.getString(GD_TEST_PROVISION_PASSWORD) != null && object.getString(GD_TEST_PROVISION_PASSWORD).length() > 0 &&
                    object.getString(GD_TEST_PROVISION_ACCESS_KEY) != null && object.getString(GD_TEST_PROVISION_ACCESS_KEY).length() > 0;
        } catch (JSONException e) {
            result = false;
        }
        return result;
    }

    private String getStringAttribute(String packageName, String key) {
        String result = null;
        Log.d("SettingsObjects", gdTestSettingsJsonObjects.toString());
        for (Object js :
                gdTestSettingsJsonObjects.keySet()) {

            Log.d("Settings files", gdTestSettingsJsonObjects.get(js).toString());
        }
        if (gdTestSettingsJsonObjects != null) {
            try {
                result = gdTestSettingsJsonObjects.get(packageName).getString(key);
            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private void processJson(String defaultPackageName, String filename, JSONObject testSettingsObject) {

        if (validateSettingsFile(testSettingsObject)) {
            String key = filename.equals(GD_TESTSETTINGS_FILENAME) ? defaultPackageName : parsePackageName(filename);

            if (gdTestSettingsJsonObjects.containsKey(key)) {
                throw new AmbiguousCredentialsException("Credentials for " + defaultPackageName + " are already defined.");
            }

            gdTestSettingsJsonObjects.put(key, testSettingsObject);
        }
    }

    /**
     * @param filename test settings filename
     * @return parsed package name
     */
    private String parsePackageName(String filename) {
        return filename.replace(".json", "");
    }

    private JSONObject parseJson(AssetManager assetManager, String filename) {
        try {
            String settingsJson = IOUtils.readAssetsFile(assetManager, filename);
            return new JSONObject(settingsJson);
        } catch (JSONException e) {
            Assert.fail("GD::TestSettings -Error parsing data " + e.toString());
            return null;
        }
    }

    private boolean isJsonFile(String file) {
        return file.endsWith(".json");
    }

    private class AmbiguousCredentialsException extends RuntimeException {
        public AmbiguousCredentialsException(String message) {
            super(message);
        }
    }
}
