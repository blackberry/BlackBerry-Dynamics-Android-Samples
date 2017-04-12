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

package com.msohm.blackberry.samples.videoplayback;

import android.net.Uri;
import com.good.gd.file.GDFileSystem;
import com.good.gd.file.FileInputStream;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import java.io.IOException;


//Implementation of an ExoPlayer DataSource that is able to read video files directly from
//the BlackBerry Dynamics file system.
public final class BDDataSource implements DataSource
{

    private FileInputStream inputStream;
    private boolean fileOpen;
    private Uri filePath;
    private long bytesRemaining = 0;

    public BDDataSource() {}

    //Open a FileInputStream of the media file stored in the BlackBerry Dynamics secure file system.
    @Override
    public long open(DataSpec dataSpec) throws IOException
    {
        filePath = dataSpec.uri;
        //Trim "file:///" that's prefixed to the start of the URI.
        inputStream = GDFileSystem.openFileInput(filePath.toString().substring(8));

        if ( inputStream != null )
        {
            fileOpen = true;
            bytesRemaining =  inputStream.available();
        }

        return bytesRemaining;
    }

    //Read and return the desired portion of the file.
    @Override
    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        if (bytesRemaining == 0)
        {
            return -1;
        }
        else
        {
            int bytesRead = 0;
            bytesRead = inputStream.read(buffer, offset, readLength);

            if (bytesRead > 0) {
                bytesRemaining -= bytesRead;
            }

            return bytesRead;
        }
    }

    //Return the file path.
    @Override
    public Uri getUri() {
        return filePath;
    }

    //Close the FileInputStream.
    @Override
    public void close() throws IOException {
        filePath = null;
        if (inputStream != null)
        {
            try
            {
                inputStream.close();
            }
            catch (IOException e)
            {
                throw new IOException(e);
            }
            finally
            {
                inputStream = null;
                if (fileOpen)
                {
                    fileOpen = false;
                }
            }
        }
    }
}
