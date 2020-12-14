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

package com.good.automated.test.screens;

import android.util.Log;

import com.good.automated.general.controls.Button;
import com.good.automated.general.controls.Clickable;
import com.good.automated.general.controls.TextView;
import com.good.automated.general.controls.impl.ButtonImpl;
import com.good.automated.general.controls.impl.TextViewImpl;
import com.good.automated.general.utils.Duration;
import com.good.automated.test.screens.biometricprompt.BiometricPromptCreator;
import com.good.automated.test.screens.biometricprompt.BiometricPromptIdProvider;

import static com.good.automated.general.utils.Duration.WAIT_FOR_SCREEN;

public class BBDBiometricPromptUI extends AbstractBBDUI implements Clickable {

    private static final String TAG = BBDFingerprintAlertUI.class.getSimpleName();
    private BiometricPromptIdProvider idProvider;
    private BBDBiometricPromptUIMap controls;

    /**
     * Mapping of BiometricPrompt dialog.
     *
     * @param packageName package name of app under test
     */
    public BBDBiometricPromptUI(String packageName) {
        idProvider = BiometricPromptCreator.createProvider(packageName);
        controls = new BBDBiometricPromptUIMap();
    }

    public BBDBiometricPromptUI(String packageName, long delay) {
        idProvider = BiometricPromptCreator.createProvider(packageName);

        if (!getUiAutomationUtils().isResourceWithIDShown(idProvider.getPackageName(), idProvider.getScreenId(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }

        controls = new BBDBiometricPromptUIMap();
    }

    /**
     * @return fingerprint title
     */
    public String getFingerprintTitle() {
        try {
            return controls.getFingerprintTitle().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    /**
     * @return fingerprint text message
     */
    public String getFingerprintText() {
        try {
            return controls.getFingerprintText().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    /**
     * Fingerprint setup will be always canceled
     * in purpose of successful provisioning of the app using predefined flow.
     *
     * @return true if dialog was canceled, otherwise false
     */
    @Override
    public boolean click() {
        try {
            return controls.getBtnCancel().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fingerprint setup will be always canceled.
     *
     * @return true if canceled, otherwise false
     */
    @Override
    public boolean doAction() {
        return click();
    }

    private class BBDBiometricPromptUIMap {

        public TextView getFingerprintTitle() {
            return TextViewImpl.getByID(idProvider.getPackageName(), idProvider.getTitleId(),
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getFingerprintText() {
            return TextViewImpl.getByID(idProvider.getPackageName(), idProvider.getDescriptionId(),
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnCancel() {
            return ButtonImpl.getByID(idProvider.getSystemPackageName(), idProvider.getCancelButtonId(),
                    Duration.of(WAIT_FOR_SCREEN));
        }

    }

}
