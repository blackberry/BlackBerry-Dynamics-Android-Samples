/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.general.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * GDSDKStateReceiver is a helper class for automated tests to receive GD SDK states broadcasts from
 * GDLocalBroadcastManager. The GD SDK states it's interested in are authorized, locked, and wiped.
 * However, more state actions (e.g., policy updated, config updated, service updated, or entitlements
 * updated)  can be registered to receive broadcast notifications. See GDStateAction
 * class for more details.
 * <p>
 * This is important because as well as checking certain UI screens are dismissed (say in case of GD
 * login) it is also important the GD has provided the authorized broadcast event before app code (or
 * other test code) runs.
 */

//To continue using this receiver call in your application
// GDAndroid.getInstance().registerReceiver(GDSDKStateReceiver.getInstance(), GDSDKStateReceiver.getInstance().getIntentFilder());
public class GDSDKStateReceiver extends BroadcastReceiver {

    private static final String TAG = "GD_TEST_BASE";
    private static final Object MONITOR = new Object();
    private static GDSDKStateReceiver instance = null;
    private boolean mIsAuthorized = false;
    private static final String GD_STATE_WIPED_ACTION = "com.good.gd.WIPED";
    private static final String GD_STATE_LOCKED_ACTION = "com.good.gd.LOCKED";
    private static final String GD_STATE_AUTHORIZED_ACTION = "com.good.gd.AUTHORIZED";
    private boolean mIsIntentFilterRegisteredOutside = false;

    //To continue using this receiver call in your application
    // GDAndroid.getInstance().registerReceiver(GDSDKStateReceiver.getInstance(), GDSDKStateReceiver.getInstance().getIntentFilter());
    public static synchronized GDSDKStateReceiver getInstance() {
        if (instance == null) {
            instance = new GDSDKStateReceiver();
        }
        return instance;
    }

    public IntentFilter getIntentFilter() {
        mIsIntentFilterRegisteredOutside = true;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GD_STATE_AUTHORIZED_ACTION);
        intentFilter.addAction(GD_STATE_LOCKED_ACTION);
        intentFilter.addAction(GD_STATE_WIPED_ACTION);
        return intentFilter;
    }

    private GDSDKStateReceiver() {
        Log.w(TAG, "To continue using this receiver call in your application\n" +
            "'GDAndroid.getInstance().registerReceiver(GDSDKStateReceiver.getInstance(), GDSDKStateReceiver.getInstance().getIntentFilter());'");
    }

    @Deprecated
    public void registerGDStateReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        throw new RuntimeException("This method is not supported any more.\n" +
                "Please add 'GDAndroid.getInstance().registerReceiver(GDSDKStateReceiver.getInstance(), GDSDKStateReceiver.getInstance().getIntentFilter());' before running a test");
    }

    @Deprecated
    public void unregisterGDStateReceiver(BroadcastReceiver receiver) {
        throw new RuntimeException("This method is not supported any more.\n" +
                "Please add 'GDAndroid.getInstance().registerReceiver(GDSDKStateReceiver.getInstance(), GDSDKStateReceiver.getInstance().getIntentFilter());' before running a test");
    }

    public boolean checkAuthorized() {
        if (!mIsIntentFilterRegisteredOutside) {
            throw new RuntimeException("Looks like checkAuthorized() is called before GDSDKStateReceiver was registered outside.\n" +
                    "Please add 'GDAndroid.getInstance().registerReceiver(GDSDKStateReceiver.getInstance(), GDSDKStateReceiver.getInstance().getIntentFilter());' before running a test");
        }

        Log.d(TAG, "checkAuthorized: mIsAuthorized = " + mIsAuthorized);
        return mIsAuthorized;
    }

    public void waitForAuthorizedChange(int aTimeWaitMilliseconds) {

        synchronized (MONITOR) {
            try {
                MONITOR.wait(aTimeWaitMilliseconds);
            } catch (InterruptedException e) {
                Log.e(TAG, "waitForAuthorizedChange: unexpected interruption", e);
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: received intent: " + intent);
        String action = intent.getAction();
        Log.d(TAG, "GDSDKStateReceiver callback with action: " + action);

        switch (action) {
            case GD_STATE_AUTHORIZED_ACTION:
                onAuthorized();
                break;
            case GD_STATE_LOCKED_ACTION:
                onLocked();
                break;
            case GD_STATE_WIPED_ACTION:
                onWiped();
                break;
        }
    }

    // App is authorized
    private void onAuthorized() {
        Log.d(TAG, "onAuthorized: " + GD_STATE_AUTHORIZED_ACTION);
        mIsAuthorized = true;
        Log.d(TAG, "onAuthorized: mIsAuthorized = " + mIsAuthorized);

        synchronized (MONITOR) {
            MONITOR.notify();
        }
    }

    // App is locked
    private void onLocked() {
        Log.d(TAG, "onLocked: " + GD_STATE_LOCKED_ACTION);
        mIsAuthorized = false;
        Log.d(TAG, "onLocked: mIsAuthorized = " + mIsAuthorized);

        synchronized (MONITOR) {
            MONITOR.notify();
        }
    }

    // App is wiped
    private void onWiped() {
        Log.d(TAG, "onWiped: " + GD_STATE_WIPED_ACTION);
        mIsAuthorized = false;
        Log.d(TAG, "onWiped: mIsAuthorized = " + mIsAuthorized);

        synchronized (MONITOR) {
            MONITOR.notify();
        }
    }
}
