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

import com.good.automated.general.utils.AbstractUIAutomatorUtils;
import com.good.automated.general.utils.UIAutomatorUtilsFactory;
import com.good.automated.test.screens.AbstractBBDUI;
import com.good.automated.test.screens.BBDForgotPasswordActivationUI;
import com.good.automated.test.screens.BBDRemoteBlockUI;
import com.good.automated.test.screens.BBDRemoteUnlockActivationUI;
import com.good.automated.test.screens.BBDUnlockUI;

public class BBDAdvancedActivationHelper extends BBDActivationHelper {

    private static final String TAG = BBDAdvancedActivationHelper.class.getCanonicalName();

    private BBDAdvancedActivationHelper() {
        super();
    }

    /**
     * Do activation of the app under test despite on displayed screen.
     *
     * @return true if activation was successful otherwise false
     */
    protected boolean doEnforceForgotPasswordActivation() {
        AbstractBBDUI screen = new BBDUnlockUI(packageName);
        //Enforce Forgot Password UI
        if (screen.doAction()) {
            screen = new BBDForgotPasswordActivationUI(packageName, userName, pin1, pin2, pin3);
            if (screen.doAction()) {
                return doActivation();
            }
        }
        printScreensQueue();
        return false;
    }

    /**
     * Do remote Unlock and activation of the app under test despite on displayed screen.
     *
     * @return true if remote unlock and activation was successful otherwise false
     */
    protected boolean doRemoteUnlockActivation() {

        AbstractBBDUI screen;

        //In case if app was Remote Locked while running, flow will start from Remote Block screen
        if (uiElementsList.indexOf(uiAutomationUtils.getUiElementShown(packageName, uiElementsList)) == 3) {
            screen = new BBDRemoteBlockUI(packageName);
            if (!screen.doAction()) {
                return false;
            }
        }
        screen = new BBDRemoteUnlockActivationUI(packageName, userName, pin1, pin2, pin3);
        if (screen.doAction()) {
            return doActivation();
        }
        printScreensQueue();
        return false;
    }

    /**
     * Login or activation application using Access Key and Unlock password of Auth Delegate (Master) app
     *
     * @param ui    object of UIAutomatorUtils
     * @param pName AppUnderTest package name
     * @param uName username
     * @param aKey  access key (15 characters)
     * @return EnforceNoPasswordActivationBuilder with set required parameters for simple login or activation logic
     */
    public static EnforceNoPasswordActivationBuilder enforceNoPasswordActivationBuilder(AbstractUIAutomatorUtils ui, String pName, String uName, String aKey) {
        return new BBDAdvancedActivationHelper().new EnforceNoPasswordActivationBuilder(ui, pName, uName, aKey);
    }

    /**
     * Provision application
     *
     * @param ui    object of UIAutomatorUtils
     * @param pName AppUnderTest package name
     * @param uName username
     * @param aKey  access key (15 characters)
     * @return ProvisioningProcessBuilder with set required parameters f
     */
    public static ProvisioningProcessBuilder provisioningProcessBuilder(AbstractUIAutomatorUtils ui, String pName, String uName, String aKey) {
        return new BBDAdvancedActivationHelper().new ProvisioningProcessBuilder(ui, pName, uName, aKey);
    }

    /**
     * Activates slave application via master using Auth Delegation feature.
     *
     * @param masterPackageName     package name of AuthDelegate(master) application
     * @param isEAProducerLocked flag that defines whether EA Producer app container is locked
     * @return true in case of success else false
     */
    public static boolean authDelegateApp(String masterPackageName, boolean isEAProducerLocked) {
        AbstractUIAutomatorUtils uiAutomationUtils = UIAutomatorUtilsFactory.getUIAutomatorUtils();
        return BBDAdvancedActivationHelper.loginOrActivateEnforceAuthDelegationBuilder(uiAutomationUtils, uiAutomationUtils.getAppPackageName(),
                uiAutomationUtils.getAppVersionName(masterPackageName),
                uiAutomationUtils.getAccessKey(masterPackageName))
                .setAuthDelegatorPackageName(masterPackageName)
                .setAuthDelegatorPassword(uiAutomationUtils.getAppProvisionPassword(masterPackageName))
                .setIsEAProviderLocked(isEAProducerLocked)
                .doAction();

    }


