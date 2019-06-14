/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.general.controls;

public interface EditText extends Clickable, Component {

    boolean isClickable();

    boolean isEnabled();

    boolean isFocusable();

    boolean isFocused();

    boolean isLongClickable();

    boolean legacySetText(String text);

    boolean setText(String text);

    void clearData();

    boolean copy();

    boolean paste(String expectedText);

}
