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

package blackberry.example.com.gettingstarted;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;


public class FileFragment extends Fragment
{

    private final String FILENAME = "myFile.txt";

    private EditText fileContents;

    public FileFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FileFragment.
     */
    public static FileFragment newInstance()
    {
        return new FileFragment();
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
        View view = inflater.inflate(R.layout.fragment_file, container, false);

        fileContents = (EditText)view.findViewById(R.id.fileContents);

        final Button clearButton = (Button) view.findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fileContents.setText("");
            }
        });

        final Button saveButton = (Button) view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                try {
                    FileOutputStream outputStream;
                    outputStream = getContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
                    outputStream.write(fileContents.getText().toString().getBytes());
                    outputStream.close();

                    showMessage("File Saved.");
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage("Failed to save file: " + e.toString());
                }
            }
        });

        final Button loadButton = (Button) view.findViewById(R.id.loadButton);
        loadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                try {
                    InputStream inputStream = getContext().openFileInput(FILENAME);

                    if ( inputStream != null ) {
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String receiveString;
                        StringBuilder stringBuilder = new StringBuilder();

                        while ( (receiveString = bufferedReader.readLine()) != null ) {
                            stringBuilder.append(receiveString);
                        }

                        inputStream.close();
                        fileContents.setText(stringBuilder.toString());
                    }
                }
                catch (FileNotFoundException e) {
                    showMessage("Nothing to load, save something first.");
                }
                catch (Exception e) {
                    e.printStackTrace();
                    showMessage("Failed to load file: " + e.toString());
                }
            }
        });

        return view;
    }

    private void showMessage(String theMessage)
    {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(getContext(), theMessage, duration);
        toast.show();
    }

}
