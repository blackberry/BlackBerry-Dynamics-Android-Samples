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

/**
 * Cases:
 * 1. One version of the app is added to your user on GC and then was moved to Deny list.
 * 2. App is not added to your user on GC, but you started provisioning.
 * 3. App container was removed from list of provisioned containers on GC
 */
public class BBDApplicationAccessDeniedUI extends AbstractBBDBlockUI {

    private String TAG = BBDApplicationAccessDeniedUI.class.getSimpleName();

    /**
     * @param packageName       app under test packageName
     */
    public BBDApplicationAccessDeniedUI(String packageName) {
        super(packageName);
        this.controls = new BBDBlockUIMap();
    }

    public BBDApplicationAccessDeniedUI(String packageName, long delay) {
        super(packageName);
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDBlockUIMap();
    }
}
