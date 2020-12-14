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

import static com.good.automated.general.utils.Duration.UI_ACTION;
import static com.good.automated.general.utils.Duration.WAIT_FOR_SCREEN;

import android.os.RemoteException;
import android.util.Log;
import java.util.Objects;

import com.good.automated.general.controls.Button;
import com.good.automated.general.controls.EditText;
import com.good.automated.general.controls.ImageView;
import com.good.automated.general.controls.TextView;
import com.good.automated.general.controls.impl.ButtonImpl;
import com.good.automated.general.controls.impl.EditTextImpl;
import com.good.automated.general.controls.impl.ImageViewImpl;
import com.good.automated.general.controls.impl.TextViewImpl;
import com.good.automated.general.utils.Duration;

public abstract class AbstractBBDActivationUI extends AbstractBBDUI {

    protected static final String SCREEN_ID = "bbde_provision_view_UI";

    protected String pin1;
    protected String pin3;
    protected String pin2;
    protected String userName;
    protected String activationPassword;
    protected BBDActivationUIMap controls;
    protected String packageName;
    private String TAG = AbstractBBDActivationUI.class.getCanonicalName();

    /**
     * @param packageName app under test packageName
     */
    public AbstractBBDActivationUI(String packageName) {
        this.packageName = packageName;
    }

    /**
     * @param packageName app under test packageName
     * @param userName    user name to provision with
     * @param pin1        pin1
     * @param pin2        pin2
     * @param pin3        pin3
     */
    public AbstractBBDActivationUI(String packageName, String userName, String pin1, String pin2, String pin3) {
        this.packageName = packageName;
        this.userName = userName;
        this.pin1 = pin1;
        this.pin2 = pin2;
        this.pin3 = pin3;
    }

    /**
     * @param packageName app under test packageName
     * @param userName    user name to provision with
     * @param activationPassword activation password
     */
    public AbstractBBDActivationUI(String packageName, String userName, String activationPassword) {
        this.packageName = packageName;
        this.userName = userName;
        this.activationPassword = activationPassword;
    }

    public static String getScreenID() {
        return SCREEN_ID;
    }

