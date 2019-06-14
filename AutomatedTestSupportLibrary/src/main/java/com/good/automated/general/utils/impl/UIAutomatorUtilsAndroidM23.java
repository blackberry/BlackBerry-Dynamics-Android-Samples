/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.general.utils.impl;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import com.good.automated.general.utils.AbstractUIAutomatorUtils;
import com.good.automated.general.utils.Duration;

import static android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS;
import static android.provider.Settings.ACTION_DATE_SETTINGS;
import static com.good.automated.general.utils.Duration.WAIT_FOR_SCREEN;

//Implemented UI interactions with Android M API
//Marshmallow - 6.0	API level 23
public class UIAutomatorUtilsAndroidM23 extends AbstractUIAutomatorUtils {

    private String TAG = UIAutomatorUtilsAndroidM23.class.getSimpleName();

    private UIAutomatorUtilsAndroidM23() {
        super();
    }

    public static AbstractUIAutomatorUtils getInstance() {
        return new UIAutomatorUtilsAndroidM23();
    }

    @Override
    public UiObject findTaskWithTextInRecentApps(String aText) throws UiObjectNotFoundException {
        openRecentApps();

        UiObject taskViewSelector = getUiDevice().findObject(new UiSelector().className("com.android.systemui.recents.views.TaskStackView"));
        UiObject fileViewer = taskViewSelector.getChild(new UiSelector().textMatches(aText));
        fileViewer.waitForExists(Duration.of(Duration.UI_WAIT));
        if (fileViewer.exists()) {
            return fileViewer;
        }
        return null;
    }

    @Override
    public boolean switchOffWindowAnimationScale(){
        return setAnimationScaleByText("Animation off", "Window animation scale");
    }

    @Override
    public boolean switchOffTransitionAnimationScale(){
        return setAnimationScaleByText("Animation off", "Transition animation scale");
    }

    @Override
    public boolean switchOffAnimatorDurationScale(){
        return setAnimationScaleByText("Animation off", "Animator duration scale");
    }

    private boolean setAnimationScaleByText(String animationOption, String animationOff) {
        launchActionSettings(ACTION_APPLICATION_DEVELOPMENT_SETTINGS);

        if (scrollToText("com.android.settings:id/container_material", animationOff)){
            return clickOnItemWithText(animationOff, Duration.of(WAIT_FOR_SCREEN)) &&
                    clickOnItemWithText(animationOption, Duration.of(WAIT_FOR_SCREEN));
        } else {
            return false;
        }
    }

    @Override
    public void launchDateSettings() {
        launchActionSettings(ACTION_DATE_SETTINGS);
    }

    /**
     *
     * @param action action from system Settings
     */
    @Override
    public void launchActionSettings(String action) {
        Context context = InstrumentationRegistry.getTargetContext();

        final Intent i = new Intent();
        i.setAction(action);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        context.startActivity(i);
    }
}
