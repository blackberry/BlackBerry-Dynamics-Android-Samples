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

import com.good.automated.general.utils.Duration;

public class BBDRetrievingAccessKeyUI extends AbstractBBDBlockUI{

    private String TAG = BBDRetrievingAccessKeyUI.class.getSimpleName();
    private String GETTING_ACCESS_KEY = "Getting Access Key";

    public BBDRetrievingAccessKeyUI(String packageName) {
        super(packageName);
        this.controls = new BBDBlockUIMap();
    }

    public BBDRetrievingAccessKeyUI(String packageName, long delay) {
        super(packageName);
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDBlockUIMap();
    }

    /**
     *
     * @return true if action was performed successfully otherwise false
     */
    @Override
    public boolean doAction() {
        boolean isGettingAccessKeyScreenDisplayed;
        try{
            isGettingAccessKeyScreenDisplayed = controls.getBlockTitle(GETTING_ACCESS_KEY).isAvailable();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
        return isGettingAccessKeyScreenDisplayed &&
                getUiAutomationUtils().waitUntilElementGoneFromUI(packageName, getScreenID(),
                        Duration.of(Duration.AUTHORIZE_CALLBACK));
    }
}