    /**
     * Login or activation application using Access Key and Unlock password of Auth Delegate (Master) app
     *
     * @param ui    object of UIAutomatorUtils
     * @param pName AppUnderTest package name
     * @param uName username
     * @param aKey  access key (15 characters)
     * @return LoginOrActivateEnforceAuthDelegationBuilder with set required parameters for simple login or activation logic
     */
    public static LoginOrActivateEnforceAuthDelegationBuilder loginOrActivateEnforceAuthDelegationBuilder(AbstractUIAutomatorUtils ui, String pName, String uName, String aKey) {
        return new BBDAdvancedActivationHelper().new LoginOrActivateEnforceAuthDelegationBuilder(ui, pName, uName, aKey);
    }

    /**
     * Activates slave application via master using Easy Activation feature.
     *
     * @param pName    package name of delegator(slave) application
     * @param masterApplicationName application name of delegate(master) application
     * @param masterPackageName     package name of delegate(master) application
     * @return true in case of success else false
     */
    public static boolean easyActivateApp(String pName, String masterApplicationName, String masterPackageName) {
        AbstractUIAutomatorUtils uiAutomationUtils = UIAutomatorUtilsFactory.getUIAutomatorUtils();
        return BBDAdvancedActivationHelper.loginOrEasyActivateBuilder(uiAutomationUtils, pName,
                masterPackageName,
                masterApplicationName,
                uiAutomationUtils.getAppProvisionPassword(masterPackageName))
                .setAppUnderTestPassword(uiAutomationUtils.getAppProvisionPassword(pName))
                .doAction();

    }

    /**
     * Login or activation application using Easy Activation feature and its own Unlock password
     *
     * @param ui      object of UIAutomatorUtils
     * @param pName   AppUnderTest package name
     * @param eaPName Easy Activator package name
     * @param eaN     Easy Activator app name
     * @param eaPass  Easy Activator password
     * @return LoginOrEasyActivateBuilder with set required parameters for simple login or activation logic
     */
    public static LoginOrEasyActivateBuilder loginOrEasyActivateBuilder(AbstractUIAutomatorUtils ui, String pName, String eaPName, String eaN, String eaPass) {
        return new BBDAdvancedActivationHelper().new LoginOrEasyActivateBuilder(ui, pName, eaPName, eaN, eaPass);
    }

    /**
     * Login or activate application using Easy Activation feature and Unlock password of Auth Delegate (Master) app
     *
     * @param ui      object of uiAutomatorUtils
     * @param pName   AppUnderTest package name
     * @param eaPName Easy Activator package name
     * @param eaN     Easy Activator app name
     * @param eaPass  Easy Activator password
     * @return LoginOrEasyActivateEnforceAuthDelegationBuilder with set required parameters for simple login or activation logic
     */
    public static LoginOrEasyActivateEnforceAuthDelegationBuilder loginOrEasyActivateEnforceAuthDelegationBuilder(AbstractUIAutomatorUtils ui, String pName, String eaPName, String eaN, String eaPass) {
        return new BBDAdvancedActivationHelper().new LoginOrEasyActivateEnforceAuthDelegationBuilder(ui, pName, eaPName, eaN, eaPass);
    }

    /**
     * Enforce Forgot Password screen and activate application using Unlock Key and it's own Unlock password
     *
     * @param ui    object of uiAutomatorUtils
     * @param pName AppUnderTest package name
     * @return EnforceForgotPasswordActivationBuilder with set required parameters for simple login or activation logic
     */
    public static EnforceForgotPasswordActivationBuilder enforceForgotPasswordActivationBuilder(AbstractUIAutomatorUtils ui, String pName, String uName, String uKey) {
        return new BBDAdvancedActivationHelper().new EnforceForgotPasswordActivationBuilder(ui, pName, uName, uKey);
    }

    /**
     * Enforce Remote Unlock screen and activate application using Unlock Key and it's own Unlock password
     *
     * @param ui    object of uiAutomatorUtils
     * @param pName AppUnderTest package name
     * @return EnforceRemoteUnlockActivationBuilder with set required parameters for simple login or activation logic
     */
    public static EnforceRemoteUnlockActivationBuilder enforceRemoteUnlockActivationBuilder(AbstractUIAutomatorUtils ui, String pName, String uName, String uKey) {
        return new BBDAdvancedActivationHelper().new EnforceRemoteUnlockActivationBuilder(ui, pName, uName, uKey);
    }

    public abstract class AbstractEasyActivateBuilder extends AbstractBaseBuilder {

        /**
         * @param ui      object of uiAutomatorUtils
         * @param pName   AppUnderTest package name
         * @param eaPName Easy Activator package name
         * @param eaN     Easy Activator app name
         * @param eaPass  Easy Activator password
         */
        public AbstractEasyActivateBuilder(AbstractUIAutomatorUtils ui, String pName, String eaPName, String eaN, String eaPass) {
            super(ui, pName);
            BBDAdvancedActivationHelper.this.eaPackageName = eaPName;
            BBDAdvancedActivationHelper.this.eaName = eaN;
            BBDAdvancedActivationHelper.this.eaPassword = eaPass;
        }
    }

