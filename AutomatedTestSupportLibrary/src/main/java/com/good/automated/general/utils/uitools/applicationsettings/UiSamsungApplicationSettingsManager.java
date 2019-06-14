package com.good.automated.general.utils.uitools.applicationsettings;

import android.support.test.uiautomator.UiSelector;


/**
 * UI Application settings manager that works with Samsung Devices.
 */
 class UiSamsungApplicationSettingsManager extends UiApplicationSettingsManager {

    UiSamsungApplicationSettingsManager(){

    }

    /**
     * Method locates the force stop button to stop the app .
     * For Samsung devices the Force Stop Button has the text "FORCE STOP"
     *
     * @return selector for the force stop button which enables to force stop the application.
     */
    @Override
    protected UiSelector locateSelectorForForceStop(){

        selectorTextForStop = "FORCE STOP";
        return new UiSelector().text(selectorTextForStop);
    }

    @Override
    protected UiSelector locateSelectorForForceStopButton() {
        return new UiSelector().resourceId(computeResourceId("com.android.settings","right_button"));
    }
}
