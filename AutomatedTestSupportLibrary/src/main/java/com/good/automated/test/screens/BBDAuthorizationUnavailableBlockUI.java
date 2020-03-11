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

import static com.good.automated.general.utils.Duration.UI_WAIT;
import static com.good.automated.general.utils.Duration.WAIT_FOR_SCREEN;

import android.util.Log;

import com.good.automated.general.controls.Button;
import com.good.automated.general.controls.impl.ButtonImpl;
import com.good.automated.general.utils.Duration;

/**
 * Case: you have provisioned your own app, but Auth Delegator (Master) app isn't installed on device.
 */
public class BBDAuthorizationUnavailableBlockUI extends AbstractBBDBlockUI {

    private static final String GETTING_ACCESS_KEY = "Getting Access Key";
    private String TAG = BBDAuthorizationUnavailableBlockUI.class.getSimpleName();

    /**
     * @param packageName app under test packageName
     */
    public BBDAuthorizationUnavailableBlockUI(String packageName) {
        super(packageName);
        this.controls = new BBDAuthorizationUnavailableBlockUIMap();
    }

    public BBDAuthorizationUnavailableBlockUI(String packageName, long delay) {
        super(packageName);
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDAuthorizationUnavailableBlockUIMap();
    }

    /**
     *
     * @return true if click on Try again was successful, otherwise false
     */
    public boolean clickTryAgain() {
        try {
            Log.d(TAG, "Trying to click on button: " + controls.getBtnUnlock().getText());
            return controls.getBtnUnlock().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     * Performs action on Block screen. For case when button is available it clicks on it.
     * For case when button is unavailable prints information about screen in log.
     *
     * @return true if action was performed successfully, otherwise false
     */
    @Override
    public boolean doAction() {
        if (clickTryAgain()){
            Log.d(TAG, "\"Try again\" button was successfully clicked");
        } else {
            Log.d(TAG, "No shown buttons on the screen");
            try {
                Log.d(TAG, "Block screen title: " + controls.getBlockTitle().getText());
                if (controls.getBlockTitle().getText().equals(GETTING_ACCESS_KEY)){
                    getUiAutomationUtils().waitUntilTextGoneFormScreen(GETTING_ACCESS_KEY, Duration.of(UI_WAIT));
                }
                Log.d(TAG, "Block screen details: " + controls.getBlockDetails().getText());
            } catch (NullPointerException e) {
                Log.d(TAG, "NullPointerException: " + e.getMessage());
            }
        }
        return true;
    }

    protected class BBDAuthorizationUnavailableBlockUIMap extends BBDBlockUIMap {

        @Override
        public Button getBtnUnlock() {
            return ButtonImpl.getByID(packageName, "gd_ok_button", Duration.of(WAIT_FOR_SCREEN));
        }
    }
}
