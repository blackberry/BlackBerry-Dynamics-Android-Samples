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

import static com.good.automated.general.utils.Duration.AUTHORIZE_CALLBACK;

import android.util.Log;

import com.good.automated.general.utils.Duration;

public class BBDAdvancedActivationUI extends AbstractBBDActivationUI {

    private String TAG = BBDAdvancedActivationUI.class.getCanonicalName();

    private String bcpURL;

    /**
     * @param packageName app under test packageName
     */
    public BBDAdvancedActivationUI(String packageName) {
        super(packageName);
        this.controls = new BBDActivationUIMap();
    }

    /**
     * @param packageName   app under test packageName
     * @param delay         duration to wait for screen
     */
    public BBDAdvancedActivationUI(String packageName, long delay) {
        super(packageName);
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDActivationUIMap();
    }

    /**
     * @param packageName app under test packageName
     * @param userName    user name to provision with
     * @param password    password
     * @param bcpURL      BlackBerry Comm Proxy URL
     */
    public BBDAdvancedActivationUI(String packageName,
                                           String userName,
                                           String password,
                                           String bcpURL) {
        this(packageName, userName, password, bcpURL, Duration.of(AUTHORIZE_CALLBACK));
    }

    /**
     * @param packageName app under test packageName
     * @param userName    user name to provision with
     * @param password        password
     * @param delay       duration to wait for screen
     * @param bcpURL      BlackBerry Comm Proxy URL
     */
    public BBDAdvancedActivationUI(String packageName,
                                           String userName,
                                           String password,
                                           String bcpURL,
                                           long delay) {
        super(packageName, userName, password);
        this.bcpURL = bcpURL;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)) {
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDActivationUIMap();
    }

    /**
     * @param bcpURL    BlackBerry Comm Proxy URL
     * @return          true if BCP URL entered successfully, otherwise false
     */
    public boolean enterURL(String bcpURL) {
        try {
            boolean urlEntered = controls.getBcpURLField().legacySetText(bcpURL);
            Log.d(TAG, "Result of entering BCP URL: " + urlEntered);
            return urlEntered;
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean doAction() {
        return enterUserLogin(userName) && enterActivationPassword(activationPassword) && enterURL(bcpURL) && clickEnter();
    }

    @Override
    public void clearData() {
        controls.getUserLogin().clearData();
        controls.getActivationPasswordField().clearData();
        controls.getBcpURLField().clearData();
    }

}
