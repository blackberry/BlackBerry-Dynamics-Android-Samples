/* Copyright (c) 2017  BlackBerry Limited.
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
*/

package com.msohm.blackberry.samples.bdvideoplayback;

import android.media.MediaDataSource;

import com.good.gd.file.File;
import com.good.gd.file.RandomAccessFile;

import java.io.IOException;


public class BDMediaDataSource extends MediaDataSource
{
    private RandomAccessFile randFile;
    private long size;

    //Open the media file stored in the BlackBerry Dynamics file secure system.
    public BDMediaDataSource(String fileName) throws IOException
    {
        File file = new File(fileName);
        size = file.length();
        randFile = new RandomAccessFile(file, "r");
    }

    //Read and return the desired portion of the file.
    @Override
    public int readAt(long position, byte[] buffer, int offset, int length) throws IOException
    {

        if (randFile != null)
        {
            randFile.seek(position);

            return randFile.read(buffer, offset, length);
        }
        else
        {
            return -1;
        }
    }

    //Return the size of the file (unencrypted size).
    @Override
    public long getSize() throws IOException
    {
        return size;
    }

    //Close the file.
    @Override
    public void close() throws IOException
    {
        if (randFile != null)
        {
            randFile.close();
            randFile = null;
        }
    }
}