    /**
     * @return      true if click on button OK was successful, otherwise false
     */
    public boolean clickOK() {
        try {
            //Hide keyboard in case it was shown
            if (getUiAutomationUtils().isKeyboardShown()) {
                getUiAutomationUtils().hideKeyboard();
                Log.d(TAG, "Keyboard is hidden: " + getUiAutomationUtils().isKeyboardShown());
            }
        } catch (RemoteException e) {
            Log.d(TAG, "Couldn't hide keyboard. RemoteException: " + e.getMessage());
        }

        try {
            //Scan device UI to ensure that button was enabled
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
     * @return true if click on button ENTER on keyboard was successful, otherwise false
     */
    public boolean clickEnter() {
        try {
            if (getUiAutomationUtils().isKeyboardShown()) {
                Log.d(TAG, "Try click keyboard OK button");
                return getUiAutomationUtils().clickKeyboardOk();
            } else {
                Log.d(TAG, "Keyboard not shown");
            }
        } catch (RemoteException e) {
            Log.d(TAG, "Keyboard not shown. RemoteException: " + e.getMessage());
        }
        return false;
    }

    /**
     * @return true if click on button Cancel was successful, otherwise false
     */
    public boolean clickCancel() {
        try {
            if (controls.getBtnCancel() != null) {
                Log.d(TAG, "Button Cancel is enabled: " + controls.getBtnCancel().isEnabled());
                return controls.getBtnCancel().click();
            } else {
                Log.d(TAG, "Button Cancel is not shown");
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "Couldn't perform action click. NullPointerException: " + e.getMessage());
        }
        return false;
    }

    /**
     * @param userLogin user login to be entered
     * @return true if login was entered successfully, otherwise false
     */
    public boolean enterUserLogin(String userLogin) {
        try {
            return controls.getUserLogin().legacySetText(userLogin);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     * @param pin1 pin1
     * @param pin2 pin2
     * @param pin3 pin3
     * @return true if access pin was entered successfully to all three parts, otherwise false
     */
    public boolean enterKey(String pin1, String pin2, String pin3) {
        try {
            boolean pin1Entered = controls.getEditTextForPin1().legacySetText(pin1);
            Log.d(TAG, "Result of entering Access Key : pin1: " + pin1Entered);
            boolean pin2Entered = controls.getEditTextForPin2().legacySetText(pin2);
            Log.d(TAG, "Result of entering Access Key : pin2: " + pin2Entered);
            boolean pin3Entered = controls.getEditTextForPin3().legacySetText(pin3);
            Log.d(TAG, "Result of entering Access Key : pin3: " + pin3Entered);
            return pin1Entered && pin2Entered && pin3Entered;
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     * @param activationPassword activation password
     * @return true if access pin was entered successfully to all three parts, otherwise false
     */
    public boolean enterActivationPassword(String activationPassword) {
        if (isLegacyFlow()) {
            if (activationPassword.length() != 15) {
                throw new IllegalArgumentException("Legacy flow detected, activation password must be 15 characters, actual length: " + activationPassword.length());
            }
            return enterKey(
                    activationPassword.substring(0, 5),
                    activationPassword.substring(5, 10),
                    activationPassword.substring(10));
        } else {
            try {
                boolean passwordEntered = controls.getActivationPasswordField().legacySetText(activationPassword);
                Log.d(TAG, "Result of entering Access Key: " + passwordEntered);
                return passwordEntered;
            } catch (NullPointerException e) {
                Log.d(TAG, "NullPointerException: " + e.getMessage());
                return false;
            }
        }
    }

    /**
     * Clear text all fields on the screen
     */
    public void clearData() {
        controls.getUserLogin().clearData();
        if (isLegacyFlow()) {
            controls.getEditTextForPin3().clearData();
            controls.getEditTextForPin2().clearData();
            controls.getEditTextForPin1().clearData();
        } else {
            controls.getActivationPasswordField().clearData();
        }
    }

    /**
     * @return true if click on Learn More button was successful, otherwise false
     */
    public boolean clickOnLearnMore() {
        try {
            return controls.getLearnMore().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     * @return user name displayed in login field
     */
    public String getUserNameDetails() {
        try {
            return controls.getUserLogin().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
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
     * @return true if click on button AdvancedSettings was successful, otherwise false
     */
    public boolean clickOnAdvancedSettings() {
        try {
            return controls.getAdvancedSettingsActionLabel().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     * @return true if click on activation password field was successful, otherwise false
     */
    public boolean clickOnActivationPasswordField() {
        try {
            return this.controls.getActivationPasswordField().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     * @return true if click on user mail field was successful, otherwise false
     */
    public boolean clickOnUserLoginField() {
        try {
            return this.controls.getUserLogin().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }


    @Override
    public boolean doAction() {

        if (isLegacyFlow()) {

            if (Objects.nonNull(activationPassword)) {

                Log.d(TAG, "Legacy flow detected, try to divide activation password and enter it");
                return enterUserLogin(userName) && enterActivationPassword(activationPassword) && clickEnter();

            } else if (Objects.nonNull(pin1) && Objects.nonNull(pin2) && Objects.nonNull(pin3)) {

                Log.d(TAG, "Legacy flow detected, pin1 pin2 pin3 will be entered");
                return enterUserLogin(userName) && enterKey(pin1, pin2, pin3) && clickOK();

            } else {

                Log.d(TAG, "Legacy flow detected, activation password and pins are null");
                return false;

            }
        } else {
            if (Objects.isNull(activationPassword)) {
                Log.d(TAG, "New flow detected, activationPassword couldn't be null");
            }
            return controls.getUserLogin().click() && enterUserLogin(userName) && enterActivationPassword(activationPassword) && clickEnter();
        }
    }

    protected boolean isLegacyFlow() {
        return !getUiAutomationUtils().isResourceWithIDShown(packageName, "COM_GOOD_GD_EPROV_PASSWORD_FIELD", Duration.of(UI_ACTION));
    }

    protected class BBDActivationUIMap {

        public EditText getUserLogin() {
            return EditTextImpl.getByID(packageName, "COM_GOOD_GD_EPROV_EMAIL_FIELD", Duration.of(WAIT_FOR_SCREEN));
        }

        public EditText getActivationPasswordField() {
            return EditTextImpl.getByID(packageName, "COM_GOOD_GD_EPROV_PASSWORD_FIELD", Duration.of(UI_ACTION));
        }

        public EditText getEditTextForPin1() {
            return EditTextImpl.getByID(packageName, "editTextForPin1", Duration.of(UI_ACTION));
        }

        public EditText getEditTextForPin2() {
            return EditTextImpl.getByID(packageName, "editTextForPin2", Duration.of(UI_ACTION));
        }

        public EditText getEditTextForPin3() {
            return EditTextImpl.getByID(packageName, "editTextForPin3", Duration.of(UI_ACTION));
        }

        public EditText getBcpURLField() {
            return EditTextImpl.getByID(packageName, "COM_GOOD_GD_EPROV_BCP_URL_FIELD", Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnCancel() {
            return ButtonImpl.getByID(packageName,
                    "COM_GOOD_GD_GDE_PROVISION_VIEW_CANCEL_BUTTON", Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnOK() {
            return ButtonImpl.getByID(packageName, "COM_GOOD_GD_EPROV_ACCESS_BUTTON",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnQRCode() {
            return ButtonImpl.getByID(packageName, "COM_GOOD_GD_SCAN_QR_CODE",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public ImageView getLearnMore() {
            return ImageViewImpl.getByID(packageName, "gd_help",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getGdSimulationLabel() {
            return TextViewImpl.getByID(packageName, "gd_simulation_label",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getGdApplicationUnlockTitle() {
            return TextViewImpl.getByID(packageName, "gd_application_unlock_title",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getGdApplicationUnlockMessage() {
            return TextViewImpl.getByID(packageName, "gd_application_unlock_message",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getAdvancedSettingsActionLabel() {
            return TextViewImpl.getByID(packageName, "gd_bottom_line_action_label",
                    Duration.of(WAIT_FOR_SCREEN));
        }
    }
}
