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

package com.good.gd.example.securestore.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.good.gd.example.securestore.FileViewer;
import com.good.gd.example.securestore.common_lib.utils.BaseFileUtils;

public class FileUtils extends BaseFileUtils {

    /*
        Singleton implementation
     */
    private static FileUtils sInstance;

    public static synchronized FileUtils getInstance() {
        if (sInstance == null) {
            sInstance = new FileUtils();
        }
        return sInstance;
    }

    private FileUtils() {

    }

    @Override
    public void openItem(Context ctx, String fullFilePath) {

        java.io.File file = (mCurrentMode == MODE_SDCARD) ? new java.io.File(fullFilePath) :
                (mCurrentMode == MODE_CONTAINER) ? new com.good.gd.file.File(fullFilePath) : null;
        if (fullFilePath.endsWith(".txt")) {
            if (file != null) {
                Intent i = new Intent();
                i.putExtra(FileViewer.FILE_VIEWER_PATH, fullFilePath);
                i.setClass(ctx, FileViewer.class);
                ctx.startActivity(i);
            }
        } else {
            Toast.makeText(ctx, "Only .txt files supported in sample", Toast.LENGTH_SHORT).show();
        }

    }
}
