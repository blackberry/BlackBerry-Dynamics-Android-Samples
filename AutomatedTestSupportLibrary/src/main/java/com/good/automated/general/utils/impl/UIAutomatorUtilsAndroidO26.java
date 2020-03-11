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

import static android.provider.Settings.ACTION_DATE_SETTINGS;
import static com.googlecode.eyesfree.utils.LogUtils.TAG;

import com.good.automated.general.utils.AbstractUIAutomatorUtils;
import com.good.automated.general.utils.Duration;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;


//Implemented UI interactions with Android O API
//Oreo - 8.0 API level 26
public class UIAutomatorUtilsAndroidO26 extends AbstractUIAutomatorUtils {

    private UIAutomatorUtilsAndroidO26() {
        super();
    }

    @Override
    public void launchDateSettings() {
        launchActionSettings(ACTION_DATE_SETTINGS);
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
        return new UIAutomatorUtilsAndroidO26();
    }

    @Override
    public UiObject findTaskWithTextInRecentApps(String aText) throws UiObjectNotFoundException {
        openRecentApps();

        UiObject taskViewSelector = getUiDevice().findObject(new UiSelector().className("android.widget.FrameLayout"));
        UiObject fileViewer = taskViewSelector.getChild(new UiSelector().textMatches(aText));
        fileViewer.waitForExists(Duration.of(Duration.UI_WAIT));
        if (fileViewer.exists()) {
            return fileViewer;
        }
        Log.d(TAG, String.format("Couldn't find UiObject for the recent task with text <%s>", aText));
        return null;
    }

    @Override
    public boolean swipeTaskWithTextInRecentApps(String aText) {
        return removeTaskWithTextInRecentApps(aText);
    }

    /**
     * Remove the task from the recent apps list.
     *
     * @param aText task with specific text, to be removed
     * @return true if action successfully performed, otherwise false
     */
    @Override
    public boolean removeTaskWithTextInRecentApps(String aText) {
        try {
            UiObject recentApp = findTaskWithTextInRecentApps(aText);

            if (recentApp != null) {

                Log.d(TAG, "Recent app UiObject not null, looking for close button.");

                UiObject taskViewSelector = getUiDevice().findObject(new UiSelector().className("android.widget.FrameLayout"));
                UiObject closeButton = taskViewSelector.getChild(new UiSelector().descriptionContains("Dismiss " + aText + "."));
                closeButton.waitForExists(Duration.of(Duration.SECONDS_10));
                if (closeButton.exists()) {
                    Log.d(TAG, "Close button exists. Will hit it now.");
                    return closeButton.click();
                } else {
                    Log.d(TAG, String.format("Couldn't find UiObject for close button of task with text <%s>", aText));
                    return false;
                }
            } else {
                 Log.d(TAG, String.format("Couldn't find recent task with text <%s>", aText));
                 return false;
            }

        } catch (UiObjectNotFoundException e) {
            Log.d(TAG, "UiObjectNotFoundException. Recent app with text: " + aText
                    + " is not found on the screen");
            return false;
        }
    }

    /**
     * Helper method which shows UI that asks to scan fingerprint.
     *
     * <p>After calling this method, device/emulator will expect to scan your fingerprint
     * To simulate fingerprint tauch on emulator you have to execute command:
     * adb -e emu finger touch 11551155
     */
    @Override
    public boolean getFingerprintScreen(String devicePass) {
        String fingerprintNextButton = "fingerprint_next_button";
        String findTheSensor = "Find the sensor";
        return super.getFingerprintScreen(devicePass, fingerprintNextButton, findTheSensor);
    }

    /**.
     * @return true if is proposed to scan your finger.
     */
    @Override
    protected boolean completeGettingOfFingerprintScan() {
        String fingerprintNextButton = "next_button";
        String fingerprintScrollViewId = "com.android.settings:id/suw_scroll_view";
        String scrollToTextForFingerprint = "NEXT";
        String scanYourFinger = "Put your finger on the sensor";

        return super.completeGettingOfFingerprintScan(
                fingerprintNextButton,
                fingerprintScrollViewId,
                scrollToTextForFingerprint,
                scanYourFinger);
    }

    /**
     * Helper method which sets device password/PIN.
     */
    @Override
    protected boolean setDevicePasswordOrPIN(String passwordPIN, String devicePasscode) {
        Log.d(TAG, "Setting device PIN or Password for 26 API level");
        String setupPasswordPinText = "Choose your " + passwordPIN;
        String confirmYourPasswordPinText = "Confirm your " + passwordPIN;
        String completeToSetPasswordPINButton = "redaction_done_button";

        return super.setDevicePasswordOrPIN(
                passwordPIN,
                devicePasscode,
                setupPasswordPinText,
                confirmYourPasswordPinText,
                completeToSetPasswordPINButton);
    }

    @Override
    public boolean selectPermissionSwitchItemWithDescription(String aDescription) {
        return clickOnItemContainingText(aDescription);
    }
}
