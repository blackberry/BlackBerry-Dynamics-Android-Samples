/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.general.utils;

import android.os.Build;

import com.good.automated.general.utils.impl.UIAutomatorUtilsAndroidM23;
import com.good.automated.general.utils.impl.UIAutomatorUtilsAndroidN24;
import com.good.automated.general.utils.impl.UIAutomatorUtilsAndroidN25;
import com.good.automated.general.utils.impl.UIAutomatorUtilsAndroidO26;
import com.good.automated.general.utils.impl.UIAutomatorUtilsAndroidO27;
import com.good.automated.general.utils.impl.UIAutomatorUtilsAndroidP28;

/**
 * UIAutomatorUtilsFactory is a helper class that return us concrete realisation of interactions with
 * system UI for specific Android API
 */
public abstract class UIAutomatorUtilsFactory {

    private static AbstractUIAutomatorUtils uiAutomatorUtils;

    public static AbstractUIAutomatorUtils getUIAutomatorUtils() {
        if (uiAutomatorUtils == null) {
            switch (Build.VERSION.SDK_INT){
                case 23:
                    uiAutomatorUtils = UIAutomatorUtilsAndroidM23.getInstance();
                    break;
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
                default:
                    uiAutomatorUtils = UIAutomatorUtilsAndroidM23.getInstance();
            }
        }

        return uiAutomatorUtils;
    }
}
