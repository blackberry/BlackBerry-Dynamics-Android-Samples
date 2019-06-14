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

public abstract class AbstractBBDBlockUI extends AbstractBBDUI {

    private final static String SCREEN_ID = "bbd_block_view_UI";

    protected String packageName;
    protected BBDBlockUIMap controls;
    private String TAG = AbstractBBDBlockUI.class.getSimpleName();

    /**
     * @param packageName app under test packageName
     */
    public AbstractBBDBlockUI(String packageName) {
        this.packageName = packageName;
    }

    public static String getScreenID() {
        return SCREEN_ID;
    }

    /**
     *
     * @return title displayed on the screen
     */
    public String getTitle() {
        try {
            return controls.getBlockTitle().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    /**
     *
     * @return details displayed on the screen
     */
    public String getDetails() {
        try {
            return controls.getBlockDetails().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
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

    /**
     *
     * @return true if action was performed successfully, otherwise false
     */
    @Override
    public boolean doAction() {
        //probably is required force stop of the app
        return getUiAutomationUtils().forceStopApp(packageName);
    }

    protected class BBDBlockUIMap {

        public TextView getBlockTitle() {
            return TextViewImpl.getByID(packageName, "COM_GOOD_GD_BLOCK_VIEW_TITLE_VIEW",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getBlockDetails() {
            return TextViewImpl.getByID(packageName, "COM_GOOD_GD_BLOCK_VIEW_MESSAGE_VIEW",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnUnlock() {
            return ButtonImpl.getByID(packageName, "gd_unlock_button",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getGdSimulationLabel() {
            return TextViewImpl.getByID(packageName, "gd_simulation_label",
                    Duration.of(WAIT_FOR_SCREEN));
        }
    }
}
