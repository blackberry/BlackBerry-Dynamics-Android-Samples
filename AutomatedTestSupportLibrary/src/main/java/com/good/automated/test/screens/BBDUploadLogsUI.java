/*
 * (c) 2018 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.test.screens;

import static com.good.automated.general.utils.Duration.WAIT_FOR_SCREEN;

import android.util.Log;

import com.good.automated.general.controls.Button;
import com.good.automated.general.controls.impl.ButtonImpl;
import com.good.automated.general.utils.Duration;

public class BBDUploadLogsUI extends AbstractBBDUI {

    private static final String SCREEN_ID = "bbd_logs_upload_view_UI";
    private String packageName;

    private BBDUploadLogsUIMap controls;
    private String TAG = BBDUnlockUI.class.getSimpleName();

    public BBDUploadLogsUI(String packageName) {
        this.packageName = packageName;
        this.controls = new BBDUploadLogsUIMap();
    }

    public BBDUploadLogsUI(String packageName, long delay) {
        this.packageName = packageName;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDUploadLogsUIMap();
    }

    public static String getScreenID() {
        return SCREEN_ID;
    }

    /**
     *
     * @return true if click on button Upload was performed successfully, otherwise false
     */
    public boolean clickUpload() {
        try {
            return controls.getChangeState().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean doAction() {
        return false;
    }

    private class BBDUploadLogsUIMap {

        public Button getChangeState() {
            return ButtonImpl.getByID(packageName, "bbd_btn_change_upload_logs_state",
                    Duration.of(WAIT_FOR_SCREEN));
        }
    }
}
