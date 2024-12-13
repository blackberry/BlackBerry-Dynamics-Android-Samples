package com.good.gd.example.appkinetics.test;

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

/**
 * Example of test suite with a test that shows basic usage of the Automated Test Support Library from BlackBerry Dynamics SDK
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSuite {

    @Rule
    public TestWatcher watcher = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            uiAutomatorUtils.printScreenMap();
            uiAutomatorUtils.captureScreenshot(description.getDisplayName());
            super.failed(e, description);
        }
    };

    private static final AbstractUIAutomatorUtils uiAutomatorUtils = UIAutomatorUtilsFactory.getUIAutomatorUtils();

    @BeforeClass
    public static void setUpClass() {
        GDAndroid.getInstance().registerReceiver(GDSDKStateReceiver.getInstance(), GDSDKStateReceiver.getInstance().getIntentFilter());

        uiAutomatorUtils.wakeUpDeviceIfNeeded();

        //Android Emulator when booted sometimes has error dialogues to dismiss
        uiAutomatorUtils.acceptSystemDialogues();

    }

    /**
     * This test shows how to use the ATSL apis to handle the UI handled by the BlackBerry Dynamics SDK
     * The same code works for the first install of the app (when the initial provisioning is needed) or any subsequent ones (where a simple login is enough)
     * The test relies on credentials defined in file com.good.gd.test.json
     */
    @Test
    public void test_1_activation() {

        uiAutomatorUtils.launchAppUnderTest();

        assertTrue("Failed to provision or login into the app.", BBDActivationHelper.loginOrActivateApp());

        assertTrue("BlackBerry Dynamics SDK is not authorized", uiAutomatorUtils.checkGDAuthorized());

        uiAutomatorUtils.pressHome();
    }

}