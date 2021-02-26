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

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.good.automated.general.utils.AbstractUIAutomatorUtils;
import com.good.automated.general.utils.UIAutomatorUtilsFactory;
import com.good.gd.webview_v2.utils.TestHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class BasicTests {

    private static final String TAG = BasicTests.class.getSimpleName();

    private AbstractUIAutomatorUtils uiAutomatorUtils = UIAutomatorUtilsFactory.getUIAutomatorUtils();

    // Update to provide your activation credentials and password
    private static final String USER_NAME = "serhii@blackhole.com";
    private static final String USER_PASSWORD = "password";
    private static final String UNLOCK_PASSWORD = "a";

    private static boolean isAppLaunched;

    @Before
    public void setUp() {
        Log.i(TAG, "setUp, started");

        if (!isAppLaunched) {
            Log.i(TAG, "setUp, launch the app");

            TestHelper.launchApp(USER_NAME, USER_PASSWORD, UNLOCK_PASSWORD);

            isAppLaunched = true;
        }

        Log.i(TAG, "setUp, finished");
    }

    @After
    public void tearDown() {
        Log.i(TAG, "tearDown, started");

        Log.i(TAG, "tearDown, finished");
    }

    @Test
    public void test01_BasicTest() {
        Log.i(TAG, "test01_BasicTest, started");

        Log.i(TAG, "test01_BasicTest, finished");
    }

}
