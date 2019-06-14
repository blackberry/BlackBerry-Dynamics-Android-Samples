/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.test.screens;

import static com.good.automated.general.utils.Duration.WAIT_FOR_SCREEN;

import android.util.Log;

import com.good.automated.general.controls.Button;
import com.good.automated.general.controls.Clickable;
import com.good.automated.general.controls.TextView;
import com.good.automated.general.controls.impl.ButtonImpl;
import com.good.automated.general.controls.impl.TextViewImpl;
import com.good.automated.general.utils.Duration;

public class BBDFingerprintAlertUI extends AbstractBBDUI implements Clickable {

    private static final String SCREEN_ID = "bbd_fingerprint_container_UI";

    private static final String TAG = BBDFingerprintAlertUI.class.getSimpleName();
    private String packageName;
    private BBDFingerprintAlertUIMap controls;

    /**
     * Mapping of Fingerprint alert dialog
     *
     * @param packageName package name of app under test
     */
    public BBDFingerprintAlertUI(String packageName) {
        this.packageName = packageName;
        controls = new BBDFingerprintAlertUIMap();
    }

    public BBDFingerprintAlertUI(String packageName, long delay) {
        this.packageName = packageName;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        controls = new BBDFingerprintAlertUIMap();
    }

    public static String getScreenID() {
        return SCREEN_ID;
    }

    /**
     * @return fingerprint title
     */
    public String getFingerprintTitle() {
        try {
            return controls.getFingerprintTitle().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    /**
     * @return fingerprint text message
     */
    public String getFingerprintText() {
        try {
            return controls.getFingerprintText().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    /**
     * @return fingerprint text near icon
     */
    public String getFingerprintIconText() {
        try {
            return controls.getFingerprintIconText().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    /**
     * Fingerprint setup will be always canceled
     * in purpose of successful provisioning of the app using predefined flow
     * <p>
     * Note: there are two possible UI schemas
     * 1. fingerprint is disabled on device:
     * fingerprint dialog will have two button with ID's:
     * - text CANCEL, id - button1
     * - text ENROLL, id - button2
     * 2. fingerprint is enabled on device:
     * fingerprint dialog will have only one button with ID:
     * - text CANCEL, id - button2
     *
     * @return true if dialog was canceled, otherwise false
     */
    @Override
    public boolean click() {
        try {
            if (controls.getBtnEnroll().getText().equals("CANCEL") || controls.getBtnEnroll().getText().equals
                    ("USE PASSWORD")) {
                return controls.getBtnEnroll().click();
            } else {
                return controls.getBtnCancel().click();
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * @return true if click on Enroll button was successful, otherwise false
     */
    public boolean clickEnroll() {
        try {
            return controls.getBtnEnroll().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fingerprint setup will be always canceled
     *
     * @return true if canceled, otherwise false
     */
    @Override
    public boolean doAction() {
        return click();
    }

    private class BBDFingerprintAlertUIMap {

        public TextView getFingerprintTitle() {
            return TextViewImpl.getByID(packageName, "fingerprint_title",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getFingerprintText() {
            return TextViewImpl.getByID(packageName, "fingerprint_text",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getFingerprintIconText() {
            return TextViewImpl.getByID(packageName, "fingerprint_icon_text",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnCancel() {
            return ButtonImpl.getByID("android", "button1",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnEnroll() {
            return ButtonImpl.getByID("android", "button2",
                    Duration.of(WAIT_FOR_SCREEN));
        }
    }
}
