/* Copyright (c) 2023 BlackBerry Ltd.
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

package com.good.gd.example.cutcopypaste.dlp;

import com.good.gd.GDAndroid;

import java.util.Map;

// Helper class for checking DLP policies
class DLPPolicies {

    private static final DLPPolicies ourInstance = new DLPPolicies();

    static DLPPolicies getInstance() {
        return ourInstance;
    }

    private DLPPolicies() {}


    /**
     * @return "Prevent copy from non-GD apps into GD apps" policy flag on GC
     */
    public boolean isInboundDlpEnabled() {
        final Map<String, Object> appConfig = GDAndroid.getInstance().getApplicationConfig();
        return (Boolean) appConfig.get(GDAndroid.GDAppConfigKeyPreventDataLeakageIn);
    }

    /**
     * @return "Prevent copy from GD apps into non-GD apps" policy flag on GC
     */
    boolean isOutboundDlpEnabled() {
        final Map<String, Object> appConfig = GDAndroid.getInstance().getApplicationConfig();
        return (Boolean) appConfig.get(GDAndroid.GDAppConfigKeyPreventDataLeakageOut);
    }

    /**
     * @return "Prevent Android Dictation" policy flag on GC
     */
    public boolean isDictationPreventionEnabled() {
        final Map<String, Object> appConfig = GDAndroid.getInstance().getApplicationConfig();
        return (Boolean) appConfig.get(GDAndroid.GDAppConfigKeyPreventAndroidDictation);
    }
}
