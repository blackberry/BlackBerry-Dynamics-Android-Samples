package com.good.automated.general.utils.uitools.applicationsettings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import com.good.automated.general.utils.Duration;

/**
 * Performs UI operations related to Application Settings for an Application
 * Should have platform specific implementation.
 * Instantiation should be performed via {@link UiApplicationSettingsManagerFactory}.
 */
public abstract class UiApplicationSettingsManager {

    private static final String TAG = UiApplicationSettingsManager.class.getSimpleName();

    private UiDevice mUiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    protected String selectorTextForStop;

    protected abstract UiSelector locateSelectorForForceStop();

    protected abstract UiSelector locateSelectorForForceStopButton();

    UiApplicationSettingsManager(){

    }
    /**
     * Helper method which launches the app specific settings page within Settings app (which allows for items such as force stop and permission changes)
     */
    public void launchAppSettings(String appPackageName) {

        Context context = InstrumentationRegistry.getTargetContext();

        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + appPackageName));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        context.startActivity(i);

        mUiDevice.waitForIdle(Duration.of(Duration.UI_WAIT));
    }

    /**
     * Method to force stop the application launched from the Application Settings UI.
     *
     * @return true if the application was force stopped successfully
     */
    public boolean forceStop(){

       return selectForceStopOption() && clickOnForceStopButton();
   }

    /**
     * Selects the force stop option from the app settings screen
     *
     * @return true in case if force stop was selected successfully.
     */
   protected boolean selectForceStopOption(){

       try {
           UiObject forceStopOption = mUiDevice.findObject(locateSelectorForForceStopButton());
           if (forceStopOption.isEnabled()) {
               return forceStopOption.click();
           } else {
               Log.e(TAG, "Application already force stopped. Unable to force stop.");
               return false;
           }
       }
       catch(UiObjectNotFoundException cause) {
           Log.e(TAG, "Failed to locate force stop option on app settings UI", cause);
           return false;
       }

   }

   /**
    * Computes the resource Id for the package name provided
    *
    * @return the resource id to locate UiObjects on the screen.
    */
   protected String computeResourceId(String packageName, String aResourceID) {
        return packageName + ":id/" + aResourceID;
    }
    /**
     * Clicks on the force stop button to force stop the app.
     *
     * @return true in case if click was performed successfully.
     */
    protected boolean clickOnForceStopButton(){

       try {
           UiObject forceStopButton = locateForceStopButton();
           return forceStopButton.click();
       }
       catch(UiObjectNotFoundException cause) {
           Log.e(TAG, "Failed to locate force stop option on the alert", cause);
           return false;
       }

   }
    /**
     * Locates the force stop button on the force stop settings alert
     *
     * @return UiObject representing force stop button.
     * @throws UiObjectNotFoundException in case if code failed to locate UiObject
     */
   protected  UiObject locateForceStopButton() throws UiObjectNotFoundException {

       Log.i(TAG, "Locating force stop button");
       UiObject forceStopOkOption = getUiDevice().findObject(locateSelectorForForceStop());
       if (forceStopOkOption != null) {
           Log.d(TAG, "Located object. Text: " + forceStopOkOption.getText());
           return forceStopOkOption;
       }

       throw new UiObjectNotFoundException("Failed to locate force stop button on alert");

   }

   protected UiDevice getUiDevice() {
        return mUiDevice;
    }
}
