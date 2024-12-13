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

package com.blackberry.dynamics.sample.gettingstartedbbd


import androidx.fragment.app.Fragment
import android.os.AsyncTask
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import com.good.gd.apache.http.client.methods.HttpGet
import com.good.gd.net.GDHttpClient
import com.good.gd.widget.GDEditText;

import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.io.UnsupportedEncodingException


/**
 * A simple [Fragment] subclass.
 * Use the [HttpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HttpFragment : Fragment() {

    private var resultView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_http, container, false)

        resultView = view.findViewById(R.id.httpContents) as TextView

        //Enable scrolling in the resultView.
        resultView!!.movementMethod = ScrollingMovementMethod()

        val theUrl = view.findViewById(R.id.theUrl) as GDEditText

        val loadButton = view.findViewById(R.id.loadButton) as Button
        loadButton.setOnClickListener {
            resultView!!.text = "Loading... " + theUrl.text.toString()
            DownloadTask().execute(theUrl.text.toString())
        }

        return view
    }

    /**
     * Implementation of AsyncTask, to fetch the data in the background away from
     * the UI thread.
     */
    private inner class DownloadTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg urls: String): String {
            return try {
                loadFromNetwork(urls[0])
            } catch (e: IOException) {
                e.toString()
            }
        }

        /**
         * Update the resultView with the text download or exception caught.
         */
        override fun onPostExecute(result: String) {
            this@HttpFragment.resultView!!.text = result
        }

    }

    /** Initiates the fetch operation.  */
    @Throws(IOException::class)
    private fun loadFromNetwork(urlString: String): String {
        var stream: InputStream? = null
        var str: String

        try {
            stream = downloadUrl(urlString)
            str = readIt(stream, 1000)
        } finally {
            stream?.close()
        }
        return str
    }

    /**
     * Given a string representation of a URL, sets up a connection and gets
     * an input stream.
     * @param urlString A string representation of a URL.
     * @return An InputStream retrieved from a successful HttpURLConnection.
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun downloadUrl(urlString: String): InputStream {

        val httpclient = GDHttpClient()
        val request = HttpGet(urlString)
        val response = httpclient.execute(request)

        return response.entity.content
    }

    /** Reads an InputStream and converts it to a String.
     * @param stream InputStream containing HTML from targeted site.
     * @param len Length of string that this method returns.
     * @return String concatenated according to len parameter.
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    @Throws(IOException::class)
    private fun readIt(stream: InputStream?, len: Int): String {
        var reader: Reader? = null
        reader = InputStreamReader(stream!!, "UTF-8")
        val buffer = CharArray(len)
        reader.read(buffer)
        return String(buffer)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment HttpFragment.
         */
        fun newInstance(): HttpFragment {
            return HttpFragment()
        }
    }
}