    public abstract class AbstractAuthDelegateBuilder extends AbstractAccessKeyBuilder {

        /**
         * Login or activate app under test using Access Key & AuthDelegation
         *
         * @param ui      object of uiAutomatorUtils
         * @param pName   AppUnderTest package name
         * @param uName   user name for activation
         * @param aKey    access key (15 characters)
         */
        public AbstractAuthDelegateBuilder(AbstractUIAutomatorUtils ui, String pName, String uName, String aKey) {
            super(ui, pName, uName, aKey);
            BBDAdvancedActivationHelper.this.userName = uName;
            BBDAdvancedActivationHelper.this.pin1 = aKey.substring(0, 5);
            BBDAdvancedActivationHelper.this.pin2 = aKey.substring(5, 10);
            BBDAdvancedActivationHelper.this.pin3 = aKey.substring(10);
        }
    }

    public class EnforceNoPasswordActivationBuilder extends AbstractAccessKeyBuilder {

        /**
         * Login or activate app under test using Access Key
         *
         * @param ui    object of uiAutomatorUtils
         * @param pName AppUnderTest package name
         * @param uName user name for activation
         * @param aKey  access key (15 characters)
         */
        public EnforceNoPasswordActivationBuilder(AbstractUIAutomatorUtils ui, String pName, String uName, String aKey) {
            super(ui, pName, uName, aKey);
        }

        @Override
        public boolean doAction() {
            BBDActivationHelper bb = new BBDAdvancedActivationHelper();
            bb.uiAutomationUtils = BBDAdvancedActivationHelper.this.uiAutomationUtils;
            bb.packageName = BBDAdvancedActivationHelper.this.packageName;
            bb.userName = BBDAdvancedActivationHelper.this.userName;
            bb.pin1 = BBDAdvancedActivationHelper.this.pin1;
            bb.pin2 = BBDAdvancedActivationHelper.this.pin2;
            bb.pin3 = BBDAdvancedActivationHelper.this.pin3;
            return bb.doActivation();
        }
    }

    public class ProvisioningProcessBuilder extends AbstractAccessKeyBuilder {

        /**
         * Provision app under test using Access Key
         *
         * @param ui    object of uiAutomatorUtils
         * @param pName AppUnderTest package name
         * @param uName user name for activation
         * @param aKey  access key (15 characters)
         */
        public ProvisioningProcessBuilder(AbstractUIAutomatorUtils ui, String pName, String uName, String aKey) {
            super(ui, pName, uName, aKey);
        }

        @Override
        public boolean doAction() {
            BBDActivationHelper bb = new BBDAdvancedActivationHelper();
            bb.uiAutomationUtils = BBDAdvancedActivationHelper.this.uiAutomationUtils;
            bb.packageName = BBDAdvancedActivationHelper.this.packageName;
            bb.userName = BBDAdvancedActivationHelper.this.userName;
            bb.pin1 = BBDAdvancedActivationHelper.this.pin1;
            bb.pin2 = BBDAdvancedActivationHelper.this.pin2;
            bb.pin3 = BBDAdvancedActivationHelper.this.pin3;
            return bb.doProvisioning();
        }
    }

    /**
     * Login or activate AppUnderTest using Access Key, unlock AppUnderTest using Auth Delegator app
     */
    public class LoginOrActivateEnforceAuthDelegationBuilder extends AbstractAuthDelegateBuilder {

        private boolean isEAProviderLocked = true;

        /**
         * @param ui    object of uiAutomatorUtils
         * @param pName AppUnderTest package name
         * @param uName user name for activation
         * @param aKey  access key (15 characters)
         */
        private LoginOrActivateEnforceAuthDelegationBuilder(AbstractUIAutomatorUtils ui, String pName, String uName, String aKey) {
            super(ui, pName, uName, aKey);
        }

        /**
         * @param pName AuthDelegatorApp package name
         * @return LoginOrActivateEnforceAuthDelegatorBuilder object with set AuthDelegatorApp package name
         */
        public LoginOrActivateEnforceAuthDelegationBuilder setAuthDelegatorPackageName(String pName) {
            BBDAdvancedActivationHelper.this.authDelPackageName = pName;
            return this;
        }

