/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.test.screens;

import android.util.Log;

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
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
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
     */
    public BBDForgotPasswordActivationUI(String packageName, String userName, String pin1, String pin2, String pin3) {
        super(packageName, userName, pin1, pin2, pin3);
        this.controls = new BBDActivationUIMap();
    }

    public BBDForgotPasswordActivationUI(String packageName, String userName, String pin1, String
            pin2, String pin3, long delay) {
        super(packageName, userName, pin1, pin2, pin3);
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDActivationUIMap();
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
