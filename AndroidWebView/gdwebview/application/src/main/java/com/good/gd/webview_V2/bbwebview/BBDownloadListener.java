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
package com.good.gd.webview_V2.bbwebview;

import android.util.Log;
import android.webkit.DownloadListener;

public class BBDownloadListener implements DownloadListener {
    private static final String TAG = "GDWebView-" +  BBDownloadListener.class.getSimpleName();

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        Log.i(TAG, "onDownloadStart [" + url+ "]");
        Log.i(TAG, "-onDownloadStart ua [" + userAgent+ "]");
        Log.i(TAG, "--onDownloadStart contentDisposition [" + contentDisposition+ "]");
        Log.i(TAG, "---onDownloadStart mimetype [" + mimetype+ "]");
        Log.i(TAG, "----onDownloadStart contentLength [" + contentLength + "]");
    }
}
