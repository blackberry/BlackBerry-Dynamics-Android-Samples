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

import com.good.automated.general.controls.Button;
import com.good.automated.general.controls.TextView;
import com.good.automated.general.controls.impl.ButtonImpl;
import com.good.automated.general.controls.impl.TextViewImpl;
import com.good.automated.general.utils.Duration;

import android.util.Log;

/**
 * Screen for Mobile Thread Defense disclaimer message
 */
public class BBDMTDDisclaimerUI extends AbstractBBDUI {

    private static final String SCREEN_ID = "gd_mtd_disclaimer_heading_text";

    private String packageName;
    private BBDMTDDisclaimerUIMap controls;
    private String TAG = BBDMTDDisclaimerUI.class.getSimpleName();

    public BBDMTDDisclaimerUI(String packageName) {
        this.packageName = packageName;
        this.controls = new BBDMTDDisclaimerUIMap();
    }

    public BBDMTDDisclaimerUI(String packageName, long delay) {
        this.packageName = packageName;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)) {
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDMTDDisclaimerUIMap();
    }

    public static String getScreenID() {
        return SCREEN_ID;
    }

    /**
     * @return true if Click Allow was performed successfully, otherwise false
     */
    public boolean clickAllow() {
        try {
            return controls.getBtnAllow().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     * @return full text of MTD disclaimer message displayed on the screen
     */
    public String getMTDDisclaimerText() {
        try {
            return controls.getMTDDisclaimerText().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    /**
     * @return full text of MTD disclaimer header displayed on the screen
     */
    public String getMTDDisclaimerHeaderText() {
        try {
            return controls.getMTDDisclaimerHeader().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean doAction() {
        return clickAllow();
    }

    protected class BBDMTDDisclaimerUIMap {

        public TextView getMTDDisclaimerHeader() {
            return TextViewImpl.getByID(packageName, "gd_mtd_disclaimer_heading_text",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getMTDDisclaimerText() {
            return TextViewImpl.getByID(packageName, "gd_mtd_disclaimer_text",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnAllow() {
            return ButtonImpl.getByID(packageName, "gd_done_button",
                    Duration.of(WAIT_FOR_SCREEN));
        }
    }
}
