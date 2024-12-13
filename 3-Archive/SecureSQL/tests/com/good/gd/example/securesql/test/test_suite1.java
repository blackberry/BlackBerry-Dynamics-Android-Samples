/*
 *  This file contains Good Sample Code subject to the Good Dynamics SDK Terms and Conditions.
 *  (c) 2016 Good Technology Corporation. All rights reserved.
 */
package com.good.gd.example.securesql.test;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.good.automated.general.helpers.BBDActivationHelper;
import com.good.automated.general.utils.AbstractUIAutomatorUtils;
import com.good.automated.general.utils.GDSDKStateReceiver;
import com.good.automated.general.utils.UIAutomatorUtilsFactory;
import com.good.gd.GDAndroid;

import org.junit.After;
import org.junit.Before;
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
 * Tests purpose - Ensure SecureSQL sample app correct basic operation
 */
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

    protected static AbstractUIAutomatorUtils uiAutomatorUtils = UIAutomatorUtilsFactory.getUIAutomatorUtils();

    /*
    Note - The test order of these tests is significant hence the explict test numbering
     */

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

    @Before
    public void setUp() {
        uiAutomatorUtils.launchAppUnderTest();
    }

    @After
    public void tearDown() {
        uiAutomatorUtils.pressHome();
    }

    /**
     * Test 1, if GD App is already activated ensure that it can be unlocked using password. If not activated ensure it
     * can be activated using Email Address & Access Key. These are set in file com.good.gd.test.json at build time
     */
    @Test
    public void test_1_activation() {

        assertTrue("Failed to login or provision app", BBDActivationHelper.loginOrActivateApp());

        assertTrue("App is not authorized", uiAutomatorUtils.checkGDAuthorized());
    }
}
