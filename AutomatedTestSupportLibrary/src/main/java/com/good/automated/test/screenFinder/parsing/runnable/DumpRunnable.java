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

package com.good.automated.test.screenFinder.parsing.runnable;


import android.os.Environment;
import android.util.Log;

import com.good.automated.test.screenFinder.parsing.DumpQueue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * {@link Runnable} to dump UI to xml in the independent thread.
 */
public class DumpRunnable implements Runnable {

    private static int unknownDumpsCounter = 0;

    private final String TAG = this.getClass().getSimpleName();

    private DumpQueue dumpQueue;

    public DumpRunnable(DumpQueue dumpQueue) {
        this.dumpQueue = dumpQueue;
    }

    @Override
    public void run() {
        try {
            dumpQueue.dumpWindow();
        } catch (InterruptedException ex) {
            Log.e(TAG, "The process for dumping UI window was interrupted!", ex);
        } catch (Exception ex) {
            Log.e(TAG, "Failed to dump UI XML.", ex);
            return;
        }
        try {
            if (!saveUIXMLDump(dumpQueue.get())) {
                Log.e(TAG, "Failed to save UI XML dump. Refer to logs for more details.");
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "Failed to get dump from the queue!", e);
        }
    }

    /**
     * Saves UI XML dump to a file.
     *
     * @param in    {@link InputStream} with the XLM content
     * @return      true - if dump was successfully saved / false - otherwise
     */
    private boolean saveUIXMLDump(InputStream in) {

        File dumpFilesDir = new File(Environment.getExternalStorageDirectory(), "UIXMLDumps");

        if (!dumpFilesDir.mkdirs()) {

            if (!dumpFilesDir.exists()) {
                Log.d(TAG, "Could not create hierarchy of folders for screenshot: " + dumpFilesDir.getAbsolutePath());
            } else {
                Log.d(TAG, "Directory already exist: " + dumpFilesDir.getAbsolutePath());
            }
        }

        File dump = new File(dumpFilesDir, "UnknownUIXMLDump_" + ++unknownDumpsCounter + ".xml");
        return writeInputStreamToFile(in, dump);
    }

    /**
     * Writes {@link InputStream} to the specified {@link File}.
     *
     * @param in                {@link InputStream} with content to write
     * @param fileToSaveInto    {@link File} to write content into
     * @return                  true - if content was successfully written to a file / false - otherwise
     */
    private boolean writeInputStreamToFile(InputStream in, File fileToSaveInto) {

        OutputStream out = null;
        // TODO: 12/14/18 Replace with try-with-resources once Java 1.8 is available
        try {
            out = new FileOutputStream(fileToSaveInto);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Specified file was not found! [" + fileToSaveInto.getAbsolutePath() + "]", e);
            return Boolean.FALSE;
        } catch (IOException e) {
            Log.e(TAG, "Failed to read InputStream or write to a file!", e);
            return Boolean.FALSE;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                in.close();
            } catch (IOException e) {
                Log.e(TAG, "Failed to close resources!", e);
            }
        }
        return Boolean.TRUE;
    }


}
