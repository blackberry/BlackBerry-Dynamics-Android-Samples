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
import com.good.automated.general.controls.Clickable;
import com.good.automated.general.controls.TextView;
import com.good.automated.general.controls.impl.ButtonImpl;
import com.good.automated.general.controls.impl.TextViewImpl;
import com.good.automated.general.utils.AbstractUIAutomatorUtils;
import com.good.automated.general.utils.Duration;
import com.good.automated.general.utils.UIAutomatorUtilsFactory;

public class BBDAlertDialogUI implements Clickable {

    private static final String TAG = BBDAlertDialogUI.class.getSimpleName();
    private String packageName;
    private String buttonId = "button1";
    private AlertDialogUIMap alertDialogScreen;
    private AbstractUIAutomatorUtils uiAutomationUtils = UIAutomatorUtilsFactory.getUIAutomatorUtils();

    public BBDAlertDialogUI() {
        this.packageName = "android";
        alertDialogScreen = new AlertDialogUIMap();
    }

    public BBDAlertDialogUI(long delay) {
        this.packageName = "android";
        if (!uiAutomationUtils.isResourceWithIDShown(packageName, "alertTitle", delay)){
            throw new RuntimeException("Alert was not shown within provided time!");
        }
        alertDialogScreen = new AlertDialogUIMap();
    }

    public BBDAlertDialogUI(String customButtonId) {
        this.buttonId = customButtonId;
        this.packageName = "android";
        alertDialogScreen = new AlertDialogUIMap();
    }

    public BBDAlertDialogUI(String customButtonId, long delay) {
        this.buttonId = customButtonId;
        this.packageName = "android";
        if (!uiAutomationUtils.isResourceWithIDShown(packageName, "alertTitle", delay)){
            throw new RuntimeException("Alert was not shown within provided time!");
        }
        alertDialogScreen = new AlertDialogUIMap();
    }

    /**
     * @return true if click on button OK was performed successfully, otherwise false
     */
    @Override
    public boolean click() {
        try {
            return alertDialogScreen.getBtnOK().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     * @return alert title
     */
    public String getAlertTitle() {
        try {
            return alertDialogScreen.getTextAlert().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    /**
     * @return alert message
     */
    public String getAlertMessage() {
        try {
            return alertDialogScreen.getTextMessage().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    private class AlertDialogUIMap {

        public TextView getTextAlert() {
            return TextViewImpl.getByID(packageName, "alertTitle", Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getTextMessage() {
            return TextViewImpl.getByID(packageName, "message", Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnOK() {
            return ButtonImpl.getByID(packageName, buttonId, Duration.of(WAIT_FOR_SCREEN));
        }
    }
}
