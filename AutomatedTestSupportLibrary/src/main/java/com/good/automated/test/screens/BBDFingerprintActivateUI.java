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
import com.good.automated.general.controls.RadioButton;
import com.good.automated.general.controls.TextView;
import com.good.automated.general.controls.impl.ButtonImpl;
import com.good.automated.general.controls.impl.RadioButtonImpl;
import com.good.automated.general.controls.impl.TextViewImpl;
import com.good.automated.general.utils.Duration;

public class BBDFingerprintActivateUI extends AbstractBBDUI {

    private static final String SCREEN_ID = "bbd_activate_fingerprint_view_UI";

    private static final String TAG = BBDFingerprintActivateUI.class.getSimpleName();
    private String packageName;
    private BBDFingerprintActivateUIMap controls;

    /**
     * Mapping of Fingerprint activate screen
     *
     * @param packageName package name of app under test
     */
    public BBDFingerprintActivateUI(String packageName) {
        this.packageName = packageName;
        controls = new BBDFingerprintActivateUIMap();
    }

    public BBDFingerprintActivateUI(String packageName, long delay) {
        this.packageName = packageName;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)) {
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        controls = new BBDFingerprintActivateUIMap();
    }

    public static String getScreenID() {
        return SCREEN_ID;
    }

    /**
     * @return fingerprint view title
     */
    public String getFingerprintViewTitle() {
        try {
            return controls.getFingerprintViewTitle().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    /**
     * @return fingerprint view text message
     */
    public String getFingerprintViewText() {
        try {
            return controls.getFingerprintViewText().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    /**
     * @return fingerprint text near switch
     */
    public String getFingerprintToggleText() {
        try {
            return controls.getFingerprintToggleText().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    /**
     * @return true if toggling successful, otherwise false
     */
    public boolean toggleFingerprint() {
        try {
            return controls.getToggleButton().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     * @return true if switch enabled, otherwise false
     */
    public boolean isToggled() {
        try {
            return controls.getToggleButton().isChecked();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     * @return true if click on Back button was successful, otherwise false
     */
    public boolean clickBack() {
        try {
            return controls.getBtnBack().click();
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
        return clickBack();
    }

    private class BBDFingerprintActivateUIMap {

        public TextView getFingerprintViewTitle() {
            return TextViewImpl.getByID(packageName, "COM_GOOD_GD_ACTIVATE_FINGERPRINT_VIEW_TITLE",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getFingerprintViewText() {
            return TextViewImpl.getByID(packageName, "COM_GOOD_GD_ACTIVATE_FINGERPRINT_VIEW_TEXT",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getFingerprintToggleText() {
            return TextViewImpl.getByID(packageName, "toggle_text",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnBack() {
            return ButtonImpl.getByID(packageName, "COM_GOOD_GD_ACTIVATE_FINGERPRINT_VIEW_BACK",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public RadioButton getToggleButton() {
            return RadioButtonImpl.getByID(packageName, "toggle_button",
                    Duration.of(WAIT_FOR_SCREEN));
        }
    }
}
