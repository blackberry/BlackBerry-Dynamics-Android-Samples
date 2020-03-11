/* Copyright (c) 2017 - 2020 BlackBerry Limited.
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

package com.good.automated.general.utils.impl;

import static com.googlecode.eyesfree.utils.LogUtils.TAG;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import com.good.automated.general.utils.AbstractUIAutomatorUtils;
import com.good.automated.general.utils.Duration;

//Implemented UI interactions with Android N API
//Nougat - 7.1	API level 25
public class UIAutomatorUtilsAndroidN25 extends AbstractUIAutomatorUtils {

    private UIAutomatorUtilsAndroidN25() {
        super();
    }

    @Override
    public void launchDateSettings() {
        //TODO: implement for Android N
    }

    @Override
    public void launchActionSettings(String action) {
        Context context = InstrumentationRegistry.getTargetContext();

        final Intent i = new Intent();
        i.setAction(action);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        context.startActivity(i);
    }

    public static AbstractUIAutomatorUtils getInstance() {
        return new UIAutomatorUtilsAndroidN25();
    }

    @Override
    public UiObject findTaskWithTextInRecentApps(String aText) throws UiObjectNotFoundException {
        openRecentApps();

        UiSelector scrollableSelector = new UiSelector().className("android.widget.ScrollView");
        UiScrollable scrollable = new UiScrollable(scrollableSelector);

        UiSelector itemWithTextSelector = new UiSelector().text(aText);
        UiObject item = scrollable.getChildByText(itemWithTextSelector, aText, true);
        item.waitForExists(Duration.of(Duration.UI_WAIT));
        if (item.exists()) {
            return item;
        }
        return null;
    }

    /**
     * Helper method which shows UI that asks to scan fingerprint
     *
     * After calling this method, device/emulator will expect to scan your fingerprint
     * To simulate fingerprint tauch on emulator you have to execute command:
     * adb -e emu finger touch 11551155
     */
    @Override
    public boolean getFingerprintScreen(String devicePass) {
        String fingerprintNextButton = "fingerprint_next_button";
        String findTheSensor = "Find the sensor";
        return super.getFingerprintScreen(devicePass, fingerprintNextButton, findTheSensor);
    }

    /**
     *
     * @return true if is proposed to scan your finger
     */
    @Override
    protected boolean completeGettingOfFingerprintScan() {
        String scanYourFinger = "Put your finger on the sensor";
        String fingerprintNextButton = "next_button";
        String fingerprintScrollViewId = "com.android.settings:id/suw_scroll_view";
        String scrollToTextForFingerprint = "NEXT";

        return super.completeGettingOfFingerprintScan(fingerprintNextButton, fingerprintScrollViewId, scrollToTextForFingerprint, scanYourFinger);
    }

    /**
     * Helper method which sets device password/PIN
     */
    @Override
    protected boolean setDevicePasswordOrPIN(String passwordPIN, String devicePasscode) {
        Log.d(TAG, "Setting device PIN or Password for 25 API level");
        String setupPasswordPinText = "Choose your " + passwordPIN;
        String confirmYourPasswordPinText = "Confirm your " + passwordPIN;
        String completeToSetPasswordPINButton = "redaction_done_button";

        return super.setDevicePasswordOrPIN(passwordPIN, devicePasscode, setupPasswordPinText, confirmYourPasswordPinText, completeToSetPasswordPINButton);
    }
}
