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

package blackberry.example.com.gettingstartedbd


import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import com.good.gd.net.GDSocket

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


/**
 * A simple [Fragment] subclass.
 * Use the [SocketFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SocketFragment : Fragment() {

    private var resultView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_socket, container, false)

        resultView = view.findViewById(R.id.httpContents) as TextView

        //Enable scrolling in the resultView.
        resultView!!.movementMethod = ScrollingMovementMethod()

        val loadButton = view.findViewById(R.id.loadButton) as Button
        loadButton.setOnClickListener {
            resultView!!.text = "Loading... "
            DownloadTask().execute()
        }

        return view
    }

    /**
     * Implementation of AsyncTask, to fetch the data in the background away from
     * the UI thread.
     */
    private inner class DownloadTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg strings: String): String {
            try {
                return doSocket()
            } catch (e: IOException) {
                return e.toString()
            }

        }

        /**
         * Update the resultView with the text download or exception caught.
         */
        override fun onPostExecute(result: String) {
            resultView!!.text = result
        }

    }

    /**
     * Creates a socket connection to developers.BlackBerry.com:80.
     * @return An InputStream retrieved from a successful HttpURLConnection.
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun doSocket(): String {
        var socket: GDSocket? = null
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            socket = GDSocket()
            socket.connect("developers.blackberry.com", 80, 1000)
            var response = ""

            //We'll make an HTTP request over the socket connection.
            val output = "GET http://developers.blackberry.com/ HTTP/1.1\r\n" +
                    "Host: developers.blackberry.com:80\r\n" +
                    "Connection: close\r\n" +
                    "\r\n"


            val byteArrayOutputStream = ByteArrayOutputStream(1024)
            val buffer = ByteArray(1024)

            var bytesRead: Int
            inputStream = socket.inputStream
            outputStream = socket.outputStream

            outputStream!!.write(output.toByteArray())

            while (true) {
                bytesRead = inputStream!!.read(buffer)
                if (bytesRead == -1) break  //Reached EOF.

                byteArrayOutputStream.write(buffer, 0, bytesRead)
                response += byteArrayOutputStream.toString("UTF-8")

                if (response.length > 1000) break //Stop reading after we've reached 1000 characters.
            }

            //Close all connections.
            inputStream.close()
            outputStream.close()
            byteArrayOutputStream.close()

            return response
        } finally {
            inputStream?.close()

            outputStream?.close()

            socket?.close()
        }
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment SocketFragment.
         */
        fun newInstance(): SocketFragment {
            return SocketFragment()
        }
    }
}
