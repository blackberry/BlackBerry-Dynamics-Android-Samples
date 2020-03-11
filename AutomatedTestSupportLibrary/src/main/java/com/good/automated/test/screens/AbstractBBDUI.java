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

import com.good.automated.general.utils.AbstractUIAutomatorUtils;
import com.good.automated.general.utils.UIAutomatorUtilsFactory;

public abstract class AbstractBBDUI {

    private AbstractUIAutomatorUtils uiAutomationUtils = UIAutomatorUtilsFactory.getUIAutomatorUtils();

    public abstract boolean doAction();

    /**
     *
     * @return unique id of the screen
     */
    public static String getScreenID() {
        throw new IllegalStateException("getScreenId() method should be overridden in the class"
                + " it was tried to be called from, but it was not overridden!");
    }

    public AbstractUIAutomatorUtils getUiAutomationUtils() {
        return uiAutomationUtils;
    }

}
