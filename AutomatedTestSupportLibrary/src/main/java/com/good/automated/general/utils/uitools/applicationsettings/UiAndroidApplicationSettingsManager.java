package com.good.automated.general.utils.uitools.applicationsettings;

import android.support.test.uiautomator.UiSelector;

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
