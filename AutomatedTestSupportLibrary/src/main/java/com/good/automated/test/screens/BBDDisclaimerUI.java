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

/**
 * Screen for agreement message
 */
public class BBDDisclaimerUI extends AbstractBBDUI {

    private static final String SCREEN_ID = "bbd_disclaimer_view_UI";

    private String packageName;
    private BBDDisclaimerUIMap controls;
    private String TAG = BBDDisclaimerUI.class.getSimpleName();

    public BBDDisclaimerUI(String packageName) {
        this.packageName = packageName;
        this.controls = new BBDDisclaimerUIMap();
    }

    public BBDDisclaimerUI(String packageName, long delay) {
        this.packageName = packageName;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDDisclaimerUIMap();
    }

    public static String getScreenID() {
        return SCREEN_ID;
    }

    /**
     *
     * @return true if Click Accept was performed successfully, otherwise false
     */
    public boolean clickAccept() {
        try {
            return controls.getBtnAccept().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * @return text full text of agreement message displayed on the screen
     */
    public String getDisclaimerText() {
        try {
            return controls.getDisclaimerText().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean doAction() {
        return clickAccept();
    }

    protected class BBDDisclaimerUIMap {

        public TextView getDisclaimerText() {
            return TextViewImpl.getByID(packageName, "gd_disclaimer_text",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnAccept() {
            return ButtonImpl.getByID(packageName, "gd_done_button",
                    Duration.of(WAIT_FOR_SCREEN));
        }
    }
}
