/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.test.screens;

import static com.good.automated.general.utils.Duration.UI_WAIT;

import android.util.Log;

import com.good.automated.general.utils.Duration;

/**
 * Case: two or more versions of the app are added to your user on GC. One that is installed on your device
 * is moved to Deny list.
 */
public class BBDApplicationBlockUI extends AbstractBBDBlockUI {

    private String TAG = BBDApplicationBlockUI.class.getSimpleName();

    /**
     * @param packageName app under test packageName
     */
    public BBDApplicationBlockUI(String packageName) {
        super(packageName);
        this.controls = new BBDBlockUIMap();
    }

    public BBDApplicationBlockUI(String packageName, long delay) {
        super(packageName);
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDBlockUIMap();
    }

    /**
     *
     * @return true if action was performed successfully, otherwise false
     */
    @Override
    public boolean doAction() {
        if (getTitle()!=null && getTitle().equals("Authenticating")){
            Log.d(TAG, "Authenticating screen was shown");
            return getUiAutomationUtils().waitUntilTextGoneFormScreen(getTitle(), Duration.of(UI_WAIT));
        }
        return false;
    }
}
