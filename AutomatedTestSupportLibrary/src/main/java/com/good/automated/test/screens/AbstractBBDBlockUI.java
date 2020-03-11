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

public abstract class AbstractBBDBlockUI extends AbstractBBDUI {

    private final static String SCREEN_ID = "bbd_block_view_UI";

    protected String packageName;
    protected BBDBlockUIMap controls;
    private String TAG = AbstractBBDBlockUI.class.getSimpleName();

    /**
     * @param packageName app under test packageName
     */
    public AbstractBBDBlockUI(String packageName) {
        this.packageName = packageName;
    }

    public static String getScreenID() {
        return SCREEN_ID;
    }

    /**
     *
     * @return title displayed on the screen
     */
    public String getTitle() {
        try {
            return controls.getBlockTitle().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    /**
     *
     * @return details displayed on the screen
     */
    public String getDetails() {
        try {
            return controls.getBlockDetails().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    /**
     *
     * @param label label to be checked
     * @return true if label matches expected one, otherwise false
     */
    public boolean checkSimulatedOrDebugLabel(String label) {
        try {
            return controls.getGdSimulationLabel().getText().equals(label);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * @return true if action was performed successfully, otherwise false
     */
    @Override
    public boolean doAction() {
        //probably is required force stop of the app
        return getUiAutomationUtils().forceStopApp(packageName);
    }

    protected class BBDBlockUIMap {

        public TextView getBlockTitle() {
            return TextViewImpl.getByID(packageName, "COM_GOOD_GD_BLOCK_VIEW_TITLE_VIEW",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getBlockTitle(String text) {
            return TextViewImpl.getByIDAndText(packageName, "COM_GOOD_GD_BLOCK_VIEW_TITLE_VIEW",
                    text, Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getBlockDetails() {
            return TextViewImpl.getByID(packageName, "COM_GOOD_GD_BLOCK_VIEW_MESSAGE_VIEW",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnUnlock() {
            return ButtonImpl.getByID(packageName, "gd_unlock_button",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getGdSimulationLabel() {
            return TextViewImpl.getByID(packageName, "gd_simulation_label",
                    Duration.of(WAIT_FOR_SCREEN));
        }
    }
}