        /**
         * @param aPass AuthDelegatorApp password
         * @return LoginOrActivateEnforceAuthDelegatorBuilder object with set AuthDelegatorApp password
         */
        public LoginOrActivateEnforceAuthDelegationBuilder setAuthDelegatorPassword(String aPass) {
            BBDAdvancedActivationHelper.this.authDelPassword = aPass;
            return this;
        }

        /**
         * @param isEAProviderLocked flag that defines whether EA Producer app container is locked
         * @return LoginOrActivateEnforceAuthDelegatorBuilder object with set isEAProviderLocked flag
         */
        public LoginOrActivateEnforceAuthDelegationBuilder setIsEAProviderLocked(boolean isEAProviderLocked) {
            LoginOrActivateEnforceAuthDelegationBuilder.this.isEAProviderLocked = isEAProviderLocked;
            return this;
        }

        /**
         * @return true if all actions performed successfully otherwise false
         */
        @Override
        public boolean doAction() {
            BBDAdvancedActivationHelper bb = new BBDAdvancedActivationHelper();
            bb.uiAutomationUtils = BBDAdvancedActivationHelper.this.uiAutomationUtils;
            bb.packageName = BBDAdvancedActivationHelper.this.packageName;
            bb.userName = BBDAdvancedActivationHelper.this.userName;
            bb.pin1 = BBDAdvancedActivationHelper.this.pin1;
            bb.pin2 = BBDAdvancedActivationHelper.this.pin2;
            bb.pin3 = BBDAdvancedActivationHelper.this.pin3;
            bb.authDelPackageName = BBDAdvancedActivationHelper.this.authDelPackageName;
            bb.authDelPassword = isEAProviderLocked ? BBDAdvancedActivationHelper.this.authDelPassword : null;
            bb.eaPackageName = authDelPackageName;

            return bb.doActivationWithAuthDelegation();
        }
    }

    /**
     * Login or activate AppUnderTest using Easy Activator app, unlock AppUnderTest using its own password
     */
    public class LoginOrEasyActivateBuilder extends AbstractEasyActivateBuilder {

        /**
         * @param ui      object of uiAutomatorUtils
         * @param pName   AppUnderTest package name
         * @param eaPName Easy Activator package name
         * @param eaN     Easy Activator app name
         * @param eaPass  Easy Activator password
         */
        public LoginOrEasyActivateBuilder(AbstractUIAutomatorUtils ui, String pName, String eaPName, String eaN, String eaPass) {
            super(ui, pName, eaPName, eaN, eaPass);
        }

        /**
         * @param pass password of AppUnderTest
         * @return LoginOrEasyActivateBuilder object with set AppUnderTest password
         */
        public LoginOrEasyActivateBuilder setAppUnderTestPassword(String pass) {
            BBDAdvancedActivationHelper.this.appPassword = pass;
            return this;
        }

        /**
         * @return true if all actions performed successfully otherwise false
         */
        @Override
        public boolean doAction() {
            BBDAdvancedActivationHelper bb = new BBDAdvancedActivationHelper();
            bb.uiAutomationUtils = BBDAdvancedActivationHelper.this.uiAutomationUtils;
            bb.packageName = BBDAdvancedActivationHelper.this.packageName;
            bb.appPassword = BBDAdvancedActivationHelper.this.appPassword;
            bb.eaName = BBDAdvancedActivationHelper.this.eaName;
            bb.eaPackageName = BBDAdvancedActivationHelper.this.eaPackageName;
            bb.eaPassword = BBDAdvancedActivationHelper.this.eaPassword;
            return bb.doActivation();
        }
    }

    /**
     * Login or activate AppUnderTest using Easy Activator app, unlock AppUnderTest using Auth Delegator app
     */
    public class LoginOrEasyActivateEnforceAuthDelegationBuilder extends AbstractEasyActivateBuilder {

        /**
         * @param ui      object of uiAutomatorUtils
         * @param pName   AppUnderTest package name
         * @param eaPName Easy Activator package name
         * @param eaN     Easy Activator app name
         * @param eaPass  Easy Activator password
         */
        public LoginOrEasyActivateEnforceAuthDelegationBuilder(AbstractUIAutomatorUtils ui, String pName, String eaPName, String eaN, String eaPass) {
            super(ui, pName, eaPName, eaN, eaPass);
        }

        /**
         * @param pName AuthDelegatorApp package name
         * @return LoginOrEasyActivateEnforceAuthDelegatorBuilder object with set AuthDelegatorApp package name
         */
        public LoginOrEasyActivateEnforceAuthDelegationBuilder setAuthDelegatorPackageName(String pName) {
            BBDAdvancedActivationHelper.this.authDelPackageName = pName;
            return this;
        }

