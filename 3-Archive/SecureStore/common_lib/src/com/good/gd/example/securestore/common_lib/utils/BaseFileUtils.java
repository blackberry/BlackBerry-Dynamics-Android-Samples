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

package com.good.gd.example.securestore.common_lib.utils;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static com.good.gd.example.securestore.common_lib.utils.AppLogUtils.DEBUG_LOG;

/** FileUtils - this class abstracts the interface to the two file stores (one the ordinary Java file
 * store) and the other the secure container. This class tracks the current mode and returns an object
 * backed by the container if in container mode.
 */
public abstract class BaseFileUtils {

    public static String SDCARD_ROOT = null;
    public final static String CONTAINER_ROOT = "/";

    public final static int MODE_SDCARD    = 1;
    public final static int MODE_CONTAINER = 2;
    protected int mCurrentMode = MODE_CONTAINER;



    /** setMode - sets the current mode
     */
    public void setMode(int mode) {
        mCurrentMode = mode;
    }

    /** getMode - returns the current mode
     */
    public int getMode() {
        return mCurrentMode;
    }

    /** getCurrentRoot - returns the path to the current root dependent on where that is
     */
    public String getCurrentRoot() {
        return (mCurrentMode == MODE_SDCARD) ? SDCARD_ROOT : CONTAINER_ROOT;
    }

    /** getFileFromPath - returns a File object rooted on the specified path
     */
    public File getFileFromPath(String path) {
        if (mCurrentMode == MODE_SDCARD) {
            return new java.io.File(path);
        } else if (mCurrentMode == MODE_CONTAINER) {
            return new com.good.gd.file.File(path);
        } else {
            return null;
        }
    }

    /** getParentFile - returns a File object rooted on the specified path's parent
     */
    public File getParentFile(String path) {
        // TODO: if GD etc
        return new File(path).getParentFile();
    }

    /** canGoUpOne - checks if we are allowed to traverse one-layer up the tree
     */
    public boolean canGoUpOne(String path) {
        return !path.equals(SDCARD_ROOT) && !path.equals(CONTAINER_ROOT);
    }

    /** copyToContainer - copies the file at the specified path (on the plain FS) into
     * the root of the container
     */
    public boolean copyToContainer(String path) {
        boolean retVal = false;
        try {
            if (mCurrentMode == MODE_SDCARD) {
                java.io.File srcFile = new java.io.File(path);
                com.good.gd.file.File destFile = new com.good.gd.file.File("/", srcFile.getName());
                copyFile(srcFile, destFile);
                retVal = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retVal;
    }


    public void copyFile(File sourceLocation , com.good.gd.file.File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists() && !targetLocation.mkdirs()) {
                throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
            }

            String[] children = sourceLocation.list();
            for (int i=0; i<children.length; i++) {
                copyFile(new File(sourceLocation, children[i]),
                        new com.good.gd.file.File(targetLocation, children[i]));
            }
        } else {

            // make sure the directory we plan to store the recording in exists
            File directory = targetLocation.getParentFile();
            if (directory != null && !directory.exists() && !directory.mkdirs()) {
                throw new IOException("Cannot create dir " + directory.getAbsolutePath());
            }

            InputStream in = new java.io.FileInputStream(sourceLocation);
            com.good.gd.file.FileOutputStream out = new com.good.gd.file.FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }

    /** openItem - opens our file viewer specifying the path to the file to open,
     * only .txt files supported
     */
    public abstract void openItem(Context ctx, String fullFilePath);


    /** deleteItem - deletes the file or folder at the specified path
     */
    public void deleteItem(String fullFilePath) {

        DEBUG_LOG( "deleteItem filepath =" + fullFilePath);

        java.io.File file = (mCurrentMode == MODE_SDCARD) ? new java.io.File(fullFilePath) :
                            (mCurrentMode == MODE_CONTAINER) ? new com.good.gd.file.File(fullFilePath) : null;

        DEBUG_LOG("deleteItem file =" + file);

        removeFile(file);
    }

    public boolean removeFile(File file) {
        if (file == null) {
            return false;
        }
        if (!file.exists()) {
            DEBUG_LOG("removeFile file does not exist");
            return true;
        }

        String[] list = file.list();

        if (list != null) {

            DEBUG_LOG("removeFile fileList = " + Arrays.toString(list));

            for (int i = 0; i < list.length; i++) {
            	File entry = (mCurrentMode == MODE_SDCARD) ? 
            			new java.io.File(file, list[i]) : (mCurrentMode == MODE_CONTAINER) ? 
            					new com.good.gd.file.File(file, list[i]) : null;
                if (entry.isDirectory()) {
                    if (!removeFile(entry))
                        return false;
                } else {
                    if (!entry.delete()) {
                        return false;
                    }
                }
            }
        } else {
            DEBUG_LOG("removeFile fileList is null ");
        }

        boolean ret = file.delete();

        DEBUG_LOG("removeFile delete ret = " + ret);

        return ret;
    }

    /** getFileData - returns a byte array of the file at the specified path
     */
    public byte[] getFileData(String path) {
        byte retData[] = null;
        try {
            java.io.InputStream is = (mCurrentMode == MODE_SDCARD) ?  new java.io.FileInputStream(path) :
                                     (mCurrentMode == MODE_CONTAINER) ? new com.good.gd.file.FileInputStream(path)
                                     : null;
            if (is != null&& is.available() > 0) {
                retData = new byte[is.available()];
                is.read(retData);
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retData;
    }

    /** makeNewDir - creates a new directory of the specified name at the specified root
     */
    public void makeNewDir(String root, String dirName) {
        java.io.File file = (mCurrentMode == MODE_SDCARD) ? new java.io.File(root, dirName) :
                            (mCurrentMode == MODE_CONTAINER) ? new com.good.gd.file.File(root, dirName) : null;
        if (file != null) {
            file.mkdir();
        }
    }

    public boolean isDir(String path){
        java.io.File file = (mCurrentMode == MODE_SDCARD) ? new java.io.File(path) :
                (mCurrentMode == MODE_CONTAINER) ? new com.good.gd.file.File(path) : null;

        if (file != null) {
            return file.isDirectory();
        }

        return false;
    }

}
