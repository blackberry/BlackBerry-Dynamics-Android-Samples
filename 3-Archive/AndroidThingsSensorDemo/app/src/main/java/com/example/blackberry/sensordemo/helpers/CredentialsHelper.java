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


package com.example.blackberry.sensordemo.helpers;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CredentialsHelper {

    Context mContext;

    public CredentialsHelper(Context aContext)  {
        mContext = aContext;
    }

    public class ActivationCredentials {
        private String email;
        private String accessKey;

        public String getEmail() {
            return email;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public void setEmail(String value) {
            email = value;
        }

        public void setAccessKey(String value) {
            accessKey = value;
        }
    }

    private static final String FILE_NAME = "access_key.json";
    private static final String EMAIL = "Email";
    private static final String ACCESS_KEY = "AccessKey";

    public ActivationCredentials getActivationCredentials( ){

        String file_contents = getSettingsFile(FILE_NAME, mContext);

        ActivationCredentials activationCredentials = new ActivationCredentials();

        activationCredentials.setEmail(checkSettingsFileForKey(EMAIL, file_contents));
        activationCredentials.setAccessKey(checkSettingsFileForKey(ACCESS_KEY, file_contents));

        return activationCredentials;
    }

    // read the contents of file from the APK assets directory; return its contents as a String
    private  String getSettingsFile(String aFile, Context ctx)
    {
        AssetManager assetManager = ctx.getAssets();
        InputStream is = null;
        try
        {
            is = assetManager.open(aFile);

            if (is == null)
            {
                throw new Exception("No such file");
            }
            return new String(readBytes(is), "UTF-8");
        }
        catch (Exception e)
        {
            // We log error message if settings.json no present. Not if com.good.gd.debug.json is present as it is only in case of specific builds com.good.gd.debug.json will be present

            // Here we explictly use logcat system log because if settings.json is missing then GD won't initialize and thus cant log using GDLog
            Log.w("GD", "Could not read file" + aFile +" [" + e.getMessage() + "]\n");

        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (Exception e) {}
            }
        }
        return null;
    }

    private  byte[] readBytes(InputStream is) throws IOException {
        byte[] buf = new byte[1024];
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        int bytesRead = 0;
        while (-1 != (bytesRead = is.read(buf))) {
            bout.write(buf, 0, bytesRead);
        }

        return bout.toByteArray();
    }

    private String checkSettingsFileForKey(String aKey, String jsonString) {

        JSONObject settings = null;
        try {
            settings = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if ( settings == null
                || !settings.has(aKey))
            return null;

        String value =null;

        try {
            value = settings.getString(aKey);
            if (value == null || value.isEmpty()) {
                return null;
            }
            else {

                return value;
            }
        } catch (JSONException e) {

            return null;
        }
    }

}
