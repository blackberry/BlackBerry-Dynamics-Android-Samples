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

public class BBDUnlockUI extends AbstractBBDUI {

    private static final String SCREEN_ID = "bbd_login_view_UI";
    private String packageName;

    private String password;
    private UnlockPasswordUIMap controls;
    private String TAG = BBDUnlockUI.class.getSimpleName();

    /**
     *
     * @param packageName app under test packageName
     */
    public BBDUnlockUI(String packageName) {
        this.packageName = packageName;
        this.controls = new UnlockPasswordUIMap();
    }

    public BBDUnlockUI(String packageName, long delay) {
        this.packageName = packageName;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new UnlockPasswordUIMap();
    }

    /**
     *
     * @param packageName app under test packageName
     * @param password password to unlock app under test
     */
    public BBDUnlockUI(String packageName, String password) {
        this.packageName = packageName;
        this.password = password;
        this.controls = new UnlockPasswordUIMap();
    }

    public BBDUnlockUI(String packageName, String password, long delay) {
        this.packageName = packageName;
        this.password = password;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new UnlockPasswordUIMap();
    }

    public static String getScreenID() {
        return SCREEN_ID;
    }

    /**
     *
     * @return true if click on button OK was successful, otherwise false
     */
    public boolean clickOK() {
        try {
            return controls.getBtnOK().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * @return true if was entered successfully, otherwise false
     */
    public boolean enterPassword(String enterPassword) {
        try {
            return controls.getEnterPassword().legacySetText(enterPassword);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * @param label label to be checked
     * @return true if label matches expected one otherwise false
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
     *
     * @return true if unlock was performed successfully, otherwise false
     */
    public boolean unlockAppWithPassword(String enterPassword) {
        return enterPassword(enterPassword) && clickOK();
    }

    /**
     *
     * @return true if click on Forgot Password was performed successfully, otherwise false
     */
    public boolean forgotPassword() {
        try {
            return controls.getForgotPassword().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean doAction() {
        if (password != null) {
            return unlockAppWithPassword(password);
        }
        return forgotPassword();
    }

    private class UnlockPasswordUIMap {

        public EditText getEnterPassword() {
            return EditTextImpl.getByID(packageName, "COM_GOOD_GD_LOGIN_VIEW_PASSWORD_FIELD",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnOK() {
            return ButtonImpl.getByID(packageName, "COM_GOOD_GD_EPROV_ACCESS_BUTTON",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getForgotPassword() {
            return TextViewImpl.getByID(packageName, "gd_bottom_line_action_label",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getGdSimulationLabel() {
            return TextViewImpl.getByID(packageName, "gd_simulation_label",
                    Duration.of(WAIT_FOR_SCREEN));
        }
    }
}
