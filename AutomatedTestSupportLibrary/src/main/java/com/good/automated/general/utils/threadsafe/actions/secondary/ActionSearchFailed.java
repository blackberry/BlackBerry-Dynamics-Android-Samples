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

import android.util.Pair;

import com.good.automated.general.utils.threadsafe.actions.ActionPriority;
import com.good.automated.general.utils.threadsafe.actions.IAction;

public class ActionSearchFailed implements IAction<Boolean> {

    @Override
    public Pair<Boolean, Boolean> doAction() {
        return Pair.create(false, false);
    }

    @Override
    public ActionPriority getPriority() {
        return ActionPriority.PRIORITY_SECONDARY;
    }
}
