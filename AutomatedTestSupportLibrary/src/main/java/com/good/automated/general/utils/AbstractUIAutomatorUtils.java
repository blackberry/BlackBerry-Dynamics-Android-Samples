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

import static android.provider.Settings.ACTION_WIFI_SETTINGS;
import static android.view.KeyEvent.KEYCODE_MENU;
import static androidx.core.content.PermissionChecker.checkSelfPermission;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.good.automated.general.utils.Duration.AUTHORIZE_CALLBACK;
import static com.good.automated.general.utils.Duration.KNOWN_WIFI_CONNECTION;
import static com.good.automated.general.utils.Duration.SCREEN_ROTATION;
import static com.good.automated.general.utils.Duration.SECONDS_10;
import static com.good.automated.general.utils.Duration.UI_ACTION;
import static com.good.automated.general.utils.Duration.UI_WAIT;
import static com.good.automated.general.utils.Duration.WAIT_FOR_SCREEN;
import static com.good.automated.general.utils.Duration.of;

import com.good.automated.general.controls.impl.ControlWrapper;
import com.good.automated.general.controls.impl.RadioButtonImpl;
import com.good.automated.general.helpers.BBDActivationHelper;
import com.good.automated.general.utils.threadsafe.SafeCommandExecutor;
import com.good.automated.general.utils.uitools.networking.UiNetworkManagerFactory;
import com.good.automated.test.screens.BBDPermissionUI;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.app.KeyguardManager;
import android.app.UiAutomation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.json.JSONArray;
import org.json.JSONException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * AbstractUIAutomatorUtils is a helper class that allow us to interact with system settings of OS Android.
 * Most of methods in this class are common for different Android APIs
 *
 * <p>Non-default implementations of some methods have to be overridden in sub-classes
 */
public abstract class AbstractUIAutomatorUtils {

    private static final String TAG = AbstractUIAutomatorUtils.class.getSimpleName();

    protected final String _ID = ":id/";
    protected String packageAndroidSettings = "com.android.settings";
    protected String packageAndroidSystemUI = "com.android.systemui";
    protected String packageAndroid = "android";
    protected String packageCertInstaller = "com.android.certinstaller";

    protected String idAlertTitle = "alertTitle";
    protected String idAlertMessage = "message";

    protected String idRecyclerViewList = "list";
    protected String idButtonRight = "right_button";
    protected String idButtonLeft = "left_button";
    protected String idButton1 = "button1";
    protected String idButton2 = "button2";
    protected String idButton3 = "button3";
    protected String idButtonNext = "next_button";
    protected String idPasswordEntry = "password_entry";
    protected String idLockPassEditText = "lockPassword";
    protected String idEncryptDontRequirePassword = "encrypt_dont_require_password";
    protected String idShowAll = "show_all";
    protected String idDateTimeRadialPicker = "radial_picker";
    protected String idDateTimeMonthView = "month_view";
    protected String idButtonImageNext = "next";
    protected String idButtonImagePrev = "prev";
    protected String idWidgetFrame = "widget_frame";
    protected String idSwitchWidget = "switch_widget";
    protected String idCredentialName = "credential_name";

    protected String textForceStop = "force stop";
    protected String textNotifications = "Notifications";
    protected String textScreenLock = "Screen lock";
    protected String textFingerprintSetup = "fingerprint set up";
    protected String textFingerprint = "Fingerprint";
    protected String textAddFingerprint = "Add fingerprint";
    protected String textButtonNext = "NEXT";
    protected String textButtonConfirm = "CONFIRM";
    protected String textButtonDone = "DONE";
    protected String textSetTime = "Set time";
    protected String textSetDate = "Set date";
    protected String textAutomaticDateTime = "Automatic date & time";
    protected String textEncryptionCredentials = "Encryption & credentials";
    protected String textInstallFromSDCard = "Install from SD card";
    protected String textInstallCertificate = "Install a certificate";
    protected String textShowRoots = "Show roots";
    protected String textDownloads = "Downloads";
    protected String textTrustedCredentials = "Trusted credentials";
    protected String textUSER = "USER";
    protected String textBlackBerryRootCA = "BlackBerry Root CA";
    protected String textUserCredentials = "User credentials";
    protected String textRecent = "Recent";

    protected static UiDevice uiDevice = UiDevice.getInstance(getInstrumentation());
    private GDTestSettings settings;

    private static SafeCommandExecutor safeCommandExecutor = new SafeCommandExecutor();

    public AbstractUIAutomatorUtils() {
        this.uiDevice = UiDevice.getInstance(getInstrumentation());
        this.settings = GDTestSettings.getInstance();
        this.settings.initialize(getInstrumentation().getContext(), getAppPackageName());
    }

    public abstract void launchDateSettings();

    public abstract void launchActionSettings(String action);

    /**
     * Launches System Location settings screen.
     */
    public boolean launchLocationSettings() {
        Intent openSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        Context applicationContext = getInstrumentation().getTargetContext().getApplicationContext();

        // Making sure that device location services intent can be consumed by O.S.
        PackageManager packageManager = applicationContext.getPackageManager();
        if (openSettingsIntent.resolveActivity(packageManager) != null) {
            applicationContext.startActivity(openSettingsIntent);
        } else {
            Log.e(TAG, "Could not resolve location settings activity");
            return false;
        }
        return true;
    }

    public UiDevice getUiDevice() {
        return uiDevice;
    }

    public SafeCommandExecutor getSafeCommandExecutor() {
        return safeCommandExecutor;
    }

    public String executeShellCommand(String command) {
        return getSafeCommandExecutor().executeShellCommand(getUiDevice(), command);
    }

