/* Copyright (c) 2021 BlackBerry Limited.
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

package com.msohm.gdhttpclientfileupload;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.apache.http.HttpConnection;
import com.good.gd.apache.http.HttpConnectionMetrics;
import com.good.gd.apache.http.HttpEntity;
import com.good.gd.apache.http.HttpResponse;
import com.good.gd.apache.http.client.methods.HttpGet;
import com.good.gd.apache.http.client.methods.HttpPost;
import com.good.gd.apache.http.entity.EntityTemplate;
import com.good.gd.net.GDHttpClient;
import com.google.android.gms.common.util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GDStateListener {

    //The URL of the form the file is uploaded too.
    //This sample makes use of Post Test Server V2.  Go to http://ptsv2.com/, create your own
    //instance and adjust the URL below accordingly.
    private static final String UPLOAD_URL = "http://ptsv2.com/t/<YOUR_ID>/post";

    //The boundary for our multipart form.  This can be any unique ASCII value.
    public static final String BOUNDARY = "----123456789987654321";

    private TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GDAndroid.getInstance().activityInit(this);
        setContentView(R.layout.activity_main);

        resultView = (TextView) findViewById(R.id.resultTextView);

        //Enable scrolling in the resultView.
        resultView.setMovementMethod(new ScrollingMovementMethod());
    }

    public void doUpload(View view)
    {
        new DownloadTask().execute();
    }

    /**
     * Implementation of AsyncTask, to fetch the data in the background away from
     * the UI thread.
     */
    private class DownloadTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadFromNetwork(UPLOAD_URL);
            } catch (IOException e) {
                return e.toString();
            }
        }

        /**
         * Update the resultView with the text download or exception caught.
         */
        @Override
        protected void onPostExecute(String result) {
            resultView.setText(result);
        }

    }

    /** Initiates the fetch operation. */
    private String loadFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        StringBuilder out = new StringBuilder();

        try {
            final int bufferSize = 1024;
            final char[] buffer = new char[bufferSize];
            stream = connectToUrl(urlString);
            Reader in = new InputStreamReader(stream, "UTF-8");
            for (; ; ) {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return out.toString();
    }

    /**
     * Given a string representation of a URL, sets up a connection and gets
     * an input stream.
     * @param urlString A string representation of a URL.
     * @return An InputStream retrieved from a successful HttpURLConnection.
     * @throws IOException
     */
    private InputStream connectToUrl(String urlString) throws IOException
    {

        GDHttpClient httpclient = new GDHttpClient();
        HttpPost post = new HttpPost(urlString);
        UploadContentProducer contentProducer = new UploadContentProducer();

        HttpEntity entity = new EntityTemplate( contentProducer );
        post.addHeader( "Connection", "Keep-Alive" );
        post.addHeader( "Content-Type", "multipart/form-data; boundary=" + BOUNDARY );
        post.setEntity( entity );

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        entity.writeTo(bos);

        HttpResponse response = httpclient.execute(post);
        InputStream stream = response.getEntity().getContent();

        return stream;
    }

    @Override
    public void onAuthorized() {

    }

    @Override
    public void onLocked() {

    }

    @Override
    public void onWiped() {

    }

    @Override
    public void onUpdateConfig(Map<String, Object> map) {

    }

    @Override
    public void onUpdatePolicy(Map<String, Object> map) {

    }

    @Override
    public void onUpdateServices() {

    }

    @Override
    public void onUpdateEntitlements() {

    }
}