/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.general.controls.impl;

import android.util.Log;

import com.good.automated.general.controls.TextView;

public class TextViewImpl implements TextView {

    private static final String TAG = TextViewImpl.class.getCanonicalName();
    private ControlWrapper control;

    public TextViewImpl(ControlWrapper control) {
        this.control = control;
    }

    @Override
    public String getText() {
        Log.d(TAG, "getText() = " + control.getText());
        return control.getText();
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

    public String getContentDescription() {
        Log.d(TAG, "getContentDescription() = " + control.getContentDescription());
        return control.getContentDescription();
    }
}
