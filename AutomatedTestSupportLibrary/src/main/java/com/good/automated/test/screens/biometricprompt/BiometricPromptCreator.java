/* Copyright (c) 2020 BlackBerry Limited.
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

package com.good.automated.test.screens.biometricprompt;

import android.os.Build;

public class BiometricPromptCreator {

    private static final String SYSTEM_PACKAGE_NAME = "com.android.systemui";
    private static final String SYSTEM_PACKAGE_NAME_OLD = "android";

    private static final String SDK_SCREEN_ID = "bbd_fingerprint_container_UI";

    private static final String FINGERPRINT_SYSTEM_ICON_ID = "fingerprint_icon";
    private static final String BIOMETRIC_SYSTEM_ICON_ID = "biometric_icon";

    private static final String FINGERPRINT_CANCEL_BTN_ID = "button2";
    private static final String BIOMETRIC_CANCEL_BTN_ID = "button_negative";


    private static final String SDK_TITLE_ID = "fingerprint_title";
    private static final String SDK_DESCRIPTION_ID = "fingerprint_text";

    private static final String SYSTEM_TITLE_ID = "title";
    private static final String SYSTEM_DESCRIPTION_ID = "description";

    public static BiometricPromptIdProvider createProvider(String appPackageName) {

        int os = Build.VERSION.SDK_INT;

        if (os < Build.VERSION_CODES.P) {
            return createForAndroidN(appPackageName);
        }

        if (os == Build.VERSION_CODES.P) {
            return createForAndroidP();
        }

        if (os == Build.VERSION_CODES.Q) {
            return createForAndroidQ();
        }

        return createForAndroidR();
    }

    private static BiometricPromptIdProvider createForAndroidN(String appPackageName) {
        return new BiometricPromptIdProvider(appPackageName, SYSTEM_PACKAGE_NAME_OLD, SDK_SCREEN_ID,
                SDK_TITLE_ID, SDK_DESCRIPTION_ID, FINGERPRINT_CANCEL_BTN_ID);
    }

    private static BiometricPromptIdProvider createForAndroidP() {
        return new BiometricPromptIdProvider(SYSTEM_PACKAGE_NAME, FINGERPRINT_SYSTEM_ICON_ID,
                SYSTEM_TITLE_ID, SYSTEM_DESCRIPTION_ID, FINGERPRINT_CANCEL_BTN_ID);
    }

    private static BiometricPromptIdProvider createForAndroidQ() {
        return new BiometricPromptIdProvider(SYSTEM_PACKAGE_NAME, BIOMETRIC_SYSTEM_ICON_ID,
                SYSTEM_TITLE_ID, SYSTEM_DESCRIPTION_ID, FINGERPRINT_CANCEL_BTN_ID);
    }

    private static BiometricPromptIdProvider createForAndroidR() {
        return new BiometricPromptIdProvider(SYSTEM_PACKAGE_NAME, BIOMETRIC_SYSTEM_ICON_ID,
                SYSTEM_TITLE_ID, SYSTEM_DESCRIPTION_ID, BIOMETRIC_CANCEL_BTN_ID);
    }

}
