package com.good.automated.general.utils.uitools.networking;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import com.good.automated.general.utils.Duration;
import com.good.automated.general.utils.uitools.exception.UiInteractionException;

/**
 * Performs operations with device networking state by performing UI operations.
 * Should have platform specific implementation.
 * Instantiation should be performed via {@link UiNetworkManagerFactory}.
 */
public abstract class UiNetworkManager {

    private static final String TAG = UiNetworkManager.class.getSimpleName();

    private UiDevice mUiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    UiNetworkManager() {
    }

    /**
     * Enables airplane (flight) mode on the device by performing next operations:
     * <ul>
     * <li>opens setting screen where the airplane mode switch is located</li>
     * <li>changes the state of airplane mode to enabled if it was disabled</li>
     * <li>presses the back button to close settings screen</li>
     * <ul/>
     *
     * @return whether the airplane mode was enabled
     */
    public final boolean enableAirplaneMode() {
        Log.i(TAG, "Enabling airplane mode");
        return changeAirplaneModeState(AirplaneModeState.ENABLED);
    }

    /**
     * Disables airplane (flight) mode on the device by performing next operations:
     * <ul>
     * <li>opens setting screen where the airplane mode switch is located</li>
     * <li>changes the state of airplane mode to disabled if it was enabled</li>
     * <li>presses the back button to close settings screen</li>
     * <ul/>
     *
     * @return whether the airplane mode was enabled
     */
    public final boolean disableAirplaneMode() {
        Log.i(TAG, "Disabling airplane mode");
        return changeAirplaneModeState(AirplaneModeState.DISABLED);
    }

    private boolean changeAirplaneModeState(AirplaneModeState newState) {
        openAirplaneModeSettingsScreen();
        AirplaneModeState currentAirplaneModeState = getAirplaneModeSwitchState();
        if (newState == currentAirplaneModeState) {
            Log.d(TAG, "Airplane mode already " + currentAirplaneModeState + " nothing to change");

            mUiDevice.pressBack();
            return true;
        }

        Log.d(TAG, "Changing airplane mode");
        boolean result = clickAirplaneModeSwitch();

        //Waiting for idle to give a chance to Android to complete all setting-up operations on the screen.
        mUiDevice.waitForIdle(Duration.of(Duration.UI_WAIT));
        mUiDevice.pressBack();
        return result;
    }

    /**
     * Returns the current state of the airplane mode switch.
     * Settings screen should be opened.
     *
     * @return the current state of the airplane mode switch
     */
    protected AirplaneModeState getAirplaneModeSwitchState() {
        try {
            UiObject airplaneModeSwitch = locateAirplaneModeSwitch();
            if (airplaneModeSwitch.isChecked()) {
                return AirplaneModeState.ENABLED;
            }

            return AirplaneModeState.DISABLED;
        } catch (UiObjectNotFoundException cause) {
            Log.e(TAG, "failed to check switch state", cause);
            throw new UiInteractionException("failed to check switch state", cause);
        }
    }

    /**
     * Clicks on the airplane mode switch in order to change its state.
     * Settings screen should be opened.
     *
     * @return true in case if click was performed successfully.
     */
    protected boolean clickAirplaneModeSwitch() {
        try {
            UiObject airplaneModeSwitch = locateAirplaneModeSwitch();
            return airplaneModeSwitch.click();
        } catch (UiObjectNotFoundException cause) {
            Log.e(TAG, "Failed to click on airplane mode switch", cause);
            throw new UiInteractionException("Failed to click on Airplane mode switch", cause);
        }
    }

    /**
     * Locates the airplane mode switch on the settings screen.
     * Can be overriden by platform-specific managers.
     *
     * @return UiObject representing airplane mode switch.
     * @throws UiObjectNotFoundException in case if code failed to locate UiObject
     */
    protected UiObject locateAirplaneModeSwitch() throws UiObjectNotFoundException {
        Log.i(TAG, "Locating airplane mode switch");
        UiObject airplaneModeSwitch = getUiDevice().findObject(locateSelectorForAirplaneModeSwitch());
        if (airplaneModeSwitch != null) {
            Log.d(TAG, "Located switch object. Text: " + airplaneModeSwitch.getText());
            return airplaneModeSwitch;
        }

        throw new UiInteractionException("Failed to locate airplane mode switch");
    }

    /**
     * Opens settings screen which contains airplane mode switch.
     * Can be overriden by platform specific implementations.
     */
    protected void openAirplaneModeSettingsScreen() {
        Log.d(TAG, "Open settings screen with airplane mode switch");
        Context context = InstrumentationRegistry.getTargetContext();

        Intent openAirplaneModeSettings = new Intent();
        openAirplaneModeSettings.setAction(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
        openAirplaneModeSettings.addCategory(Intent.CATEGORY_DEFAULT);
        openAirplaneModeSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        openAirplaneModeSettings.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        openAirplaneModeSettings.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        context.startActivity(openAirplaneModeSettings);
        mUiDevice.waitForIdle(Duration.of(Duration.UI_WAIT));
    }

    /**
     * Returns Airplane mode switch locator.
     * Should be implemented by platform-specific managers.
     *
     * @return ui selector for airplane mode switch
     */
    protected abstract UiSelector locateSelectorForAirplaneModeSwitch();

    protected UiDevice getUiDevice() {
        return mUiDevice;
    }

    /**
     * Represents airplane mode state.
     */
    protected enum AirplaneModeState {
        ENABLED, DISABLED;
    }
}
