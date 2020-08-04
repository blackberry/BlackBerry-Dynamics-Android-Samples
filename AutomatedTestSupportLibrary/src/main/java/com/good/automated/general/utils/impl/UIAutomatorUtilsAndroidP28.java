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

import android.content.Context;
import android.content.Intent;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;

import android.os.RemoteException;
import android.util.Log;

import com.good.automated.general.utils.AbstractUIAutomatorUtils;
import com.good.automated.general.utils.Duration;

import static android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS;
import static android.provider.Settings.ACTION_DATE_SETTINGS;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.good.automated.general.utils.Duration.UI_WAIT;
import static com.good.automated.general.utils.Duration.WAIT_FOR_SCREEN;
import static com.good.automated.general.utils.Duration.of;

//Implemented UI interactions with Android P API
//? - 9 API level 28
public class UIAutomatorUtilsAndroidP28 extends AbstractUIAutomatorUtils {

    private static final String TAG = UIAutomatorUtilsAndroidP28.class.getSimpleName();

    private UIAutomatorUtilsAndroidP28() {
        super();
    }

    @Override
    public void launchDateSettings() {
        launchActionSettings(ACTION_DATE_SETTINGS);
    }

    /**
     *
     * @param action action from system Settings
     */
    @Override
    public void launchActionSettings(String action) {
        Context context = getInstrumentation().getTargetContext();

        final Intent i = new Intent();
        i.setAction(action);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        context.startActivity(i);
    }

    public static AbstractUIAutomatorUtils getInstance() {
        return new UIAutomatorUtilsAndroidP28();
    }

    @Override
    public UiObject findTaskWithTextInRecentApps(String aText) throws UiObjectNotFoundException {
        openRecentApps();

        //TODO: implement for Android P
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
        //No buttons and click for Android Q-28
        return true;
    }

    /**
     * @return true if fingerprint was accepted successfully
     */
    @Override
    public boolean completeFingerprintSetup() {
        if (clickOnItemWithID("com.android.settings", "skip_button", Duration.of(Duration.WAIT_FOR_SCREEN), Duration.of(Duration.UI_WAIT))) {
            return true;
        } else {
            Log.d(TAG, "Couldn't complete fingerprint setup");
        }
        return false;
    }

    /**
     * Helper method which sets device password/PIN
     */
    @Override
    protected boolean setDevicePasswordOrPIN(String passwordPIN, String devicePasscode) {
        Log.d(TAG, "Setting device PIN or Password for 28 API level");
        String setupPasswordPinText = "Set a screen lock";
        String confirmYourPasswordPinText = "Re-enter your " + passwordPIN;
        String completeToSetPasswordPINButton = "redaction_done_button";

        return super.setDevicePasswordOrPIN(passwordPIN, devicePasscode, setupPasswordPinText, confirmYourPasswordPinText, completeToSetPasswordPINButton);
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

            if (clickOnItemWithText("Fingerprint", Duration.of(WAIT_FOR_SCREEN))) {
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

    @Override
    public boolean addCertificateToTrustedCredentials(String certificateName, String devicePIN) throws RemoteException {
        //!TODO: implement for Android API 28
        return false;
    }

    @Override
    public boolean removeCertificateFromTrustedCredentials(String certificateName) {
        //!TODO: implement for Android API 28
        return false;
    }

    @Override
    public boolean forceStopApp(String applicationId) {
        launchAppSettings(applicationId);

        UiObject forceStopButton = findByResourceId("com.android.settings:id/button2_negative");

        if (forceStopButton.waitForExists(of(WAIT_FOR_SCREEN))) {
            Log.d(TAG, "com.android.settings:id/button2_negative was found on system UI");
        } else {
            //Possible timing issue in opening system Setting UI
            waitForUI(of(UI_WAIT));
            Log.d(TAG, "Second attempt to find com.android.settings:id/button1_negative on system UI");
            forceStopButton = findByResourceId("com.android.settings:id/button1_negative");
        }
        try {
            if (forceStopButton.getText().toLowerCase().contains("force stop")) {
                //This is classic placing of Force stop button on system UI
                return performForceStopAction("button2_negative");
            } else {
                //Mirror placing of Force stop button on system UI
                return performForceStopAction("button1_negative");
            }
        } catch (UiObjectNotFoundException e) {
            Log.d(TAG, "com.android.settings:id/button2_negative wasn't found on system UI");
        }
        return false;
    }

    private boolean setAnimationScaleByText(String animationOption, String animationOff) {
        launchActionSettings(ACTION_APPLICATION_DEVELOPMENT_SETTINGS);

        if (scrollToText("com.android.settings:id/container_material", animationOff)){
            return clickOnItemWithText(animationOff, Duration.of(WAIT_FOR_SCREEN)) &&
                    clickOnItemWithText(animationOption, Duration.of(WAIT_FOR_SCREEN));
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean selectPermissionSwitchItemWithDescription(String aDescription) {

        UiObject appPermissionsLabel = getUiDevice().findObject(new UiSelector().text("App permissions"));
        appPermissionsLabel.waitForExists(Duration.of(Duration.UI_WAIT));
        UiScrollable permissionList = new UiScrollable(new UiSelector().resourceId("android:id/list"));
        permissionList.waitForExists(Duration.of(UI_WAIT));

        try {
            UiObject object = permissionList.getChildByText(new UiSelector().className("android.widget.RelativeLayout"), aDescription);
            return object.click();
        } catch (UiObjectNotFoundException e) {
            return false;
        }
    }
}
