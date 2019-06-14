package com.good.automated.general.utils.uitools.networking;

import android.support.test.uiautomator.UiSelector;
import android.widget.Switch;

/**
 * UI network settings manager that works with default Android platform.
 * Should work fine with Nexus devices and with Android emulator provided by Google.
 */
class AndroidDefaultNetworkManager extends UiNetworkManager {

    AndroidDefaultNetworkManager() {
    }

    /**
     * Method locates the switch for changing the Airplane mode state on the screen.
     * For standard Android screen that contains Airplane mode settings can be opened in the next way:
     * Settings->More.
     * <p>
     * This screen contains two elements of a type Switch:
     * 0 - Airplane mode switch
     * 1 - NFC switch
     * <p>
     * This method creates a selector for the first element of a type Switch on the current screen.
     *
     * @return selector for the switch which enables and disables the airplane (flight) mode
     */
    @Override
    protected UiSelector locateSelectorForAirplaneModeSwitch() {
        return new UiSelector().className(Switch.class);
    }
}
