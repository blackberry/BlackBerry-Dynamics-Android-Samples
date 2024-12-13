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
import android.widget.EditText;
import android.widget.TextView;

import com.good.gd.net.GDSocket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SocketFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocketFragment extends Fragment
{

    private TextView resultView;

    public SocketFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment SocketFragment.
     */
    public static SocketFragment newInstance()
    {
        return new SocketFragment();
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
        View view = inflater.inflate(R.layout.fragment_socket, container, false);

        resultView = (TextView) view.findViewById(R.id.httpContents);

        //Enable scrolling in the resultView.
        resultView.setMovementMethod(new ScrollingMovementMethod());

        final EditText theUrl = (EditText) view.findViewById(R.id.theUrl);

        final Button loadButton = (Button) view.findViewById(R.id.loadButton);
        loadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resultView.setText("Loading... ");
                new DownloadTask().execute();
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
        protected String doInBackground(String... strings) {
            try {
                return doSocket();
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


    /**
     * Creates a socket connection to developers.BlackBerry.com:80.
     * @return An InputStream retrieved from a successful HttpURLConnection.
     * @throws IOException
     */
    private String doSocket() throws IOException
    {
        GDSocket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try
        {
            socket = new GDSocket();
            socket.connect("developers.blackberry.com", 80, 1000);
            String response = "";

            //We'll make an HTTP request over the socket connection.
            String output = "GET http://developers.blackberry.com/ HTTP/1.1\r\n" +
                    "Host: developers.blackberry.com:80\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";


            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];

            int bytesRead;
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(output.getBytes());

            while ((bytesRead = inputStream.read(buffer)) != -1)
            {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                response += byteArrayOutputStream.toString("UTF-8");

                if (response.length() > 1000)
                {
                    //Stop reading after we've reached 1000 characters.
                    break;
                }
            }

            //Close all connections.
            inputStream.close();
            outputStream.close();
            byteArrayOutputStream.close();

            return response;
        }
        finally
        {
            if (inputStream != null)
            {
                inputStream.close();
            }

            if (outputStream != null)
            {
                outputStream.close();
            }

            if (socket != null)
            {
                socket.close();
            }
        }
    }
}
