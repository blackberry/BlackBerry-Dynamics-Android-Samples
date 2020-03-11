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

package com.good.automated.test.screens;

import android.util.Log;

public class BBDRemoteUnlockActivationUI extends AbstractBBDActivationUI {

    private String TAG = BBDRemoteUnlockActivationUI.class.getSimpleName();

    /**
     * @param packageName app under test packageName
     */
    public BBDRemoteUnlockActivationUI(String packageName) {
        super(packageName);
        this.packageName = packageName;
        this.controls = new BBDActivationUIMap();
    }

    public BBDRemoteUnlockActivationUI(String packageName, long delay) {
        super(packageName);
        this.packageName = packageName;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDActivationUIMap();
        Log.d(TAG, "Screen " + SCREEN_ID + " for package name: " + packageName + " is shown!");
    }

    /**
     * @param packageName app under test packageName
     * @param userName    user name to unlock app with
     * @param pin1        pin1
     * @param pin2        pin2
     * @param pin3        pin3
     */
    public BBDRemoteUnlockActivationUI(String packageName, String userName, String pin1, String
            pin2, String pin3) {
        super(packageName, userName, pin1, pin2, pin3);
        this.controls = new BBDActivationUIMap();
    }

    public BBDRemoteUnlockActivationUI(String packageName, String userName, String pin1, String
            pin2, String pin3, long delay) {
        super(packageName, userName, pin1, pin2, pin3);
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDActivationUIMap();
    }

    /**
     *
     * @return true if title of the screen is expected, otherwise false
     */
    public boolean checkApplicationRemoteUnlockTitle(String title) {
        try {
            return controls.getGdApplicationUnlockTitle().getText().equals(title);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * @return true if message of the screen is expected, otherwise false
     */
    public boolean checkApplicationRemoteUnlockMessage(String title) {
        try {
            return controls.getGdApplicationUnlockMessage().getText().equals(title);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean doAction() {
        boolean result = enterUserLogin(userName) && enterKey(pin1, pin2, pin3);
        try {
            if (controls.getBtnOK().click()) {
                return result;
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "Button OK wasn't found on Remote Unlock screen. Screen will be scrolled down.");
        }
        getUiAutomationUtils().scrollToText(packageName + ":id/provision_view", "OK");

        return result && clickOK();
    }
}
