/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.test.screens;

import com.good.automated.general.utils.Duration;

import static com.good.automated.general.utils.Duration.AUTHORIZE_CALLBACK;

public class BBDActivationUI extends AbstractBBDActivationUI {

    private String TAG = BBDActivationUI.class.getSimpleName();

    /**
     * @param packageName app under test packageName
     */
    public BBDActivationUI(String packageName) {
        super(packageName);
        this.controls = new BBDActivationUIMap();
    }


    /**
     * @param packageName   app under test packageName
     * @param delay         duration to wait for screen
     */
    public BBDActivationUI(String packageName, long delay) {
        super(packageName);
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDActivationUIMap();
    }

    /**
     * @param packageName app under test packageName
     * @param userName    user name to provision with
     * @param pin1        pin1
     * @param pin2        pin2
     * @param pin3        pin3
     */
    public BBDActivationUI(String packageName,
                           String userName,
                           String pin1,
                           String pin2,
                           String pin3) {
        this(packageName, userName, pin1, pin2, pin3, Duration.of(AUTHORIZE_CALLBACK));
    }

    /**
     * @param packageName app under test packageName
     * @param userName    user name to provision with
     * @param pin1        pin1
     * @param pin2        pin2
     * @param pin3        pin3
     * @param delay       duration to wait for screen
     */
    public BBDActivationUI(String packageName,
                           String userName,
                           String pin1,
                           String pin2,
                           String pin3,
                           long delay) {
        super(packageName, userName, pin1, pin2, pin3);
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDActivationUIMap();
    }


}
