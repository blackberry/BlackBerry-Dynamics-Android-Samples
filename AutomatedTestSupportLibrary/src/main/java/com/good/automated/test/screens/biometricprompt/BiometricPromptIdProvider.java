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

public class BiometricPromptIdProvider {

    private String packageName;
    private String systemPackageName;
    private String screenId;
    private String titleId;
    private String descriptionId;
    private String cancelButtonId;

    public BiometricPromptIdProvider(String packageName, String screenId, String titleId,
                                     String descriptionId, String cancelButtonId) {
        this.packageName = packageName;
        this.screenId = screenId;
        this.titleId = titleId;
        this.descriptionId = descriptionId;
        this.cancelButtonId = cancelButtonId;
        this.systemPackageName = packageName;
    }

    public BiometricPromptIdProvider(String packageName, String systemPackageName, String screenId,
                                     String titleId, String descriptionId, String cancelButtonId) {
        this(packageName, screenId, titleId, descriptionId, cancelButtonId);
        this.systemPackageName = systemPackageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getSystemPackageName() {
        return systemPackageName;
    }

    public String getScreenId() {
        return screenId;
    }

    public String getTitleId() {
        return titleId;
    }

    public String getDescriptionId() {
        return descriptionId;
    }

    public String getCancelButtonId() {
        return cancelButtonId;
    }
}
