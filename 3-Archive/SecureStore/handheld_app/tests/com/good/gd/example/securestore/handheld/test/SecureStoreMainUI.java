package com.good.gd.example.securestore.handheld.test;

import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import android.util.Log;

import com.good.automated.general.controls.impl.TextViewImpl;
import com.good.automated.general.utils.AbstractUIAutomatorUtils;
import com.good.automated.general.utils.Duration;
import com.good.automated.general.utils.UIAutomatorUtilsFactory;


class SecureStoreMainUI {

    private static AbstractUIAutomatorUtils uiAutomatorUtils = UIAutomatorUtilsFactory.getUIAutomatorUtils();

    private static final String TAG = SecureStoreMainUI.class.getCanonicalName();
    private static final String PACKAGE_NAME = uiAutomatorUtils.getAppPackageName();
    private static final String ANDROID_PACKAGE_NAME = "android";
    private static final String CREATE_DIRECTORY_ID = "action_create_folder";
    private static final String CONTAINER_ID = "action_btn_container";
    private static final String SDCARD_ID = "action_btn_sdcard";


    /**
     * Looks for the "Create folder" icon on the action bar. If it is absent there tries to find it and
     * in the ... menu (where icons go when there is not enough space for them on action bar) and click on it.
     *
     * @return  true - if click was successful / false - otherwise
     */
    boolean clickCreateButton() {
        if (uiAutomatorUtils.isScreenShown(PACKAGE_NAME, CREATE_DIRECTORY_ID, Duration.of(Duration.UI_ACTION))) {
            Log.i(TAG, "Found create folder icon. Will click on it.");
            return TextViewImpl.getByID(PACKAGE_NAME, CREATE_DIRECTORY_ID, Duration.of(Duration.WAIT_FOR_SCREEN)).click();
        } else {
            Log.i(TAG, "Didn't find create folder icon. Will look for it in ... menu.");
            UiObject actionBar = uiAutomatorUtils.getUIObjectById(ANDROID_PACKAGE_NAME, "action_bar");
            UiObject dotsMenu;
            try {
                dotsMenu = actionBar.getChild(new UiSelector().className("android.widget.ImageButton"));
                dotsMenu.click();
            } catch (UiObjectNotFoundException e) {
                Log.e(TAG, "Failed to find ... menu.", e);
                return false;
            }
            String create_folder = "Create folder";
            if (uiAutomatorUtils.isTextShown(create_folder, Duration.of(Duration.UI_WAIT))) {
                Log.i(TAG, "Found 'Create folder' button. Will click on it.");
                return uiAutomatorUtils.clickOnItemWithText(create_folder);
            } else {
                Log.e(TAG, "Couldn't find 'Create folder' button.");
                return false;
            }
        }
    }

    /**
     * Clicks on the icon selecting the container storage.
     *
     * @return  true - if the click was successful / false - otherwise
     */
    boolean clickContainerButton() {
        return uiAutomatorUtils.clickOnItemWithID(
                PACKAGE_NAME,
                CONTAINER_ID,
                Duration.of(Duration.UI_WAIT),
                Duration.of(Duration.UI_ACTION));
    }

    /**
     * Clicks on the icon selecting the SD card storage.
     *
     * @return  true - if the click was successful / false - otherwise
     */
    boolean clickSDCardButton() {
        return uiAutomatorUtils.clickOnItemWithID(
                PACKAGE_NAME,
                SDCARD_ID,
                Duration.of(Duration.UI_WAIT),
                Duration.of(Duration.UI_ACTION));
    }
}
