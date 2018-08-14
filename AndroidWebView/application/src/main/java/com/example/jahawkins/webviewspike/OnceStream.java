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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OnceStream extends InputStream {
//    private String onceValue = null;
//    private BufferedReader reader = null;

    /** Constructor, which must receive an open InputStream.
     *
     * @param stream
     */
    private int readSoFar = 0;
    private int replaceOffset;
    private final byte[] replacement;
    private final InputStream stream;
    private final int replaceEnd;
    public OnceStream(InputStream stream, int replaceOffset, byte[] replacement) {
        this.stream = stream;
        this.replaceOffset = replaceOffset;
        this.replacement = replacement;
        this.replaceEnd = this.replaceOffset + this.replacement.length;
    }

    @Override
    public int read() throws IOException {
        int toRead = stream.read();
        if (toRead == -1) {
            return toRead;
        }

        if (this.readSoFar >= this.replaceOffset && this.readSoFar < this.replaceEnd) {
            toRead = this.replacement[this.readSoFar - this.replaceOffset];
        }
        this.readSoFar += 1;

        return toRead;

    }
}
