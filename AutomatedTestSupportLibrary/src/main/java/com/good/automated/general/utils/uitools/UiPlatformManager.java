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

package com.good.automated.general.utils.uitools;

import android.os.Build;
import android.util.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides help to other classes to determine the Platform for the device.
 */
public class UiPlatformManager {

    private static final String TAG = UiPlatformManager.class.getSimpleName();

    private static final String SAMSUNG_MANUFACTURER_IDENTIFIER = "samsung";
    private static final String LG_MANUFACTURER_IDENTIFIER = "LGE";
    private static final String OLD_EMULATOR_IDENTIFIER = "goldfish";
    private static final String NEW_EMULATOR_IDENTIFIER = "ranchu";

    private static UiPlatformManager _instance = null;

    public static UiPlatformManager getInstance(){

        if (_instance == null) {
            _instance = new UiPlatformManager();
        }
        return _instance;

    }

    private UiPlatformManager(){

    }

    public static boolean isSamsungDevice() {
        return SAMSUNG_MANUFACTURER_IDENTIFIER.equalsIgnoreCase(Build.MANUFACTURER);
    }

    public static boolean isLGDevice() {
        return LG_MANUFACTURER_IDENTIFIER.equalsIgnoreCase(Build.MANUFACTURER);
    }

    public static boolean satisfiesApiLevel(List<Integer> apiLevels) {
        Log.d(TAG, "Version: " + Build.VERSION.SDK_INT);

        Set<Integer> supportedApiLevels = new HashSet<>(apiLevels);
        return supportedApiLevels.contains(Build.VERSION.SDK_INT);
    }

    public static boolean isNexus() {

        String model = Build.MODEL;

        if (model != null) {
            Log.d(TAG, "Model: " + model);
            return model.contains("Nexus");
        }

        return false;
    }

    public static boolean isEmulator() {
        return OLD_EMULATOR_IDENTIFIER.equals(Build.HARDWARE) || NEW_EMULATOR_IDENTIFIER.equals(Build.HARDWARE);
    }
}
