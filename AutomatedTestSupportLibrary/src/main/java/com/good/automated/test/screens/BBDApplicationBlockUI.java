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

import android.util.Log;

import com.good.automated.general.utils.Duration;

/**
 * Case: two or more versions of the app are added to your user on GC. One that is installed on your device
 * is moved to Deny list.
 */
public class BBDApplicationBlockUI extends AbstractBBDBlockUI {

    private String TAG = BBDApplicationBlockUI.class.getSimpleName();

    /**
     * @param packageName app under test packageName
     */
    public BBDApplicationBlockUI(String packageName) {
        super(packageName);
        this.controls = new BBDBlockUIMap();
    }

    public BBDApplicationBlockUI(String packageName, long delay) {
        super(packageName);
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDBlockUIMap();
    }

    /**
     *
     * @return true if action was performed successfully, otherwise false
     */
    @Override
    public boolean doAction() {
        if (getTitle()!=null && getTitle().equals("Authenticating")){
            Log.d(TAG, "Authenticating screen was shown");
            return getUiAutomationUtils().waitUntilTextGoneFormScreen("Authenticating", Duration.of(UI_WAIT));
        }
        return false;
    }
}
