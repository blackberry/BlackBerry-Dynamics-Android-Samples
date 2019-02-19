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

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.ArrayList;

public class JavaScriptAssetStream extends InputStream {
    private static final String TAG = JavaScriptAssetStream.class.getSimpleName();
    private String logStr(String value) {
        if (value == null) {
            return " null";
        }
        return " \"" + value + "\"";
    }

    private final static SecureRandom secureRandom = new SecureRandom();

    private final static char onceChars[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };
    private final static int onceLength = 50;

    private final static Charset utf8Charset = Charset.forName("UTF-8");
    private final static byte DOCTYPE[] = "<!DOCTYPE HTML>\n".getBytes(utf8Charset);
    private final static byte SCRIPT_CLOSE[] = "</script>\n".getBytes(utf8Charset);

    private Context assetContext;
    private String assetFilename;
    JavaScriptAssetStream(Context context, String assetFilename) {
        this.assetContext = context;
        this.assetFilename = new String(assetFilename);
    }

    public String getName() {
        return new String(this.assetFilename);
    }

    private Boolean addDOCTYPE = true;
    private byte scriptTagOpen[] = null;
    private byte onceValue[] = null;

    public JavaScriptAssetStream setAddDOCTYPE(Boolean setValue) {
        this.addDOCTYPE = setValue;
        return this;
    }
    public JavaScriptAssetStream setAddScriptNOnce(Boolean setValue) {
        if (setValue) {
            StringBuilder onceString = new StringBuilder(onceLength);

            for (int index = 0; index < onceLength; index++) {
                onceString.append(onceChars[secureRandom.nextInt(onceChars.length)]);
            }
            // Assume the injected asset file is encoded in UTF-8.
            this.onceValue = onceString.toString().getBytes(utf8Charset);
            this.scriptTagOpen = ("<script nonce=\"" + this.getOnceValue() + "\">\n")
                .getBytes(utf8Charset);
        }
        else {
            this.onceValue = null;
            this.scriptTagOpen = null;
        }
        return this;
    }

    public String getOnceValue() {
        if (this.onceValue == null) {
            return null;
        }

        try {
            return new String(this.onceValue, utf8Charset.name());
        } catch (UnsupportedEncodingException exception) {
            Log.d(TAG, "Couldn't generate once value for" +
                logStr(this.onceValue.toString()) + " " + exception.toString());
        }
        return this.onceValue.toString();
    }

    public long getLength() throws IOException {
        InputStream inputStream = this.assetContext.getAssets().open(this.assetFilename);
        long length =
            (this.addDOCTYPE ? DOCTYPE.length : 0
            ) + (this.scriptTagOpen == null ? 0 : this.scriptTagOpen.length + SCRIPT_CLOSE.length);


        byte[] bytes = new byte[1024];
        int read = inputStream.read(bytes);
        while (read >= 0) {
            length += read;
            read = inputStream.read(bytes);
        }
        inputStream.close();
        return length;
    }

    private InputStream streams[] = null;
    private int streamIndex = 0;

    private void open() throws IOException {
        int count = 1 + (this.addDOCTYPE ? 1 : 0) + (this.scriptTagOpen == null ? 0 : 2);

        this.streams = new InputStream[count];
        int index = 0;
        if (this.addDOCTYPE) {
            this.streams[index++] = new ByteArrayInputStream(DOCTYPE);
        }
        if (this.scriptTagOpen != null) {
            this.streams[index++] = new ByteArrayInputStream(this.scriptTagOpen);
        }
        this.streams[index++] = this.assetContext.getAssets().open(this.assetFilename);
        if (this.scriptTagOpen != null) {
            this.streams[index++] = new ByteArrayInputStream(SCRIPT_CLOSE);
        }
        this.streamIndex = 0;
    }

    @Override
    public int read() throws IOException {
        if (this.streams == null) {
            this.open();
        }
        int readResult = -1;
        while (this.streamIndex < this.streams.length) {
            readResult = this.streams[this.streamIndex].read();
            if (readResult != -1) {
                break;
            }
            this.streams[this.streamIndex].close();
            this.streamIndex++;
        }
        return readResult;
    }

}
