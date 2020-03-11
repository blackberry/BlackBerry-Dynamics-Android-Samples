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

package com.good.automated.general.controls.impl;

import android.util.Log;

import com.good.automated.general.controls.ScrollView;
import com.good.automated.general.utils.AbstractUIAutomatorUtils;
import com.good.automated.general.utils.UIAutomatorUtilsFactory;

public class ScrollViewImpl implements ScrollView {
    private static final String TAG = ScrollViewImpl.class.getCanonicalName();
    private final ControlWrapper control;
    private static AbstractUIAutomatorUtils uiAutomatorUtils =
            UIAutomatorUtilsFactory.getUIAutomatorUtils();

    public ScrollViewImpl(ControlWrapper control) {
        this.control = control;
    }

    @Override
    public boolean scrollToBeginning() {
        return uiAutomatorUtils.scrollToTheBeginning(control.getResourceID());
    }

    @Override
    public boolean scrollToEnd() {
        return uiAutomatorUtils.scrollToTheEnd(control.getResourceID());
    }

    @Override
    public boolean scrollToText(String text) {
        return uiAutomatorUtils.scrollToText(control.getResourceID(), text);
    }

    @Override
    public boolean click() {
        try {
            return this.control.click();
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public String getText() {
        Log.d(TAG, "getText() = " + control.getText());
        return this.control.getText();
    }

    @Override
    public String getResourceID() {
        return this.control.getResourceID();
    }

    @Override
    public String getClassName() {
        return this.control.getClassName();
    }

    @Override
    public String getPackage() {
        return this.control.getPackage();
    }

    public static ScrollView getByID(String packageID, String id, int delay) {
        return new ScrollViewImpl(new ControlWrapper().getControlWrapperObject(packageID, id, delay));
    }

    public static ScrollView getByID(String packageID, String id) {
        return new ScrollViewImpl(new ControlWrapper().getControlWrapperObject(packageID, id));
    }
}