        /**
         * @param aPass AuthDelegatorApp password
         * @return LoginOrEasyActivateEnforceAuthDelegatorBuilder object with set AuthDelegatorApp password
         */
        public LoginOrEasyActivateEnforceAuthDelegationBuilder setAuthDelegatorPassword(String aPass) {
            BBDAdvancedActivationHelper.this.authDelPassword = aPass;
            return this;
        }

        /**
         * @return true if all actions performed successfully otherwise false
         */
        @Override
        public boolean doAction() {
            BBDAdvancedActivationHelper bb = new BBDAdvancedActivationHelper();
            bb.uiAutomationUtils = BBDAdvancedActivationHelper.this.uiAutomationUtils;
            bb.packageName = BBDAdvancedActivationHelper.this.packageName;
            bb.eaName = BBDAdvancedActivationHelper.this.eaName;
            bb.eaPackageName = BBDAdvancedActivationHelper.this.eaPackageName;
            bb.eaPassword = BBDAdvancedActivationHelper.this.eaPassword;
            bb.authDelPackageName = BBDAdvancedActivationHelper.this.authDelPackageName;
            bb.authDelPassword = BBDAdvancedActivationHelper.this.authDelPassword;
            return bb.doActivationWithAuthDelegation();
        }
    }

    /**
     * Enforce Forgot Password screen and activate AppUnderTest using Unlock Key, unlock AppUnderTest using it's own password
     * This logic start working from Unlock password screen by tapping on Forgot Password label.
     */
    public class EnforceForgotPasswordActivationBuilder extends LoginOrActivateBuilder {

        /**
         * Activate AppUnderTest using Unlock Key
         *
         * @param ui    object of uiAutomatorUtils
         * @param pName AppUnderTest package name
         * @param uName user name for activation
         * @param uKey  unlock key (15 characters)
         */
        public EnforceForgotPasswordActivationBuilder(AbstractUIAutomatorUtils ui, String pName, String uName, String uKey) {
            super(ui, pName, uName, uKey);
        }

        /**
         * @return true if all actions performed successfully otherwise false
         */
        @Override
        public boolean doAction() {
            BBDAdvancedActivationHelper bb = new BBDAdvancedActivationHelper();
            bb.uiAutomationUtils = BBDAdvancedActivationHelper.this.uiAutomationUtils;
            bb.packageName = BBDAdvancedActivationHelper.this.packageName;
            bb.appPassword = BBDAdvancedActivationHelper.this.appPassword;
            bb.userName = BBDAdvancedActivationHelper.this.userName;
            bb.pin1 = BBDAdvancedActivationHelper.this.pin1;
            bb.pin2 = BBDAdvancedActivationHelper.this.pin2;
            bb.pin3 = BBDAdvancedActivationHelper.this.pin3;
            return bb.doEnforceForgotPasswordActivation();
        }
    }

    /**
     * Enforce Remote Unlock screen and activate AppUnderTest using Unlock Key, unlock AppUnderTest using it's own password.
     * If app was running in foreground we will start Unlocking it from RemoteBlock screen
     * If app was force re-started, then we will start activation process from Unlock Key screen.
     */
    public class EnforceRemoteUnlockActivationBuilder extends LoginOrActivateBuilder {

        /**
         * Activate AppUnderTest using Unlock Key
         *
         * @param ui    object of uiAutomatorUtils
         * @param pName AppUnderTest package name
         * @param uName user name for activation
         * @param uKey  unlock key (15 characters)
         */
        public EnforceRemoteUnlockActivationBuilder(AbstractUIAutomatorUtils ui, String pName, String uName, String uKey) {
            super(ui, pName, uName, uKey);
        }

        /**
         * @return true if all actions performed successfully otherwise false
         */
        @Override
        public boolean doAction() {
            BBDAdvancedActivationHelper bb = new BBDAdvancedActivationHelper();
            bb.uiAutomationUtils = BBDAdvancedActivationHelper.this.uiAutomationUtils;
            bb.packageName = BBDAdvancedActivationHelper.this.packageName;
            bb.appPassword = BBDAdvancedActivationHelper.this.appPassword;
            bb.userName = BBDAdvancedActivationHelper.this.userName;
            bb.pin1 = BBDAdvancedActivationHelper.this.pin1;
            bb.pin2 = BBDAdvancedActivationHelper.this.pin2;
            bb.pin3 = BBDAdvancedActivationHelper.this.pin3;
            return bb.doRemoteUnlockActivation();
        }
    }
}
