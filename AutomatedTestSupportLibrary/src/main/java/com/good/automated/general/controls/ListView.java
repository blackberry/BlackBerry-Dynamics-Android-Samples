/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.general.controls;

import com.good.automated.general.controls.impl.ControlWrapper;

public interface ListView extends Component, Clickable{
    int getChildCount();

    ControlWrapper getChildElement(int i);
}
