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
import com.good.automated.general.controls.EditText;
import com.good.automated.general.controls.TextView;
import com.good.automated.general.controls.impl.ButtonImpl;
import com.good.automated.general.controls.impl.EditTextImpl;
import com.good.automated.general.controls.impl.TextViewImpl;
import com.good.automated.general.utils.Duration;

public abstract class AbstractBBDPasswordUI extends AbstractBBDUI {

    private static final String SCREEN_ID = "bbd_set_password_view_UI";

    protected String packageName;
    protected String newPassword;
    protected PasswordUIMap controls;
    private String TAG = AbstractBBDPasswordUI.class.getSimpleName();

    public AbstractBBDPasswordUI(String packageName) {
        this.packageName = packageName;
    }

    public static String getScreenID() {
        return SCREEN_ID;
    }

    /**
     * @return true if click was performed, otherwise false
     */
    public boolean clickOK() {
        try {
            if (controls.getBtnOK() != null) {
                Log.d(TAG, "Button OK is enabled: " + controls.getBtnOK().isEnabled());
                return controls.getBtnOK().click();
            } else {
                Log.d(TAG, "Button OK is null");
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "Couldn't perform action click. NullPointerException: " + e.getMessage());
        }
        return false;
    }

    /**
     * @param newPass new password to be entered in New Password field
     * @return true if password was entered, otherwise false
     */
    public boolean enterNewPassword(String newPass) {
        try {
            return controls.getNewPassword().legacySetText(newPass);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     * @param confirmPass password confirmation to be entered in Confirm Password field
     * @return true if password was entered, otherwise false
     */
    public boolean enterConfirmPassword(String confirmPass) {
        try {
            return controls.getConfirmPassword().legacySetText(confirmPass);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     * @param label label to be checked
     * @return true if label matches expected one, otherwise false
     */
    public boolean checkSimulatedOrDebugLabel(String label) {
        try {
            return controls.getGdSimulationLabel().getText().equals(label);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     * @param newPass new password to be entered in New Password field
     * @return true if password was entered, otherwise false
     * <p>
     * NOTE: this method won't work in release build
     */
    public boolean enterNewPasswordDebugBuild(String newPass) {
        try {
            return controls.getNewPassword().setText(newPass);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     * @param confirmPass password confirmation to be entered in Confirm Password field
     * @return true if password was entered, otherwise false
     * <p>
     * NOTE: this method won't work in release build
     */
    public boolean enterConfirmPasswordDebugBuild(String confirmPass) {
        try {
            return controls.getConfirmPassword().setText(confirmPass);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     * @return Alert Dialog with password requirements
     */
    public BBDAlertDialogUI getPasswordRequirements() {
        controls.getPasswordRequirements().click();
        return new BBDAlertDialogUI();
    }

    protected class PasswordUIMap {

        public EditText getOldPassword() {
            return EditTextImpl.getByID(packageName,
                    "COM_GOOD_GD_EPROV_SET_PWD_DLG_OLD_PWD_EDIT",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public EditText getNewPassword() {
            return EditTextImpl.getByID(packageName,
                    "COM_GOOD_GD_EPROV_SET_PWD_DLG_NEW_PWD_EDIT",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public EditText getConfirmPassword() {
            return EditTextImpl.getByID(packageName,
                    "COM_GOOD_GD_EPROV_SET_PWD_DLG_CONFIRM_PWD_EDIT",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnOK() {
            return ButtonImpl.getByID(packageName, "COM_GOOD_GD_EPROV_ACCESS_BUTTON",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getPasswordRequirements() {
            return TextViewImpl.getByID(packageName, "gd_bottom_line_action_label",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getGdSimulationLabel() {
            return TextViewImpl.getByID(packageName, "gd_simulation_label",
                    Duration.of(WAIT_FOR_SCREEN));
        }
    }
}
