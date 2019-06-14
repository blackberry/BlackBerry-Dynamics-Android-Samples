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

public class BBDNoPasswordUI extends AbstractBBDUI {

    private static final String SCREEN_ID = "bbd_nopassword_notification_view_UI";
    private String packageName;
    private NoPasswordUIMap controls;
    private String TAG = BBDNoPasswordUI.class.getSimpleName();

    /**
     * @param packageName app under test packageName
     */
    public BBDNoPasswordUI(String packageName) {
        this.packageName = packageName;
        this.controls = new NoPasswordUIMap();
    }

    public BBDNoPasswordUI(String packageName, long delay) {
        this.packageName = packageName;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new NoPasswordUIMap();
    }

    public static String getScreenID() {
        return SCREEN_ID;
    }

    /**
     *
     * @return true if click on button OK was performed successfully, otherwise false
     */
    public boolean clickOK() {
        try {
            return controls.getBtnOK().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean doAction() {
        return clickOK();
    }

    private class NoPasswordUIMap {

        public EditText getEnterPassword() {
            return EditTextImpl.getByID(packageName, "COM_GOOD_GD_NO_PASSWORD_FIELD",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnOK() {
            return ButtonImpl.getByID(packageName, "COM_GOOD_GD_OK_BUTTON",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getGdSimulationLabel() {
            return TextViewImpl.getByID(packageName, "gd_simulation_label",
                    Duration.of(WAIT_FOR_SCREEN));
        }
    }
}
