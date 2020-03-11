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

import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;

import com.good.automated.general.controls.EditText;
import com.good.automated.general.utils.AbstractUIAutomatorUtils;
import com.good.automated.general.utils.Duration;
import com.good.automated.general.utils.UIAutomatorUtilsFactory;

import static com.good.automated.general.utils.Duration.WAIT_FOR_SCREEN;

public class EditTextImpl extends ControlBase implements EditText {

    private static AbstractUIAutomatorUtils uiAutomatorUtils =
            UIAutomatorUtilsFactory.getUIAutomatorUtils();

    public EditTextImpl(ControlWrapper control) {
        super(control, EditTextImpl.class.getCanonicalName());
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
    public boolean isClickable() {
        return control.isClickable();
    }

    @Override
    public boolean isEnabled() {
        return control.isEnabled();
    }

    @Override
    public boolean isFocusable() {
        return control.isFocusable();
    }

    @Override
    public boolean isFocused() {
        return control.isFocused();
    }

    @Override
    public boolean isLongClickable() {
        return control.isLongClickable();
    }

    @Override
    public boolean legacySetText(String text) {
        try {
            uiAutomatorUtils.hideKeyboardInLandscape();
        } catch (RemoteException e) {
            Log.e(TAG, "There were issues when handling software keyboard: " + e.getMessage());
        }
        return control.legacySetText(text);
    }

    @Override
    public boolean setText(String text) {
        try {
            uiAutomatorUtils.hideKeyboardInLandscape();
        } catch (RemoteException e) {
            Log.e(TAG, "There were issues when handling software keyboard: " + e.getMessage());
        }
        control.click();
        return control.setText(text);
    }

    @Override
    public String getText() {
        return super.getText();
    }

    @Override
    public void clearData() {
        control.click();
        control.clearData();
    }

    @Override
    public boolean selectAll() {
        return this.click() && this.selectAllAction();
    }

    public boolean copy() {
        return selectAll()
                && this.copyAction();
    }

    public boolean paste(String expectedText) {
        this.click();
        pasteAction();

        //Delay for text to be pasted
        uiAutomatorUtils.waitForUI(Duration.of(WAIT_FOR_SCREEN));
        boolean result = expectedText.equals(this.getText());

        try {
            if (uiAutomatorUtils.isKeyboardShown()) {
                uiAutomatorUtils.hideKeyboard();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Caught RemoteException while trying to hide keyboard.");
            return Boolean.FALSE;
        }
        return result;
    }

    public static EditText getByID(String packageID, String id) {
        return new EditTextImpl(new ControlWrapper().getControlWrapperObject(packageID, id));
    }

    public static EditText getByID(String packageID, String id, int delay) {
        return new EditTextImpl(new ControlWrapper().getControlWrapperObject(packageID, id, delay));
    }

    public static EditText getByText(String text, int delay) {
        return new EditTextImpl(new ControlWrapper().getControlWrapperObject(text, delay));
    }

    /**
     * Selects all text in a focused ui element using combination of 2 {@link KeyEvent}.
     *
     * @return  true - if action was successful / false - otherwise
     */
    private boolean selectAllAction() {
        return uiAutomatorUtils.pressKeyCode(KeyEvent.KEYCODE_A, KeyEvent.META_CTRL_ON);
    }

    /**
     * Copies selected text from the focused ui element using combination of 2 {@link KeyEvent}.
     *
     * @return  true - if action was successful / false - otherwise
     */
    private boolean copyAction() {
        return uiAutomatorUtils.pressKeyCode(KeyEvent.KEYCODE_C, KeyEvent.META_CTRL_ON);
    }

    /**
     * Pasts text from the clipboard to the  focused ui element using combination of 2 {@link KeyEvent}.
     *
     * @return  true - if action was successful / false - otherwise
     */
    private boolean pasteAction() {
        return uiAutomatorUtils.pressKeyCode(KeyEvent.KEYCODE_V, KeyEvent.META_CTRL_ON);
    }
}
