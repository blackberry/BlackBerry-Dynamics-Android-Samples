/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.general.utils.impl;

import static com.good.automated.general.utils.Duration.UI_WAIT;
import static com.good.automated.general.utils.Duration.WAIT_FOR_SCREEN;
import static com.good.automated.general.utils.Duration.of;
import static com.googlecode.eyesfree.utils.LogUtils.TAG;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import com.good.automated.general.utils.AbstractUIAutomatorUtils;
import com.good.automated.general.utils.Duration;

//Implemented UI interactions with Android O API
//Oreo - 8.1 API level 27
public class UIAutomatorUtilsAndroidO27 extends AbstractUIAutomatorUtils {

    private UIAutomatorUtilsAndroidO27() {
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
        return new UIAutomatorUtilsAndroidO27();
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
        String findTheSensor = "Touch the sensor";
        String fingerprintNextButton = "fingerprint_next_button";
        return super.getFingerprintScreen(devicePass, fingerprintNextButton, findTheSensor);
    }

    /**
     * @return true if is proposed to scan your finger
     */
    @Override
    protected boolean completeGettingOfFingerprintScan() {
        //No buttons and click for Android 27
        return true;
    }

    /**
     * @return true if fingerprint was accepted successfully
     */
    @Override
    public boolean completeFingerprintSetup() {
        if (clickOnItemWithID("com.android.settings", "next_button",
                Duration.of(Duration.WAIT_FOR_SCREEN), Duration.of(Duration.UI_WAIT))) {
            return true;
        } else {
            if (isFingerprintSet()){
                Log.d(TAG, "Fingerprint was set, but without any additional actions");
                return true;
            } else {
                Log.d(TAG, "Fingerprint was not set. Couldn't complete fingerprint setup");
            }
        }
        return false;
    }

    /**
     * Helper method which sets device password/PIN
     */
    @Override
    protected boolean setDevicePasswordOrPIN(String passwordPIN, String devicePasscode) {
        Log.d(TAG, "Setting device PIN or Password for 27 API level");
        String setupPasswordPinText = "Set a screen lock";
        String confirmYourPasswordPinText = "Re-enter your " + passwordPIN;
        String completeToSetPasswordPINButton = "redaction_done_button";

        return super.setDevicePasswordOrPIN(passwordPIN, devicePasscode, setupPasswordPinText, confirmYourPasswordPinText, completeToSetPasswordPINButton);
    }

    @Override
    protected void openSecuritySettings() {
        Context context = InstrumentationRegistry.getTargetContext();

        final Intent i = new Intent();
        i.setAction(Settings.ACTION_SECURITY_SETTINGS);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        context.startActivity(i);

        uiDevice.waitForIdle(of(UI_WAIT));
    }

    /**
     * @param fingerprintNameToRemove name of fingerprint to be removed
     * @param passwordPIN             password or PIN
     * @return true if fingerprint was removed otherwise false
     */
    @Override
    public boolean removeFingerprint(String fingerprintNameToRemove, String passwordPIN) {
        openSecuritySettings();
        if (isFingerprintSupported() && isDevicePasswordSet()) {
            Log.d(TAG, "Fingerprint is supported by this hardware!");
            if (isTextShown("Fingerprint")) {
                Log.d(TAG, "\"Fingerprint\" was found on Security screen");
            } else {
                Log.d(TAG, "Cannot find \"Fingerprint\" on Security screen. Try to scroll to it");
                if (scrollToText("com.android.settings:id/list", "Fingerprint")) {
                    Log.d(TAG, "\"Fingerprint\" was found on Security screen after scrolling to it");
                } else {
                    Log.d(TAG, "Cannot find \"Fingerprint\" on Security screen");
                    return false;
                }
            }

            if (clickOnItemWithText("Fingerprint", com.good.automated.general.utils.Duration.of(WAIT_FOR_SCREEN))) {
                if (enterTextToItemWithID("com.android.settings", "password_entry", passwordPIN) && clickKeyboardOk() && isTextShown(fingerprintNameToRemove)) {
                    UiObject listIDs = findByResourceId("com.android.settings:id/list");
                    try {
                        for (int i = 0; i < listIDs.getChildCount(); i++) {
                            UiObject item = listIDs.getChild(new UiSelector().index(i));
                            if (item.getChild(new UiSelector().index(0))
                                    .getChild(new UiSelector().index(1))
                                    .getChild(new UiSelector().index(0))
                                    .getText().equals(fingerprintNameToRemove)) {
                                Log.d(TAG, "Specified fingerprint was found!");
                                item.getChild(new UiSelector().index(2))
                                        .getChild(new UiSelector().index(0)).click();
                                if (clickOnItemWithID("android", "button1", Duration.of(Duration.WAIT_FOR_SCREEN))) {
                                    Log.d(TAG, "Specified fingerprint was removed");
                                    return true;
                                }
                                Log.d(TAG, "Cannot remove specified fingerprint");
                            }
                        }
                        Log.d(TAG, "Cannot fnt specified fingerprint");
                    } catch (UiObjectNotFoundException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                Log.d(TAG, "No access to fingerprint settings");
            }
            Log.d(TAG, "Fingerprint setting not found in Security list");
            return false;
        } else {
            Log.d(TAG, "Fingerprint in not supported by this hardware");
            return true;
        }
    }
}
