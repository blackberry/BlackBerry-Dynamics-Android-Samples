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

package com.good.automated.general.controls.stub;

import android.util.Log;

import com.good.automated.general.controls.CheckBox;
import com.good.automated.general.controls.impl.CheckBoxImpl;

public class CheckBoxStub implements CheckBox {

    private static final String TAG = CheckBoxImpl.class.getCanonicalName();

    private CheckBoxStub() { }

    @Override
    public boolean click() {
        return true;
    }

    @Override
    public String getResourceID() {
        return "";
    }

    @Override
    public String getClassName() {
        return "";
    }

    @Override
    public String getPackage() {
        return "";
    }

    @Override
    public boolean isSelected() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isClickable() {
        return true;
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    @Override
    public boolean isCheckable() {
        return true;
    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public String getText() {
        Log.d(TAG, "getText() = Stub");
        return "Stub";
    }

    public static CheckBox getStub() {
        return new CheckBoxStub();
    }
}
