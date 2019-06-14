/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.test.screens;

import android.util.Log;

/**
 * Case: Remote Lock app container in your user GC
 */
public class BBDRemoteBlockUI extends AbstractBBDBlockUI {
    
    private String TAG = BBDRemoteBlockUI.class.getSimpleName();

    /**
     * @param packageName app under test packageName
     */
    public BBDRemoteBlockUI(String packageName) {
        super(packageName);
        this.controls = new BBDBlockUIMap();
    }

    public BBDRemoteBlockUI(String packageName, long delay) {
        super(packageName);
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDBlockUIMap();
    }

    /**
     *
     * @return true if click on button Unlock was successful otherwise false
     */
    public boolean clickUnlock() {
        try {
            return controls.getBtnUnlock().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * @return true if action was performed successfully otherwise false
     */
    @Override
    public boolean doAction() {
        return clickUnlock();
    }
}
