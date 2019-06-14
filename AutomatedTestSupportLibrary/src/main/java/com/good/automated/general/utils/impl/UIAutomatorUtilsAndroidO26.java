/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.general.utils.impl;

import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import com.good.automated.general.utils.AbstractUIAutomatorUtils;
import com.good.automated.general.utils.Duration;

import static com.googlecode.eyesfree.utils.LogUtils.TAG;

//Implemented UI interactions with Android O API
//Oreo - 8.0 API level 26
public class UIAutomatorUtilsAndroidO26 extends AbstractUIAutomatorUtils {

    private UIAutomatorUtilsAndroidO26() {
        super();
    }

    @Override
    public void launchDateSettings() {
        //TODO: implement for Android O
    }

    @Override
    public boolean switchOffWindowAnimationScale() {
        //TODO: implement for Android O
        return false;
    }

    @Override
    public boolean switchOffTransitionAnimationScale() {
        //TODO: implement for Android O
        return false;
    }

    @Override
    public boolean switchOffAnimatorDurationScale() {
        //TODO: implement for Android O
        return false;
    }

    @Override
    public void launchActionSettings(String action) {
        //TODO: implement for Android O
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
        return null;
    }

    /**
     * Helper method which shows UI that asks to scan fingerprint
     * <p>
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
     * @return true if is proposed to scan your finger
     */
    @Override
    protected boolean completeGettingOfFingerprintScan() {
        String fingerprintNextButton = "next_button";
        String fingerprintScrollViewId = "com.android.settings:id/suw_scroll_view";
        String scrollToTextForFingerprint = "NEXT";
        String scanYourFinger = "Put your finger on the sensor";

        return super.completeGettingOfFingerprintScan(fingerprintNextButton, fingerprintScrollViewId, scrollToTextForFingerprint, scanYourFinger);
    }

    /**
     * Helper method which sets device password/PIN
     */
    @Override
    protected boolean setDevicePasswordOrPIN(String passwordPIN, String devicePasscode) {
        Log.d(TAG, "Setting device PIN or Password for 26 API level");
        String setupPasswordPinText = "Choose your " + passwordPIN;
        String confirmYourPasswordPinText = "Confirm your " + passwordPIN;
        String completeToSetPasswordPINButton = "redaction_done_button";

        return super.setDevicePasswordOrPIN(passwordPIN, devicePasscode, setupPasswordPinText, confirmYourPasswordPinText, completeToSetPasswordPINButton);
    }
}
