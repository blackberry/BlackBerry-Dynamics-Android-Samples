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

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.webkit.WebView;

public class BBWebView extends WebView {


    private Bitmap defaultIcon;

    private OnPageFinished noOppageFinish = new OnPageFinished() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    //nothing
                }
            };

    private OnPageFinished pageFinishedTask = noOppageFinish;

    /**
     * Construct a new WebView with a Context object.
     *
     * @param context A Context object used to access application assets.
     */
    public BBWebView(Context context) {
        super(context);
    }

    /**
     * Construct a new WebView with layout parameters.
     *
     * @param context A Context object used to access application assets.
     * @param attrs   An AttributeSet passed to our parent.
     */
    public BBWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Construct a new WebView with layout parameters and a default style.
     *
     * @param context      A Context object used to access application assets.
     * @param attrs        An AttributeSet passed to our parent.
     * @param defStyleAttr
     */
    public BBWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BBWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    public interface OnPageFinished {
        void onPageFinished(WebView view, String url);
    }

    public void setOnPageFinishedAction(OnPageFinished pageFinishedTask){

        if(pageFinishedTask == null){
            this.pageFinishedTask = noOppageFinish;
        }else {
            this.pageFinishedTask = pageFinishedTask;
        }
    }

    public OnPageFinished getOnPageFinishedTask() {
        return pageFinishedTask;
    }
}
