/* Copyright (c) 2023 BlackBerry Ltd.
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

package com.good.gd.example.gdinteraction;

import android.util.Log;

import com.good.gd.file.FileOutputStream;
import com.good.gd.file.FileInputStream;
import com.good.gd.file.GDFileSystem;

import java.io.IOException;
import java.io.InputStream;

final class FileUtils {

    private static final String folderName = "PackageWatcher_Folder";
    private static final String filename ="packageWatcher.txt";
    private static final String TAG = "GDInteractionFileUtils:";

    static void writeToFile(String data) {
        try {
            com.good.gd.file.File file = new com.good.gd.file.File(folderName);
            boolean isDirectoryCreated = file.mkdir();
            if (isDirectoryCreated) {
                FileOutputStream out = GDFileSystem.openFileOutput(folderName + "/" + filename, GDFileSystem.MODE_APPEND);
                out.write(data.getBytes());
                out.flush();
                out.close();
                Log.i(TAG, "writeToFile : " + data);
            } else {
                Log.i(TAG, "Directory was not created");
            }

        } catch (IOException e) {
            Log.e("Exception", "writeToFile FAILED" + e.toString());
        }
    }

    static String readDataFromFile() {
        Log.i(TAG, "readDataFromFile");
        final String filePath = folderName + "/" + filename;
        String dataFromFile = "";
        byte data[];
        try {
            final InputStream inputStream = new FileInputStream(filePath);
            if (inputStream.available() > 0) {
                data = new byte[inputStream.available()];
                inputStream.read(data);
                dataFromFile = new String(data, "UTF-8");
                inputStream.close();
            }

        } catch (final IOException ioException) {
            dataFromFile = "File read error";
        }

        return dataFromFile;
    }

    static void clearFileData() {
        try {
            String data = "";
            com.good.gd.file.File file = new com.good.gd.file.File(folderName);
            boolean isDirectoryCreated = file.mkdir();
            if (isDirectoryCreated) {
                FileOutputStream out = GDFileSystem.openFileOutput(folderName + "/" + filename, GDFileSystem.MODE_PRIVATE);
                out.write(data.getBytes());
                out.flush();
                out.close();
            }
            Log.i(TAG, "data wiped");
        } catch (IOException e) {
            Log.e("Exception", "clearFileData FAILED" + e.toString());
        }
    }
}
