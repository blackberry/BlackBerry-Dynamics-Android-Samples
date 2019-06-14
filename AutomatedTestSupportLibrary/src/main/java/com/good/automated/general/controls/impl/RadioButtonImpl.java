/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.general.controls.impl;

import com.good.automated.general.controls.RadioButton;

public class RadioButtonImpl implements RadioButton {
    private ControlWrapper control;

    private RadioButtonImpl(ControlWrapper control) {
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
        return control.getText();
    }

    public static RadioButton getByID(String packageID, String id, int delay) {
        return new RadioButtonImpl(new ControlWrapper().getControlWrapperObject(packageID, id, delay));
    }

    public static RadioButton getByID(String packageID, String id) {
        return new RadioButtonImpl(new ControlWrapper().getControlWrapperObject(packageID, id));
    }

    @Override
    public boolean isCheckable() {
        return control.isCheckable();
    }

    @Override
    public boolean isChecked() {
        return control.isChecked();
    }
}
