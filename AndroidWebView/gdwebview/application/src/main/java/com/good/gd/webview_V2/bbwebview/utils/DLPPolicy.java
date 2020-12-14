/*
 * Copyright (c) 2020 BlackBerry Limited.
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
package com.good.gd.webview_V2.bbwebview.utils;

import com.good.gd.GDAndroid;

import java.util.Map;

public class DLPPolicy {

    public static boolean isInboundDlpEnabled() {
        final Map<String, Object> appConfig = GDAndroid.getInstance().getApplicationConfig();
        return (Boolean) appConfig.get(GDAndroid.GDAppConfigKeyPreventDataLeakageIn);
    }

    public static boolean isDictationPreventionEnabled() {
        final Map<String, Object> appConfig = GDAndroid.getInstance().getApplicationConfig();
        return (Boolean) appConfig.get(GDAndroid.GDAppConfigKeyPreventAndroidDictation);
    }

    public static boolean isKeyboardRestrictionModeEnabled() {
        final Map<String, Object> appConfig = GDAndroid.getInstance().getApplicationConfig();
        return (Boolean) appConfig.get(GDAndroid.GDAppConfigKeyAndroidKeyboardRestrictedMode);
    }

}
