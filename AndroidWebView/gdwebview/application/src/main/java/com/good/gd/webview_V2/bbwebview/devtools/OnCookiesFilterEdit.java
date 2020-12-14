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
package com.good.gd.webview_V2.bbwebview.devtools;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.widget.TextView;

import com.good.gd.apache.http.client.CookieStore;
import com.good.gd.apache.http.cookie.Cookie;
import com.good.gd.net.GDHttpClient;

import java.util.ArrayList;
import java.util.List;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static java.lang.String.format;

public class OnCookiesFilterEdit extends onPaneEditListener {

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        GDHttpClient client = new GDHttpClient();
        CookieStore cookieStore = client.getCookieStore();

        List<Cookie> cookies = cookieStore.getCookies();

        final String domain = inputView.getText().toString();
        final String domainFilterReversed = new StringBuilder(domain).reverse().toString();

        final List<Spannable> filteredCookies = new ArrayList<>();

        StringBuilder strReverser = new StringBuilder();
        for (Cookie cookie : cookies) {
            String cookieDomainReversed = strReverser.append(cookie.getDomain()).reverse().toString();
            strReverser.setLength(0);

            if(cookieDomainReversed.startsWith(domainFilterReversed)){

                SpannableStringBuilder ssb = new SpannableStringBuilder()
                        .append(cookie.getName(), new ForegroundColorSpan(Color.rgb(0, 254, 0)), SPAN_EXCLUSIVE_EXCLUSIVE)
                        .append("=")
                        .append(cookie.getValue()+"\n", new ForegroundColorSpan(Color.MAGENTA), SPAN_EXCLUSIVE_EXCLUSIVE);

                filteredCookies.add(ssb);
            }
        }

        outputView.setText("");

        final long maxDelay = 2000L;
        final int size = filteredCookies.size();
        long tick = 17L;
        if(size != 0) {
            tick = Math.min(tick, maxDelay / size);
        }

        long delayMillis = 0L;
        for (final Spannable filteredCookie : filteredCookies) {

            outputView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    outputView.append(filteredCookie);
                }
            }, delayMillis);


            delayMillis+= tick;
        }

        outputView.postDelayed(new Runnable() {
            @Override
            public void run() {
                outputView.append(format("%s %d cookies",domain, size));
            }
        },delayMillis+60);

        inputView.setText("");

        return true;
    }
}
