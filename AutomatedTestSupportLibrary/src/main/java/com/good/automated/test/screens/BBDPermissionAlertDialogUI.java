/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.test.screens;

import static com.good.automated.general.utils.Duration.WAIT_FOR_SCREEN;

import android.util.Log;

import com.good.automated.general.controls.Button;
import com.good.automated.general.controls.CheckBox;
import com.good.automated.general.controls.TextView;
import com.good.automated.general.controls.impl.ButtonImpl;
import com.good.automated.general.controls.impl.CheckBoxImpl;
import com.good.automated.general.controls.impl.TextViewImpl;
import com.good.automated.general.utils.Duration;

public class BBDPermissionAlertDialogUI {

    private PermissionAlertDialogUIMap permissionAlertDialog;
    private String packageName;
    private String TAG = BBDPermissionAlertDialogUI.class.getSimpleName();

    public BBDPermissionAlertDialogUI() {
        this.packageName = "com.android.packageinstaller";
        this.permissionAlertDialog = new PermissionAlertDialogUIMap();
    }

    /**
     *
     * @param packageName app under test packageName
     */
    public BBDPermissionAlertDialogUI(String packageName) {
        this.packageName = packageName;
        this.permissionAlertDialog = new PermissionAlertDialogUIMap();
    }

    /**
     *
     * @return true if click on button Allow was performed successfully, otherwise false
     */
    public boolean clickAllow() {
        try {
            return permissionAlertDialog.getBtnAllow().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * @return true if click on button Deny was performed successfully, otherwise false
     */
    public boolean clickDeny() {
        try {
            return permissionAlertDialog.getBtnDeny().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * @return never ask again text
     */
    public String getNeverAskAgainText(){
        try {
            return permissionAlertDialog.getCheckBoxDontAskAgain().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    /**
     *
     * @return message of alert dialog
     */
    public String getAlertMessage() {
        try {
            return permissionAlertDialog.getTextMessage().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }


    /**
     * Method to enable "Never Ask Again" checkbox
     * And perform a check if the checkbox has been checked
     *
     * @return true if checkbox was enabled successfully, otherwise false
     */
    public boolean enableCheckBoxDontAskAgain() {
        try {
            permissionAlertDialog.getCheckBoxDontAskAgain().click();
            return permissionAlertDialog.getCheckBoxDontAskAgain().isChecked();
        } catch (NullPointerException e) {
            Log.d(TAG, "Couldn't enable checkbox. NullPointerException: " + e.getMessage());
            return false;
        }
    }


    private class PermissionAlertDialogUIMap {

        public TextView getTextMessage() {
            return TextViewImpl.getByID(packageName, "permission_message",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnDeny() {
            return ButtonImpl.getByID(packageName, "permission_deny_button",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnAllow() {
            return ButtonImpl.getByID(packageName, "permission_allow_button",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public CheckBox getCheckBoxDontAskAgain() {
            return CheckBoxImpl.getByID(packageName,"do_not_ask_checkbox",
                    Duration.of(WAIT_FOR_SCREEN));
        }
    }
}
