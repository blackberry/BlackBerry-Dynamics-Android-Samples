/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.general.controls.impl;

import android.util.Log;

import com.good.automated.general.controls.Button;

public class ButtonImpl implements Button {
    private static final String TAG = ButtonImpl.class.getCanonicalName();
    private ControlWrapper control;

    private ButtonImpl(ControlWrapper control) {
        this.control = control;
    }

    @Override
    public boolean click() {
        return control.click();
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

    @Override
    public boolean isSelected() {
        return control.isSelected();
    }

    @Override
    public boolean isEnabled() {
        return control.isEnabled();
    }

    @Override
    public boolean isClickable() {
        return control.isClickable();
    }

    @Override
    public boolean isFocusable() {
        return control.isFocusable();
    }

    @Override
    public String getText() {
        Log.d(TAG, "getText() = " + control.getText());
        return control.getText();
    }

    public static Button getByID(String packageID, String id, int delay) {
        return new ButtonImpl(new ControlWrapper().getControlWrapperObject(packageID, id, delay));
    }

    public static Button getByID(String packageID, String id) {
        return new ButtonImpl(new ControlWrapper().getControlWrapperObject(packageID, id));
    }
}
