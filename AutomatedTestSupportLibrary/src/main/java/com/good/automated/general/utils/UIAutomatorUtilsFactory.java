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

package com.good.automated.general.utils;

import android.os.Build;

import com.good.automated.general.utils.impl.UIAutomatorUtilsAndroidN24;
import com.good.automated.general.utils.impl.UIAutomatorUtilsAndroidN25;
import com.good.automated.general.utils.impl.UIAutomatorUtilsAndroidO26;
import com.good.automated.general.utils.impl.UIAutomatorUtilsAndroidO27;
import com.good.automated.general.utils.impl.UIAutomatorUtilsAndroidP28;
import com.good.automated.general.utils.impl.UIAutomatorUtilsAndroidQ29;

/**
 * UIAutomatorUtilsFactory is a helper class that return us concrete realisation of interactions with
 * system UI for specific Android API
 */
public abstract class UIAutomatorUtilsFactory {

    private static AbstractUIAutomatorUtils uiAutomatorUtils;

    public static AbstractUIAutomatorUtils getUIAutomatorUtils() {
        if (uiAutomatorUtils == null) {
            switch (Build.VERSION.SDK_INT) {
                case 24:
                    uiAutomatorUtils = UIAutomatorUtilsAndroidN24.getInstance();
                    break;
                case 25:
                    uiAutomatorUtils = UIAutomatorUtilsAndroidN25.getInstance();
                    break;
                case 26:
                    uiAutomatorUtils = UIAutomatorUtilsAndroidO26.getInstance();
                    break;
                case 27:
                    uiAutomatorUtils = UIAutomatorUtilsAndroidO27.getInstance();
                    break;
                case 28:
                    uiAutomatorUtils = UIAutomatorUtilsAndroidP28.getInstance();
                    break;
                case 29:
                    uiAutomatorUtils = UIAutomatorUtilsAndroidQ29.getInstance();
                    break;
                default:
                    uiAutomatorUtils = UIAutomatorUtilsAndroidO26.getInstance();
            }
        }

        return uiAutomatorUtils;
    }
}
