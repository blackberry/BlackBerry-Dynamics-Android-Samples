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

package com.good.automated.general.helpers;

import static com.good.automated.general.utils.Duration.PROVISIONING;
import static com.good.automated.general.utils.Duration.UI_WAIT;

import android.util.Log;

import com.good.automated.general.utils.AbstractUIAutomatorUtils;
import com.good.automated.general.utils.Duration;
import com.good.automated.general.utils.UIAutomatorUtilsFactory;
import com.good.automated.test.screens.AbstractBBDUI;
import com.good.automated.test.screens.BBDActivationProgressUI;
import com.good.automated.test.screens.BBDActivationUI;
import com.good.automated.test.screens.BBDAlertDialogUI;
import com.good.automated.test.screens.BBDApplicationBlockUI;
import com.good.automated.test.screens.BBDApplicationMTDBlockUI;
import com.good.automated.test.screens.BBDAuthorizationUnavailableBlockUI;
import com.good.automated.test.screens.BBDDisclaimerUI;
import com.good.automated.test.screens.BBDEasyActivationSelectionUI;
import com.good.automated.test.screens.BBDEasyActivationUnlockUI;
import com.good.automated.test.screens.BBDFingerprintAlertUI;
import com.good.automated.test.screens.BBDLearnMoreUI;
import com.good.automated.test.screens.BBDMTDDisclaimerUI;
import com.good.automated.test.screens.BBDNoPasswordUI;
import com.good.automated.test.screens.BBDPermissionUI;
import com.good.automated.test.screens.BBDRetrievingAccessKeyUI;
import com.good.automated.test.screens.BBDSetPasswordUI;
import com.good.automated.test.screens.BBDUnlockUI;
import com.good.automated.test.screens.BBDWelcomeUI;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class BBDActivationHelper {

    private boolean noPasswordCanBeEnabled = false;
    private AbstractBBDUI uiElement = null;
    private String currentPackageName;
    private int unsupportedScreenCounter = 0;
    protected AbstractUIAutomatorUtils uiAutomationUtils;
    protected String packageName;
    protected String appPassword;
    protected String eaPackageName;
    protected String eaName;
    protected String authDelPackageName;
    protected String authDelPassword;
    protected String eaPassword;
    protected String userName;
    protected String activationPassword;
    protected boolean fingerPrintEnabled = false;

    protected List<String> shownScreensQueue;

    private final String TAG = BBDAdvancedActivationHelper.class.getCanonicalName();

    /**
     * List of supported screens. Each ID is located in proper place in this list. It will be used in
     * switch/case mechanism of screen recognition.
     * <p>
     * In case of adding new screen make sure that it is handled in {@link BBDActivationHelper#discoverCurrentUI()} method
     * <p>
     * NOTE: don't change sequentials numbers in this list without performing similar actions in {@link BBDActivationHelper#discoverCurrentUI()}
     */
    protected final List<String> uiElementsList = new ArrayList<String>() {{
        add(0, "bbd_runtimepermissions_introfragment_UI");
        add(1, "bbd_activation_delegate_view_UI");
        add(2, "bbd_activation_login_view_UI");
        add(3, "bbd_block_view_UI");
        add(4, "bbd_disclaimer_view_UI");
        add(5, "bbde_noc_selection_view_UI");
        add(6, "bbde_provision_view_UI");
        add(7, "bbde_provision_progress_view_UI");
        add(8, "bbd_learn_more_view_UI");
        add(9, "bbd_login_view_UI");
        add(10, "bbd_nopassword_notification_view_UI");
        add(11, "bbd_set_password_view_UI");
        add(12, "bbd_welcome_view_UI");
        add(13, "bbd_logs_upload_view_UI");
        add(14, "bbd_activate_fingerprint_view_UI");
        add(15, "bbd_fingerprint_container_UI");
        add(16, "bbd_mtd_disclaimer_view_UI");
        add(17, "bbd_mtd_block_view_UI");
        // add(15, "some_unique_ID");
        // etc...e.g. add(sequential_number_of_the_screen, "unique_id_of_the_screen");
    }};

    protected Integer[] shownScreensCounter = new Integer[uiElementsList.size()];

    protected void refreshShownScreensCounter() {
        shownScreensQueue = new LinkedList<>();
        unsupportedScreenCounter = 0;
        shownScreensCounter = new Integer[uiElementsList.size()];
        for (int i = 0; i < shownScreensCounter.length; i++) {
            shownScreensCounter[i] = 0;
        }
    }

    protected BBDActivationHelper() {
    }

    /**
     * Unlock application with its own password
     * @return LoginBuilder with set required parameters for simple login
     */
    public static boolean loginApp() {
        AbstractUIAutomatorUtils uiAutomationUtils = UIAutomatorUtilsFactory.getUIAutomatorUtils();
        return loginApp(uiAutomationUtils.getAppPackageName());
    }

    /**
     * Unlock application with its own password
     * @param pName AppUnderTest package name
     * @return LoginBuilder with set required parameters for simple login
     */
    public static boolean loginApp(String pName) {
        AbstractUIAutomatorUtils uiAutomationUtils = UIAutomatorUtilsFactory.getUIAutomatorUtils();
        return loginApp(pName, uiAutomationUtils.getAppProvisionPassword(pName));
    }

    /**
     * Unlock application with custom password
     * @param pName AppUnderTest package name
     * @param password password to login with
     * @return LoginBuilder with set required parameters for simple login
     */
    public static boolean loginApp(String pName, String password) {
        AbstractUIAutomatorUtils uiAutomationUtils = UIAutomatorUtilsFactory.getUIAutomatorUtils();
        return loginApp(uiAutomationUtils, pName, password);
    }

    /**
     * Unlock application with custom password
     * @param uiAutomationUtils instance of AbstractUIAutomatorUtils
     * @param pName AppUnderTest package name
     * @param password password to login with
     * @return LoginBuilder with set required parameters for simple login
     */
    public static boolean loginApp(AbstractUIAutomatorUtils uiAutomationUtils, String pName, String password) {
        return new BBDActivationHelper().new LoginBuilder(uiAutomationUtils, pName)
                .setAppUnderTestPassword(password)
                .doAction();
    }

    /**
     * Unlock application with its own password
     *
     * @param ui    object of uiAutomatorUtils
     * @param pName AppUnderTest package name
     * @return LoginBuilder with set required parameters for simple login
     */
    public static LoginBuilder loginBuilder(AbstractUIAutomatorUtils ui, String pName) {
        return new BBDActivationHelper().new LoginBuilder(ui, pName);
    }

    /**
     * Login or activate application using Activation Password and its own Unlock password
     * @return LoginOrActivateBuilder with set required parameters for simple login or activate logic
     */
    public static boolean loginOrActivateApp() {
        AbstractUIAutomatorUtils uiAutomationUtils = UIAutomatorUtilsFactory.getUIAutomatorUtils();
        return loginOrActivateApp(uiAutomationUtils.getAppPackageName());
    }

    /**
     * Login or activate application using Activation Password and its own Unlock password
     * @return LoginOrActivateBuilder with set required parameters for simple login or activate logic
     */
    public static boolean loginOrActivateApp(String pName) {
        AbstractUIAutomatorUtils uiAutomationUtils = UIAutomatorUtilsFactory.getUIAutomatorUtils();
        return new BBDActivationHelper().new LoginOrActivateBuilder(uiAutomationUtils,
                pName,
                uiAutomationUtils.getProvisionLogin(pName),
                uiAutomationUtils.getAccessKey(pName))
                .setAppUnderTestPassword(uiAutomationUtils.getAppProvisionPassword(pName))
                .doAction();
    }

    /**
     * Login or activate application using Activation Password and its own Unlock password
     *
     * @param ui    object of uiAutomatorUtils
     * @param pName AppUnderTest package name
     * @return LoginOrActivateBuilder with set required parameters for simple login or activate logic
     */
    public static LoginOrActivateBuilder loginOrActivateBuilder(AbstractUIAutomatorUtils ui, String pName, String uName, String aPass) {
        return new BBDActivationHelper().new LoginOrActivateBuilder(ui, pName, uName, aPass);
    }

    /**
     * Unlock application with its own password. Cancel fingerprint after activation
     *
     * @param ui    object of uiAutomatorUtils
     * @param pName AppUnderTest package name
     * @return LoginWithFingerprintBuilder with set required parameters for simple login
     */
    public static LoginWithFingerprintBuilder loginWithFingerprintBuilder(AbstractUIAutomatorUtils ui, String pName) {
        return new BBDActivationHelper().new LoginWithFingerprintBuilder(ui, pName);
    }

    /**
     * Login or activate application using Activation Password and its own Unlock password. Cancel fingerprint after activation
     *
     * @param ui    object of uiAutomatorUtils
     * @param pName AppUnderTest package name
     * @return LoginOrActivateWithFingerprintBuilder with set required parameters for simple login or activate logic
     */
    public static LoginOrActivateWithFingerprintBuilder loginOrActivateWithFingerprintBuilder(AbstractUIAutomatorUtils ui, String pName, String uName, String aPass) {
        return new BBDActivationHelper().new LoginOrActivateWithFingerprintBuilder(ui, pName, uName, aPass);
    }

    /**
     * Login or activate application using Activation Password and its own Unlock password. Cancel fingerprint after activation
     *
     * @param ui    object of uiAutomatorUtils
     * @param pName AppUnderTest package name
     * @return LoginOrActivateWithFingerprintBuilder with set required parameters for simple login or activate logic
     */
    public static LoginOrEasyActivateWithFingerprintBuilder loginOrEasyActivateWithFingerprintBuilder(AbstractUIAutomatorUtils ui, String pName, String uName, String aPass) {
        return new BBDActivationHelper().new LoginOrEasyActivateWithFingerprintBuilder(ui, pName, uName, aPass);
    }

    /**
     * Unlock application with its own password. Cancel fingerprint after activation
     * Note: recommended to use only in APP tests
     *
     * @param ui                   object of uiAutomatorUtils
     * @param pName                AppUnderTest package name
     * @param isFingerprintEnabled true if fingerprint is enabled in user policy and device supports it, otherwise false
     * @return LoginWithFingerprintBuilder with set required parameters for simple login
     */
    public static boolean loginWithFingerprint(AbstractUIAutomatorUtils ui, String pName, boolean isFingerprintEnabled) {
        return new BBDActivationHelper().new LoginWithFingerprintBuilder(ui, pName).setAppUnderTestPassword(ui.getAppProvisionPassword(pName)).
                setFingerprintCheck(isFingerprintEnabled).
                doAction();
    }

    /**
     * Login or activate application using Activation Password and its own Unlock password. Cancel fingerprint after activation
     * Note: recommended to use only in APP tests
     *
     * @param ui                   object of uiAutomatorUtils
     * @param pName                AppUnderTest package name
     * @param isFingerprintEnabled true if fingerprint is enabled in user policy and device supports it, otherwise false
     * @return LoginOrActivateWithFingerprintBuilder with set required parameters for simple login or activate logic
     */
    public static boolean loginOrActivateWithFingerprint(AbstractUIAutomatorUtils ui, String pName, boolean isFingerprintEnabled) {
        return new BBDActivationHelper().new LoginOrActivateWithFingerprintBuilder(ui, pName,
                ui.getProvisionLogin(pName),
                ui.getAccessKey(pName)).
                setAppUnderTestPassword(ui.getAppProvisionPassword(pName)).
                setFingerprintCheck(isFingerprintEnabled).
                doAction();
    }

    /**
     *
     * @return list of unique IDs of supported BlackBerry screens in strict order.
     */
    public static List<String> getUIElementsList() {
        return new BBDActivationHelper().uiElementsList;
    }

    /**
     * How to be able to get UI object using this method:
     * 1. Extend from AbstractBBDUI your own UI class
     * 2. Add unique ID to {@link BBDActivationHelper#uiElementsList}
     * 3. Add index of added unique ID from {@link BBDActivationHelper#uiElementsList} to switch/case of this method.
     * Implement logic for creation of your screen.
     *
     * @return BBDUI object displayed on the device screen otherwise null
     */
    protected AbstractBBDUI discoverCurrentUI() {
        int displayedScreen = getCurrentUIID();

        if (!checkScreenCounter(displayedScreen)) {
            Log.d(TAG, "Limit of showing screen. ID of shown screen: " + uiElementsList.get(displayedScreen));
            return null;
        }

        Log.d(TAG, "Package name : " + currentPackageName + " screen ID : " + displayedScreen + " : " + (displayedScreen >= 0 ? uiElementsList.get(displayedScreen) : "unsupported screen"));

        switch (displayedScreen) {
            case -1:
                uiAutomationUtils.printScreenMap();
                if(unsupportedScreenCounter <= 1) {
                    //Should available only after permission screen.
                    //Workaround for limitation: Sometimes provisioning fails as Home screen appears for a moment on first app launching
                    //Sometimes during ICC between two apps we can get Android Home screen
                    uiAutomationUtils.waitForUI(Duration.of(UI_WAIT));
                    unsupportedScreenCounter++;
                    return discoverCurrentUI();
                } else {
                    if (appPassword == null && noPasswordCanBeEnabled) {
                        //No password feature
                        return new AbstractBBDUI() {
                            @Override
                            public boolean doAction() {
                                return true;
                            }
                        };
                    }
                    BBDAlertDialogUI alert = new BBDAlertDialogUI();
                    Log.d(TAG, "Probably is shown Alert Dialog with title: " + alert.getAlertTitle() + " message: " + alert.getAlertMessage());
                    return null;
                }
            case 0:
                uiElement = new BBDPermissionUI(packageName);
                break;
            case 1:
                unsupportedScreenCounter++;
                if (eaPackageName != null && eaName != null) {
                    uiElement = new BBDEasyActivationSelectionUI(packageName, eaPackageName, eaName);
                } else {
                    uiElement = new BBDEasyActivationSelectionUI(packageName);
                }
                break;
            case 2:
                //This variable can be True only after activating of the app
                noPasswordCanBeEnabled = false;
                uiElement = new BBDEasyActivationUnlockUI(eaPackageName, eaPassword);
                if (authDelPackageName != null && authDelPackageName.equals(eaPackageName) && eaPassword != null){
                    return uiElement;
                }
                break;
            case 3:
                if (currentPackageName.equals(authDelPackageName)) {
                    //Authorization Unavailable or any other Block screen might be shown
                    uiElement = new BBDAuthorizationUnavailableBlockUI(packageName);
                } else {
                    if (currentPackageName.equals(eaPackageName)) {
                        uiElement = new BBDRetrievingAccessKeyUI(eaPackageName);
                    } else {
                        Log.d(TAG, "Provision Failed due to shown Block UI");
                        try {
                            uiElement = new BBDApplicationBlockUI(currentPackageName);
                            Log.d(TAG, "package ID: " + currentPackageName + " Block UI title: " + ((BBDApplicationBlockUI) uiElement).getTitle());
                            Log.d(TAG, "Block UI description: " + ((BBDApplicationBlockUI) uiElement).getDetails());
                        } catch (NullPointerException e) {
                            Log.d(TAG, "Couldn't find title or description on Block screen. NullPointerException: " + e.toString());
                        }
                    }
                }
                break;
            case 4:
                uiElement = new BBDDisclaimerUI(packageName);
                break;
            case 5:
                uiElement = null;
                break;
            case 6:
                unsupportedScreenCounter++;
                //This variable can be True only after activating of the app
                noPasswordCanBeEnabled = false;
                uiElement = new BBDActivationUI(packageName, userName, activationPassword);
                break;
            case 7:
                uiElement = new BBDActivationProgressUI(packageName, true);
                if (authDelPackageName != null) {
                    Log.d(TAG, "Provision using Easy Activator and Auth Delegator apps is complete");
                    return uiElement;
                }
                noPasswordCanBeEnabled = true;
                break;
            case 8:
                uiElement = new BBDLearnMoreUI(packageName);
                break;
            case 9:
                if (authDelPackageName != null) {
                    //In case if we want to use Easy Activation and Auth Delegation features - in both cases is used same app (Master)
                    uiElement = new BBDUnlockUI(authDelPackageName, authDelPassword, Duration.of(PROVISIONING));
                } else if (eaPassword != null) {
                    uiElement = new BBDUnlockUI(eaPackageName, eaPassword);
                } else {
                    return new BBDUnlockUI(packageName, appPassword);
                }
                break;
            case 10:
                uiElement = new BBDNoPasswordUI(packageName);
                break;
            case 11:
                return new BBDSetPasswordUI(packageName, appPassword);
            case 12:
                uiElement = new BBDWelcomeUI(packageName);
                break;
            case 13:
                uiElement = null;
                break;
            case 14:
                uiElement = null;
                break;
            case 15:
                return new BBDFingerprintAlertUI(packageName);
            case 16:
                uiElement = new BBDMTDDisclaimerUI(packageName);
                break;
            case 17:
                Log.d(TAG, "Provision Failed due to shown MTD Block UI");
                try {
                    uiElement = new BBDApplicationMTDBlockUI(currentPackageName);
                    Log.d(TAG, "package ID: " + currentPackageName + " MTD Block UI title: " + ((BBDApplicationMTDBlockUI) uiElement).getTitle());
                    Log.d(TAG, "MTD Block UI description: " + ((BBDApplicationMTDBlockUI) uiElement).getDetails());
                } catch (NullPointerException e) {
                    Log.d(TAG, "Couldn't find title or description on MTD Block screen. NullPointerException: " + e.toString());
                }
            default:
                Log.d(TAG, "Screen ID : none : default");
                uiElement = null;
        }
        if (uiElement != null && uiElement.doAction()) {
            shownScreensCounter[displayedScreen]++;
            return discoverCurrentUI();
        }
        return null;
    }

    /**
     * Helper method which checks how many times each screen was shown.
     *
     * @param displayedScreen screen position in list of supported screens
     * @return true if it is allowed to continue provision flow, false if we stuck on some screen or some screen was shown a few times (more than in can be shown)
     */
    protected boolean checkScreenCounter(int displayedScreen) {

        if (displayedScreen >= 0 && shownScreensCounter[displayedScreen] > 0) {
            if (displayedScreen == 7 && shownScreensCounter[displayedScreen] < 50) {    // displayedScreen = 7 = bbde_provision_progress_view_UI    50 times means maximum 50 second (doAction() method waits for 1 second)
                return true;
            } else if (displayedScreen == 9 && eaPassword != null && shownScreensCounter[9] > 0) {  // displayedScreen = 9 = bbd_login_view_UI
                //Case when EA Activation ans AuthDelegation are both used
                if (eaPassword != null && shownScreensCounter[displayedScreen] < 2) {   // displayedScreen = 9 = bbd_login_view_UI
                    return true;
                }
            } else if (displayedScreen == 12 && shownScreensCounter[displayedScreen] < 50) {    // displayedScreen = 12 = bbd_welcome_view_UI       50 times means maximum 50 second (doAction() method waits for 1 second)
                return true;
            } else if (displayedScreen == 3 && (eaPassword != null || authDelPassword != null) && shownScreensCounter[displayedScreen] < 2){     // displayedScreen = 3 = bbd_block_view_UI   2 times
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * Helper method, to find out index of displayed screen from list of already supported screens.
     *
     * @return index of unique_screen_id located in {@link BBDActivationHelper#uiElementsList}
     * that is currently displayed on device screen
     */
    protected int getCurrentUIID() {
        String resourceID = uiAutomationUtils.getUiElementShown(packageName, uiElementsList);
        Log.d(TAG, "resourceID: " + (resourceID == null));
        currentPackageName = packageName;
        if (resourceID == null) {
            if (eaPackageName != null) {
                //For EasyActivation case
                currentPackageName = eaPackageName;
                Log.d(TAG, "Searching for Easy Activator resourceID: " + eaPackageName);
                resourceID = uiAutomationUtils.getUiElementShown(eaPackageName, uiElementsList);
            }
            if (resourceID == null && authDelPackageName != null) {
                //For Auth Delegation case
                currentPackageName = authDelPackageName;
                Log.d(TAG, "Searching for Auth Delegator resourceID: " + authDelPackageName);
                resourceID = uiAutomationUtils.getUiElementShown(authDelPackageName, uiElementsList);
            }
            if (eaPackageName == null && authDelPackageName == null) {
                eaPackageName = uiAutomationUtils.getUiDevice().getCurrentPackageName();
                Log.d(TAG, "Searching for possible Easy Activator resourceID: " + eaPackageName);
                resourceID = uiAutomationUtils.getUiElementShown(eaPackageName, uiElementsList);
            }
        }
        shownScreensQueue.add("" + currentPackageName + ":id/" + resourceID);
        return uiElementsList.indexOf(resourceID);
    }

    /**
     * Starts the activation of the AppUnderTest regardless of the app currently in foreground.
     *
     * @return true if activation was successful otherwise false
     */
    protected boolean doActivation() {
        refreshShownScreensCounter();
        AbstractBBDUI screen = discoverCurrentUI();
        boolean result = false;
        if (screen != null) {
            result = screen.doAction();
        }
        printScreensQueue();
        Log.d(TAG, "Result of Login or activate: " + result);
        return result;
    }

    /**
     * Starts the activation of the AppUnderTest regardless of the app currently in foreground.
     * Auth Delegation is enabled in policy
     *
     * @return true if activation was successful otherwise false
     */
    protected boolean doActivationWithAuthDelegation() {
        refreshShownScreensCounter();
        AbstractBBDUI screen = discoverCurrentUI();
        boolean result = false;
        if (screen != null) {
            result = screen.doAction();
        }

        boolean isEasyActivatorSet = eaPackageName != null && eaPackageName.equals(authDelPackageName) && eaPassword != null;
        boolean shouldUnlockMaster = authDelPassword != null;
        if (isEasyActivatorSet || !shouldUnlockMaster) {
            Log.d(TAG, "Complete of provisioning. Check your Slave app");
        } else {
            Log.d(TAG, "Try to unlock Master app");
            result &= new BBDUnlockUI(authDelPackageName, authDelPassword, Duration.of(Duration.PROVISIONING)).doAction();
        }

        printScreensQueue();
        Log.d(TAG, "Result of Login or activate: " + result);
        return result;
    }

    /**
     * Starts the provisioning of the AppUnderTest regardless of the app currently in foreground.
     *
     * @return true if provision was successful otherwise false
     */
    boolean doProvisioning() {
        refreshShownScreensCounter();
        AbstractBBDUI screen = discoverCurrentUI();
        boolean result = false;
        if (screen != null) {

            String screenID = null;
            try {
                screenID = (String) screen.getClass().getMethod("getScreenID").invoke(screen);
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "Method 'getScreenID' is not defined in the screen class '"
                        + screen.getClass().getSimpleName() + "'", e);
            } catch (IllegalAccessException | InvocationTargetException e) {
                Log.e(TAG, "Filed to invoke 'getScreenID' method of the screen class '"
                        + screen.getClass().getSimpleName() + "'", e);
            }

            if (Objects.equals(screenID, "bbd_set_password_view_UI")) {
                result = true;
            } else {
                screen.doAction();
            }
        }
        printScreensQueue();
        Log.d(TAG, "Result of provisioning: " + result);
        return result;
    }

    /**
     * Starts the activation of the AppUnderTest regardless of the app currently in foreground. Cancel fingerprint screen after setting up new password
     *
     * @return true if activation was successful otherwise false
     */
    protected boolean doActivationWithFingerprint() {
        boolean result = doActivation();
        if (result && fingerPrintEnabled) {
            if (getCurrentUIID() == 15) { //15 == bbd_fingerprint_container_UI
                new BBDFingerprintAlertUI(packageName).doAction();
            }
        }
        return result;
    }

    /**
     * Starts the activation of the AppUnderTest regardless of the app currently in foreground. Cancel fingerprint screen during easy activation.
     *
     * @return true if activation was successful otherwise false
     */
    protected boolean doEasyActivationWithFingerprint() {
        refreshShownScreensCounter();
        AbstractBBDUI screen = discoverCurrentUI();

        boolean result = false;

        if (screen != null) {
            if (screen instanceof BBDFingerprintAlertUI) {
                screen.doAction();
                result = doActivation();
            } else {
                result = screen.doAction();
            }
        }
        return result;
    }

    /**
     * Prints in Log file a queue of shown screens
     */
    protected void printScreensQueue() {
        Log.d(TAG, "Order of shown screens: ");
        for (int i = 0; i < shownScreensQueue.size(); i++) {
            Log.d(TAG, "Index of screen = " + i + " screen ID: " + shownScreensQueue.get(i));
        }
    }

    //Helper builders
    public abstract class AbstractBaseBuilder {
        /**
         * @param ui    object of uiAutomatorUtils
         * @param pName AppUnderTest package name
         */
        public AbstractBaseBuilder(AbstractUIAutomatorUtils ui, String pName) {
            BBDActivationHelper.this.uiAutomationUtils = ui;
            BBDActivationHelper.this.packageName = pName;
        }

        public abstract boolean doAction();
    }

    public abstract class AbstractAccessKeyBuilder extends AbstractBaseBuilder {

        /**
         * Login or activate AppUnderTest using Activation Password
         *
         * @param ui    object of uiAutomatorUtils
         * @param pName AppUnderTest package name
         * @param uName user name for activation
         * @param aPass activation password
         */
        public AbstractAccessKeyBuilder(AbstractUIAutomatorUtils ui, String pName, String uName, String aPass) {
            super(ui, pName);
            BBDActivationHelper.this.userName = uName;
            BBDActivationHelper.this.activationPassword = aPass;
        }
    }

    /**
     * Unlock AppUnderTest using its own password
     */
    public class LoginBuilder extends AbstractBaseBuilder {

        /**
         * @param ui    object of uiAutomatorUtils
         * @param pName AppUnderTest package name
         */
        private LoginBuilder(AbstractUIAutomatorUtils ui, String pName) {
            super(ui, pName);
        }

        /**
         * @param pass password of AppUnderTest
         * @return LoginBuilder object with set AppUnderTest password
         */
        public LoginBuilder setAppUnderTestPassword(String pass) {
            BBDActivationHelper.this.appPassword = pass;
            return this;
        }

        /**
         * @return true if all actions performed successfully otherwise false
         */
        @Override
        public boolean doAction() {
            BBDActivationHelper bb = new BBDActivationHelper();
            bb.uiAutomationUtils = BBDActivationHelper.this.uiAutomationUtils;
            bb.packageName = BBDActivationHelper.this.packageName;
            bb.appPassword = BBDActivationHelper.this.appPassword;
            return bb.doActivation();
        }
    }

    /**
     * Unlock AppUnderTest using its own password
     * closes fingerprint setup dialog in case if it exists
     */
    public class LoginWithFingerprintBuilder extends LoginBuilder {

        /**
         * @param ui    object of uiAutomatorUtils
         * @param pName AppUnderTest package name
         */
        private LoginWithFingerprintBuilder(AbstractUIAutomatorUtils ui, String pName) {
            super(ui, pName);
        }

        /**
         * @param pass password of AppUnderTest
         * @return LoginBuilder object with set AppUnderTest password
         */
        @Override
        public LoginWithFingerprintBuilder setAppUnderTestPassword(String pass) {
            return (LoginWithFingerprintBuilder) super.setAppUnderTestPassword(pass);
        }

        /**
         * @param fingerprint true if fingerprint is enabled, otherwise false
         * @return LoginFingerprintBuilder object with set value of fingerprint
         */
        public LoginWithFingerprintBuilder setFingerprintCheck(boolean fingerprint) {
            BBDActivationHelper.this.fingerPrintEnabled = fingerprint;
            return this;
        }

        /**
         * @return true if all actions performed successfully otherwise false
         */
        @Override
        public boolean doAction() {
            BBDActivationHelper bb = new BBDActivationHelper();
            bb.uiAutomationUtils = BBDActivationHelper.this.uiAutomationUtils;
            bb.packageName = BBDActivationHelper.this.packageName;
            bb.appPassword = BBDActivationHelper.this.appPassword;
            bb.fingerPrintEnabled = BBDActivationHelper.this.fingerPrintEnabled;
            return bb.doActivationWithFingerprint();
        }
    }

    /**
     * Login or activate AppUnderTest using Activation Password, unlock AppUnderTest using its own password
     */
    public class LoginOrActivateBuilder extends AbstractAccessKeyBuilder {

        /**
         * Login or activate AppUnderTest using Activation Password
         *
         * @param ui    object of uiAutomatorUtils
         * @param pName AppUnderTest package name
         * @param uName user name for activation
         * @param aPass activation password
         */
        protected LoginOrActivateBuilder(AbstractUIAutomatorUtils ui, String pName, String uName, String aPass) {
            super(ui, pName, uName, aPass);
        }

        /**
         * @param pass password of AppUnderTest
         * @return LoginOrActivateBuilder object with set AppUnderTest password
         */
        public LoginOrActivateBuilder setAppUnderTestPassword(String pass) {
            BBDActivationHelper.this.appPassword = pass;
            return this;
        }

        /**
         * @return true if all actions performed successfully otherwise false
         */
        @Override
        public boolean doAction() {
            BBDActivationHelper bb = new BBDActivationHelper();
            bb.uiAutomationUtils = BBDActivationHelper.this.uiAutomationUtils;
            bb.packageName = BBDActivationHelper.this.packageName;
            bb.appPassword = BBDActivationHelper.this.appPassword;
            bb.userName = BBDActivationHelper.this.userName;
            bb.activationPassword = BBDActivationHelper.this.activationPassword;
            return bb.doActivation();
        }
    }

    /**
     * Login or activate AppUnderTest using Activation Password, unlock AppUnderTest using its own password
     * closes fingerprint setup dialog in case if it exists
     */
    public class LoginOrActivateWithFingerprintBuilder extends LoginOrActivateBuilder {

        /**
         * Login or activate AppUnderTest using Activation Password
         *
         * @param ui    object of uiAutomatorUtils
         * @param pName AppUnderTest package name
         * @param uName user name for activation
         * @param aPass activation password
         */
        protected LoginOrActivateWithFingerprintBuilder(AbstractUIAutomatorUtils ui, String pName, String uName, String aPass) {
            super(ui, pName, uName, aPass);
        }

        /**
         * @param pass password of AppUnderTest
         * @return LoginOrActivateBuilder object with set AppUnderTest password
         */
        @Override
        public LoginOrActivateWithFingerprintBuilder setAppUnderTestPassword(String pass) {
            return (LoginOrActivateWithFingerprintBuilder) super.setAppUnderTestPassword(pass);
        }

        /**
         * @param fingerprint true if fingerprint is enabled, otherwise false
         * @return LoginFingerprintBuilder object with set value of fingerprint
         */
        public LoginOrActivateWithFingerprintBuilder setFingerprintCheck(boolean fingerprint) {
            BBDActivationHelper.this.fingerPrintEnabled = fingerprint;
            return this;
        }

        /**
         * @return true if all actions performed successfully otherwise false
         */
        @Override
        public boolean doAction() {
            BBDActivationHelper bb = new BBDActivationHelper();
            bb.uiAutomationUtils = BBDActivationHelper.this.uiAutomationUtils;
            bb.packageName = BBDActivationHelper.this.packageName;
            bb.appPassword = BBDActivationHelper.this.appPassword;
            bb.userName = BBDActivationHelper.this.userName;
            bb.activationPassword = BBDActivationHelper.this.activationPassword;
            bb.fingerPrintEnabled = BBDActivationHelper.this.fingerPrintEnabled;
            return bb.doActivationWithFingerprint();
        }
    }

    /**
     * Login or activate AppUnderTest using Activation Password, unlock AppUnderTest using its own password
     * closes fingerprint setup dialog during easy activation
     */
    public class LoginOrEasyActivateWithFingerprintBuilder extends LoginOrActivateBuilder {

        /**
         * Login or activate AppUnderTest using Activation Password
         *
         * @param ui    object of uiAutomatorUtils
         * @param pName AppUnderTest package name
         * @param uName user name for activation
         * @param aPass activation password
         */
        protected LoginOrEasyActivateWithFingerprintBuilder(AbstractUIAutomatorUtils ui, String pName, String uName, String aPass) {
            super(ui, pName, uName, aPass);
        }

        /**
         * @return true if all actions performed successfully otherwise false
         */
        @Override
        public boolean doAction() {
            BBDActivationHelper bb = new BBDActivationHelper();
            bb.uiAutomationUtils = BBDActivationHelper.this.uiAutomationUtils;
            bb.packageName = BBDActivationHelper.this.packageName;
            bb.appPassword = BBDActivationHelper.this.appPassword;
            bb.userName = BBDActivationHelper.this.userName;
            bb.activationPassword = BBDActivationHelper.this.activationPassword;
            bb.fingerPrintEnabled = BBDActivationHelper.this.fingerPrintEnabled;
            return bb.doEasyActivationWithFingerprint();
        }
    }
}