    /**
     * Performs launch of application with flag {@link Intent#FLAG_ACTIVITY_CLEAR_TASK}.
     * Old app activities will be finished
     *
     * @param packageName app package name
     */
    public void launchApp(String packageName) {
        launchAppActivityWithFlag(packageName, Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    /**
     * Performs launch application with flag {@link Intent#FLAG_ACTIVITY_SINGLE_TOP}.
     * Activity will not be launched if it is already running at the top of the history stack
     *
     * @param packageName app package name
     */
    public void launchAppActivityOnTop(String packageName) {
        launchAppActivityWithFlag(packageName, Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }

    /**
     * Launches app main activity with flag.
     * See {@link Intent#setFlags} for a list of all possible flags.
     * @param packageName   app package name
     * @param flags         flags to be set for launching activity
     */
    private void launchAppActivityWithFlag(String packageName, int flags) {
        Context context = getInstrumentation().getTargetContext();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            Log.d(TAG, "Cannot find App with package name: " + packageName);
        }
        intent.addFlags(flags);
        context.startActivity(intent);

        uiDevice.wait(Until.hasObject(By.pkg(packageName)), of(UI_WAIT));

        GDSDKStateReceiver.getInstance();
    }

    /**
     * Performs force stop (via UI) and launch of application by passed native ID.
     *
     * @param appNativeId native id of an app to terminate
     * @return true - if force stop during relaunch was successful / false - otherwise
     */
    public boolean relaunchApp(String appNativeId) {
        return relaunchApp(Boolean.FALSE, appNativeId);
    }

    /**
     * Performs force stop and launch of application by passed native ID.
     *
     * @param immediately pass true to terminate app immediately (with adb) / false - via UI
     * @param appNativeId native id of an app to terminate
     * @return true - if force stop during relaunch was successful / false - otherwise
     */
    public boolean relaunchApp(boolean immediately, String appNativeId) {

        boolean forceStopSuccess = false;

        if (immediately) {
            terminateAppADB(appNativeId);
            Log.d(TAG, "Terminated app " + appNativeId + " with an ADB command.");
        } else {
            forceStopSuccess = forceStopApp(appNativeId);
            Log.d(TAG, "Terminated app " + appNativeId + " via device system UI.");
        }
        waitForUI(Duration.of(Duration.WAIT_FOR_SCREEN));
        launchApp(appNativeId);

        return forceStopSuccess;
    }

    /**
     * Performs application force stop with ADB shell command.
     *
     * @param appNativeId native id of app to stop
     */
    public void terminateAppADB(String appNativeId) {
        getInstrumentation().getUiAutomation()
                .executeShellCommand("am force-stop " + appNativeId);
        // Wait to ensure force stop is finished.
        UIAutomatorUtilsFactory.getUIAutomatorUtils()
                .waitForUI(Duration.of(Duration.WAIT_FOR_SCREEN));
    }

    /**
     * Helper method that return UiObject which was created in .xml file by provided ID.
     *
     * @param packageName container package ID
     * @param id          id of UI element
     * @return UiObject
     */
    public UiObject getUIObjectById(String packageName, String id) {
        return getUIObjectById(packageName, id, Duration.of(UI_WAIT));
    }

    /**
     * Helper method that return UiObject which was created in runtime by provided ID with a delay.
     *
     * @param id    id of UI element
     * @param delay delay to wait for object appearance
     * @return UiObject
     */
    public UiObject getUIObjectById(String id, long delay) {
        long startTime = System.currentTimeMillis();
        long stopTime = startTime + Long.valueOf(delay);

        UiObject uiObject = null;

        do {
            uiObject = uiDevice.findObject(new UiSelector().resourceId(id));
            if (uiObject != null && uiObject.exists()) {
                Log.d(TAG, "UiObject with packageName: " + id + " was found");
                break;
            } else if (stopTime <= System.currentTimeMillis()) {
                Log.d(TAG, "UiObject with packageName: " + id + " wasn't found during " + delay + "ms");
                break;
            }
        }
        while (uiObject == null || !uiObject.exists());

        return uiObject;
    }

    /**
     * Helper method that return UiObject which was created in .xml file by provided ID with a delay.
     *
     * @param packageName container package ID
     * @param id          id of UI element
     * @param delay       delay to wait for object appearance
     * @return UiObject
     */
    public UiObject getUIObjectById(String packageName, String id, long delay) {
        return getUIObjectById(computeResourceId(packageName, id), delay);
    }

    /**
     * Helper method which launches the app under test and waits for it to be on the screen.
     * After launching the app, it also register a broadcast receiver to get GD SDK authorization
     * states notifications such as authorized/locked/wipped
     */
    public void launchAppUnderTest() {
        launchApp(getAppPackageName());
    }

    /**
     * Helper method which launches the Activity under test and waits for it to be on the screen.
     *
     * @param activityClass specific activity class name
     * @param packageName   test application package name
     */
    public void launchSpecificActivity(Class activityClass, String packageName) {
        Context context = getInstrumentation().getTargetContext();
        Intent intent = new Intent(context, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        uiDevice.wait(Until.hasObject(By.pkg(packageName)), Duration.of(UI_WAIT));
        uiDevice.waitForIdle(Duration.of(UI_WAIT));
    }

    /**
     * Perform force kill of an application.
     *
     * @param applicationId ID of app on GC (is set in settings.json)
     * @return true if successful, otherwise false
     *
     * <p>Added second attempt to force stop app for cases when two buttons are swapped
     *
     */
    public boolean forceStopApp(String applicationId) {
        launchAppSettings(applicationId);

        UiObject forceStopButton = findByResourceId(packageAndroidSettings + _ID + idButtonRight);

        if (forceStopButton.exists()) {
            Log.d(TAG, packageAndroidSettings + _ID + idButtonRight
                    + " was found on system UI");
        } else {
            //Possible timing issue in opening system Setting UI
            waitForUI(of(UI_WAIT));
            Log.d(TAG, "Second attempt to find "
                    + packageAndroidSettings + _ID + idButtonRight + " on system UI");
            forceStopButton = findByResourceId(packageAndroidSettings + _ID + idButtonRight);
        }
        try {
            if (forceStopButton.getText().toLowerCase().contains(textForceStop)) {
                //This is classic placing of Force stop button on system UI
                return performForceStopAction(idButtonRight);
            } else {
                //Mirror placing of Force stop button on system UI
                return performForceStopAction(idButtonLeft);
            }
        } catch (UiObjectNotFoundException e) {
            Log.d(TAG, packageAndroidSettings + _ID + idButtonRight
                    + " wasn't found on system UI");
        }
        return false;
    }

    /**
     * Helper method to check if software keyboard is present on screen.
     *
     * @return true if keyboard was shown and button Back was pressed, otherwise false
     * @throws RemoteException catch
     */
    public boolean isKeyboardShown() throws RemoteException {
        return ControlWrapper.isKeyboardShown();
    }

    /**
     * Helper method to check and close software keyboard if it is present on screen in landscape mode.
     *
     * @return true if keyboard was shown and button Back was pressed, otherwise false
     * @throws RemoteException exception
     */
    public boolean hideKeyboard() throws RemoteException {
        if (isKeyboardShown()) {
            Log.d(TAG, "Try to press back button");
            return pressBack();
        }
        Log.e(TAG, "Probably keyboard wasn't shown");
        return false;
    }

    /**
     * Helper method to check and close software keyboard if it is present on screen in landscape mode.
     *
     * @return true if keyboard was shown in landscape mode and button Back was pressed, otherwise false
     * @throws RemoteException exception
     */
    public boolean hideKeyboardInLandscape() throws RemoteException {
        if (!isNaturalOrientation()) {
            Log.d(TAG, "Device is in Landscape mode");
            return hideKeyboard();
        }
        Log.e(TAG, "Device is in Portrait mode");
        return false;
    }

    /**
     * Helper method to determine child count for element. Can be used for counting elements in list.
     *
     * @param packageName application package name
     * @param aResourceID resource ID
     */
    public int listViewSize(String packageName, String aResourceID) {
        String resourceID = computeResourceId(packageName, aResourceID);
        int childCount = 0;

        try {
            return findByResourceId(resourceID).getChildCount();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Emulates force stop action.
     *
     * @param forceStopID id of force stop button
     * @return true if successful, otherwise false
     */
    protected boolean performForceStopAction(String forceStopID) {
        boolean result = clickOnItemWithID(packageAndroidSettings, forceStopID, Duration.of(WAIT_FOR_SCREEN),
                Duration.of(UI_ACTION));
        UiObject uiAlertTitle = findByResourceId(packageAndroid + _ID + idAlertTitle,
                Duration.of(WAIT_FOR_SCREEN));
        UiObject uiAlertMessage = findByResourceId(packageAndroid + _ID + idAlertMessage,
                Duration.of(WAIT_FOR_SCREEN));

        try {
            if (result && (uiAlertMessage.getText().toLowerCase().contains(textForceStop)
                    || uiAlertTitle.getText().toLowerCase().contains(textForceStop))) {
                return clickOnItemWithID(packageAndroid, idButton1, of(WAIT_FOR_SCREEN), of(UI_ACTION));
            } else {
                Log.d(TAG, "Wrong action tried to be performed on UI");
                return false;
            }
        } catch (UiObjectNotFoundException e) {
            Log.d(TAG, "Couldn't find Force Stop text on UI");
            return false;
        }
    }

    /**
     * Helper method which launches the app specific settings page within Settings app
     * (which allows for items such as force stop and permission changes).
     */
    public void launchAppUnderTestSettings() {
        launchAppSettings(getAppPackageName());
    }

    /**
     * Helper method which launches the app specific settings page within Settings app
     * (which allows for items such as force stop and permission changes).
     *
     * @param appPackageName app package name
     */
    public void launchAppSettings(String appPackageName) {
        Context context = getInstrumentation().getTargetContext();

        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + appPackageName));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        context.startActivity(i);

        uiDevice.waitForIdle(of(UI_WAIT));
    }

    /**
     * Helper method to determine if specific text is shown on screen.
     *
     * @param aText text to be found on screen
     * @return true if successful, otherwise false
     */
    public boolean isTextShown(String aText) {
        return isTextShown(aText, of(UI_WAIT));
    }

    /**
     * Helper method to determine if specific text is shown on screen with default timeout.
     *
     * @param aText      text to be found on screen
     * @param totalDelay wait for exist
     * @return true if successful, otherwise false
     */
    public boolean isTextShown(String aText, long totalDelay) {

        UiObject objectContainsText;
        objectContainsText = uiDevice.findObject(new UiSelector().textContains(aText));
        if (objectContainsText.waitForExists(totalDelay)) {
            Log.d(TAG, "Text was found: " + aText);
            return true;
        }
        Log.d(TAG, "Text wasn't found on this screen: " + aText);
        return false;
    }

    public boolean isTextShownMatchingRegex(String aRegex, int aTimeMilliseconds) {

        UiDevice device = UiDevice.getInstance(getInstrumentation());

        UiObject textObject = device.findObject(new UiSelector().textMatches(aRegex));

        textObject.waitForExists(aTimeMilliseconds);

        return textObject.exists();
    }

    /**
     * Helper method to determine if specific description is shown on screen.
     *
     * @param aDesc description to be found on screen
     * @return true if successful, otherwise false
     */
    public boolean isDescriptionShown(String aDesc) {
        return isDescriptionShown(aDesc, of(UI_WAIT));
    }

    /**
     * Helper method to determine if specific description is shown on screen with default timeout.
     *
     * @param aDesc      description to be found on screen
     * @param totalDelay wait for exist
     * @return true if successful, otherwise false
     */
    public boolean isDescriptionShown(String aDesc, long totalDelay) {

        UiObject objectContainsText;
        objectContainsText = uiDevice.findObject(new UiSelector().descriptionContains(aDesc));
        if (objectContainsText.waitForExists(totalDelay)) {
            Log.d(TAG, "Description was found: " + aDesc);
            return true;
        }
        Log.d(TAG, "Description wasn't found on this screen: " + aDesc);
        return false;
    }

    /**
     * Helper method to determine if specific resource ID is shown on screen.
     *
     * @param packageName package name
     * @param aID         text to be found on screen
     * @return true if successful, otherwise false
     */
    public boolean isResourceWithIDShown(String packageName, String aID) {
        return isResourceWithIDShown(packageName, aID, 0);
    }

    /**
     * Helper method to determine if specific resource ID is shown on screen with default timeout.
     *
     * @param packageName package name
     * @param aResourceID id of resource to be found on the screen
     * @param totalDelay  wait for exist
     * @return true if successful, otherwise false
     */
    public boolean isResourceWithIDShown(String packageName, String aResourceID, long totalDelay) {
        String resourceID = computeResourceId(packageName, aResourceID);
        UiObject objectContainsText;
        objectContainsText = uiDevice.findObject(new UiSelector().resourceId(resourceID));
        if (objectContainsText.waitForExists(totalDelay)) {
            Log.d(TAG, "Resource with ID was found: " + resourceID);
            return true;
        }
        Log.d(TAG, "Resource with ID wasn't found on this screen: " + resourceID);
        return false;
    }

    /**
     * Helper method to determine if specific text is shown on the screen.
     *
     * @param appID     application ID
     * @param elementID ID of UI element
     * @param text      text to be searched
     * @return is text found on the screen
     */
    public boolean isElementWithIdContainsText(String appID, String elementID, String text) {
        try {
            UiObject uiObject = getUIObjectById(appID, elementID);
            if (uiObject != null) {
                return uiObject.getText().contains(text);
            }
        } catch (UiObjectNotFoundException e) {
            Log.d(TAG, "UI object not found: " + e.getMessage());
        }
        return false;
    }

    /**
     * Helper method to determine if specific text is shown on screen and click on it without a delay.
     *
     * @param aText text to be found on screen and clicked
     * @return true if successful, otherwise false
     */
    public boolean clickOnItemWithText(String aText) {
        return clickOnItemWithText(aText, 0);
    }

    /**
     * Helper method to determine if specific text is shown on screen with default timeout and click on it.
     *
     * @param aText      text to be found on screen and clicked
     * @param totalDelay wait for exist
     * @return true if successful, otherwise false
     */
    public boolean clickOnItemWithText(String aText, long totalDelay) {

        UiObject objectContainsText;

        objectContainsText = uiDevice.findObject(new UiSelector().text(aText));

        if (objectContainsText.exists() || objectContainsText.waitForExists(totalDelay)) {
            try {
                objectContainsText.click();
                //wait till state will be changed
                if (totalDelay > 0) {
                    objectContainsText.waitUntilGone(of(UI_ACTION));
                }
                Log.d(TAG, "Click on text was performed: " + aText);
                return true;
            } catch (UiObjectNotFoundException e) {
                Log.d(TAG, "Text to be clicked wasn't found on this screen: " + e.getMessage());
                return false;
            }
        }

        Log.d(TAG, "Text to be clicked wasn't found on this screen: " + aText);
        return false;
    }

    /**
     * Helper method to determine if part of specific text is shown on screen and click on it without a delay.
     *
     * @param aText text to be found on screen and clicked
     * @return true if successful, otherwise false
     */
    public boolean clickOnItemContainingText(String aText) {
        return clickOnItemContainingText(aText, 0);
    }

    /**
     * Helper method to determine if part of specific text is shown on screen with default timeout and click on it.
     *
     * @param aText      text to be found on screen and clicked
     * @param totalDelay wait for exist
     * @return true if successful, otherwise false
     */
    public boolean clickOnItemContainingText(String aText, long totalDelay) {

        UiObject objectContainsText;

        objectContainsText = uiDevice.findObject(new UiSelector().textContains(aText));

        if (objectContainsText.exists() || objectContainsText.waitForExists(totalDelay)) {
            try {
                objectContainsText.click();
                //wait till state will be changed
                if (totalDelay > 0) {
                    objectContainsText.waitUntilGone(of(UI_ACTION));
                }
                Log.d(TAG, "Click on text was performed: " + aText);
                return true;
            } catch (UiObjectNotFoundException e) {
                Log.d(TAG, "Text to be clicked wasn't found on this screen: " + e.getMessage());
                return false;
            }
        }

        Log.d(TAG, "Text to be clicked wasn't found on this screen: " + aText);
        return false;
    }

    /**
     * Helper method for click on a element with specified content description.
     *
     * @param text text to search in element's content description
     * @return true if successful, otherwise false
     */
    public boolean clickOnItemWithContentDescriptionText(String text) {
        return clickOnItemWithContentDescriptionText(text, of(WAIT_FOR_SCREEN));
    }

    /**
     * Helper method for click on a element with specified content description.
     *
     * @param text       text to search in element's content description
     * @param totalDelay wait for exist
     * @return true if successful, otherwise false
     */
    public boolean clickOnItemWithContentDescriptionText(String text, long totalDelay) {
        UiObject uiObject = uiDevice.findObject(new UiSelector().descriptionContains(text));

        if (uiObject.waitForExists(totalDelay)) {
            try {
                uiObject.click();
                uiDevice.waitForIdle(of(UI_WAIT));
                Log.d(TAG, "Long click on text was performed: " + text);
                return true;
            } catch (UiObjectNotFoundException e) {
                Log.d(TAG, "Text wasn't found on this screen: " + e.getMessage());
                return false;
            }
        }
        Log.d(TAG, "Text wasn't found on this screen: " + text);
        return false;
    }

    /**
     * Helper method for long tap on a element with specified content description.
     *
     * @param text       text to search in element's content description
     * @param totalDelay wait for exist
     * @return true if successful, otherwise false
     */
    public boolean longTapOnItemWithContentDescriptionText(String text, long totalDelay) {
        UiObject uiObject = uiDevice.findObject(new UiSelector().descriptionContains(text));
        if (uiObject.waitForExists(totalDelay)) {
            try {
                Rect longTapButton = uiObject.getBounds();
                getUiDevice().swipe(longTapButton.centerX(), longTapButton.centerY(), longTapButton.centerX(), longTapButton.centerY(), 400);
                uiDevice.waitForIdle(of(UI_WAIT));
                Log.d(TAG, "Long click on text was performed: " + text);
                return true;
            } catch (UiObjectNotFoundException e) {
                Log.d(TAG, "Text wasn't found on this screen: " + e.getMessage());
                return false;
            }
        }
        Log.d(TAG, "Text wasn't found on this screen: " + text);
        return false;
    }

    /**
     * Helper method for long tap on a element with specified id.
     *
     * On 29 API will always returns false according to this issue: https://issuetracker.google.com/issues/134089827.
     *
     * @param packageName app package name
     * @param aResourceID resource id to be clicked
     * @return true if successful, otherwise false
     */

    public boolean longTapOnItemWithID(String packageName, String aResourceID) {

        UiObject objectWithID = findByResourceId(packageName, aResourceID,
                Duration.of(UI_ACTION));
        if (objectWithID != null) {
            try {
                if (objectWithID.longClick()) {
                    Log.d(TAG, "Object with ID: " + aResourceID + " was long tapped");
                    return true;
                } else {
                    Log.d(TAG, "Object with ID: " + aResourceID + " cannot be long tapped");
                    return false;
                }
            } catch (UiObjectNotFoundException e) {
                Log.d(TAG, "UiObjectNotFoundException: " + e.getMessage());
                return false;
            }
        } else {
            Log.d(TAG, "Object with ID: " + aResourceID + " was not found");
            return false;
        }
    }

    /**
     * Helper method for long tap on a element containing specified text.
     *
     * On 29 API will always returns false according to this issue: https://issuetracker.google.com/issues/134089827.
     *
     * @param packageName app package name
     * @param aText       text of UI Object to be clicked
     * @return true if successful, otherwise false
     */

    public boolean longTapOnItemWithText(String packageName, String aText) {

        UiObject objectContainsText = uiDevice.findObject(new UiSelector().textContains(aText));

        if (objectContainsText != null) {
            try {
                Rect longTapButton = objectContainsText.getBounds();
                getUiDevice().swipe(longTapButton.centerX(), longTapButton.centerY(), longTapButton.centerX(), longTapButton.centerY(), 400);
                uiDevice.waitForIdle(of(UI_WAIT));
                Log.d(TAG, "Long click on text was performed: " + aText);
                return true;
            } catch (UiObjectNotFoundException e) {
                Log.d(TAG, "UiObjectNotFoundException: " + e.getMessage());
                return false;
            }
        } else {
            Log.d(TAG, "Object containing text: " + aText + " was not found");
            return false;
        }
    }

    /**
     * Helper method to click on specific item, specified by resourceID, with a default timeout.
     */
    public boolean clickOnItemWithID(String aResourceID) {
        return clickOnItemWithID(getAppPackageName(), aResourceID, Duration.of(UI_WAIT), Duration.of(UI_ACTION));
    }

    /**
     * Helper method to click on specific item, specified by package name and resourceID, with a default timeout.
     */
    public boolean clickOnItemWithID(String packageName, String aResourceID) {
        return clickOnItemWithID(packageName, aResourceID, Duration.of(UI_WAIT), Duration.of(UI_ACTION));
    }

    /**
     * Helper method to click on specific item, specified by resourceID, with specified timeouts
     */
    public boolean clickOnItemWithID(String aResourceID, int aTimeMSWaitExists, int aTimeMSWaitGone) {
        return clickOnItemWithID(getAppPackageName(), aResourceID, aTimeMSWaitExists, aTimeMSWaitGone);
    }

    /**
     * Helper method to determine if an item with the specific id is shown on screen
     * with default timeout and click on it.
     *
     * @param packageName app package name
     * @param aResourceID resource id to be clicked
     * @return true if successful, otherwise false
     */
    public boolean clickOnItemWithID(String packageName, String aResourceID, long aTimeMSWaitExists,
                                     long... aTimeMSWaitGone) {
        String resourceID = computeResourceId(packageName, aResourceID);
        try {
            UiObject testItem = findByResourceId(resourceID);
            testItem.waitForExists(aTimeMSWaitExists);

            if (testItem.exists()) {
                // The result of this click is not returned because it is unreliable:
                // It often returns false despite the click itself was successful
                testItem.click();

                Log.d(TAG, "Click on resource with id: " + resourceID + " was performed");

                if (aTimeMSWaitGone.length > 0 && testItem.waitUntilGone(aTimeMSWaitGone[0])) {
                    Log.d(TAG, "Resource with id: " + resourceID + " was gone from screen");
                    return true;
                } else if (aTimeMSWaitGone.length == 0) {
                    Log.d(TAG, "Looks like click was performed.");
                    return true;
                }
                return true;
            } else {
                Log.d(TAG, "Resource with id: " + resourceID + " is not found on the screen");
            }
        } catch (UiObjectNotFoundException e) {
            Log.d(TAG, "UiObjectNotFoundException. Resource with id: " + resourceID
                    + " is not found on the screen");
            return false;
        }
        return false;
    }

    /**
     * Helper method that returns isChecked value of UI element.
     *
     * @param packageName container package ID
     * @param id          id of UI element that could be Checked
     * @return isChecked value of UI element
     */
    public boolean isElementCheckedByID(String packageName, String id) {
        try {
            return getUIObjectById(packageName, id).isChecked();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Helper method that grants Runtime Permissions for Android API level 23+.
     * For Android API level 22 and lower permissions are already granted
     *
     * @param permissions list of required permissions
     */
    public void grantPermissionsInRuntime(String[] permissions) {
        for (String permission : permissions) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getInstrumentation().getTargetContext().getPackageName() + " " + permission);
        }

    }

    /**
     * Helper method to toggle a permission switch in App Settings.
     * View Hierarchy can be determined in App Settings by using the UIAutomatorViewer tool in SDK/tools DIR
     */
    public boolean selectPermissionSwitchItemWithDescription(String aDescription) {

        UiScrollable permissionList = new UiScrollable(new UiSelector().className(RecyclerView.class));
        permissionList.waitForExists(Duration.of(UI_WAIT));

        try {
            UiObject object = permissionList.getChildByText(
                    new UiSelector().className("android.widget.RelativeLayout"), aDescription);
            return object.click();
        } catch (UiObjectNotFoundException e) {
            return false;
        }
    }


    /**
     * Method to change the state of a permission swith in the App Settings.
     * Can be determined to navigate to Permission settings directly from the app UI or not
     *
     * @param appPackageName package name of the appUnderTest
     * @param aDescription   title of the permission which state should be changed
     * @param fromAppUI      initial place from where the state should be changed
     *                       true - if user have to change the permission from app UI by clicking
     *                       on 'GO TO SETTINGS' button
     *                       false - if user should go to android app settings to change the permission
     */
    public boolean changePermissionsState(String appPackageName, String aDescription, boolean fromAppUI) {
        if (fromAppUI) {
            new BBDPermissionUI(appPackageName).clickAllow();
            Log.i(TAG, "Navigate to the app" + appPackageName
                    + " settings from app UI. Click on \"GO TO SETTINGS\" button");
        } else {
            launchAppSettings(appPackageName);
            Log.i(TAG, "Navigate to the app" + appPackageName + " Android settings");
        }
        clickOnItemContainingText("Permissions", Duration.of(Duration.ACCEPTING_PASSWORD));
        Log.i(TAG, "Navigate to Permission section of app settings");
        return clickOnItemContainingText(aDescription, Duration.of(Duration.ACCEPTING_PASSWORD));
    }


    /**
     * Toggles specified checkbox to specified state.
     *
     * @param packageName       package name of the radio
     * @param aResourceID       id of the radio
     * @param aTimeMSWaitExists timeout to exist
     * @param checkUnCheck      state to toggle checkbox into
     * @return true - if check was successful
     * false - otherwise
     */
    public boolean setCheckBox(String packageName, String aResourceID, long aTimeMSWaitExists,
                               boolean checkUnCheck) {

        String resourceID = computeResourceId(packageName, aResourceID);
        UiObject checkBox = findByResourceId(resourceID);
        checkBox.waitForExists(aTimeMSWaitExists);

        if (checkBox.exists()) {
            try {
                if (checkBox.isChecked() != checkUnCheck) {
                    checkBox.click();
                    if (checkBox.isChecked() == checkUnCheck) {
                        Log.d(TAG, "Checkbox with id: " + resourceID + " was checked");
                        return true;
                    } else {
                        Log.d(TAG, "Checkbox with id: " + resourceID + " was not checked");
                    }
                } else {
                    Log.d(TAG, "Checkbox is already " + (checkUnCheck ? "checked." : "unchecked."));
                    return true;
                }

            } catch (UiObjectNotFoundException e) {
                Log.d(TAG, "UiObjectNotFoundException. Resource with id: " + packageName
                        + " is not found on the screen");
            }
        } else {
            Log.d(TAG, "Required checkbox (" + resourceID + ") does not exists on the screen.");
        }

        return false;
    }

    /**
     * Checks specified radio button.
     *
     * @param packageName       package name of the radio
     * @param aResourceID       id of the radio
     * @param aTimeMSWaitExists timeout to exist
     * @return true - if check was successful / false - otherwise
     */
    public boolean setRadio(String packageName, String aResourceID, long aTimeMSWaitExists) {

        String resourceID = computeResourceId(packageName, aResourceID);
        UiObject radio = findByResourceId(resourceID);
        radio.waitForExists(aTimeMSWaitExists);

        if (radio.exists()) {
            try {
                if (radio.isChecked()) {
                    Log.d(TAG, "Radio button with id: " + resourceID + " was already checked");
                    return true;
                } else {
                    radio.click();
                    if (radio.isChecked()) {
                        Log.d(TAG, "Radio button with id: " + resourceID + " was checked");
                        return true;
                    }
                }
            } catch (UiObjectNotFoundException e) {
                Log.d(TAG, "UiObjectNotFoundException. Resource with id: " + packageName
                        + " is not found on the screen");
                return false;
            }
        } else {
            Log.d(TAG, "Required radio button (" + resourceID + ") does not exists on the screen.");
        }
        return false;
    }

    public boolean openRecentApps() {
        try {
            return uiDevice.pressRecentApps();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Could not open recent apps");
        return false;
    }

    /**
     * Press key by its code.
     *
     * @param keyCode - key code {@link KeyEvent}
     * @return true if operation successful, false otherwise
     */
    public boolean pressKeyCode(int keyCode) {
        return uiDevice.pressKeyCode(keyCode);
    }

    /**
     * Press key by its code with passed meta.
     *
     * @param keyCode - key code {@link KeyEvent}
     * @return true if operation successful, false otherwise
     */
    public boolean pressKeyCode(int keyCode, int meta) {
        return uiDevice.pressKeyCode(keyCode, meta);
    }

    /**
     * Helper method which presses HOME.
     */
    public void pressHome() {
        pressHome(of(UI_WAIT));
    }

    /**
     * Helper method which presses HOME.
     *
     * @param delay delay after pressing Home
     */
    public void pressHome(long delay) {

        // Simulate a short press on the HOME button to ensure we always start from a known State
        uiDevice.pressHome();
        waitForUI(delay);
    }

    /**
     * Helper method which presses BACK.
     */
    public boolean pressBack() {

        // Simulate a short press on the BACK button to ensure we always start from a known State
        boolean result = uiDevice.pressBack();
        waitForUI(of(UI_WAIT));
        if (result) {
            Log.d(TAG, "Press Back action was successfully performed");
            return true;
        }
        Log.d(TAG, "Press Back action was failed");
        return false;
    }

    /**
     * Helper method to get App Target API level.
     */
    public int getAppTargetAPILevel() {
        return getInstrumentation().getTargetContext().getApplicationInfo().targetSdkVersion;
    }

    /**
     * @param aText task with specific text, to be opened
     * @return true if action successfully performed, otherwise false
     */
    public boolean openTaskWithTextInRecentApps(String aText) {
        try {
            UiObject recentApp = findTaskWithTextInRecentApps(aText);
            return recentApp != null && recentApp.click();

        } catch (UiObjectNotFoundException e) {
            Log.d(TAG, "UiObjectNotFoundException. Recent app with text: " + aText
                    + " is not found on the screen");
            return false;
        }
    }

    /**
     * Helper method to enter text into screen element which belongs to a certain class.
     */
    public boolean enterTextScreenWithClass(String aClass, String aText) {

        UiObject textScreen = uiDevice.findObject(new UiSelector().className(aClass));

        if (textScreen.waitForExists(Duration.of(SECONDS_10))) {
            try {
                textScreen.setText(aText);
            } catch (UiObjectNotFoundException e) {
                Log.e(TAG, "Failed to enter text into element: " + aClass);
                return false;
            }
            return true;
        }
        Log.e(TAG, "Element for a class " + aClass + " was not found");
        return false;
    }


    /**
     * Remove the task from the recent apps list.
     *
     * @param aText task with specific text, to be removed
     * @return true if action successfully performed, otherwise false
     */
    public boolean removeTaskWithTextInRecentApps(String aText) {
        return swipeTaskWithTextInRecentApps(aText);
    }

    /**
     * Remove (swipe) the task from the recent apps list.
     *
     * @param aText task with specific text, to be removed
     * @return true if action successfully performed, otherwise false
     * @deprecated starting from 26 API {@link com.good.automated.general.utils.impl.UIAutomatorUtilsAndroidO26#removeTaskWithTextInRecentApps(String)} should be used
     */
    @Deprecated
    public boolean swipeTaskWithTextInRecentApps(String aText) {
        try {
            UiObject recentApp = findTaskWithTextInRecentApps(aText);

            // The number of step is set to 10 after a few attempts
            // to balance swipe speed and distance. No better rationale for it.
            return recentApp != null && recentApp.swipeLeft(10);

        } catch (UiObjectNotFoundException e) {
            Log.d(TAG, "UiObjectNotFoundException. Recent app with text: " + aText
                    + " is not found on the screen");
            return false;
        }
    }

    /**
     * Helper method for GD Wearable App provisioning.
     */
    public boolean provisionWearableGDApp() {

        boolean success = false;

        // Initial screen should prompt user to start activation
        if (isScreenShown("gd_button_start_activation")) {

            success = clickOnItemWithID("gd_button_start_activation");

            // After clicking on start activation button Activation should start, wait 30seconds for user validation
            if (success && isScreenShown(getAppPackageName(), idButton1, 30000)) {

                success = clickOnItemWithID(idButton1);
                success = success && isScreenShown(getAppPackageName(), "gd_activation_complete", 30000);

            }
        }
        return success;
    }

    /**
     * Finds and returns the UiObject corresponding to the task matching the text.
     *
     * @param aText task with specific text
     * @return the UiObject corresponding to the task matching the text
     * @throws UiObjectNotFoundException in case {@link UiObject} with specified text was not found
     */
    protected abstract UiObject findTaskWithTextInRecentApps(String aText) throws UiObjectNotFoundException;


    public String computeResourceId(String packageName, String aResourceID) {
        return packageName + _ID + aResourceID;
    }

    /**
     * Helper method which gets an object by resourceID.
     *
     * @param resourceID id of view to match
     * @return matched object
     */
    public UiObject findByResourceId(String resourceID) {
        Log.d(TAG, "Finding UiObject with resourceID: " + resourceID);
        return uiDevice.findObject(new UiSelector().resourceId(resourceID));
    }

    /**
     * Helper method which gets an object by resourceID which was created in runtime.
     *
     * @param resourceID id of view to match
     * @param delay      time to wait for object appearance
     * @return matched object
     */
    public UiObject findByResourceId(String resourceID, long delay) {
        UiObject uiObject = findByResourceId(resourceID);
        if (uiObject.waitForExists(delay)) {
            Log.d(TAG, "UiObject with resourceID: " + resourceID + " was found");
            return uiObject;
        }
        Log.d(TAG, "UiObject with resourceID: " + resourceID + " wasn't found");
        return null;
    }

    /**
     * Helper method which gets an object by resourceID which was created in .xml file.
     *
     * @param packageName of app which object you need to get
     * @param resourceID  id of view to match
     * @param delay       time to wait for object appearance
     * @return matched object
     */
    public UiObject findByResourceId(String packageName, String resourceID, long delay) {
        return findByResourceId(computeResourceId(packageName, resourceID), delay);
    }

    /**
     * Helper method that returns the numeric id of a resource in the current application,
     * provided the resource name.
     *
     * @param aResourceIDName the resource name, uniquely identifying the resource
     * @return the numeric id of the resource
     */
    public int getResourceID(String aResourceIDName) {
        return getResourceID(getAppPackageName(), aResourceIDName);
    }

    /**
     * Helper method that returns the numeric id of a resource in the package specified,
     * provided the resource name.
     *
     * @param packageName     the package name where to look the resource up
     * @param aResourceIDName the resource name, uniquely identifying the resource
     *                        in scope of the package name
     * @return the numeric id of the resource
     */
    public int getResourceID(String packageName, String aResourceIDName) {
        return getInstrumentation().getTargetContext().getResources().getIdentifier(aResourceIDName, "id", packageName);
    }

    /**
     * Helper method which wakes up device if needed.
     */
    public void wakeUpDeviceIfNeeded() {
        try {
            if (!uiDevice.isScreenOn()) {
                uiDevice.wakeUp();
            }
            uiDevice.pressKeyCode(KEYCODE_MENU);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param scrollableContainerId id of container to be scrolled.
     * @param aText                 text to scroll to
     * @return true if text found, otherwise false
     */
    public boolean scrollToText(String scrollableContainerId, String aText) {
        String scrollableResId = scrollableContainerId;
        UiSelector scrollableSelector = new UiSelector().resourceId(scrollableResId);
        UiScrollable scrollable = new UiScrollable(scrollableSelector);
        try {
            UiSelector itemWithTextSelector = new UiSelector().text(aText);
            UiObject item = scrollable.getChildByText(itemWithTextSelector, aText, true);
            return item.waitForExists(of(UI_WAIT));
        } catch (UiObjectNotFoundException e) {
            Log.d(TAG, "UiObjectNotFoundException: " + e.getMessage());
            return false;
        }
    }


    /**
     * Scrolls to an element with specified id.
     *
     * @param scrollableContainerId id of a scrollable element
     * @param elementId             id of an element to search for
     * @return true - if scroll was performed successfully / false - otherwise
     */
    public boolean scrollToTheElementWithId(String scrollableContainerId, String elementId) {
        UiSelector scrollableSelector = new UiSelector().resourceId(scrollableContainerId);
        return scrollToTheElementWithId(scrollableSelector, elementId);
    }

    /**
     * Scrolls to an element with specified id.
     *
     * @param scrollableSelector UiSelector object
     * @param elementId          id of an element to search for
     * @return true - if scroll was performed successfully / false - otherwise
     */
    public boolean scrollToTheElementWithId(UiSelector scrollableSelector, String elementId) {
        UiSelector elementSelector = new UiSelector().resourceId(elementId);
        UiScrollable scrollable = new UiScrollable(scrollableSelector);

        try {
            return scrollable.scrollIntoView(elementSelector);
        } catch (UiObjectNotFoundException e) {
            Log.d(TAG, "UiObjectNotFoundException: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper method which handles System Dialogues which may be showing and thus impact tests.
     */
    public void acceptSystemDialogues() {
        acceptSystemWelcomeMessageIfShown();
        cancelSystemCrashDialogueIfShown();
    }

    /**
     * Helper method which cancels a system error message if shown
     * (can be at start of emulator boot if part of system has crashed).
     */
    public void cancelSystemCrashDialogueIfShown() {
        // We don't wait for long duration, if it is showing then deal with it, otherwise move on
        if (isTextShown("has stopped", of(WAIT_FOR_SCREEN))) {
            if (!clickOnItemWithText("OK", of(UI_WAIT))) {
                clickOnItemContainingText("Close app", of(UI_WAIT));
            }
        }
    }

    /**
     * Helper method which accepts and dismisses System Welcome message
     * (can be at first boot of emulator or device).
     */
    public void acceptSystemWelcomeMessageIfShown() {

        // We don't wait for long duration, if it is showing then deal with it, otherwise move on
        if (isTextShown("Welcome", of(WAIT_FOR_SCREEN))) {
            clickOnItemWithText("GOT IT", of(UI_WAIT));
        }
    }

    /**
     * Helper method to get app under test Package Name.
     */
    public String getAppPackageName() {
        return getInstrumentation().getTargetContext().getPackageName();
    }

    /**
     * Helper method to get app Package Name that is shown in foreground.
     */
    public String getAppPackageNameInForeground() {
        return UiDevice.getInstance(getInstrumentation()).getCurrentPackageName();
    }

    /**
     * Helper method that checks whether any of the elements passed in input is displayed on screen.
     *
     * @param appID         package name of the app
     * @param uiElementsMap list of IDs of ui elements
     * @return the string id if the element is found, null otherwise
     */
    @Deprecated
    public String getDisplayedComponentOnTheScreen(String appID, List<String> uiElementsMap) {
        return getUiElementShown(appID, uiElementsMap);
    }

    /**
     * Helper method that checks whether any of the elements passed in input is displayed on screen.
     *
     * @param packageName package name of the app
     * @param uiElements  list of IDs of ui elements
     * @return the string id if the element is found, null otherwise
     */
    public String getUiElementShown(String packageName, List<String> uiElements) {
        UiObject ob;
        for (String res : uiElements) {
            ob = findByResourceId(computeResourceId(packageName, res));
            if (ob.exists()) {
                return res;
            }
        }
        return null;
    }

    /**
     * @param packageName package to work with
     */
    public void initialiseSettings(String packageName) {
        settings.initialize(getInstrumentation().getContext(), packageName);
    }

    /**
     * @param packageName package to be provisioned
     * @return an email or userId that can be used for provision of an application after
     * installation or during unlock as String
     */
    public String getProvisionLogin(String packageName) {
        return settings.getAppProvisionEmail(packageName);
    }

    /**
     * @param packageName package to be provisioned
     * @return the access key that can be used for provision of an application after installation as String
     */
    public String getAccessKey(String packageName) {
        return settings.getAppProvisionAccessKey(packageName);
    }

    /**
     * @param packageName unlock password for app under test
     * @return the password for an application as String
     */
    public String getAppProvisionPassword(String packageName) {
        return settings.getAppProvisionPassword(packageName);
    }

    /**
     * @param packageName unlock key for app under test
     * @return the unlock key that can be used for provision the application if needed
     * after the initial provision as String
     */
    public String getAppUnlockKey(String packageName) {
        return GDTestSettings.getInstance().getAppUnlockKey(packageName);
    }

    /**
     * Overrides default credentials
     *
     * @throws JSONException exception if json is malformed
     * @param credentials credentials to be used instead those from test launcher
     *
     * JSONArray should be provided in the next format
     * [
     *    {
     *      "GD_TEST_PROVISION_EMAIL": "user@email.com",
     *      "GD_TEST_PROVISION_ACCESS_KEY": "puam3mr2235xyo1",
     *      "GD_TEST_PROVISION_CONFIG_NAME": "com.good.gd.example.sample1",
     *      "GD_TEST_PROVISION_PASSWORD": "abcd"
     *    },
     *    {
     *      //
     *    }
     * ]
     */
    public void overrideActivationCredentials(JSONArray credentials) throws JSONException {
        settings.overrideActivationCredentials(credentials);
    }

    /**
     * Wrapper method for uiautomator internal wait realisation.
     *
     * @param delay waits for UI
     */
    public void waitForUI(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper method which checks that GD has sent the  GD Authorized callback
     * (which needs to be sent before app code can run).
     */
    public boolean checkGDAuthorized() {

        boolean ret = GDSDKStateReceiver.getInstance().checkAuthorized();

        if (ret) {
            //If already authorized return immediately
            Log.d(TAG, "checkGDAuthorized: already TRUE");
            return true;
        }

        // If we aren't already authorized we wait up to 10secs for auth change to occur
        // (i.e. if we have just logged in or finished activation)
        // We are explicitly  waiting for 10 seconds here, not the Duration.of(Duration.UI_WAIT)
        GDSDKStateReceiver.getInstance().waitForAuthorizedChange(of(AUTHORIZE_CALLBACK));

        ret = GDSDKStateReceiver.getInstance().checkAuthorized();
        Log.d(TAG, "checkGDAuthorized: finished waiting result = " + ret);

        // This time we return value we receive
        return ret;

    }

    /**
     * Helper method to get application version name.
     *
     * @param packageName package name of application
     * @return application version name (e.g. 1.4.0.0)
     */
    public String getAppVersionName(String packageName) {
        String versionName = "";
        try {
            versionName = getInstrumentation()
                    .getTargetContext().getPackageManager().getPackageInfo(
                            packageName, PackageManager.GET_SERVICES).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "Could not find specified package");
        }
        return versionName;
    }

    public boolean waitUntilTextGoneFormScreen(String sText, long timeout) {
        long i = timeout / of(UI_ACTION);
        for (long j = 0; j < i; j++) {
            waitForUI(of(UI_ACTION));
            if (!getUiDevice().findObject(new UiSelector().text(sText)).exists()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method to determine if a screen (Either GD screen or App UI screen)
     * containing specified ResourceID is shown (with default timeout and default current app).
     */
    public boolean isScreenShown(String aResourceID) {
        return isScreenShown(getAppPackageName(), aResourceID, Duration.of(UI_ACTION));
    }

    /**
     * Helper method to determine if a screen in the provided app
     * (Either GD screen or App UI screen) containing specified ResourceID is shown (with default timeout).
     */
    public boolean isScreenShown(String packageName, String aResourceID) {
        return isScreenShown(packageName, aResourceID, Duration.of(UI_ACTION));
    }

    /**
     * Helper method to determine if a screen in the provided app
     * (Either GD screen or App UI screen) containing specified ResourceID is shown waiting a certain timeout.
     *
     * @param packageName package name of app under test
     * @param aResourceID id to be found on UI
     * @param delay       delay to find element on the screen
     */
    public boolean isScreenShown(String packageName, String aResourceID, long delay) {
        String resourceID = computeResourceId(packageName, aResourceID);
        Log.d(TAG, "Try to find resource with ID: " + resourceID);
        UiObject testScreen = findByResourceId(resourceID);

        if (testScreen.waitForExists(delay)) {
            Log.d(TAG, "Resource is found! ID: " + resourceID);
            return true;
        } else {
            Log.d(TAG, "Resource not found! ID: " + resourceID);
            return false;
        }
    }

    /**
     * Scrolls to the end of the scrollable.
     *
     * @param scrollableContainerID ID of a scrollable
     * @return true - if scrolled
     * false - otherwise
     */
    public boolean scrollToTheEnd(String scrollableContainerID) {
        String scrollableResId = scrollableContainerID;
        UiSelector scrollableSelector = new UiSelector().resourceId(scrollableResId);
        UiScrollable scrollable = new UiScrollable(scrollableSelector);

        try {
            return scrollable.scrollToEnd(scrollable.getMaxSearchSwipes());
        } catch (UiObjectNotFoundException e) {
            Log.d(TAG, "UiObjectNotFoundException: " + e.getMessage());
            return false;
        }
    }

    /**
     * Scrolls to the beginning of the scrollable.
     *
     * @param scrollableContainerID ID of a scrollable
     * @return true - if scrolled
     * false - otherwise
     */
    public boolean scrollToTheBeginning(String scrollableContainerID) {
        String scrollableResId = scrollableContainerID;
        UiSelector scrollableSelector = new UiSelector().resourceId(scrollableResId);
        UiScrollable scrollable = new UiScrollable(scrollableSelector);

        try {
            return scrollable.scrollToBeginning(scrollable.getMaxSearchSwipes());
        } catch (UiObjectNotFoundException e) {
            Log.d(TAG, "UiObjectNotFoundException: " + e.getMessage());
            return false;
        }
    }

    /**
     * Swipe on UI element from the top.
     *
     * @param uiElementId UI element ID
     * @return true if swipe was successful, false otherwise
     */
    public boolean swipe(String uiElementId) {
        UiSelector uiElement = new UiSelector().resourceId(uiElementId);
        UiObject uiObject = uiDevice.findObject(uiElement);
        uiObject.waitForExists(of(UI_ACTION));
        try {
            Rect bound = uiObject.getBounds();
            return uiDevice.swipe(bound.centerX(), bound.top, bound.centerX(), bound.centerY(), 50);

        } catch (UiObjectNotFoundException e) {
            Log.d(TAG, "UiObjectNotFoundException: " + e.getMessage());
            return false;
        }
    }

    /**
     * Turns device screen off.
     * Works properly on API 23.
     * May mulfunction on higher API levels.
     */
    public void turnScreenOnOff() {
        uiDevice.pressKeyCode(KeyEvent.KEYCODE_POWER);
        waitForUI(of(WAIT_FOR_SCREEN));
    }

    /**
     * Helper method which locks device.
     */
    public void lockDevice() {
        // Simulate a lock of the device
        uiDevice.pressKeyCode(KeyEvent.KEYCODE_SLEEP);
    }

    /**
     * Helper method which sets device password.
     * Works only for pure Android UI
     */
    public boolean setDevicePassword(String devicePassword) {
        return setDevicePasswordOrPIN("Password", devicePassword);
    }

    /**
     * Helper method which sets device PIN.
     * Works only for pure Android UI
     */
    public boolean setDevicePIN(String devicePIN) {
        return setDevicePasswordOrPIN("PIN", devicePIN);
    }

    /**
     * Helper method which sets device password/PIN.
     * By default it configured to support 23 API level
     * Works only for pure Android UI
     */
    protected boolean setDevicePasswordOrPIN(String passwordPIN, String devicePasscode) {
        Log.d(TAG, "Default setting device PIN or Password");
        String setupPasswordPinText = "Choose your " + passwordPIN;
        String confirmYourPasswordPinText = "Confirm your " + passwordPIN;
        String completeToSetPasswordPINButton = idButtonNext;

        return setDevicePasswordOrPIN(passwordPIN, devicePasscode, setupPasswordPinText, confirmYourPasswordPinText,
                completeToSetPasswordPINButton);
    }

    /**
     * Works only for pure Android UI.
     *
     * @param passwordPIN                    what should be selected PIN or Password
     * @param devicePasscode                 device password or PIN
     * @param setupPasswordPinText           helper text
     * @param confirmYourPasswordPinText     helper text
     * @param completeToSetPasswordPINButton complete setup PIN or Password
     * @return true if PIN or Password was successfully set, otherwise false
     */
    protected final boolean setDevicePasswordOrPIN(String passwordPIN,
                                                   String devicePasscode,
                                                   String setupPasswordPinText,
                                                   String confirmYourPasswordPinText,
                                                   String completeToSetPasswordPINButton) {
        openSecuritySettings();

        if (isDevicePasswordSet()) {
            return true;
        }

        if (!isTextShown(textScreenLock, of(UI_WAIT))) {
            Log.d(TAG, "\"Screen lock\" screen is not shown");
            return false;
        }

        if (clickOnItemWithText(textScreenLock, Duration.of(WAIT_FOR_SCREEN))) {
            if (clickOnItemWithText(passwordPIN, Duration.of(WAIT_FOR_SCREEN))) {
                //Some device/emulators might have intermediate screen before Password/PIN set.
                if (isTextShown(setupPasswordPinText)
                        || (clickOnItemWithID(packageAndroidSettings, idEncryptDontRequirePassword,
                        Duration.of(WAIT_FOR_SCREEN), Duration.of(UI_WAIT))
                        && (clickOnItemWithID(packageAndroidSettings, idButtonNext,
                        Duration.of(WAIT_FOR_SCREEN), Duration.of(UI_WAIT))
                        || isTextShown(setupPasswordPinText)))) {
                    if (enterTextToItemWithID(packageAndroidSettings, idPasswordEntry, devicePasscode)
                            && ((clickOnItemWithID(packageAndroidSettings, idButtonNext,
                            Duration.of(WAIT_FOR_SCREEN), Duration.of(UI_WAIT))
                            || clickOnItemContainingText(textButtonNext, Duration.of(UI_WAIT)))
                            || isTextShown(confirmYourPasswordPinText))) {
                        if (enterTextToItemWithID(packageAndroidSettings, idPasswordEntry, devicePasscode)
                                && ((clickOnItemWithID(packageAndroidSettings, idButtonNext,
                                Duration.of(WAIT_FOR_SCREEN), Duration.of(UI_WAIT))
                                || clickOnItemContainingText(textButtonConfirm, Duration.of(UI_WAIT)))
                                || isTextShown(textNotifications))) {
                            if (setRadio(packageAndroidSettings, idDateTimeRadialPicker, Duration.of(WAIT_FOR_SCREEN))
                                    && clickOnItemWithID(packageAndroidSettings, completeToSetPasswordPINButton,
                                    Duration.of(WAIT_FOR_SCREEN), Duration.of(UI_WAIT))) {
                                Log.d(TAG, "Password was successfully set: " + devicePasscode);
                                return true;
                            }
                            if (isTextShown(textButtonDone) && clickOnItemContainingText(textButtonDone)) {
                                Log.d(TAG, "Password was successfully set: " + devicePasscode);
                                return true;
                            }
                            Log.d(TAG, "Couldn't complete to set of device PIN or Password. Was left default value");
                            return true;
                        }
                        Log.d(TAG, textNotifications + " screen is not shown. Couldn't enter text into field with id: "
                                + packageAndroidSettings + _ID + "show_all");
                        return false;
                    }
                    Log.d(TAG, "\"Choose your PIN\" screen is not shown. Couldn't enter password into field with id: "
                            + packageAndroidSettings + _ID + idPasswordEntry);
                    return false;
                }
                Log.d(TAG, idEncryptDontRequirePassword + " screen is not shown.");
                return false;
            }
            Log.d(TAG, "\"Security\" screen is not shown ");
            return false;
        }
        Log.d(TAG, "Probably \"Security\" screen is not shown. Couldn't click on \"Screen lock\" text");
        return false;
    }

    /**
     * Helper method which removes device PIN.
     * By default it configured to support 23 API level and pure Android UI.
     */
    public boolean removeDevicePIN(String devicePIN) {
        return removeDevicePasswordOrPIN(devicePIN);
    }

    /**
     * Helper method which removes device Password.
     * By default it configured to support 23 API level and pure Android UI.
     */
    public boolean removeDevicePassword(String devicePassword) {
        return removeDevicePasswordOrPIN(devicePassword);
    }

    /**
     * Helper method which removes device password/PIN.
     * By default it configured to support 23 API level and pure Android UI.
     */
    protected boolean removeDevicePasswordOrPIN(String devicePasscode) {
        if (!isDevicePasswordSet()) {
            return true;
        }

        openSecuritySettings();

        if (!isTextShown(textScreenLock, Duration.of(UI_WAIT))) {
            Log.i(TAG, String.format("Couldn't find %s text. Will try to scroll to it.", textScreenLock));
            scrollToText(packageAndroidSettings + _ID + idRecyclerViewList, textScreenLock);
        }

        if (clickOnItemWithText(textScreenLock, of(UI_WAIT))) {
            if (enterTextToItemWithID(packageAndroidSettings, idPasswordEntry, devicePasscode)
                    && clickKeyboardOk()) {
                if (clickOnItemWithText("None", of(WAIT_FOR_SCREEN))
                        && clickOnItemWithID(packageAndroid, idButton1, of(WAIT_FOR_SCREEN), of(UI_ACTION))) {
                    Log.d(TAG, "Device password was successfully removed");
                    return true;
                }
                Log.d(TAG, "\"Choose screen lock\" screen is not shown");
                return false;
            }
            Log.d(TAG, "Cannot enter device password");
            return false;
        }
        Log.d(TAG, "Probably \"Security\" screen is not shown. Couldn't click on \"Screen lock\" text");
        return false;
    }

    /**
     * @return true if click was performed successfully, otherwise false
     */
    public boolean clickKeyboardOk() {
        if (uiDevice.pressKeyCode(KeyEvent.KEYCODE_ENTER)) {
            uiDevice.waitForIdle(of(UI_WAIT));
            return true;
        }
        return false;
    }

    /**
     * @param packageName package name
     * @param aResourceID resource id
     * @param textToEnter text to be entered in specified resource id
     * @return true if text was entered, otherwise false
     */
    public boolean enterTextToItemWithID(String packageName, String aResourceID, String textToEnter) {
        try {
            hideKeyboardInLandscape();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        String resourceID = computeResourceId(packageName, aResourceID);
        UiObject testItem = findByResourceId(resourceID);
        try {
            if (testItem.waitForExists(of(SECONDS_10))) {
                testItem.legacySetText(textToEnter);
                Log.d(TAG, "Text: \"" + textToEnter + "\" was entered");
                if (testItem.getText() != null) {
                    Log.d(TAG, "Confirmation. Text: \"" + textToEnter + "\" was entered");
                    return true;
                } else {
                    Log.d(TAG, "Field is empty. ResourceID: " + resourceID);
                    return false;
                }
            } else {
                Log.d(TAG, "resourceID: " + resourceID + " doesn't exist");
                return false;
            }
        } catch (UiObjectNotFoundException e) {
            Log.d(TAG, "Couldn't enter text to UI element with ID: " + resourceID
                    + " UiObjectNotFoundException: " + e.getMessage());
            return false;
        }
    }

    public boolean enterTextToItemWithText(String text, String textToEnter) {
        return enterTextToItemWithText(text, textToEnter, of(WAIT_FOR_SCREEN));
    }

    public boolean enterTextToItemWithText(String text, String textToEnter, long delay) {
        try {
            hideKeyboardInLandscape();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Finding UiObject with text: " + text);

        UiObject uiObject = uiDevice.findObject(new UiSelector().text(text));

        if (uiObject.waitForExists(delay)) {
            Log.d(TAG, "UiObject with text: " + text + " was found");
            try {
                return uiObject.setText(textToEnter);
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
                Log.d(TAG, "Text " + textToEnter + " wasn't entered to UiObject with text: " + text);
            }
        }
        Log.d(TAG, "UiObject with text: " + text + " wasn't found");
        return false;
    }

    public boolean enterTextToItemWithDescription(String textDescription, String textToEnter, long
            delay) {
        Log.d(TAG, "Finding UiObject with text: " + textDescription);

        UiObject uiObject = uiDevice.findObject(new UiSelector().descriptionContains(textDescription));

        if (uiObject.waitForExists(delay)) {
            Log.d(TAG, "UiObject with text: " + textDescription + " was found");
            try {
                return uiObject.setText(textToEnter);
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
                Log.d(TAG, "Text " + textToEnter + " wasn't entered to UiObject with text: " + textDescription);
            }
        }
        Log.d(TAG, "UiObject with text: " + textDescription + " wasn't found");
        return false;
    }

    /**
     * @param packageName package name
     * @param aResourceID resource id
     * @return true if text was erased, otherwise false
     */
    public boolean eraseTextFieldWithID(String packageName, String aResourceID) {
        try {
            hideKeyboardInLandscape();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        String resourceID = computeResourceId(packageName, aResourceID);
        UiObject testItem = findByResourceId(resourceID);
        try {
            if (testItem.waitForExists(of(UI_WAIT))) {
                testItem.clearTextField();
                return true;
            } else {
                Log.d(TAG, "resourceID: " + resourceID + " doesn't exist");
                return false;
            }
        } catch (UiObjectNotFoundException e) {
            Log.d(TAG, "Couldn't erase text from UI element with ID: " + resourceID + " Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper method which opens device security settings UI.
     */
    protected void openSecuritySettings() {
        openSpecifiedSettings(Settings.ACTION_SECURITY_SETTINGS);
    }

    /**
     * Helper method which opens system settings UI.
     */
    protected void openSystemSettings() {
        openSpecifiedSettings(Settings.ACTION_SETTINGS);
    }

    /**
     * Helper method which opens system settings UI.
     */
    protected void openDisplaySettings() {
        openSpecifiedSettings(Settings.ACTION_DISPLAY_SETTINGS);
    }

    /**
     * Helper method which opens system settings UI.
     */
    protected void openDeviceInfoSettings() {
        openSpecifiedSettings(Settings.ACTION_DEVICE_INFO_SETTINGS);
    }

    /**
     * Method which opens specific device UI by its intent ID.
     *
     * @param settingsToOpen settings to be opened
     */
    private void openSpecifiedSettings(String settingsToOpen) {
        Context context = getInstrumentation().getTargetContext();

        final Intent i = new Intent();
        i.setAction(settingsToOpen);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        context.startActivity(i);

        uiDevice.waitForIdle(of(UI_WAIT));
    }

    /**
     * Enum with available sleep times for emulator.
     */
    public enum SleepTime {
        SECONDS_15("15 seconds"),
        SECONDS_30("30 seconds"),
        MINUTES_1("1 minutes"),
        MINUTES_2("2 minutes"),
        MINUTES_5("5 minutes"),
        MINUTES_10("10 minutes"),
        MINUTES_30("30 minutes");

        private final String time;

        SleepTime(final String time) {
            this.time = time;
        }

        public String getTime() {
            return time;
        }
    }

    /**
     * Helper method which sets sleep time for device.
     *
     * @param textTime available time to be set
     */
    public boolean setSleepTime(SleepTime textTime) {
        openDisplaySettings();

        return clickOnItemContainingText("Sleep", Duration.of(WAIT_FOR_SCREEN))
                && clickOnItemContainingText(textTime.getTime(), Duration.of(WAIT_FOR_SCREEN));
    }

    /**
     * Helper method which shows UI that asks to scan fingerprint.<p>
     * After calling this method, device/emulator will expect to scan your fingerprint
     * To simulate fingerprint tauch on emulator you have to execute command:
     * adb -e emu finger touch 11551155
     */
    public boolean getFingerprintScreen(String devicePass) {
        String findTheSensor = "Find the sensor";
        String fingerprintNextButton = idButtonNext;
        return getFingerprintScreen(devicePass, fingerprintNextButton, findTheSensor);
    }

    public final boolean isFingerprintSet() {
        openSecuritySettings();

        if (isFingerprintSupported() && isDevicePasswordSet()) {
            Log.d(TAG, "Fingerprint is supported by this hardware!");
            if (isTextShown(textFingerprintSetup)) {
                return true;
            } else {
                return scrollToText(packageAndroidSettings + _ID + idRecyclerViewList,
                        textFingerprintSetup);
            }
        } else {
            Log.d(TAG, "Fingerprint is not supported by this hardware OR device PIN wasn't set");
            return false;
        }
    }

    public final boolean getFingerprintScreen(String devicePass, String fingerprintNextButton, String findTheSensor) {
        openSecuritySettings();

        if (isFingerprintSupported() && isDevicePasswordSet()) {
            Log.d(TAG, "Fingerprint is supported by this hardware!");
            if (isTextShown(textFingerprint)) {
                Log.d(TAG, textFingerprint + " was found on Security screen");
            } else {
                Log.d(TAG, "Cannot find \"Fingerprint\" on Security screen. Try to scroll to it");
                if (scrollToText(packageAndroidSettings + _ID + idRecyclerViewList,
                        textFingerprint)) {
                    waitForUI(Duration.of(WAIT_FOR_SCREEN));
                    Log.d(TAG, textFingerprint + " was found on Security screen after scrolling to it");
                } else {
                    Log.d(TAG, "Cannot find " + textFingerprint + " on Security screen");
                    return false;
                }
            }

            if (clickOnItemWithText(textFingerprint, of(WAIT_FOR_SCREEN))) {
                if (!isResourceWithIDShown(packageAndroidSettings, idPasswordEntry,
                        Duration.of(UI_WAIT))) {
                    clickOnItemWithID(packageAndroidSettings, fingerprintNextButton, of(UI_WAIT),
                            of(UI_WAIT));
                }

                if (enterTextToItemWithID(packageAndroidSettings, idPasswordEntry, devicePass) && clickKeyboardOk()) {
                    if (isTextShown("Unlock with fingerprint") && isTextShown(textButtonNext)) {
                        clickOnItemContainingText(textButtonNext);
                    }
                    if (isTextShown(findTheSensor)) {
                        if (completeGettingOfFingerprintScan()) {
                            return true;
                        } else {
                            Log.d(TAG, "Cannot proceed with fingerprint setup");
                            return false;
                        }
                    } else if (isTextShown(textAddFingerprint,
                            Duration.of(UI_WAIT))) {
                        return clickOnItemContainingText(textAddFingerprint)
                                && isTextShown("Put your finger on the sensor",
                                Duration.of(WAIT_FOR_SCREEN) * 2);
                    } else {
                        Log.d(TAG, "Could not proceed with fingerprint setup after password set");
                        return false;
                    }
                }
                Log.d(TAG, "Couldn't enter device password");
                return false;
            }
            Log.d(TAG, "Cannot find \"Fingerprint\" on Security screen");
        } else {
            Log.d(TAG, "Fingerprint is not supported by this hardware");
        }
        return false;
    }

    /**
     * @return true if is proposed to scan your finger
     */
    protected boolean completeGettingOfFingerprintScan() {
        String fingerprintNextButton = idButtonNext;
        String fingerprintScrollViewId = packageAndroidSettings + _ID + "suw_bottom_scroll_view";
        String scrollToTextForFingerprint = textButtonNext;
        String scanYourFinger = "Put your finger on the sensor";

        return completeGettingOfFingerprintScan(fingerprintNextButton, fingerprintScrollViewId,
                scrollToTextForFingerprint, scanYourFinger);
    }

    /**
     * @return true if is proposed to scan your finger
     */
    protected final boolean completeGettingOfFingerprintScan(String fingerprintNextButton, String fingerprintScrollViewId,
                                                             String scrollToTextForFingerprint, String scanYourFinger) {
        if (isResourceWithIDShown(packageAndroidSettings, fingerprintNextButton)) {
            Log.d(TAG, fingerprintNextButton + " was found on the screen");
        } else {
            Log.d(TAG, "Cannot find " + fingerprintNextButton + " on the screen. Try to scroll to it");
            if (scrollToText(fingerprintScrollViewId, scrollToTextForFingerprint)) {
                Log.d(TAG, fingerprintNextButton + " was found on the screen");
            } else {
                Log.d(TAG, "Cannot find " + fingerprintNextButton + " button on the screen");
                return false;
            }
        }
        return clickOnItemWithID(packageAndroidSettings, fingerprintNextButton, of(WAIT_FOR_SCREEN), of(UI_WAIT))
                && isTextShown(scanYourFinger);
    }

    /**
     * @return true if fingerprint was accepted successfully
     */
    public boolean completeFingerprintSetup() {
        String fingerprintNextButton = idButtonNext;
        String completeFingerprintScanScrollViewId = packageAndroidSettings + _ID + "suw_scroll_view";
        String scrollToTextToCompleteFingerprintScan = textButtonDone;
        return completeFingerprintSetup(fingerprintNextButton, completeFingerprintScanScrollViewId,
                scrollToTextToCompleteFingerprintScan);
    }

    public final boolean completeFingerprintSetup(String fingerprintNextButton, String completeFingerprintScanScrollViewId,
                                                  String scrollToTextToCompleteFingerprintScan) {

        if (isResourceWithIDShown(packageAndroidSettings, fingerprintNextButton, of(UI_WAIT))) {
            Log.d(TAG, fingerprintNextButton + " was found on the screen");
        } else {
            Log.d(TAG, "Cannot find " + fingerprintNextButton + " on the screen. Try to scroll to it");
            if (scrollToText(completeFingerprintScanScrollViewId, scrollToTextToCompleteFingerprintScan)) {
                Log.d(TAG, fingerprintNextButton + " was found on the screen");
            } else {
                Log.d(TAG, "Cannot find " + fingerprintNextButton + " button on the screen");
                return false;
            }
        }

        return clickOnItemWithID(packageAndroidSettings, fingerprintNextButton, of(WAIT_FOR_SCREEN), of(UI_WAIT));
    }

    /**
     * @param fingerprintNameToRemove name of fingerprint to be removed
     * @param passwordPIN             password or PIN
     * @return true if fingerprint was removed otherwise false
     */
    public boolean removeFingerprint(String fingerprintNameToRemove, String passwordPIN) {
        openSecuritySettings();
        if (isFingerprintSupported() && isDevicePasswordSet()) {
            Log.d(TAG, "Fingerprint is supported by this hardware!");
            if (isTextShown(textFingerprint)) {
                Log.d(TAG, textFingerprint + " was found on Security screen");
            } else {
                Log.d(TAG, "Cannot find " + textFingerprint + " on Security screen. Try to scroll to it");
                if (scrollToText(packageAndroidSettings + _ID + idRecyclerViewList,
                        textFingerprint)) {
                    Log.d(TAG, textFingerprint + " was found on Security screen after scrolling to it");
                } else {
                    Log.d(TAG, "Cannot find " + textFingerprint + " on Security screen");
                    return false;
                }
            }

            if (clickOnItemWithText(textFingerprint, of(WAIT_FOR_SCREEN))) {
                if (enterTextToItemWithID(packageAndroidSettings, idPasswordEntry, passwordPIN)
                        && clickKeyboardOk() && isTextShown(fingerprintNameToRemove)) {
                    if (clickOnItemWithText(fingerprintNameToRemove, of(WAIT_FOR_SCREEN))) {
                        if (clickOnItemWithID(packageAndroid, idButton2, of(WAIT_FOR_SCREEN))) {
                            if (isTextShown("Remove all fingerprints")
                                    && clickOnItemWithID(packageAndroid, idButton1, of(WAIT_FOR_SCREEN))) {
                                Log.d(TAG, "The last one fingerprint was removed");
                                return true;
                            }
                            Log.d(TAG, "Specified fingerprint was removed. Looks like some fingerprints were left.");
                            return true;
                        }
                        Log.d(TAG, "Cannot remove specified fingerprint");
                        return false;
                    }
                }
                Log.d(TAG, "No access to fingerprint settings");
            }
            Log.d(TAG, "Fingerprint setting not found in Security list");
            return false;
        } else {
            Log.d(TAG, "Fingerprint in not supported by this hardware");
            return true;
        }
    }

    public boolean isDevicePasswordSet() {
        KeyguardManager keyguardManager = (KeyguardManager) getInstrumentation().getContext()
                .getSystemService(Context.KEYGUARD_SERVICE); //api 23+
        return keyguardManager != null && keyguardManager.isDeviceSecure();
    }

    @SuppressWarnings("MissingPermission")
    public boolean isFingerprintSupported() {
        //Fingerprint API only available on from Android 6.0 (M)
        FingerprintManager fingerprintManager = (FingerprintManager) getInstrumentation().getTargetContext()
                .getSystemService(Context.FINGERPRINT_SERVICE);
        return fingerprintManager != null && fingerprintManager.isHardwareDetected();
    }

    /**
     * Opens the notification shade.
     *
     * @return true if successful, false otherwise
     */
    public boolean openNotifications() {
        return uiDevice.openNotification();
    }

    /**
     * Disables WiFi on a device.
     *
     * @return true - if switch was successfull
     * false - otherwise
     * @throws UiObjectNotFoundException in case if switch was not found on the screen
     */
    public boolean disableWiFi() throws UiObjectNotFoundException {
        boolean result = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = switchWiFi("on", "off");
        }
        pressHome();
        return result;
    }

    /**
     * Enables WiFi on the device.
     *
     * @return true - if switch was successful
     * false - otherwise
     * @throws UiObjectNotFoundException in case if switch was not found on the screen
     */
    public boolean enableWiFi() throws UiObjectNotFoundException {
        boolean result = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = switchWiFi("off", "on");
        }
        if (!isTextShown("Connected", of(KNOWN_WIFI_CONNECTION))) {
            Log.d(TAG, "Did not connect to some known network.");
            return false;
        }
        pressHome();
        return result;

    }

    /**
     * Switches WiFi to On or Off state.
     *
     * @param fromState state of WiFi to switch from
     * @param toState   state of WiFi to switch to
     * @return true - if switch was successfull
     * false - otherwise
     * @throws UiObjectNotFoundException in case if switch was not found on the screen
     */
    private boolean switchWiFi(String fromState, String toState) throws UiObjectNotFoundException {

        String wifiStatus = getWiFiStatus();

        if (!TextUtils.isEmpty(wifiStatus)) {
            if (wifiStatus.equalsIgnoreCase(toState)) {

                Log.d(TAG, "WiFi is already " + toState);
                return true;
            } else {
                getWiFiSwitch().click();
                if (getWiFiSwitch().getText().equalsIgnoreCase(toState)) {

                    Log.d(TAG, "WiFi was switched from " + fromState + " to " + toState);
                    return true;
                } else {
                    Log.d(TAG, "Failed to switch WiFi form " + fromState + " to " + toState);
                }
            }
        } else {
            Log.d(TAG, "Couldn't get WiFi status (null was returned).");
        }
        return false;
    }

    /**
     * Opens WiFi settings and gets its status (whether it is turned on or not).
     *
     * @return the status of WiFi (On/Off)
     * @throws UiObjectNotFoundException if WiFi switch was not found
     */
    public String getWiFiStatus() throws UiObjectNotFoundException {

        if (!getWiFiSwitch().exists()) {
            launchActionSettings(ACTION_WIFI_SETTINGS);
            waitForUI(of(WAIT_FOR_SCREEN));
        }

        if (getWiFiSwitch().exists()) {
            return getWiFiSwitch().getText();
        } else {
            Log.d(TAG, "WiFi settings were not opened.");
            return null;
        }
    }

    /**
     * @param shouldBeEnabled true if Automatic Date & Time should be enabled, otherwise false
     * @param totalDelay      wait for UI changes
     * @param timeout         wait for existence of element
     * @deprecated Use {@link #selectAutomaticDateTime(boolean, long)}
     */
    @Deprecated
    public void selectAutomaticDateTime(boolean shouldBeEnabled, long totalDelay, long timeout) {
        selectAutomaticDateTime(shouldBeEnabled, timeout);
    }

    /**
     * @param shouldBeEnabled true if Automatic Date & Time should be enabled, otherwise false
     * @param timeout         wait for existence of element
     */
    public void selectAutomaticDateTime(boolean shouldBeEnabled, long timeout) {

        UiObject list;
        UiObject switcher;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                list = findByResourceId(packageAndroidSettings + _ID + idRecyclerViewList);
                switcher = list.getChild(new UiSelector().index(0))
                        .getChild(new UiSelector().resourceId(packageAndroid + _ID + idWidgetFrame))
                        .getChild(new UiSelector().resourceId(packageAndroid + _ID + idSwitchWidget));
            } else {
                list = findByResourceId(packageAndroid + _ID + idRecyclerViewList);
                switcher = list.getChild(new UiSelector().index(0))
                        .getChild(new UiSelector().resourceId(packageAndroid + _ID + idSwitchWidget));
            }

            switcher.waitForExists(timeout);

            if (switcher.isChecked() != shouldBeEnabled) {
                clickOnItemWithText(textAutomaticDateTime);
            } else {
                Log.d(TAG, "No need to change Automatic date & time");
            }
        } catch (UiObjectNotFoundException e) {
            Log.d(TAG, e.getMessage());
        }
        Log.d(TAG, "Text wasn't found on this screen!");
    }

    /**
     * Increase date for specified number of days.
     *
     * @param daysToAdd count of days to add to calendar
     * @param timeout   time to wait for existence of needed UI element
     */
    public void changeDateSettings(int daysToAdd, long timeout) {

        UiObject list;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            list = findByResourceId(packageAndroidSettings + _ID + idRecyclerViewList);
        } else {
            list = findByResourceId(packageAndroid + _ID + idRecyclerViewList);
        }

        try {
            UiObject setDate = list.getChild(new UiSelector().textMatches(textSetDate));

            setDate.waitForExists(timeout);
            setDate.click();

            UiObject monthView = findByResourceId(packageAndroid + _ID + idDateTimeMonthView);
            int currentDate = Integer.parseInt(monthView.getChild(new UiSelector().checked(true)).getText());
            int daysInMonth = monthView.getChildCount();
            if (currentDate + daysToAdd > monthView.getChildCount()) {
                UiObject buttonNext = findByResourceId(packageAndroid + _ID + idButtonImageNext);
                buttonNext.click();
                monthView.getChild(new UiSelector().index(currentDate + daysToAdd - daysInMonth - 1)).click();

            } else {
                monthView.getChild(new UiSelector().index(currentDate + daysToAdd - 1)).click();
            }

            findByResourceId(packageAndroid + _ID + idButton1).click();

        } catch (UiObjectNotFoundException e) {
            Log.d(TAG, "Waiting for element");
        }
        Log.d(TAG, "Text wasn't found on this screen!");
    }

    /**
     * Increase or decrease date depends on input parameter.
     *
     * @param increase true in case date should be increased, otherwise false
     * @param calendar Calendar object expected to apply
     * @param timeout  wait for existence of specific UI element
     * @return true if date was changed, otherwise false
     */
    public boolean changeDateSettings(boolean increase, Calendar calendar, long timeout) {
        UiObject list;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            list = findByResourceId(packageAndroidSettings + _ID + idRecyclerViewList);
        } else {
            list = findByResourceId(packageAndroid + _ID + idRecyclerViewList);
        }

        try {
            UiObject setDate = list.getChild(new UiSelector().textMatches(textSetDate));

            setDate.waitForExists(timeout);
            setDate.click();

            if (selectExpectedDate(increase, calendar)) {
                return findByResourceId(packageAndroid + _ID + idButton1).click();
            } else {
                Log.d(TAG, "Could not select expected date");
                return false;
            }
        } catch (UiObjectNotFoundException e) {
            Log.e(TAG, "Could not find element: " + e.getMessage());
        }
        Log.d(TAG, "Failed to set expected date");
        return false;
    }

    /**
     * Select or disable automatic Date&Time depends on input parameter.
     *
     * @param increase true in case date should be increased, otherwise false
     * @param calendar Calendar object expected to apply
     * @return true if date was changed, otherwise false
     */
    private boolean selectExpectedDate(boolean increase, Calendar calendar) {
        UiObject nextMonth = null;
        if (increase) {
            nextMonth = findByResourceId(packageAndroid + _ID + idButtonImageNext);
        } else {
            nextMonth = findByResourceId(packageAndroid + _ID + idButtonImagePrev);
        }

        String textDateFormat = computeTextDateFormat(calendar);
        int indexToSelect = calendar.get(Calendar.DAY_OF_MONTH) - 1;
        Log.d(TAG, "Index: " + indexToSelect + " dateFormat: " + textDateFormat);
        try {
            UiObject uiDayOfMonth = findByResourceId(packageAndroid + _ID
                    + idDateTimeMonthView).getChild(new UiSelector().index(indexToSelect));
            uiDayOfMonth.waitForExists(2000);
            if (uiDayOfMonth != null && uiDayOfMonth.getContentDescription().toLowerCase()
                    .contains(textDateFormat.toLowerCase())) {
                Log.d(TAG, "Expected data was selected!");
                return uiDayOfMonth.click();
            } else {
                Log.d(TAG, "Expected element was not found");
                if (nextMonth.click()) {
                    Log.d(TAG, "Move to next month");
                    return selectExpectedDate(increase, calendar);
                } else {
                    Log.d(TAG, "Could not move to the next month");
                    return false;
                }
            }
        } catch (UiObjectNotFoundException e) {
            Log.e(TAG, "Some element was not found on the screen: " + e.getMessage());
        }
        Log.d(TAG, "Could not select expected date");
        return false;
    }

    /**
     * .
     * @param calendar Calendar object expected to apply
     * @return expected date
     */
    private String computeTextDateFormat(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH)
                + " " + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
                + " " + calendar.get(Calendar.YEAR);
    }

    /**
     * Change time in 12 hours format.
     *
     * @param calendar Calendar object expected to apply. Time format 12 hours
     * @return true if time changed successfully, otherwise false
     */
    public boolean changeTimeIn12HoursFormat(Calendar calendar) {

        UiObject list;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            list = findByResourceId(packageAndroidSettings + _ID + idRecyclerViewList);
        } else {
            list = findByResourceId(packageAndroid + _ID + idRecyclerViewList);
        }

        try {
            UiObject setTime = list.getChild(new UiSelector().textMatches(textSetTime));

            setTime.waitForExists(of(WAIT_FOR_SCREEN));
            setTime.click();

            int indexOfHoursToSelect = calendar.get(Calendar.HOUR) - 1;
            Log.d(TAG, "Try to set " + (indexOfHoursToSelect + 1) + " hour in 12 hours format");
            UiObject radialTimePicker = findByResourceId(packageAndroid + _ID + idDateTimeRadialPicker)
                    .getChild(new UiSelector().index(indexOfHoursToSelect));
            Log.d(TAG, "Hours item was selected: " + radialTimePicker.click());

            int indexOfMinutesToSelect = calendar.get(Calendar.MINUTE) / 5;
            Log.d(TAG, "Try to set " + calendar.get(Calendar.MINUTE) + " minute in 12 hours format");
            radialTimePicker = findByResourceId(packageAndroid + _ID + idDateTimeRadialPicker,
                    Duration.of(WAIT_FOR_SCREEN)).getChild(new UiSelector().index(indexOfMinutesToSelect));

            Log.d(TAG, "Minutes item was selected: " + radialTimePicker.click());

            String resultAmPm = "";
            if (calendar.get(Calendar.AM_PM) == 1) {
                resultAmPm = "pm_label";
            } else {
                resultAmPm = "am_label";
            }
            return RadioButtonImpl.getByID(packageAndroid, resultAmPm).click()
                    && findByResourceId(packageAndroid + _ID + idButton1).click();

        } catch (UiObjectNotFoundException e) {
            Log.d(TAG, "Waiting for element");
        }
        Log.d(TAG, "Text wasn't found on this screen!");
        return false;
    }

    /**
     * Gets {@link UiObject} of a WiFi switch.
     *
     * @return {@link UiObject} of a WiFi switch
     */
    private UiObject getWiFiSwitch() {
        String settingsPackageId = packageAndroidSettings;
        String wifiSwitchResourceId = "switch_bar";

        return findByResourceId(computeResourceId(settingsPackageId, wifiSwitchResourceId));
    }

    public boolean isNaturalOrientation() throws RemoteException {
        return uiDevice.isNaturalOrientation();
    }

    public void rotateDeviceLeft() throws RemoteException {
        uiDevice.setOrientationLeft();
        waitForUI(Duration.of(SCREEN_ROTATION));
    }

    public void rotateDeviceRight() throws RemoteException {
        uiDevice.setOrientationRight();
        waitForUI(Duration.of(SCREEN_ROTATION));
    }

    public void rotateDeviceNatural() throws RemoteException {
        uiDevice.setOrientationNatural();
        waitForUI(Duration.of(SCREEN_ROTATION));
    }

    /**
     * Helper method to capture screenshot (requires app to have the write external storage permission).
     */
    public boolean captureScreenshot(String filename) {

        grantPermissionsInRuntime(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE});

        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        File dir = new File(Environment.getExternalStorageDirectory(),
                "Screenshots" + File.separator + stack[3].getClassName() + File.separator + stack[3].getMethodName());

        if (!dir.exists()) {
            Log.d(TAG, "Folder for screenshot does not exist. Try to create it.");
            if (!dir.mkdirs()) {

                if (checkSelfPermission(getInstrumentation().getTargetContext(),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
                    Log.v(TAG,"Permission was not granted for EXTERNAL_STORAGE");
                    grantPermissionsInRuntime(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE});
                }

                //wait till permissions be granted
                waitForUI(of(UI_WAIT));

                if (!dir.mkdirs()) {
                    Log.d(TAG, "Could not create hierarchy of folders for screenshot: " + dir.getAbsolutePath());
                    return false;
                }
            }
        }

        File file = new File(dir, filename + new Date().getTime() + ".png");

        boolean isCaptured = uiDevice.takeScreenshot(file);

        Log.d(TAG, "Capturing screenshot: " + isCaptured + " | file: " + file.getName());

        return isCaptured;
    }

    /**
     * Enables airplane (flight) mode on the device by performing UI interaction with the device.
     * Networking will be completely disabled after this operation.
     *
     * @return whether the operation was performed successfully.
     */
    public boolean enableAirplaneMode() {
        return UiNetworkManagerFactory.getManager().enableAirplaneMode();
    }

    /**
     * Disables airplane (flight) mode on the device by performing UI interaction with the device.
     *
     * @return whether the operation was performed successfully.
     */
    public boolean disableAirplaneMode() {
        return UiNetworkManagerFactory.getManager().disableAirplaneMode();
    }

    /**
     * Enables split screen view
     *
     * @return whether the operation was performed successfully.
     */
    public boolean enableSplitScreenMode(){
        UiAutomation uiAutomation = InstrumentationRegistry.getInstrumentation().getUiAutomation();
        return uiAutomation.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN);
    }

    /**
     * Enables/disables Location service on the device.
     *
     * @param shouldBeEnabled true if Location Service should be enabled, otherwise false
     * @param timeout         wait for existence of element
     */
    public void selectLocationService(boolean shouldBeEnabled, long timeout) {

        UiObject locationSwitcher;

        try {
            // com.android.settings:id/switch_widget
            locationSwitcher = findByResourceId(packageAndroidSettings + _ID + idSwitchWidget);

            locationSwitcher.waitForExists(timeout);

            if (locationSwitcher.isChecked() != shouldBeEnabled) {
                clickOnItemWithID(packageAndroidSettings, idSwitchWidget);
            } else {
                Log.d(TAG, "No need to change location");
            }
        } catch (UiObjectNotFoundException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    /**
     * In simulation mode, a valid activation key is not required to open the application
     * because there is no direct communication with BlackBerry Dynamics servers at the enterprise;
     * and, in fact, no need for these servers to even be in place.
     * Communication with the BlackBerry Dynamics NOC, however, continues to take place.
     * In Enterprise Simulation mode your BlackBerry Dynamics applications are run on a device emulator.
     * To enable Enterprise Simulation mode, whether you use an IDE or an outside text editor,
     * you need change the following line in your application's settings.json file as shown below:
     * "GDLibraryMode":"GDEnterprise" -> "GDLibraryMode":"GDEnterpriseSimulation"
     * The settings.json file is located in the ../assets/ folder of the application and must remain there.
     * More information is here: https://community.good.com/docs/DOC-1351
     */
    public boolean activateGDAppInSimulationMode(String packageName, String email, String
            accessPin, String unlockPassword) {

        // When starting unprovisioned GD App will check NOC for Easy Activation options,
        // if present will show screen prompting one can be installed
        // Otherwise will directly show the enter email and access key screen
        return BBDActivationHelper.loginOrActivateBuilder(UIAutomatorUtilsFactory.getUIAutomatorUtils(),
                packageName,
                email,
                accessPin)
                .setAppUnderTestPassword(unlockPassword)
                .doAction();
    }

    /**
     * Helper method to return GD Shared Preferences.
     */
    @Deprecated
    public SharedPreferences getGDSharedPreferences(String aName, int aMode) {
        throw new RuntimeException("This method is deprecated. "
                + "Please use GDAndroid.getInstance().getGDSharedPreferences(aName, aMode); in your code ");
    }

    @Deprecated
    public void registerGDStateReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        throw new RuntimeException("This method is deprecated. "
                + "Please use GDAndroid.getInstance().registerReceiver(receiver, filter); in your code ");
    }

    @Deprecated
    public void unregisterGDStateReceiver(BroadcastReceiver receiver) {
        throw new RuntimeException("This method is deprecated. "
                + "Please use GDAndroid.getInstance().unregisterReceiver(receiver); in your code ");
    }

    /**
     * Prints current screen hierarchy map to the logs.
     */
    public void printScreenMap() {

        Log.d(TAG, "Dumping current UI hierarchy map.");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            getUiDevice().dumpWindowHierarchy(os);
        } catch (IOException e) {
            Log.e(TAG, "Current window hierarchy dump failed!", e);
        }

        System.out.println(os);
    }

    /**
     * @param packageName package name
     * @param elementID   id of element expected to gone from screen
     * @param delay       total delay within which element has to be gone from screen
     * @return true if element gone from screen within specified time, otherwise false
     */
    public boolean waitUntilElementGoneFromUI(String packageName, String elementID, long delay) {
        for (int i = 0; i < delay / Duration.of(Duration.SHORT_UI_ACTION); i++) {
            if (isResourceWithIDShown(packageName, elementID)) {
                waitForUI(Duration.of(Duration.SHORT_UI_ACTION));
            } else {
                Log.d(TAG, "Element has gone from screen!");
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method to add user certificate to trusted credentials.
     * Works only if PIN was set on device before.
     *
     * @param certificateName name of certificate to remove
     * @param devicePIN PIN on device
     *
     * @return true if certificate was added
     *         otherwise - false
     */
    public abstract boolean addCertificateToTrustedCredentials(String certificateName, String devicePIN) throws RemoteException;

    /**
     * Helper method to remove user certificate from trusted credentials.
     *
     * @param certificateName name of certificate to remove
     *
     * @return true if certificate was added
     *         otherwise - false
     */
    public abstract boolean removeCertificateFromTrustedCredentials(String certificateName);

    public GDTestSettings getSettings() {
        return settings;
    }

    public void setSettings(GDTestSettings settings) {
        this.settings = settings;
    }
}
