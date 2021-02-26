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

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import com.good.automated.general.utils.AbstractUIAutomatorUtils;
import com.good.automated.general.utils.Duration;
import com.good.automated.general.utils.UIAutomatorUtilsFactory;
import com.good.automated.test.screens.BBDActivationUI;
import com.good.automated.test.screens.BBDBISPrimerUI;
import com.good.automated.test.screens.BBDSetPasswordUI;
import com.good.automated.test.screens.BBDUnlockUI;
import com.good.gd.GDAndroid;

import static com.good.automated.general.utils.Duration.MINUTE_2;
import static com.good.automated.general.utils.Duration.SECONDS_10;
import static com.good.automated.general.utils.Duration.SECONDS_30;
import static com.good.automated.general.utils.Duration.UI_WAIT;
import static org.junit.Assert.assertTrue;

public class TestHelper {

    private static final String TAG = TestHelper.class.getSimpleName();

    public static void launchApp(String userName, String userPassword, String unlockPassword) {

        AbstractUIAutomatorUtils uiAutomatorUtils = UIAutomatorUtilsFactory.getUIAutomatorUtils();

        String packageName = TestHelper.getAppPackageName();

        uiAutomatorUtils.launchApp(packageName);

        boolean isAppActivated = GDAndroid.getInstance().isActivated(getContext());

        if (isAppActivated) {

            if (uiAutomatorUtils.isScreenShown(getAppPackageName(), "home_activity", Duration.of(SECONDS_10))) {

                Log.i(TAG, "The app is unlocked");

                return;
            }

            assertTrue("Failed to unlock the app",
                    new BBDUnlockUI(packageName, unlockPassword, Duration.of(SECONDS_30)).doAction());

        } else {

            assertTrue("Failed to activate the app",
                    uiAutomatorUtils.isScreenShown(packageName,
                            BBDActivationUI.getScreenID(), Duration.of(SECONDS_30)));

            BBDActivationUI activationUI = new BBDActivationUI(packageName);

            assertTrue("Failed to enter user name", activationUI.enterUserLogin(userName));
            assertTrue("Failed to enter user password", activationUI.enterActivationPassword(userPassword));

            assertTrue("Failed to submit user name and password", activationUI.clickEnter());

            assertTrue("Failed to provision the app",
                    uiAutomatorUtils.isScreenShown(packageName,
                            BBDSetPasswordUI.getScreenID(), Duration.of(MINUTE_2)));

            BBDSetPasswordUI setPasswordUI = new BBDSetPasswordUI(packageName, unlockPassword);
            assertTrue("Failed to set password", setPasswordUI.doAction());

            boolean isBisUiShown = uiAutomatorUtils.isScreenShown(packageName,
                    BBDBISPrimerUI.getScreenID(), Duration.of(UI_WAIT));

            if (isBisUiShown) {
                new BBDBISPrimerUI(packageName, Duration.of(UI_WAIT)).clickNeverAskAgain();
            }

        }

        uiAutomatorUtils.waitForUI(Duration.of(UI_WAIT));
    }

    public static String getAppPackageName() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        return context.getApplicationContext().getPackageName();
    }

    public static Context getContext() {
        return InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    public static void runOnMainThread(Runnable runnable) {
        // Check if running on the main thread
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            Log.i(TAG, "runOnMainThread, already on the main thread");
            runnable.run();
            return;
        }
        InstrumentationRegistry.getInstrumentation().runOnMainSync(runnable);
    }

}
