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

import android.webkit.WebView;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebClientObserver {

    private Set<OnPageFinished> finishedListeners = ConcurrentHashMap.newKeySet();
    private Set<OnPageStarted> startedListeners = ConcurrentHashMap.newKeySet();
    private Set<OnPageContentVisible> contentVisibleListeners = ConcurrentHashMap.newKeySet();
    private Set<OnLoadUrl> loadUrlListeners = ConcurrentHashMap.newKeySet();

    public interface OnPageFinished {
        void onPageFinished(WebView view, String url);
    }

    public interface OnPageStarted {
        void onPageStarted(WebView view, String url);
    }

    public interface OnPageContentVisible {
        void onPageContentVisible(WebView view, String url);
    }

    public interface OnLoadUrl {
        void onLoadUrl(String url);
    }

    public WebClientObserver() {
    }

    public void addOnPageStartedListener(OnPageStarted onPageStarted) {
        if (onPageStarted != null) {
            startedListeners.add(onPageStarted);
        }
    }

    public void addOnPageFinishedListener(OnPageFinished onPageFinished) {
        if (onPageFinished != null) {
            finishedListeners.add(onPageFinished);
        }
    }

    public void addOnContentVisibleListener(OnPageContentVisible onPageCommitVisible) {
        if (onPageCommitVisible != null) {
            contentVisibleListeners.add(onPageCommitVisible);
        }
    }

    public void addLoadUrlListener(OnLoadUrl onLoadUrl) {
        if (onLoadUrl != null) {
            loadUrlListeners.add(onLoadUrl);
        }
    }

    public void removeLoadUrlListener(OnLoadUrl onLoadUrl) {
        if (onLoadUrl != null) {
            loadUrlListeners.remove(onLoadUrl);
        }
    }

    public void removeOnPageStartedListener(OnPageStarted onPageStarted) {
        if (onPageStarted != null) {
            startedListeners.remove(onPageStarted);
        }
    }

    public void removeOnPageFinishedListener(OnPageFinished onPageFinished) {
        if (onPageFinished != null) {
            finishedListeners.remove(onPageFinished);
        }
    }

    public void removeOnContentVisibleListener(OnPageContentVisible onPageCommitVisible) {
        if (onPageCommitVisible != null) {
            contentVisibleListeners.remove(onPageCommitVisible);
        }
    }

    public void notifyPageFinished(WebView webView, String url) {
        for (OnPageFinished listener : finishedListeners) {
            listener.onPageFinished(webView, url);
        }
    }

    public void notifyPageStarted(WebView webView, String url) {
        for (OnPageStarted listener : startedListeners) {
            listener.onPageStarted(webView, url);
        }
    }

    public void notifyPageContentVisible(WebView webView, String url) {
        for (OnPageContentVisible listener : contentVisibleListeners) {
            listener.onPageContentVisible(webView, url);
        }
    }

    public void notifyLoadUrl(String url) {
        for (OnLoadUrl listener : loadUrlListeners) {
            listener.onLoadUrl(url);
        }
    }

}
