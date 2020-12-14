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

import android.util.Log;

import com.good.automated.general.controls.Button;
import com.good.automated.general.controls.EditText;
import com.good.automated.general.controls.ScrollView;
import com.good.automated.general.controls.TextView;
import com.good.automated.general.controls.impl.ButtonImpl;
import com.good.automated.general.controls.impl.EditTextImpl;
import com.good.automated.general.controls.impl.ScrollViewImpl;
import com.good.automated.general.controls.impl.TextViewImpl;
import com.good.automated.general.utils.Duration;

import static com.good.automated.general.utils.Duration.WAIT_FOR_SCREEN;

public class BBDReauthUnlockUI extends AbstractBBDUI {

    private static final String SCREEN_ID = "bbd_reauth_login_view_UI";

    private static final String TAG = BBDReauthUnlockUI.class.getSimpleName();
    private String password;
    private RequestingAppUIMap controls;
    private String packageName;

    /** Constructor
     *
     * @param reauthSamplePackageName Reauth application packageID
     */
    public BBDReauthUnlockUI(String reauthSamplePackageName) {
        this.packageName = reauthSamplePackageName;
        this.controls = new RequestingAppUIMap();
    }

    /** Constructor
     *
     * @param reauthSamplePackageName Reauth application packageID
     * @param delay delay to wait for the screen
     */
    public BBDReauthUnlockUI(String reauthSamplePackageName, long delay) {
        this.packageName = reauthSamplePackageName;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)) {
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new RequestingAppUIMap();
    }

    /** Constructor
     *
     * @param reauthSamplePackageName Reauth application packageID
     * @param reauthSamplePassword Reauth application password
     */
    public BBDReauthUnlockUI(String reauthSamplePackageName, String reauthSamplePassword) {
        this.packageName = reauthSamplePackageName;
        this.password = reauthSamplePassword;
        this.controls = new RequestingAppUIMap();
    }

    /** Constructor
     *
     * @param reauthSamplePackageName Reauth application packageID
     * @param reauthSamplePassword Reauth application password
     * @param delay delay to wait for the screen
     */
    public BBDReauthUnlockUI(String reauthSamplePackageName, String reauthSamplePassword, long delay) {
        this.packageName = reauthSamplePackageName;
        this.password = reauthSamplePassword;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new RequestingAppUIMap();
    }

    public static String getScreenID() {
        return SCREEN_ID;
    }

    /** Get requesting app text
     *
     * @return requesting app text
     */
    public String getRequestingAppNameText() {
        try {
            return controls.getReqAppNameText().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    /** Get instruction text
     *
     * @return user instruction text
     */
    public String getUserInstructionText() {
        try {
            return controls.getUserInstructionText().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    /** Click on Ok buttn
     *
     * @return true if click on button Ok was performed successfully, otherwise false
     */
    public boolean clickOK() {
        try {
            return controls.getBtnOK().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /** Click on Cancel button
     *
     * @return true if click on button Cancel was performed successfully, otherwise false
     */
    public boolean clickCancel() {
        try {
            return controls.getBtnCancel().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /** Check if there is a cancel button on the screen
     *
     * @return true if button is visible, false if it is gone or not present
     */
    public boolean hasCancelButton() {
        try {
            Button btnCancel = controls.getBtnCancel();
            return btnCancel.isAvailable();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /** Enter password
     *
     * @param appPassword password to be entered
     * @return true if password was entered successfully, otherwise false
     */
    public boolean enterPassword(String appPassword) {
        try {
            return controls.getAppPasssword().legacySetText(appPassword);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /** Click on Bottom button button
     *
     * @return true if click on Bottom button was performed successfully, otherwise false
     */
    public boolean clickBottomButton() {
        try {
            return controls.getBottomAction().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /** Check if there is debug labels
     *
     * @param label label to be checked
     * @return true if label was performed successfully, otherwise false
     */
    public boolean checkSimulatedOrDebugLabel(String label) {
        try {
            return controls.getGdSimulationLabel().getText().equals(label);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /** Scroll container to the beginning
     *
     * @return true if content was scrolled successfully, otherwise false
     */
    public boolean scrollViewToBeginning() {
        try {
            return controls.getScrollView().scrollToBeginning();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /** Scroll container to the end
     *
     * @return true if content was scrolled successfully, otherwise false
     */
    public boolean scrollViewToEnd() {
        try {
            return controls.getScrollView().scrollToEnd();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /** Do the specific screen action like to enter the password and click on Ok button
     *
     * @return true if action was performed successfully, otherwise false
     */
    @Override
    public boolean doAction() {
        return enterPassword(password) && scrollViewToEnd() && clickOK();
    }

    private class RequestingAppUIMap {

        public TextView getReqAppNameText() {
            return TextViewImpl.getByID(packageName, "reqAppNameText",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getUserInstructionText() {
            return TextViewImpl.getByID(packageName, "userInstructionText",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public EditText getAppPasssword() {
            return EditTextImpl.getByID(packageName, "passwordEditor",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnCancel() {
            return ButtonImpl.getByID(packageName, "btnCancel", Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnOK() {
            return ButtonImpl.getByID(packageName, "btnOk", Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getBottomAction() {
            return TextViewImpl.getByID(packageName, "gd_bottom_line_action_label",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getGdSimulationLabel() {
            return TextViewImpl.getByID(packageName, "gd_simulation_label",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public ScrollView getScrollView() {
            return ScrollViewImpl.getByID(packageName, "reauth_provision_view",
                    Duration.of(WAIT_FOR_SCREEN));
        }
    }
}
