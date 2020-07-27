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

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;

import android.os.RemoteException;
import android.util.Log;

import com.good.automated.general.utils.AbstractUIAutomatorUtils;
import com.good.automated.general.utils.Duration;

//Implemented UI interactions with Android N API
//Nougat - 7.0	API level 24
public class UIAutomatorUtilsAndroidN24 extends AbstractUIAutomatorUtils {

    private static final String TAG = UIAutomatorUtilsAndroidN24.class.getSimpleName();

    private UIAutomatorUtilsAndroidN24() {
        super();
    }

    @Override
    public void launchDateSettings() {
        //TODO: implement for Android N
    }

    @Override
    public void launchActionSettings(String action) {
        android.content.Context context = getInstrumentation().getTargetContext();

        final android.content.Intent i = new android.content.Intent();
        i.setAction(action);
        i.addCategory(android.content.Intent.CATEGORY_DEFAULT);
        i.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        context.startActivity(i);
    }

    public static AbstractUIAutomatorUtils getInstance() {
        return new UIAutomatorUtilsAndroidN24();
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
     *
     * @return true if fingerprint was accepted successfully
     */
    @Override
    public boolean completeFingerprintSetup() {
        String completeFingerprintScanScrollViewId = "com.android.settings:id/suw_bottom_scroll_view";
        String fingerprintNextButton = "next_button";
        String scrollToTextToCompleteFingerprintScan = "DONE";

        return super.completeFingerprintSetup(fingerprintNextButton, completeFingerprintScanScrollViewId, scrollToTextToCompleteFingerprintScan);
    }

    @Override
    public boolean addCertificateToTrustedCredentials(String certificateName, String devicePIN) throws RemoteException {
        //!TODO: implement for Android API 24
        return false;
    }

    @Override
    public boolean removeCertificateFromTrustedCredentials(String certificateName) {
        //!TODO: implement for Android API 24
        return false;
    }

    /**
     * Helper method which sets device password/PIN
     */
    @Override
    protected boolean setDevicePasswordOrPIN(String passwordPIN, String devicePasscode) {
        Log.d(TAG, "Setting device PIN or Password for 24 API level");

        String setupPasswordPinText = "Choose your " + passwordPIN;
        String confirmYourPasswordPinText = "Confirm your " + passwordPIN;
        String completeToSetPasswordPINButton = "next_button";

        return super.setDevicePasswordOrPIN(passwordPIN, devicePasscode, setupPasswordPinText, confirmYourPasswordPinText, completeToSetPasswordPINButton);
    }
}
