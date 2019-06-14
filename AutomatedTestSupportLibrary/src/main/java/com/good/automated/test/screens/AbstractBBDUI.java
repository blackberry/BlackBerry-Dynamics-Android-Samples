/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
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
