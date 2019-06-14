/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.test.screens;

import static com.good.automated.general.utils.Duration.WAIT_FOR_SCREEN;

import com.good.automated.general.controls.ImageView;
import com.good.automated.general.controls.impl.ImageViewImpl;
import com.good.automated.general.utils.Duration;

public class BBDWelcomeUI extends AbstractBBDUI {

    private static final String SCREEN_ID = "bbd_welcome_view_UI";
    private String TAG = BBDWelcomeUI.class.getSimpleName();
    private String packageName;

    private BBDWelcomeUIMap controls;

    public BBDWelcomeUI(String packageName) {
        this.packageName = packageName;
        this.controls = new BBDWelcomeUIMap();
    }

    public BBDWelcomeUI(String packageName, long delay) {
        this.packageName = packageName;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new IllegalStateException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDWelcomeUIMap();
    }

    public static String getScreenID() {
        return SCREEN_ID;
    }

    @Override
    public boolean doAction() {
        return getUiAutomationUtils().waitUntilElementGoneFromUI(packageName, getScreenID(),
                Duration.of(Duration.AUTHORIZE_CALLBACK));
    }

    private class BBDWelcomeUIMap {

        public ImageView getWelcomeLogo() {
            return ImageViewImpl.getByID(packageName, "gd_welcome_logo",
                    Duration.of(WAIT_FOR_SCREEN));
        }
    }

}
