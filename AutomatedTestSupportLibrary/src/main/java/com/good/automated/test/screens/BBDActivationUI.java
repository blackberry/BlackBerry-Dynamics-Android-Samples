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

import com.good.automated.general.utils.Duration;

import static com.good.automated.general.utils.Duration.AUTHORIZE_CALLBACK;

public class BBDActivationUI extends AbstractBBDActivationUI {

    private String TAG = BBDActivationUI.class.getSimpleName();

    /**
     * @param packageName app under test packageName
     */
    public BBDActivationUI(String packageName) {
        super(packageName);
        this.controls = new BBDActivationUIMap();
    }


    /**
     * @param packageName   app under test packageName
     * @param delay         duration to wait for screen
     */
    public BBDActivationUI(String packageName, long delay) {
        super(packageName);
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDActivationUIMap();
    }

    /**
     * @param packageName app under test packageName
     * @param userName    user name to provision with
     * @param pin1        pin1
     * @param pin2        pin2
     * @param pin3        pin3
     */
    public BBDActivationUI(String packageName,
                           String userName,
                           String pin1,
                           String pin2,
                           String pin3) {
        this(packageName, userName, pin1, pin2, pin3, Duration.of(AUTHORIZE_CALLBACK));
    }

    /**
     * @param packageName app under test packageName
     * @param userName    user name to provision with
     * @param pin1        pin1
     * @param pin2        pin2
     * @param pin3        pin3
     * @param delay       duration to wait for screen
     */
    public BBDActivationUI(String packageName,
                           String userName,
                           String pin1,
                           String pin2,
                           String pin3,
                           long delay) {
        super(packageName, userName, pin1, pin2, pin3);
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)) {
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDActivationUIMap();
    }


}
