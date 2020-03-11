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

import com.good.automated.general.controls.ImageView;
import com.good.automated.general.controls.impl.ImageViewImpl;
import com.good.automated.general.utils.Duration;

public class BBDWelcomeUI extends AbstractBBDUI {

    private static final String SCREEN_ID = "bbd_welcome_view_UI";
    private String TAG = BBDWelcomeUI.class.getSimpleName();
    private String packageName;

    private BBDWelcomeUIMap controls;

    public BBDWelcomeUI(String packageName) {
        this.packageName = packageName;
        this.controls = new BBDWelcomeUIMap();
    }

    public BBDWelcomeUI(String packageName, long delay) {
        this.packageName = packageName;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new IllegalStateException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDWelcomeUIMap();
    }

    public static String getScreenID() {
        return SCREEN_ID;
    }

    @Override
    public boolean doAction() {
        return getUiAutomationUtils().waitUntilElementGoneFromUI(packageName, getScreenID(),
                Duration.of(Duration.AUTHORIZE_CALLBACK));
    }

    private class BBDWelcomeUIMap {

        public ImageView getWelcomeLogo() {
            return ImageViewImpl.getByID(packageName, "gd_welcome_logo",
                    Duration.of(WAIT_FOR_SCREEN));
        }
    }

}
