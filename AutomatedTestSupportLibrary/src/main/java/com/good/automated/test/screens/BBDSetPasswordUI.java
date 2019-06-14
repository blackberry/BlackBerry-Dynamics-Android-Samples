/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.test.screens;

import android.util.Log;

public class BBDSetPasswordUI extends AbstractBBDPasswordUI {

    private String TAG = BBDSetPasswordUI.class.getSimpleName();

    /**
     *
     * @param packageName app under test packageName
     */
    public BBDSetPasswordUI(String packageName) {
        super(packageName);
        this.controls = new PasswordUIMap();
    }

    public BBDSetPasswordUI(String packageName, long delay) {
        super(packageName);
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new PasswordUIMap();
    }

    /**
     *
     * @param packageName app under test packageName
     * @param password set custom password for app
     */
    public BBDSetPasswordUI(String packageName, String password) {
        super(packageName);
        this.newPassword = password;
        this.controls = new PasswordUIMap();
    }

    public BBDSetPasswordUI(String packageName, String password, long delay) {
        super(packageName);
        this.newPassword = password;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new PasswordUIMap();
    }

    /**
     *
     * @param newPassword new password to be set
     * @return true if password was set otherwise false
     */
    public boolean setPassword(String newPassword) {
        boolean result = enterNewPassword(newPassword) && enterConfirmPassword(newPassword) && clickOK();
        BBDAlertDialogUI alert = new BBDAlertDialogUI();
        if (result && (alert.getAlertTitle() == null)){
            Log.d(TAG, "Password was successfully set");
            return true;
        }

        //Case when at once after setting password is shown dialog
        if (result && alert.getAlertTitle() != null && !alert.getAlertTitle().contains("Password Requirements")){
            Log.d(TAG, "Password was successfully set");
            return true;
        } else {
            Log.d(TAG, "Password wasn't successfully set. Alert dialog is shown with title: " + alert.getAlertTitle());
            return false;
        }
    }

    /**
     *
     * @param newPass new password to be set in New Password field
     * @param confirmPass password confirmation to be set in Confirm Password field
     * @return true if password and confirmation were entered otherwise false
     */
    public boolean enterDifferentPasswords(String newPass, String confirmPass) {
        return enterNewPassword(newPass) && enterConfirmPassword(confirmPass);
    }

    /**
     *
     * @param newPass new password to be set in New Password field
     * @param confirmPass password confirmation to be set in Confirm Password field
     * @return true if password and confirmation were set otherwise false
     */
    public boolean setDifferentPasswords(String newPass, String confirmPass) {
        return enterDifferentPasswords(newPass, confirmPass) && clickOK();
    }

    /**
     *
     * @return true if action was performed successfully otherwise false
     */
    @Override
    public boolean doAction() {
        return setPassword(newPassword);
    }
}
