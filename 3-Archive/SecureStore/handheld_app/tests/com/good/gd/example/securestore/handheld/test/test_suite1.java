package com.good.gd.example.securestore.handheld.test;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.good.automated.general.helpers.BBDActivationHelper;
import com.good.automated.general.utils.AbstractUIAutomatorUtils;
import com.good.automated.general.utils.GDSDKStateReceiver;
import com.good.automated.general.utils.UIAutomatorUtilsFactory;
import com.good.gd.GDAndroid;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class test_suite1 {

    @Rule
    public TestWatcher watcher = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            uiAutomatorUtils.printScreenMap();
            uiAutomatorUtils.captureScreenshot(description.getDisplayName());
            super.failed(e, description);
        }
    };

    private static AbstractUIAutomatorUtils uiAutomatorUtils = UIAutomatorUtilsFactory.getUIAutomatorUtils();
    private final String APP_UNDER_TEST = uiAutomatorUtils.getAppPackageName();

    /**
     * Setup Test, like all tests makes use of helper functions in GD_UIAutomator_Lib Test library project
     */
    @BeforeClass
    public static void setUpClass() {
        GDAndroid.getInstance().registerReceiver(GDSDKStateReceiver.getInstance(), GDSDKStateReceiver.getInstance().getIntentFilter());

        uiAutomatorUtils.wakeUpDeviceIfNeeded();

        //Android Emulator when booted sometimes has error dialogues to dismiss
        uiAutomatorUtils.acceptSystemDialogues();

    }

    /**
     * Test 1, if GD App is already activated ensure that it can be unlocked using password. If not activated ensure it
     * can be activated using Email Address & Access Key. These are set in file com.good.gd.test.json at build time
     */
    @Test
    public void test_1_activation() {

        uiAutomatorUtils.launchApp(APP_UNDER_TEST);

        assertTrue("Cannot login or provision", BBDActivationHelper.loginOrActivateApp().isSuccessful());

        assertTrue("App is not authorized", uiAutomatorUtils.checkGDAuthorized());

        uiAutomatorUtils.pressHome();
    }
}
