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

public class BBDChangePasswordUI extends AbstractBBDPasswordUI {

    protected String oldPassword;

    private String TAG = BBDChangePasswordUI.class.getSimpleName();

    /**
     *
     * @param packageName app under test packageName
     */
    public BBDChangePasswordUI(String packageName) {
        super(packageName);
        this.controls = new PasswordUIMap();
    }

    public BBDChangePasswordUI(String packageName, long delay) {
        super(packageName);
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new PasswordUIMap();
    }

    /**
     *
     * @param packageName app under test packageName
     * @param oldPassword old password for app
     * @param newPassword new password for app
     */
    public BBDChangePasswordUI(String packageName, String oldPassword, String newPassword) {
        super(packageName);
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.controls = new PasswordUIMap();
    }

    public BBDChangePasswordUI(String packageName, String oldPassword, String newPassword, long
            delay) {
        super(packageName);
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new PasswordUIMap();
    }

    /**
     *
     * @param oldPass old password to be set
     * @param newPass password and confirmation to be set
     * @return true if password was set, otherwise false
     */
    public boolean setPassword(String oldPass, String newPass) {
        return enterOldPassword(oldPass)
                && enterNewPassword(newPass)
                && enterConfirmPassword(newPass)
                && getUiAutomationUtils().clickKeyboardOk();
    }

    /**
     *
     * @param oldPass old password to be entered in Old Password field
     * @return true if password was entered, otherwise false
     */
    public boolean enterOldPassword(String oldPass) {
        try {
            return controls.getOldPassword().legacySetText(oldPass);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * @param oldPass password confirmation to be entered in Old Password field
     * @return true if password was entered, otherwise false
     *
     * NOTE: this method won't work in release build
     */
    public boolean enterOldPasswordDebugBuild(String oldPass) {
        try {
            return controls.getOldPassword().setText(oldPass);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * @param oldPass new password to be entered in Old Password field
     * @param newPass new password to be entered in New Password field
     * @param confirmPass password confirmation to be entered in Confirm Password field
     * @return true if passwords were entered, otherwise false
     */
    public boolean enterDifferentPasswords(String oldPass, String newPass, String confirmPass) {
        return enterOldPassword(oldPass) && enterNewPassword(newPass) && enterConfirmPassword(confirmPass);
    }

    /**
     *
     * @param oldPass new password to be entered in Old Password field
     * @param newPass new password to be entered in New Password field
     * @param confirmPass password confirmation to be entered in Confirm Password field
     * @return true if passwords were set, otherwise false
     */
    public boolean setDifferentPasswords(String oldPass, String newPass, String confirmPass) {
        return enterDifferentPasswords(oldPass, newPass, confirmPass) && clickOK();
    }

    /**
     *
     * @return true if password change was cancelled, otherwise false
     */
    public boolean clickCancel() {
        return super.clickCancel();
    }

    /**
     *
     * @return true if action was performed successfully, otherwise false
     */
    @Override
    public boolean doAction() {
        return setPassword(oldPassword, newPassword);
    }
}
