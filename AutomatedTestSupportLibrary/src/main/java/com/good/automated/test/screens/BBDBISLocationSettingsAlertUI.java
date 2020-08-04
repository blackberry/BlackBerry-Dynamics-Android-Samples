/* Copyright (c) 2020 BlackBerry Limited.
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

import android.util.Log;

import com.good.automated.general.controls.Button;
import com.good.automated.general.controls.impl.ButtonImpl;
import com.good.automated.general.utils.Duration;

import static com.good.automated.general.utils.Duration.WAIT_FOR_SCREEN;

public class BBDBISLocationSettingsAlertUI extends AbstractBBDUI  {

    private static final String SCREEN_ID = "sis_settings_root_view";

    private static final String TAG = BBDBISLocationSettingsAlertUI.class.getSimpleName();
    private String packageName;
    private BBDBISLocationAlertUIMap controls;

    /**
     * Mapping of BIS Location Settings alert dialog
     *
     * @param packageName package name of app under test
     */
    public BBDBISLocationSettingsAlertUI(String packageName) {
        this.packageName = packageName;
        controls = new BBDBISLocationAlertUIMap();
    }

    /**
     * Mapping of BIS Location Settings alert dialog
     *
     * @param packageName package name of app under test
     * @param delay delay to wait the screen
     */
    public BBDBISLocationSettingsAlertUI(String packageName, long delay) {
        this.packageName = packageName;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        controls = new BBDBISLocationAlertUIMap();
    }

    public static String getScreenID() {
        return SCREEN_ID;
    }

    /** Do default action on this screen
     *
     * @return true if action was performed successfully otherwise false
     */
    @Override
    public boolean doAction() {
        try {
            return controls.getBtnSettings().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /** Click on Cancel button
     *
     * @return true if click on button was performed successfully, otherwise false
     */
    public boolean cancel() {
        try {
            return controls.getBtnCancel().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    private class BBDBISLocationAlertUIMap {

        Button getBtnCancel() {
            return ButtonImpl.getByID(packageName, "btn_cancel",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        Button getBtnSettings() {
            return ButtonImpl.getByID(packageName, "btn_settings",
                    Duration.of(WAIT_FOR_SCREEN));
        }
    }
}
