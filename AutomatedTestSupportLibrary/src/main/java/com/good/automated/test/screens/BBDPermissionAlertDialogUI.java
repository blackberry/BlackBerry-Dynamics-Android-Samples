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

import static com.good.automated.general.utils.Duration.UI_WAIT;
import static com.good.automated.general.utils.Duration.WAIT_FOR_SCREEN;

import android.os.Build;
import android.util.Log;

import com.good.automated.general.controls.Button;
import com.good.automated.general.controls.CheckBox;
import com.good.automated.general.controls.TextView;
import com.good.automated.general.controls.impl.ButtonImpl;
import com.good.automated.general.controls.stub.ButtonStub;
import com.good.automated.general.controls.impl.CheckBoxImpl;
import com.good.automated.general.controls.impl.TextViewImpl;
import com.good.automated.general.controls.stub.CheckBoxStub;
import com.good.automated.general.controls.stub.TextViewStub;
import com.good.automated.general.utils.Duration;

public class BBDPermissionAlertDialogUI {

    public static BBDPermissionAlertDialogUI getUiWithStub() {
        return new BBDPermissionAlertDialogUI("com.android.packageinstaller", new PermissionAlertDialogStub());
    }

    private UIMap permissionAlertDialog;
    private String packageName;
    private String TAG = BBDPermissionAlertDialogUI.class.getSimpleName();

    public BBDPermissionAlertDialogUI() {
        //Android Q has different package ID for permission screen
        if (Build.VERSION.SDK_INT >= 29) {
            this.packageName = "com.android.permissioncontroller";
        } else {
            this.packageName = "com.android.packageinstaller";
        }

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
     */
    private BBDPermissionAlertDialogUI(String packageName, UIMap uiMap) {
        this.packageName = "com.android.packageinstaller";
        this.permissionAlertDialog = uiMap;
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


    private interface UIMap {
        TextView getTextMessage();
        Button getBtnDeny();
        Button getBtnAllow();
        CheckBox getCheckBoxDontAskAgain();
    }

    private class PermissionAlertDialogUIMap implements UIMap {

        @Override
        public TextView getTextMessage() {
            return TextViewImpl.getByID(packageName, "permission_message",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        @Override
        public Button getBtnDeny() {
            return ButtonImpl.getByID(packageName, "permission_deny_button",
                    Duration.of(UI_WAIT));
        }

        @Override
        public Button getBtnAllow() {
            return ButtonImpl.getByID(packageName, "permission_allow_button",
                    Duration.of(UI_WAIT));
        }

        @Override
        public CheckBox getCheckBoxDontAskAgain() {
            return CheckBoxImpl.getByID(packageName,"do_not_ask_checkbox",
                    Duration.of(WAIT_FOR_SCREEN));
        }
    }

    private static class PermissionAlertDialogStub implements UIMap {

        @Override
        public TextView getTextMessage() {
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

        @Override
        public CheckBox getCheckBoxDontAskAgain() {
            return CheckBoxStub.getStub();
        }
    }

}
