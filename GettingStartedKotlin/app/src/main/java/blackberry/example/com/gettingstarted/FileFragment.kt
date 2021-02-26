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

package blackberry.example.com.gettingstarted

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.io.*

private const val FILENAME = "myFile.txt"

class FileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_file, container, false)

        val fileContents: EditText = view.findViewById(R.id.fileContents) as EditText

        val clearButton = view.findViewById(R.id.clearButton) as Button
        clearButton.setOnClickListener { fileContents.setText("") }

        val saveButton = view.findViewById(R.id.saveButton) as Button
        saveButton.setOnClickListener {
            try {
                val outputStream: FileOutputStream =
                    context!!.openFileOutput(FILENAME, Context.MODE_PRIVATE)
                outputStream.write(fileContents.text.toString().toByteArray())
                outputStream.close()

                showMessage("File Saved.")
            } catch (e: Exception) {
                e.printStackTrace()
                showMessage("Failed to save file: $e")
            }
        }

        val loadButton = view.findViewById(R.id.loadButton) as Button
        loadButton.setOnClickListener {
            try {
                val inputStream: FileInputStream? = context!!.openFileInput(FILENAME)

                if (inputStream != null) {
                    val inputStreamReader = InputStreamReader(inputStream)
                    val bufferedReader = BufferedReader(inputStreamReader)
                    var receiveString: String?
                    val stringBuilder = StringBuilder()

                    while (true) {
                        receiveString = bufferedReader.readLine()
                        if (receiveString == null) break
                        stringBuilder.append(receiveString)
                    }

                    inputStream.close()
                    fileContents.setText(stringBuilder.toString())
                }
            } catch (e: FileNotFoundException) {
                showMessage("Nothing to load, save something first.")
            } catch (e: Exception) {
                e.printStackTrace()
                showMessage("Failed to load file: $e")
            }
        }

        return view
    }

    private fun showMessage(theMessage: String) {
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(context, theMessage, duration)
        toast.show()
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment FileFragment.
         */
        fun newInstance(): FileFragment {
            return FileFragment()
        }
    }
}
