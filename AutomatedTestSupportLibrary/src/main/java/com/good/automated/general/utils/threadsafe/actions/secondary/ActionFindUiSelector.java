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

package com.good.automated.general.utils.threadsafe.actions.secondary;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;
import android.util.Pair;

import com.good.automated.general.utils.threadsafe.actions.ActionPriority;
import com.good.automated.general.utils.threadsafe.actions.UiDeviceBasedAbstractAction;

public class ActionFindUiSelector extends UiDeviceBasedAbstractAction<Boolean> {

    private String resourceId;

    public ActionFindUiSelector(UiDevice uiDevice, String... params) {
        super(uiDevice);

        StringBuilder sb = new StringBuilder();
        for (String param : params) {
            sb.append(param);
        }

        this.resourceId = sb.toString();
    }

    @Override
    public Pair<Boolean, Boolean> doAction() {
        UiObject uiObject = getUiDevice().findObject(new UiSelector().resourceId(resourceId));
        return Pair.create(true, uiObject.exists());
    }

    @Override
    public ActionPriority getPriority() {
        return ActionPriority.PRIORITY_SECONDARY;
    }
}
