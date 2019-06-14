/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.general.controls.impl;

import com.good.automated.general.controls.ImageView;

public final class ImageViewImpl implements ImageView{
    private ControlWrapper control;

    public ImageViewImpl(ControlWrapper control) {
        this.control = control;
    }

    @Override
    public String getText() {
        return null;
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
    public boolean isEnabled() {
        return control.isEnabled();
    }

    public static ImageView getByID(String packageID, String id, int delay) {
        return new ImageViewImpl(new ControlWrapper().getControlWrapperObject(packageID, id, delay));
    }

    public static ImageView getByID(String packageID, String id) {
        return new ImageViewImpl(new ControlWrapper().getControlWrapperObject(packageID, id));
    }
}
