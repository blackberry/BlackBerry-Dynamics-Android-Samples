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

import static com.good.automated.general.utils.Duration.WAIT_FOR_SCREEN;

import android.util.Log;

import com.good.automated.general.controls.Button;
import com.good.automated.general.controls.TextView;
import com.good.automated.general.controls.impl.ButtonImpl;
import com.good.automated.general.controls.stub.ButtonStub;
import com.good.automated.general.controls.impl.TextViewImpl;
import com.good.automated.general.controls.stub.TextViewStub;
import com.good.automated.general.utils.Duration;

public class BBDPermissionUI extends AbstractBBDUI {

    private static final String SCREEN_ID = "bbd_runtimepermissions_introfragment_UI";

    private String packageName;
    private UIMap controls;
    private String TAG = BBDPermissionUI.class.getSimpleName();

    private BBDPermissionAlertDialogUI alertDialogUI;

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
            this.controls = new PermissionUIStub();
            this.alertDialogUI = BBDPermissionAlertDialogUI.getUiWithStub();
        } else {
            this.controls = new PermissionUIMap();
        }
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
        if (alertDialogUI != null){
            return alertDialogUI.clickAllow();
        }
        return new BBDPermissionAlertDialogUI().clickAllow();
    }

    /**
     * Method to deny permission in Permission alert dialog
     *
     * @return true if permissions were denied, otherwise false
     */
    public boolean denyPermissions() {
        clickAllow();
        if (alertDialogUI != null){
            return alertDialogUI.clickDeny();
        }
        return new BBDPermissionAlertDialogUI().clickDeny();
    }

    /**
     * Method to deny permission with enabled "Don't ask again" checkbox in Permission alert dialog
     *
     * @return true if permissions were denied with enabled ckeckbox, otherwise false
     */
    public boolean denyPermissionsCheckboxEnabled() {
        clickAllow();
        if (alertDialogUI != null){
            return alertDialogUI.clickDeny();
        }
        BBDPermissionAlertDialogUI permissionAlertDialogUI = new BBDPermissionAlertDialogUI();
        permissionAlertDialogUI.enableCheckBoxDontAskAgain();
        return permissionAlertDialogUI.clickDeny();
    }


    @Override
    public boolean doAction() {
        return grantPermissions();
    }

    private interface UIMap {
        TextView getTextTitle();
        TextView getTextPermissionsBody();
        Button getBtnDeny();
        Button getBtnAllow();
    }

    private class PermissionUIMap implements UIMap {

        @Override
        public TextView getTextTitle() {
            return TextViewImpl.getByID(packageName, "gd_header_base_title_text",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        @Override
        public TextView getTextPermissionsBody() {
            return TextViewImpl.getByID(packageName, "gd_runtimepermissions_body",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        @Override
        public Button getBtnDeny() {
            return ButtonImpl.getByID(packageName, "gd_cancel_button1",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        @Override
        public Button getBtnAllow() {
            return ButtonImpl.getByID(packageName, "gd_ok_button2",
                    Duration.of(WAIT_FOR_SCREEN));
        }

    }

    private class PermissionUIStub implements UIMap {

        @Override
        public TextView getTextTitle() {
            return TextViewStub.getStub();
        }

        @Override
        public TextView getTextPermissionsBody() {
            return TextViewStub.getStub();
        }

        @Override
        public Button getBtnDeny() {
            return ButtonStub.getStub();
        }

        @Override
        public Button getBtnAllow() {
            return ButtonStub.getStub();
        }
    }
}
