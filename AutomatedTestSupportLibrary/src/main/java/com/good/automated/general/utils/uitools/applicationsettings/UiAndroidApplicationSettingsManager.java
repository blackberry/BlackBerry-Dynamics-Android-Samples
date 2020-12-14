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

package com.good.automated.general.utils.uitools.applicationsettings;

import androidx.test.uiautomator.UiSelector;

/**
 * UI Application settings manager that works with default Android platform.
 * Should work fine with Nexus devices and with Android emulator provided by Google.
 */
class UiAndroidApplicationSettingsManager extends UiApplicationSettingsManager {

    UiAndroidApplicationSettingsManager(){

    }

    /**
     * Method locates the force stop button to stop the app .
     * For Android devices the Force Stop Button has the text "OK"
     *
     * @return selector for the force stop button which enables to force stop the application.
     */
    @Override
    protected UiSelector locateSelectorForForceStop(){

        selectorTextForStop = "OK";
        return new UiSelector().text(selectorTextForStop);
    }

    @Override
    protected UiSelector locateSelectorForForceStopButton() {
        return new UiSelector().resourceId(computeResourceId("com.android.settings","right_button"));
    }
}
