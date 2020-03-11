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

package com.good.automated.general.utils.uitools.networking;

import android.os.Build;
import android.util.Log;

import com.good.automated.general.utils.uitools.exception.UiNetworkManagerException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Factory for creating platform specific {@link UiNetworkManager} instances.
 */
public class UiNetworkManagerFactory {

    private static final String TAG = UiNetworkManagerFactory.class.getSimpleName();

    private static final String SAMSUNG_MANUFACTURER_IDENTIFIER = "samsung";
    private static final String MOTOROLA_MANUFACTURER_IDENTIFIER = "motorola";
    private static final String OLD_EMULATOR_IDENTIFIER = "goldfish";
    private static final String NEW_EMULATOR_IDENTIFIER = "ranchu";
    private static final String INSTANTIATION_ERROR_MASSAGE = "Failed to instantiate manager for this platform. Manufacturer: %s; Model: %s; Hardware: %s; API level: %d";

    private static UiNetworkManager managerInstance;

    /**
     * Returns an instance of {@link UiNetworkManager} for current Android platform.
     * Usage of components which interacts with the UI is highly relays on the UI structure.
     * So for the support of any new Android version or for the specific vendor of the Android devices
     * this method should be revisited and if it is necessary new subclasses of {@link UiNetworkManager}
     * created.
     *
     * @return platform-specific {@link UiNetworkManager}
     * @throws UiNetworkManagerException in case if there is no {@link UiNetworkManager} for the current Android API version or vendor
     */
    public static UiNetworkManager getManager() {
        if (managerInstance != null) {
            return managerInstance;
        }

        if ((isNexus() || isMotorollaDevice() || isEmulator()) && satisfiesApiLevel(Arrays.asList(22, 23, 24, 25))) {
            Log.d(TAG, "Instantiating default manager");
            return managerInstance = new AndroidDefaultNetworkManager();
        }

        if (isSamsungDevice() && satisfiesApiLevel(Arrays.asList(22))) {
            Log.d(TAG, "Instantiating manager for Samsung devices with Android Lollipop");
            //default manager works fine in this case
            return managerInstance = new AndroidDefaultNetworkManager();
        }

        if (isSamsungDevice() && satisfiesApiLevel(Arrays.asList(24, 25))) {
            Log.d(TAG, "Instantiating manager for Samsung devices with Android Nougat");
            return managerInstance = new SamsungNougatNetworkManager();
        }

        if (satisfiesApiLevel(Arrays.asList(26, 27, 28, 29))) {
            Log.d(TAG, "Instantiating manager for Android API 26, 27, 28 and 29");
            return managerInstance = new AndroidOPNetworkManager();
        }

        //If no instance of UiNetworkManager was created for the current platform - invoking an exception
        String errorMessage = String.format(Locale.getDefault(), INSTANTIATION_ERROR_MASSAGE, Build.MANUFACTURER, Build.MODEL, Build.HARDWARE, Build.VERSION.SDK_INT);

        Log.e(TAG, errorMessage);
        throw new UiNetworkManagerException(errorMessage);

    }

    private static boolean isSamsungDevice() {
        return SAMSUNG_MANUFACTURER_IDENTIFIER.equalsIgnoreCase(Build.MANUFACTURER);
    }

    private static boolean isMotorollaDevice() {
        return MOTOROLA_MANUFACTURER_IDENTIFIER.equalsIgnoreCase(Build.MANUFACTURER);
    }

    private static boolean satisfiesApiLevel(List<Integer> apiLevels) {
        Log.d(TAG, "Version: " + Build.VERSION.SDK_INT);

        Set<Integer> supportedApiLevels = new HashSet<>(apiLevels);
        return supportedApiLevels.contains(Build.VERSION.SDK_INT);
    }

    private static boolean isNexus() {

        String model = Build.MODEL;

        if (model != null) {
            Log.d(TAG, "Model: " + model);
            return model.contains("Nexus");
        }

        return false;
    }

    private static boolean isEmulator() {
        return OLD_EMULATOR_IDENTIFIER.equals(Build.HARDWARE) || NEW_EMULATOR_IDENTIFIER.equals(Build.HARDWARE);
    }
}
