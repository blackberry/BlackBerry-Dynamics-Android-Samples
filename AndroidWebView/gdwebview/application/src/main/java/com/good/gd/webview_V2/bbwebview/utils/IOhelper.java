/*
 * Copyright (c) 2020 BlackBerry Limited.
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
package com.good.gd.webview_V2.bbwebview.utils;

import android.util.Log;

import org.brotli.dec.BrotliInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class IOhelper {

    private static final String TAG = "GDWebView-" + IOhelper.class.getSimpleName();

    public static InputStream inputStreamDecorator(InputStream is, String contentEncoding, boolean isChunked) throws IOException {

        Log.i(TAG, "inputStreamDecorator() " + contentEncoding);

        if ("gzip".equalsIgnoreCase(contentEncoding)) {
            return new GZIPInputStream(is);
        } else if ("deflate".equalsIgnoreCase(contentEncoding)) {
            return new InflaterInputStream(is);
        } else if ("br".equalsIgnoreCase(contentEncoding)) {
            return new BrotliInputStream(is);
        } else {
            Log.w(TAG, "inputStreamDecorator() no decor for " + contentEncoding);
            return is;
        }
    }
}