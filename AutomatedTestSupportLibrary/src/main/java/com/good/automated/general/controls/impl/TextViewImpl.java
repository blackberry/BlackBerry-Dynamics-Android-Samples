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

import com.good.automated.general.controls.TextView;

public class TextViewImpl extends ControlBase implements TextView {

    public TextViewImpl(ControlWrapper control) {
        super(control, TextViewImpl.class.getCanonicalName());
    }

    @Override
    public String getText() {
       return super.getText();
    }

    @Override
    public String getResourceID() {
        return control.getResourceID();
    }

    @Override
    public String getClassName() {
        return control.getClassName();
    }

    @Override
    public String getPackage() {
        return control.getPackage();
    }

    public static TextView getByID(String packageID, String id, long delay) {
        return new TextViewImpl(new ControlWrapper().getControlWrapperObject(packageID, id, delay));
    }

    public static TextView getByID(String packageID, String id) {
        return new TextViewImpl(new ControlWrapper().getControlWrapperObject(packageID, id));
    }

    public static TextView getByIDAndText(String packageID, String id, String text, long delay) {
        return new TextViewImpl(new ControlWrapper().getControlWrapperObject(packageID, id, text, delay));
    }

    public static TextView getByText(String packageID, String text, long delay) {
        return new TextViewImpl(new ControlWrapper().getControlWrapperObjectByText(packageID, text, delay));
    }

    public static TextView getByText(String packageID, String text) {
        return new TextViewImpl(new ControlWrapper().getControlWrapperObjectByText(packageID, text));
    }

    @Override
    public boolean isEnabled() {
        return control.isEnabled();
    }

    @Override
    public boolean click() {
        return control.click();
    }

    @Override
    public boolean isAvailable() {
        return control.isAvailable();
    }

    public String getContentDescription() {
        String contentDescription = control.getContentDescription();
        Log.d(TAG, "getContentDescription() = " + contentDescription);
        return contentDescription;
    }
}
