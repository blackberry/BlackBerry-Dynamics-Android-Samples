/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.test.screens;

import static com.good.automated.general.utils.Duration.WAIT_FOR_SCREEN;

import android.util.Log;

import com.good.automated.general.controls.Button;
import com.good.automated.general.controls.TextView;
import com.good.automated.general.controls.impl.ButtonImpl;
import com.good.automated.general.controls.impl.TextViewImpl;
import com.good.automated.general.utils.Duration;

public class BBDPermissionUI extends AbstractBBDUI {

    private static final String SCREEN_ID = "bbd_runtimepermissions_introfragment_UI";

    private String packageName;
    private PermissionUIMap controls;
    private String TAG = BBDPermissionUI.class.getSimpleName();

    /**
     *
     * @param packageName app under test packageName
     */
    public BBDPermissionUI(String packageName) {
        this.packageName = packageName;
        this.controls = new PermissionUIMap();
    }

    /**
     *
     * @param packageName app under test packageName
     * @param delay wait for screen
     */
    public BBDPermissionUI(String packageName, long delay) {
        this.packageName = packageName;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new PermissionUIMap();
    }

    public static String getScreenID() {
        return SCREEN_ID;
    }

    /**
     *
     * @return title of the screen
     */
    public String getTitle() {
        try {
            return controls.getTextTitle().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    /**
     *
     * @return true if click on button Allow was performed successfully, otherwise false
     */
    public boolean clickAllow() {
        try {
            return controls.getBtnAllow().click();
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
            return controls.getBtnDeny().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * @return true if permissions were granted, otherwise false
     */
    public boolean grantPermissions() {
        clickAllow();
        return new BBDPermissionAlertDialogUI().clickAllow();
    }

    /**
     * Method to deny permission in Permission alert dialog
     *
     * @return true if permissions were denied, otherwise false
     */
    public boolean denyPermissions() {
        clickAllow();
        return new BBDPermissionAlertDialogUI().clickDeny();
    }

    /**
     * Method to deny permission with enabled "Don't ask again" checkbox in Permission alert dialog
     *
     * @return true if permissions were denied with enabled ckeckbox, otherwise false
     */
    public boolean denyPermissionsCheckboxEnabled() {
        clickAllow();
        BBDPermissionAlertDialogUI permissionAlertDialogUI = new BBDPermissionAlertDialogUI();
        permissionAlertDialogUI.enableCheckBoxDontAskAgain();
        return permissionAlertDialogUI.clickDeny();
    }


    @Override
    public boolean doAction() {
        return grantPermissions();
    }

    private class PermissionUIMap {

        public TextView getTextTitle() {
            return TextViewImpl.getByID(packageName, "gd_header_base_title_text",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getTextPermissionsBody() {
            return TextViewImpl.getByID(packageName, "gd_runtimepermissions_body",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnDeny() {
            return ButtonImpl.getByID(packageName, "gd_cancel_button1",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnAllow() {
            return ButtonImpl.getByID(packageName, "gd_ok_button2",
                    Duration.of(WAIT_FOR_SCREEN));
        }

    }
}
