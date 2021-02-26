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
package com.good.gd.webview_v2.utils;

import android.util.Log;
import android.webkit.WebView;

import com.good.gd.webview_V2.bbwebview.WebClientObserver;

public class PageFinishedListener implements WebClientObserver.OnPageFinished {

    private static final String TAG = PageFinishedListener.class.getSimpleName();

    public boolean isPageFinished;
    public String url = "";

    public boolean isPageLoaded(String url) {
        return isPageFinished && this.url.equals(url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Log.i(TAG, "onPageFinished, url " + url);
        isPageFinished = true;
        this.url = url;
    }

}
