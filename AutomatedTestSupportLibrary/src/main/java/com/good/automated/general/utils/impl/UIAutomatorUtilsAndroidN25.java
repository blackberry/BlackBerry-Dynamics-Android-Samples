/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.general.utils.impl;

import static com.googlecode.eyesfree.utils.LogUtils.TAG;

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
    public boolean switchOffWindowAnimationScale() {
        //TODO: implement for Android N
        return false;
    }

    @Override
    public boolean switchOffTransitionAnimationScale() {
        //TODO: implement for Android N
        return false;
    }

    @Override
    public boolean switchOffAnimatorDurationScale() {
        //TODO: implement for Android N
        return false;
    }

    @Override
    public void launchActionSettings(String action) {
        //TODO: implement for Android N
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
