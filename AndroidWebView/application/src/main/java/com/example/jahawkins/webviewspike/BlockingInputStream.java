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


public class BlockingInputStream extends InputStream {
    private static final String TAG = BlockingInputStream.class.getSimpleName();

    private InputStream stream = null;
    private String name = null;
    private int soFar = 0;
    private Boolean atEOF = false;
    private int returnedEOF = 0;
    private int closes = 0;
    private GDHttpClient httpClient = null;
    // These are now unused, i.e. always zero. They would allow a diagnostic mode in which:
    // -   The stream sleeps after every read.
    // -   The stream appends extra whitespace to the end of the actual content.
    private int sleep = 0; //500;
    private int extras = 0; //1000000;

    /** Construct a placeholder empty stream.
     *
     * It returns EOF on first read.
     */
    public BlockingInputStream() {
        this.atEOF = true;
        this.name = "End of file.";
    }

    /** Construct a stream for a local file.
     *
     * @param inputStream stream object for the file. It must be open already.
     * @param name name for logging.
     */
    public BlockingInputStream(InputStream inputStream, @NonNull String name) {
        this.name = new String(name);
        if (this.name.equals("index.html")) {
            this.sleep = 0;
            this.extras = 0;
        }
        Thread thread = Thread.currentThread();
        Log.d(TAG, "Opening \"" + name + "\" \"" + thread.getName() + "\".");
        this.stream = inputStream;
    }

    /** Construct a stream for a BlackBerry Dynamics HTTP request.
     *
     * @param inputStream stream from the returned HTTP entity.
     * @param uri used to set the stream logging name.
     * @param gdHttpClient HTTP client whose connection will be shut down when the stream has
     *                     all been read.
     */
    public BlockingInputStream(InputStream inputStream, Uri uri, GDHttpClient gdHttpClient) {
        if (uri.getPathSegments().size() > 0) {
            this.name = uri.getLastPathSegment();
        }
        else {
            this.name = "index.html";
            this.sleep = 0;
            this.extras = 0;
        }
        this.httpClient = gdHttpClient;
        this.stream = inputStream;
    }

    @Override
    public int read() throws IOException {
        Thread thread = Thread.currentThread();
        if (this.soFar <= 0) {
            Log.d(TAG, "Starting \"" + name + "\" \"" + thread.getName() + "\" ...");
        }
        if (this.sleep > 0) {
            try {
                Thread.sleep(this.sleep);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int return_ = -1;
        if (!this.atEOF) {
            return_ = this.stream.read();
            if (return_ < 0) {
                Log.d(TAG, "Reached EOF \"" + name + "\" \"" + thread.getName() + "\".");
                this.atEOF = true;
            }
            else {
                if ((++this.soFar) % 100000 == 1 /*&& this.sleep > 0*/) {
                    Log.d(TAG,
                    "Reading \"" + name + "\" \"" + thread.getName() + "\" " +
                    this.soFar + " ...");
                }
            }
        }
//            if (this.atEOF && this.httpClient != null) {
//                Log.d(TAG,
//                    "Shutting down connection \"" + name + "\" \"" + thread.getName() + "\".");
//                this.httpClient.getConnectionManager().shutdown();
//                this.httpClient = null;
//            }
        if (this.atEOF && this.returnedEOF <= 0) {
            if (this.extras > 0) {
                if (this.extras-- % 10000 == 0) {
                    Log.d(TAG,
                    "Extras \"" + name + "\" \"" + thread.getName() + "\" " +
                    this.extras + "...");
                }
                return_ = ' ';
                if (this.extras == 0) {
                    Log.d(TAG, "Extras finished \"" + name + "\" \"" + thread.getName() + "\".");
                }
            }
        }
        if (return_ < 0) {
            // Log the number of times EOF is returned.
            this.returnedEOF++;
            Log.d(TAG,
            "Returning EOF " + this.returnedEOF + " \"" + name + "\" \"" +
            thread.getName() + "\".");
        }

        return return_;
    }

    @Override
    public void close() {
        this.closes++;
        Log.d(TAG,
        "Call to close() " + this.closes + " \"" + name + "\" \"" +
        Thread.currentThread().getName() + "\".");
        if (this.httpClient != null) {
            Log.d(TAG,
            "Shutting down connection in close() \"" + name + "\" \"" +
            Thread.currentThread().getName() + "\".");
            this.httpClient.getConnectionManager().shutdown();
            this.httpClient = null;
        }
    }
}
