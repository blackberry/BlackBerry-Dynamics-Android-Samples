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

import com.good.gd.apache.http.entity.ContentProducer;

import java.io.IOException;
import java.io.OutputStream;

class UploadContentProducer implements ContentProducer {

    //The content of the file to be uploaded.  Usually you would read this from the file system.
    //For simplicity, this sample uploads this file contents from memory.
    private static final String FILE_CONTENTS = "Hello File!";
    //The file name.
    private static final String FILE_NAME = "myfile.txt";
    //This is the name of the file parameter in the multipart form.
    private static final String FILE_PARAMETER = "file_form_name";
    //The prefix for the boundary, this must be --.
    private static final String DELIMITER = "--";

    public UploadContentProducer()
    {}

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {

        outputStream.write(("Content-Type: multipart/form-data; boundary=" + MainActivity.BOUNDARY).getBytes());
        outputStream.write(("\r\n\r\n").getBytes()); // add this line before inserting any content

        //Add the form parts required by your multipart form.
        addFormPart( outputStream, "form_parameter_name", "Form Parameter Value" );
        //Add the file as a binary.
        addFilePart(outputStream, FILE_PARAMETER, FILE_NAME, FILE_CONTENTS.getBytes());
        //Add this line at the end of the request
        outputStream.write((DELIMITER + MainActivity.BOUNDARY + DELIMITER).getBytes());

        outputStream.flush();
        outputStream.close();
    }

    public void addFormPart( OutputStream os, String paramName, String value ) throws IOException {
        os.write((DELIMITER + MainActivity.BOUNDARY + "\r\n").getBytes());
        os.write(("Content-Disposition: form-data; name=\"" + paramName + "\"\r\n").getBytes());
        os.write("Content-Type: text/plain\r\n".getBytes());
        os.write(("\r\n" + value + "\r\n").getBytes());
    }

    public void addFilePart(OutputStream os, String paramName, String fileName, byte[] data) throws IOException {
        os.write((DELIMITER + MainActivity.BOUNDARY + "\r\n").getBytes());
        os.write(("Content-Disposition: form-data; name=\"" + paramName + "\"; filename=\"" + fileName + "\"\r\n").getBytes());
        os.write(("Content-Type: application/octet-stream\r\n").getBytes());
        os.write(("Content-Transfer-Encoding: binary\r\n").getBytes());
        os.write("\r\n".getBytes());
        os.write(data);
        os.write("\r\n".getBytes());
    }
}
