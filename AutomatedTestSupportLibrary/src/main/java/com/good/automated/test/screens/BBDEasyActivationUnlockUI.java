/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
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

public class BBDEasyActivationUnlockUI extends AbstractBBDUI {

    private static final String SCREEN_ID = "bbd_activation_login_view_UI";

    private static final String TAG = BBDEasyActivationUnlockUI.class.getSimpleName();
    private String password;
    private RequestingAppUIMap controls;
    private String packageName;

    /**
     *
     * @param eASamplePackageName Easy Activator packageID
     */
    public BBDEasyActivationUnlockUI(String eASamplePackageName) {
        this.packageName = eASamplePackageName;
        this.controls = new RequestingAppUIMap();
    }

    public BBDEasyActivationUnlockUI(String eASamplePackageName, long delay) {
        this.packageName = eASamplePackageName;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new RequestingAppUIMap();
    }

    /**
     *
     * @param eASamplePackageName Easy Activator packageID
     * @param eaSamplePassword Easy Activator password
     */
    public BBDEasyActivationUnlockUI(String eASamplePackageName, String eaSamplePassword) {
        this.packageName = eASamplePackageName;
        this.password = eaSamplePassword;
        this.controls = new RequestingAppUIMap();
    }

    /**
     *
     * @param eASamplePackageName Easy Activator packageID
     * @param eaSamplePassword Easy Activator password
     */
    public BBDEasyActivationUnlockUI(String eASamplePackageName, String eaSamplePassword, long
            delay) {
        this.packageName = eASamplePackageName;
        this.password = eaSamplePassword;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new RequestingAppUIMap();
    }

    public static String getScreenID() {
        return SCREEN_ID;
    }

    /**
     *
     * @return requesting app text
     */
    public String getRequestingAppNameText() {
        try {
            return controls.getReqAppNameText().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    /**
     *
     * @return user instruction text
     */
    public String getUserInstructionText() {
        try {
            return controls.getUserInstructionText().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    /**
     *
     * @return true if click on button Cancel was performed successfully, otherwise false
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
     * @return true if click on button Cancel was performed successfully, otherwise false
     */
    public boolean clickCancel() {
        try {
            return controls.getBtnCancel().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * @param appPassword password to be entered
     * @return true if password was entered successfully, otherwise false
     */
    public boolean enterPassword(String appPassword) {
        try {
            return controls.getAppPasssword().legacySetText(appPassword);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * @return true if click on Learn More was performed successfully, otherwise false
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
     *
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
        return enterPassword(password) && clickOK();
    }

    private class RequestingAppUIMap {

        public TextView getReqAppNameText() {
            return TextViewImpl.getByID(packageName, "reqAppNameText",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getUserInstructionText() {
            return TextViewImpl.getByID(packageName, "userInstructionText",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public EditText getAppPasssword() {
            return EditTextImpl.getByID(packageName, "passwordEditor",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnCancel() {
            return ButtonImpl.getByID(packageName, "btnCancel", Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnOK() {
            return ButtonImpl.getByID(packageName, "btnOk", Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getLearnMore() {
            return TextViewImpl.getByID(packageName, "gd_bottom_line_action_label",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getGdSimulationLabel() {
            return TextViewImpl.getByID(packageName, "gd_simulation_label",
                    Duration.of(WAIT_FOR_SCREEN));
        }
    }
}
