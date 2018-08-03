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

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.good.gd.net.GDHttpClient;

import java.io.IOException;
import java.io.InputStream;

public class WebInputStream extends InputStream {
    private static final String TAG = WebInputStream.class.getSimpleName();

    private String name = null;
    private int readPlainCalls = 0;
    private int soFar = 0;
    private int closes = 0;
    private int returnedEOF = 0;

    private InputStream[] streams = null;
    private int streamIndex = 0;
    public GDHttpClient httpClient = null;
    private boolean shutdownDone = false;

    public int progressIncrement = 100000;

    public WebInputStream() {
        super();
        this.streams = new InputStream[]{};
        this.streamIndex = 0;
        this.name = "End of file.";
    }

    /** Construct a stream for a local file.
     *
     * @param inputStream stream object for the file. It must be open already.
     * @param name name for logging.
     */
    public WebInputStream(InputStream inputStream, @NonNull String name) {
        super();
        this.streams = new InputStream[]{inputStream};
        this.streamIndex = 0;
        this.name = new String(name);
    }

    /** Construct a stream for a BlackBerry Dynamics HTTP request.
     *
     * @param uri used to set the stream logging name.
     * @param gdHttpClient HTTP client whose connection will be shut down when the stream has
     *                     all been read.
     * @param inputStreams stream from the returned HTTP entity.
     */
    public WebInputStream(Uri uri,
                          GDHttpClient gdHttpClient,
                          InputStream... inputStreams
    ) {
        super();
        this.streams = inputStreams;
        this.name = uri.toString();
        this.httpClient = gdHttpClient;
    }

    private String logName() {
        return " \"" + name + "\" \"" + Thread.currentThread().getName() + "\"";
    }

    /* Here are some observations on use of streams by WebView.
     *
     * The Android WebResourceResponse documentation states:
     * >   Callers must implement InputStream.read(byte[]).
     * Therefore this class implements it here. However, it seems that it doesn't get called and
     * only the plain InputStream.read() gets called. The InputStream docco says that it calls
     * read() if read(byte[]) isn't implemented.
     *
     * It seems that the consumer of the stream will continue to call it after it has returned -1,
     * which indicates end-of-file (EOF). From the logs, it appears to be called three times in
     * total.
     */
    @Override
    public int read(@NonNull byte[] bytes) throws IOException {
        if (this.soFar <= 0) {
            Log.d(TAG, "Starting" + this.logName() + " buffer length:" + bytes.length + ".");
        }
        int return_ = -1;
        while (this.streamIndex < this.streams.length) {
            return_ = this.streams[this.streamIndex].read(bytes);
            if (return_ != -1) {
                this.soFar += return_;
                if (this.soFar % this.progressIncrement == 1) {
                    Log.d(TAG, "Reading" + this.logName() + " " + this.soFar + " ...");
                }
                break;
            }

            Log.d(TAG,
                "Reached EOF [" + this.streamIndex + "] read:" + this.soFar + this.logName() + ".");
            streamIndex += 1;
        }
        if (return_ == -1) {
            // Log the number of times EOF is returned.
            this.returnedEOF++;
            Log.d(TAG, "Returning EOF " + this.returnedEOF + this.logName() + ".");
        }

        return return_;
    }

    private byte[] readBuffer = new byte[1];
    @Override
    public int read() throws IOException {
        if (this.readPlainCalls <= 0) {
            Log.d(TAG, "Plain read() called, not read(byte[])" + this.logName() + ".");
        }
        this.readPlainCalls++;
        int readReturn = this.read(readBuffer);
        if (readReturn == -1) {
            return readReturn;
        }
        //
        // Java treats a byte as a signed number and therefore extends the sign when casting to int.
        // To fix this, the sign portion is masked out in the following line.
        readReturn = readBuffer[0] & 0xFF;
        if (readReturn < 0 || readReturn > 255) {
            throw new AssertionError("read return out of range " + readReturn);
        }
        return readReturn;
    }

    @Override
    public void close() {
        this.closes++;
        StringBuilder message = new StringBuilder("In close() ");
        message.append(this.closes);
        int closedOK = 0;
        int closeExceptions = 0;
        for (int index=0; index < this.streams.length; index++) {
            try {
                this.streams[index].close();
                closedOK++;
            } catch (IOException exception) {
                closeExceptions++;
                Log.e(TAG, "Exception closing stream " + index + " " + exception.toString() + ".");
            }
        }
        message.append(" ok:").append(closedOK).append(" exceptions:").append(closeExceptions);
        if (this.httpClient == null) {
            message.append(" httpClient:null");
        }
        else {
            if (this.shutdownDone) {
                message.append(" GDHttpClient already shut down");
            }
            else {
                message.append(" GDHttpClient connection shut down");
                this.httpClient.getConnectionManager().shutdown();
                this.shutdownDone = true;
            }
        }
        message.append(this.logName()).append(" read:").append(this.soFar).append(".");
        Log.d(TAG, message.toString());
    }

    public int drain() {
        int drainage = 0;
        try {
            int drainRead = this.read();
            while (drainRead != -1) {
                drainage++;
                drainRead = this.read();
            }
        }
        catch (IOException exception) {
            Log.d(TAG, "Exception draining stream " + exception.toString() + this.logName() + ".");
        }
        return drainage;
    }
}
