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
 * Case: Malisious application installed on a device.
 */
public class BBDApplicationMTDBlockUI extends AbstractBBDBlockUI {

    private final static String SCREEN_ID = "bbd_mtd_block_view_UI";

    private String TAG = BBDApplicationMTDBlockUI.class.getSimpleName();

    /**
     * Return unique id for the screen.
     *
     * @return screen id
     */
    public static String getScreenID() {
        return SCREEN_ID;
    }

    /**
     * @param packageName app under test packageName
     */
    public BBDApplicationMTDBlockUI(String packageName) {
        super(packageName);
        this.controls = new BBDBlockUIMap();
    }

    public BBDApplicationMTDBlockUI(String packageName, long delay) {
        super(packageName);
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)) {
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDBlockUIMap();
    }

}
