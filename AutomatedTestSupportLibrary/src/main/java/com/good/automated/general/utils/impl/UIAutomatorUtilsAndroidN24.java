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
//Nougat - 7.0	API level 24
public class UIAutomatorUtilsAndroidN24 extends AbstractUIAutomatorUtils {

    private UIAutomatorUtilsAndroidN24() {
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
