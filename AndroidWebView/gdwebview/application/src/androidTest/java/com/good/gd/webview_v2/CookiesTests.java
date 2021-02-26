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
package com.good.gd.webview_v2;

import android.util.Log;
import android.webkit.WebView;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.good.automated.general.utils.AbstractUIAutomatorUtils;
import com.good.automated.general.utils.UIAutomatorUtilsFactory;
import com.good.gd.apache.http.client.CookieStore;
import com.good.gd.apache.http.cookie.Cookie;
import com.good.gd.net.GDHttpClient;
import com.good.gd.webview_V2.bbwebview.BBWebView;
import com.good.gd.webview_V2.bbwebview.BBWebViewClient;
import com.good.gd.webview_v2.utils.PageFinishedListener;
import com.good.gd.webview_v2.utils.TestHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4ClassRunner.class)
public class CookiesTests {

    private static final String TAG = CookiesTests.class.getSimpleName();

    private AbstractUIAutomatorUtils uiAutomatorUtils = UIAutomatorUtilsFactory.getUIAutomatorUtils();

    // Update to provide your activation credentials and password
    private static final String USER_NAME = "user@mail.com";
    private static final String USER_PASSWORD = "password";
    private static final String UNLOCK_PASSWORD = "a";

    private static final String cookiesTestURL = "http://httpbin.org/cookies/set?";
    private static final String cookieName1 = "abc";
    private static final String cookieValue1 = "test";
    private static final String cookieName2 = "k2";
    private static final String cookieValue2 = "v2";

    private static final String cookiesTestURLWithJS = "http://gd-lviv22.gd.sw.rim.net:8050/";
    private static final String firstCookieName = "firstCookie";
    private static final String firstCookieValue = "TheFirstCookie";
    private static final String secondCookieName = "secondCookie";
    private static final String secondCookieValue = "TheSecondCookie";

    private WebView webView;
    private CookieStore cookieStore;

    private static boolean isAppLaunched;

    @Before
    public void setUp() {
       Log.i(TAG, "setUp, started");

       if (!isAppLaunched) {
           Log.i(TAG, "setUp, launch the app");

           TestHelper.launchApp(USER_NAME, USER_PASSWORD, UNLOCK_PASSWORD);

           isAppLaunched = true;
       }

       GDHttpClient httpClient = new GDHttpClient();
       cookieStore = httpClient.getCookieStore();

       cookieStore.clear();

       Log.i(TAG, "setUp, finished");
    }

    @After
    public void tearDown() {
        Log.i(TAG, "tearDown, started");

        cookieStore.clear();

        Log.i(TAG, "tearDown, finished");
    }

    /**
     * Test checks, that cookies, received from server are stored in WebView
     */
    @Test
    public void test01_CookiesBasicTest() {
        Log.i(TAG, "test01_CookiesBasicTest, started");

        final String url = String.format(cookiesTestURL + "%s=%s&%s=%s", cookieName1, cookieValue1, cookieName2, cookieValue2);

        final PageFinishedListener listener = new PageFinishedListener();

        List<Cookie> cookies = cookieStore.getCookies();

        assertTrue("Cookie store is not empty", cookies.isEmpty());

        TestHelper.runOnMainThread(new Runnable() {
            @Override
            public void run() {

                webView = new BBWebView(TestHelper.getContext());

                ((BBWebViewClient) webView.getWebViewClient()).getObserver().addOnPageFinishedListener(listener);

                webView.loadUrl(url);

            }
        });

        // Waite for url to load
        for (int i = 1; i < 30; i++) {

            uiAutomatorUtils.waitForUI(1 * 1000);

            if (listener.isPageLoaded(url)) {
                break;
            }

        }

        assertTrue("Failed to load url ", listener.isPageLoaded(url));

        cookies = cookieStore.getCookies();

        assertTrue("Failed to check cookies size", (cookies.size() == 2));

        boolean expectedCookie1 = false;
        boolean expectedCookie2 = false;

        for (Cookie cookie : cookies) {

            if (cookie.getName().equals(cookieName1) && cookie.getValue().equals(cookieValue1)) {
                expectedCookie1 = true;
            }

            if (cookie.getName().equals(cookieName2) && cookie.getValue().equals(cookieValue2)) {
                expectedCookie2 = true;
            }

        }

        assertTrue ("Not found expected cookies1", expectedCookie1);
        assertTrue ("Not found expected cookies2", expectedCookie2);

        webView = null;

        Log.i(TAG, "test01_CookiesBasicTest, finished");
    }

    /**
     * Test checks, that cookies, received from server and set from JavaScript are stored in WebView
     */
    @Test
    public void test02_CookiesWithJsTest() {
        Log.i(TAG, "test02_CookiesWithJsTest, started");

        final String url = cookiesTestURLWithJS;

        final PageFinishedListener listener = new PageFinishedListener();

        List<Cookie> cookies = cookieStore.getCookies();

        assertTrue("Cookie store is not empty", cookies.isEmpty());

        TestHelper.runOnMainThread(new Runnable() {
            @Override
            public void run() {

                webView = new BBWebView(TestHelper.getContext());

                ((BBWebViewClient) webView.getWebViewClient()).getObserver().addOnPageFinishedListener(listener);

                webView.loadUrl(url);

            }
        });

        // Waite for url to load
        for (int i = 1; i < 30; i++) {

            uiAutomatorUtils.waitForUI(1 * 1000);

            if (listener.isPageLoaded(cookiesTestURLWithJS)) {
                break;
            }

        }

        assertTrue("Failed to load url ", listener.isPageLoaded(cookiesTestURLWithJS));

        // We will wait for XHR which will set first cookie and will send request to server.
        // The response will set second cookie, our task is to waite both of those cookies.

        boolean expectedCookie1 = false;
        boolean expectedCookie2 = false;

        for (int i = 1; i < 30; i++) {

            uiAutomatorUtils.waitForUI(1 * 1000);

            cookies = cookieStore.getCookies();

            for (Cookie cookie : cookies) {

                if (cookie.getName().equals(firstCookieName) && cookie.getValue().equals(firstCookieValue)) {
                    Log.i(TAG, "the first cookie is found");
                    expectedCookie1 = true;
                }

                if (cookie.getName().equals(secondCookieName) && cookie.getValue().equals(secondCookieValue)) {
                    Log.i(TAG, "the second cookie is found");
                    expectedCookie2 = true;
                }

            }

            if (expectedCookie1 && expectedCookie2) {
                break;
            }

        }

        assertTrue("Failed to check cookies size", (cookies.size() == 2));

        assertTrue ("Not found expected cookies1", expectedCookie1);
        assertTrue ("Not found expected cookies2", expectedCookie2);

        // Destroy
        webView = null;

        Log.i(TAG, "test02_CookiesWithJsTest, finished");
    }

}
