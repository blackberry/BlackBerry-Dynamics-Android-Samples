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


// BIS UI with request to grant Location permissions.
public class BBDBISPrimerUI extends AbstractBBDUI {

    private static final String TAG = BBDBISPrimerUI.class.getSimpleName();

    private static final String SCREEN_ID = "ll_primer_root_layout";

    private RequestingAppUIMap controls;
    private String packageName;

    /** Constructor
     *
     * @param appPackageName Sample application packageID
     */
    public BBDBISPrimerUI(String appPackageName, long delay) {
        this.packageName = appPackageName;

        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }

        this.controls = new BBDBISPrimerUI.RequestingAppUIMap();
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
        return grantPermissions();
    }

    /** Click on "Allow" button
     *
     * @return true if click on button was performed successfully, otherwise false
     */
    public boolean clickAllow() {
        try {
            return controls.getAllowButton().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /** Click on "Maybe Later" button
     *
     * @return true if click on button was performed successfully, otherwise false
     */
    public boolean clickMaybeLater() {
        try {
            return controls.getMaybeLaterButton().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /** Click on "Never ask again" button
     *
     * @return true if click on button was performed successfully, otherwise false
     */
    public boolean clickNeverAskAgain() {
        try {
            return controls.getNeverAskAgainButton().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /** Grant location permissions.
     *
     * @return true if permissions were granted, otherwise false
     */
    public boolean grantPermissions() {
        try {
            if (controls.getAllowButton().click()) {
                return new BBDPermissionAlertDialogUI().clickAllowIfPresentAny();
            }
            return false;
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    private class RequestingAppUIMap {

        public Button getAllowButton() {
            return ButtonImpl.getByID(packageName, "btn_allow", Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getMaybeLaterButton() {
            return ButtonImpl.getByID(packageName, "btn_may_be_later", Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getNeverAskAgainButton() {
            return ButtonImpl.getByID(packageName, "btn_never_ask_again", Duration.of(WAIT_FOR_SCREEN));
        }
    }
}
