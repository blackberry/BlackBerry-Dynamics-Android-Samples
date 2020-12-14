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

import static com.good.automated.general.utils.Duration.AUTHORIZE_CALLBACK;

import android.util.Log;

import com.good.automated.general.utils.Duration;

public class BBDForgotPasswordActivationUI extends AbstractBBDActivationUI {

    private String TAG = BBDForgotPasswordActivationUI.class.getSimpleName();

    /**
     * @param packageName app under test packageName
     */
    public BBDForgotPasswordActivationUI(String packageName) {
        super(packageName);
        this.controls = new BBDActivationUIMap();
        Log.d(TAG, "Screen " + SCREEN_ID + " for package name: " + packageName + " is shown!");
    }

    public BBDForgotPasswordActivationUI(String packageName, long delay) {
        super(packageName);
        this.controls = new BBDActivationUIMap();
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)) {
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        Log.d(TAG, "Screen " + SCREEN_ID + " for package name: " + packageName + " is shown!");
    }

    /**
     * @param packageName app under test packageName
     * @param userName    user name to unlock app with
     * @param pin1        pin1
     * @param pin2        pin2
     * @param pin3        pin3
     * Instead, use a constructor that supports activation password.
     * {@link  #BBDForgotPasswordActivationUI(String, String, String)}
     */
    @Deprecated
    public BBDForgotPasswordActivationUI(String packageName, String userName, String pin1, String pin2, String pin3) {
        super(packageName, userName, pin1, pin2, pin3);
        this.controls = new BBDActivationUIMap();
    }

    /**
     * @param packageName app under test packageName
     * @param userName    user name to unlock app with
     * @param pin1        pin1
     * @param pin2        pin2
     * @param pin3        pin3
     * @param delay       duration to wait for screen
     * @deprecated constructor is used just for Legacy flow.
     * Instead, use a constructor that supports activation password.
     * {@link #BBDForgotPasswordActivationUI(String, String, String, long)}
     */
    @Deprecated
    public BBDForgotPasswordActivationUI(String packageName, String userName, String pin1, String
            pin2, String pin3, long delay) {
        super(packageName, userName, pin1, pin2, pin3);
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)) {
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDActivationUIMap();
    }

    /**
     * @param packageName        app under test packageName
     * @param userName           user name to unlock app with
     * @param activationPassword unlock key
     */
    public BBDForgotPasswordActivationUI(String packageName, String userName, String activationPassword) {
        this(packageName, userName, activationPassword, Duration.of(AUTHORIZE_CALLBACK));
    }

    /**
     * @param packageName        app under test packageName
     * @param userName           user name to unlock app with
     * @param activationPassword unlock key
     * @param delay              duration to wait for screen
     */
    public BBDForgotPasswordActivationUI(String packageName, String userName, String activationPassword, long delay) {
        super(packageName, userName, activationPassword);
        if (activationPassword.length() != 15) {
            throw new IllegalArgumentException("Activation password must be 15 characters, actual length: " + activationPassword.length());
        }
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)) {
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDActivationUIMap();
    }

    /**
     * @param activationPassword activation password
     * @return true if access pin was entered successfully to all three parts, otherwise false
     */
    public boolean enterActivationPassword(String activationPassword) {
        boolean result;
        if (activationPassword.length() != 15) {
            throw new IllegalArgumentException("Activation password must be 15 characters, actual length: " + activationPassword.length());
        }
        if (isLegacyFlow()) {
            result = enterKey(
                    activationPassword.substring(0, 5),
                    activationPassword.substring(5, 10),
                    activationPassword.substring(10));
        } else {
            try {
                boolean passwordEntered = controls.getActivationPasswordField().legacySetText(activationPassword);
                Log.d(TAG, "Result of entering Access Key: " + passwordEntered);
                result = passwordEntered;
            } catch (NullPointerException e) {
                Log.d(TAG, "NullPointerException: " + e.getMessage());
                result = false;
            }
        }
        return result;
    }

    /**
     *
     * @param title title to be checked
     * @return
     */
    public boolean checkForgotPasswordTitle(String title) {
        try {
            return controls.getGdApplicationUnlockTitle().getText().equals(title);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }
}
