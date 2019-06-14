/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.general.controls.impl;

import android.util.Log;

import com.good.automated.general.controls.CheckBox;

public class CheckBoxImpl implements CheckBox {
    private static final String TAG = CheckBoxImpl.class.getCanonicalName();
    private ControlWrapper control;

    private CheckBoxImpl(ControlWrapper control) {
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
    public boolean isCheckable() {
        return control.isCheckable();
    }

    @Override
    public boolean isChecked() {
        return control.isChecked();
    }

    @Override
    public String getText() {
        Log.d(TAG, "getText() = " + control.getText());
        return control.getText();
    }

    public static CheckBox getByID(String packageID, String id, int delay) {
        return new CheckBoxImpl(new ControlWrapper().getControlWrapperObject(packageID, id, delay));
    }

    public static CheckBox getByID(String packageID, String id) {
        return new CheckBoxImpl(new ControlWrapper().getControlWrapperObject(packageID, id));
    }
}
