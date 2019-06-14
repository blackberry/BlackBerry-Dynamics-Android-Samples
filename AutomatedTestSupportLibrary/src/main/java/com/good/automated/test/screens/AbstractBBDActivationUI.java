/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.test.screens;

import static com.good.automated.general.utils.Duration.UI_ACTION;
import static com.good.automated.general.utils.Duration.WAIT_FOR_SCREEN;

import android.util.Log;

import com.good.automated.general.controls.Button;
import com.good.automated.general.controls.EditText;
import com.good.automated.general.controls.TextView;
import com.good.automated.general.controls.impl.ButtonImpl;
import com.good.automated.general.controls.impl.EditTextImpl;
import com.good.automated.general.controls.impl.TextViewImpl;
import com.good.automated.general.utils.Duration;

public abstract class AbstractBBDActivationUI extends AbstractBBDUI {

    protected static final String SCREEN_ID = "bbde_provision_view_UI";

    protected String pin1;
    protected String pin3;
    protected String pin2;
    protected String userName;
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

    public static String getScreenID() {
        return SCREEN_ID;
    }

    /**
     * @return true if click on button OK was successful, otherwise false
     */
    public boolean clickOK() {
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
     * @return true if access pin was enetered successfully to all three parts, otherwise false
     */
    public boolean enterKey(String pin1, String pin2, String pin3) {
        try {
            boolean aPin1 = controls.getEditTextForPin1().legacySetText(pin1);
            Log.d(TAG, "Result of entering Access Key : pin1: " + aPin1);
            boolean aPin2 = controls.getEditTextForPin2().legacySetText(pin2);
            Log.d(TAG, "Result of entering Access Key : pin2: " + aPin2);
            boolean aPin3 = controls.getEditTextForPin3().legacySetText(pin3);
            Log.d(TAG, "Result of entering Access Key : pin3: " + aPin3);
            return aPin1 && aPin2 && aPin3;
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     * Clear text all fields on the screen
     */
    public void clearData() {
        controls.getUserLogin().clearData();
        controls.getEditTextForPin3().clearData();
        controls.getEditTextForPin2().clearData();
        controls.getEditTextForPin1().clearData();
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

    @Override
    public boolean doAction() {
        return enterUserLogin(userName) && enterKey(pin1, pin2, pin3) && clickOK();
    }

    protected class BBDActivationUIMap {

        public EditText getUserLogin() {
            return EditTextImpl.getByID(packageName, "COM_GOOD_GD_EPROV_EMAIL_FIELD");
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

        public Button getBtnCancel() {
            return ButtonImpl.getByID(packageName,
                    "COM_GOOD_GD_GDE_PROVISION_VIEW_CANCEL_BUTTON", Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnOK() {
            return ButtonImpl.getByID(packageName, "COM_GOOD_GD_EPROV_ACCESS_BUTTON",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getLearnMore() {
            return TextViewImpl.getByID(packageName, "gd_bottom_line_action_label",
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
    }
}
