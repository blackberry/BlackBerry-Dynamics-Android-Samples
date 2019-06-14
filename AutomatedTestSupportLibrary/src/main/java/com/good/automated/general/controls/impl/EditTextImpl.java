/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.general.controls.impl;

import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;

import com.good.automated.general.controls.EditText;
import com.good.automated.general.utils.AbstractUIAutomatorUtils;
import com.good.automated.general.utils.UIAutomatorUtilsFactory;

public class EditTextImpl implements EditText {

    private static final String TAG = EditTextImpl.class.getCanonicalName();
    private ControlWrapper control;
    private static AbstractUIAutomatorUtils uiAutomatorUtils =
            UIAutomatorUtilsFactory.getUIAutomatorUtils();

    public EditTextImpl(ControlWrapper control) {
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
        Log.d(TAG, "getText() = " + control.getText());
        return control.getText();
    }

    @Override
    public void clearData() {
        control.click();
        control.clearData();
    }

    public boolean copy() {
        return this.click()
                && this.selectAllAction()
                && this.copyAction();
    }

    public boolean paste(String expectedText) {
        this.click();
        pasteAction();
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
