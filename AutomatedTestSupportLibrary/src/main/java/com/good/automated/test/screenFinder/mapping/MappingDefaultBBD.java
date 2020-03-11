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

package com.good.automated.test.screenFinder.mapping;

import com.good.automated.test.screens.AbstractBBDActivationUI;
import com.good.automated.test.screens.AbstractBBDPasswordUI;
import com.good.automated.test.screens.BBDActivationProgressUI;
import com.good.automated.test.screens.BBDApplicationBlockUI;
import com.good.automated.test.screens.BBDDisclaimerUI;
import com.good.automated.test.screens.BBDEasyActivationSelectionUI;
import com.good.automated.test.screens.BBDEasyActivationUnlockUI;
import com.good.automated.test.screens.BBDFingerprintActivateUI;
import com.good.automated.test.screens.BBDFingerprintAlertUI;
import com.good.automated.test.screens.BBDLearnMoreUI;
import com.good.automated.test.screens.BBDNoPasswordUI;
import com.good.automated.test.screens.BBDPermissionUI;

import com.good.automated.test.screens.BBDUnlockUI;
import com.good.automated.test.screens.BBDUploadLogsUI;
import com.good.automated.test.screens.BBDWelcomeUI;
import com.good.automated.test.screenFinder.view.SearchableView;

import java.util.ArrayList;
import java.util.List;

public class MappingDefaultBBD {

    private static final String RESOURCE_ALERT = "alertTitle";
    private static final String RESOURCE_UNKNOWN = "unknown";
    private static final String RESOURCE_APP_UI = "appUI";
    private static final String RESOURCE_NOC_SELECTION_UI = "bbde_noc_selection_view_UI";

    private static final int ID_UNKNOWN = -1;
    private static final int ID_APP_UI = 0;
    private static final int ID_MIN_AVAILABLE = 1;

    public static final List<String> uiElementsList = new ArrayList<String>() {{
        add(BBDPermissionUI.getScreenID());
        add(BBDEasyActivationSelectionUI.getScreenID());
        add(BBDEasyActivationUnlockUI.getScreenID());
        add(BBDApplicationBlockUI.getScreenID());
        add(BBDDisclaimerUI.getScreenID());
        add(RESOURCE_NOC_SELECTION_UI);
        add(AbstractBBDActivationUI.getScreenID());
        add(BBDActivationProgressUI.getScreenID());
        add(BBDLearnMoreUI.getScreenID());
        add(BBDUnlockUI.getScreenID());
        add(BBDNoPasswordUI.getScreenID());
        add(AbstractBBDPasswordUI.getScreenID());
        add(BBDWelcomeUI.getScreenID());
        add(BBDUploadLogsUI.getScreenID());
        add(BBDFingerprintActivateUI.getScreenID());
        add(BBDFingerprintAlertUI.getScreenID());
        add(RESOURCE_ALERT);
    }};
    public static final SearchableView unknownView = new SearchableView(ID_UNKNOWN, RESOURCE_UNKNOWN);
    public static final SearchableView unknownAppUI = new SearchableView(ID_APP_UI, RESOURCE_UNKNOWN);
    public static final SearchableView appUI = new SearchableView(ID_APP_UI, RESOURCE_APP_UI);

    public static int getUniqueIdForMapper(ViewMapper viewMapper) {
        return viewMapper != null ? viewMapper.getList().size() + ID_MIN_AVAILABLE : ID_MIN_AVAILABLE;
    }
}
