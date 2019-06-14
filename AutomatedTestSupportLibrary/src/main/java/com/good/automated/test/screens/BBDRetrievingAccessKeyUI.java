/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.test.screens;

import android.util.Log;

import com.good.automated.general.utils.Duration;

public class BBDRetrievingAccessKeyUI extends AbstractBBDBlockUI{

    private String TAG = BBDRetrievingAccessKeyUI.class.getSimpleName();
    private String GETTING_ACCESS_KEY = "Getting Access Key";

    public BBDRetrievingAccessKeyUI(String packageName) {
        super(packageName);
        this.controls = new BBDBlockUIMap();
    }

    public BBDRetrievingAccessKeyUI(String packageName, long delay) {
        super(packageName);
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDBlockUIMap();
    }

    /**
     *
     * @return true if action was performed successfully otherwise false
     */
    @Override
    public boolean doAction() {
        boolean isGettingAccessKeyScreenDisplayed;
        try{
            isGettingAccessKeyScreenDisplayed = controls.getBlockTitle().getText()
                    .equals(GETTING_ACCESS_KEY);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
        return isGettingAccessKeyScreenDisplayed &&
                getUiAutomationUtils().waitUntilElementGoneFromUI(packageName, getScreenID(),
                        Duration.of(Duration.AUTHORIZE_CALLBACK));
    }
}
