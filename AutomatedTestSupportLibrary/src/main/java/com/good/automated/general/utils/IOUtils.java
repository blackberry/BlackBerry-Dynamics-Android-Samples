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

import android.content.res.AssetManager;
import android.util.Log;

import org.junit.Assert;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Often used IO utils for reading streams / files.
 */
public final class IOUtils {

    private static final String TAG = IOUtils.class.getName();

    private static final String UTF_8_CHARSET = "UTF-8";

    private static final int DEFAULT_BLOCK_SIZE = 1024;

    private IOUtils() {
    }

    /**
     * Read the assets/settings.json file from the APK bundle; return its
     * contents as a String
     */
    public static String readAssetsFile(AssetManager assetManager, String settingsFile) {
        InputStream is = null;
        try {
            is = assetManager.open(settingsFile);

            if (is == null) {
                throw new FileNotFoundException("No such file: " + settingsFile);
            }

            return new String(IOUtils.readBytes(is), UTF_8_CHARSET);
        } catch (IOException e) {
            Assert.fail(String.format("GD::TestSettings - Could not read %s: %s\n", settingsFile, e.getMessage()));
        } finally {
            close(is);
        }
        return null;
    }


    /**
     * Closes the stream with suppressing checked exception
     *
     * @param inputStream stream to close
     */
    public static void close(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing stream");
            }
        }
    }

    /**
     * Reads the stream into byte array
     *
     * @param is stream to read
     * @return byte array with stream content
     */
    public static byte[] readBytes(InputStream is) {
        byte[] buf = new byte[DEFAULT_BLOCK_SIZE];
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        int bytesRead;
        try {
            while (-1 != (bytesRead = is.read(buf))) {
                bout.write(buf, 0, bytesRead);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading input stream.");
        }

        return bout.toByteArray();
    }

}
