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
import com.good.automated.general.controls.TextView;
import com.good.automated.general.controls.impl.ButtonImpl;
import com.good.automated.general.controls.impl.TextViewImpl;
import com.good.automated.general.utils.Duration;

public class BBDLearnMoreUI extends AbstractBBDUI {

    private static final String SCREEN_ID = "bbd_learn_more_view_UI";
    private String packageName;

    private LearnMoreUIMap controls;
    private String TAG = BBDLearnMoreUI.class.getSimpleName();

    /**
     *
     * @param packageName app under test packageName
     */
    public BBDLearnMoreUI(String packageName) {
        this.packageName = packageName;
        this.controls = new LearnMoreUIMap();
    }

    public BBDLearnMoreUI(String packageName, long delay) {
        this.packageName = packageName;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new LearnMoreUIMap();
    }

    public static String getScreenID() {
        return SCREEN_ID;
    }

    /**
     *
     * @return title of Learn More screen
     */
    public String getTitle() {
        try {
            return controls.getLearnMoreTitle().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    /**
     *
     * @return details shown on Learn More screen
     */
    public String getDetails() {
        try {
            return controls.getLearnMoreDetails().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    /**
     *
     * @return true if click on button Done was performed successfully, otherwise false
     */
    public boolean clickDone() {
        try {
            return controls.getBtnDone().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * @param label label to be checked
     * @return true if label matches expected one otherwise false
     */
    public boolean checkSimulatedOrDebugLabel(String label) {
        try {
            return controls.getGdSimulationLabel().getText().equals(label);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean doAction() {
        return clickDone();
    }

    private class LearnMoreUIMap {

        public TextView getLearnMoreDetails() {
            return TextViewImpl.getByID(packageName, "gd_learn_more_details",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getLearnMoreTitle() {
            return TextViewImpl.getByID(packageName, "gd_learn_more_title",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnDone() {
            return ButtonImpl.getByID(packageName, "gd_done_button",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getGdSimulationLabel() {
            return TextViewImpl.getByID(packageName, "gd_simulation_label",
                    Duration.of(WAIT_FOR_SCREEN));
        }
    }
}
