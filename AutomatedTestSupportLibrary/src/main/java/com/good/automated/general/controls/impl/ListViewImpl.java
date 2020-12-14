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

import com.good.automated.general.controls.ListView;

public class ListViewImpl extends ControlBase implements ListView {

    public ListViewImpl(ControlWrapper control)  {
        super(control, ListViewImpl.class.getCanonicalName());
    }

    @Override
    public String getText() {
        return super.getText();
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

    @Override
    public boolean click() {
        try {
            return this.control.click();
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static ListView getByID(String packageID, String id, int delay) {
        return new ListViewImpl(new ControlWrapper().getControlWrapperObject(packageID, id, delay));
    }

    public static ListView getByID(String packageID, String id) {
        return new ListViewImpl(new ControlWrapper().getControlWrapperObject(packageID, id));
    }

    @Override
    public int getChildCount() {
        return control.getChildCount();
    }

    @Override
    public ControlWrapper getChildElement(int i) {
        return control.getChildElement(i);
    }
}
