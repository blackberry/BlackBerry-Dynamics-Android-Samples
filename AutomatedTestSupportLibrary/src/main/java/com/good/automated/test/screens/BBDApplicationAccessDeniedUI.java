/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.test.screens;

/**
 * Cases:
 * 1. One version of the app is added to your user on GC and then was moved to Deny list.
 * 2. App is not added to your user on GC, but you started provisioning.
 * 3. App container was removed from list of provisioned containers on GC
 */
public class BBDApplicationAccessDeniedUI extends AbstractBBDBlockUI {

    private String TAG = BBDApplicationAccessDeniedUI.class.getSimpleName();

    /**
     * @param packageName       app under test packageName
     */
    public BBDApplicationAccessDeniedUI(String packageName) {
        super(packageName);
        this.controls = new BBDBlockUIMap();
    }

    public BBDApplicationAccessDeniedUI(String packageName, long delay) {
        super(packageName);
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDBlockUIMap();
    }
}
