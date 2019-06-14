/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.general.controls;

public interface Button extends Clickable, Component {

    boolean isSelected();

    boolean isEnabled();

    boolean isClickable();

    boolean isFocusable();

}
