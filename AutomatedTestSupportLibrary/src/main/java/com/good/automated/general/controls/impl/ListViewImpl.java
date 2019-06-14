/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.general.controls.impl;

import android.util.Log;

import com.good.automated.general.controls.ListView;

public class ListViewImpl implements ListView {
    private static final String TAG = ListViewImpl.class.getCanonicalName();
    private final ControlWrapper control;

    public ListViewImpl(ControlWrapper control)  {
        this.control = control;
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
