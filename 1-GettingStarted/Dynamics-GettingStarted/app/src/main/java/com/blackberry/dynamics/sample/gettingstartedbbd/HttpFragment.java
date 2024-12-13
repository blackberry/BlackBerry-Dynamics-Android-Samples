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

package com.blackberry.dynamics.sample.gettingstartedbbd;


import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.good.gd.apache.http.HttpResponse;
import com.good.gd.apache.http.client.methods.HttpGet;
import com.good.gd.net.GDHttpClient;
import com.good.gd.widget.GDEditText;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HttpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HttpFragment extends Fragment
{

    private TextView resultView;

    public HttpFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment HttpFragment.
     */
    public static HttpFragment newInstance()
    {
        return new HttpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_http, container, false);

        resultView = (TextView) view.findViewById(R.id.httpContents);

        //Enable scrolling in the resultView.
        resultView.setMovementMethod(new ScrollingMovementMethod());

        final GDEditText theUrl = (GDEditText) view.findViewById(R.id.theUrl);

        final Button loadButton = (Button) view.findViewById(R.id.loadButton);
        loadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resultView.setText("Loading... " + theUrl.getText().toString());
                new DownloadTask().execute(theUrl.getText().toString());
            }
        });


        return view;
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
                return loadFromNetwork(urls[0]);
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
        String str = "";

        try {
            stream = downloadUrl(urlString);
            str = readIt(stream, 1000);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return str;
    }

    /**
     * Given a string representation of a URL, sets up a connection and gets
     * an input stream.
     * @param urlString A string representation of a URL.
     * @return An InputStream retrieved from a successful HttpURLConnection.
     * @throws IOException
     */
    private InputStream downloadUrl(String urlString) throws IOException
    {

        GDHttpClient httpclient = new GDHttpClient();
        final HttpGet request = new HttpGet(urlString);

        HttpResponse response = httpclient.execute(request);
        InputStream stream = response.getEntity().getContent();

        return stream;
    }

    /** Reads an InputStream and converts it to a String.
     * @param stream InputStream containing HTML from targeted site.
     * @param len Length of string that this method returns.
     * @return String concatenated according to len parameter.
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    private String readIt(InputStream stream, int len) throws IOException
    {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}
