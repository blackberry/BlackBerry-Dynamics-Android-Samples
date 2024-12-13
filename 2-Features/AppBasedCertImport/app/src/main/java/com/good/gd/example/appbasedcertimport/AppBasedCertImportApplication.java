/* Copyright (c) 2023 BlackBerry Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.good.gd.example.appbasedcertimport;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import android.util.Log;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.pki.CredentialsProfile;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class AppBasedCertImportApplication extends Application implements Application.ActivityLifecycleCallbacks, GDStateListener {

	private static final String TAG = AppBasedCertImportApplication.class.getSimpleName();

	private static AppBasedCertImportApplication instance;

	private WeakReference<Activity> currentActivityRef;

	private List<CredentialsProfile> ucpList = new ArrayList<>();
    private UcpUpdateListener mUcpUpdateListener;
    private List<Intent> pendingIntents = new Vector<>();

    private boolean isMainActivityAvailable = false;
    private boolean isAuthorized = false;

	@Override
	public void onCreate() {
		super.onCreate();

        instance = this;

        // turn off (suppress) night mode for this app
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        GDAndroid.getInstance().applicationInit(this);
		//Singleton AppEvent listener is set in Application class so it receives events independantly of Activity lifecycle
		GDAndroid.getInstance().setGDStateListener(this);
	}

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: " + activity.toString());
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d(TAG, "onActivityStarted: " + activity.toString());
        if (activity instanceof AppBasedCertImportActivity) {
            isMainActivityAvailable = true;
        }
    }

    @Override
    public synchronized void onActivityResumed(Activity activity) {
        Log.d(TAG, "onActivityResumed: " + activity.toString());
        currentActivityRef = new WeakReference<>(activity);
        if (activity instanceof AppBasedCertImportActivity) {
            if (mUcpUpdateListener != null) {
                if (!pendingIntents.isEmpty()) {
                    for (Intent intentItem:pendingIntents) {
                        mUcpUpdateListener.onUCPUpdated(intentItem);
                    }
                    pendingIntents.clear();
                }
            }
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d(TAG, "onActivityPaused: " + activity.toString());
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d(TAG, "onActivityStopped: " + activity.toString());
        if (activity instanceof AppBasedCertImportActivity) {
            isMainActivityAvailable = false;
        }
        currentActivityRef = null;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.d(TAG, "onActivitySaveInstanceState: " + activity.toString());
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d(TAG, "onActivityDestroyed: " + activity.toString());
    }

    @Override
    public void onAuthorized() {
	    isAuthorized = true;

	    //Register receiver and callbacks.
        IntentFilter filter = new IntentFilter(CredentialsProfile.GD_CREDENTIAL_PROFILE_STATE_CHANGE_ACTION);
        GDAndroid.getInstance().registerReceiver(new UCPReceiver(), filter);
        registerActivityLifecycleCallbacks(this);

        Collection<CredentialsProfile> credentialProfiles = CredentialsProfile.getMap().values();
        ucpList.addAll(credentialProfiles);
    }

    @Override
    public void onLocked() {
        isAuthorized = false;
    }

    @Override
    public void onWiped() {
        isAuthorized = false;
    }

    @Override
    public void onUpdateConfig(Map<String, Object> settings) {
    }

    @Override
    public void onUpdatePolicy(Map<String, Object> policyValues) {
    }

    @Override
    public void onUpdateServices() {
    }

    @Override
    public void onUpdateEntitlements() {
    }

    public class UCPReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String id = CredentialsProfile.getId(intent);
            Log.d(TAG, "UCP ID updated: " + id);
            Log.d(TAG, "UCP updated: " + CredentialsProfile.getName(intent) + ", state: " + CredentialsProfile.getState(intent));
            if (CredentialsProfile.getState(intent) == CredentialsProfile.State.GDCredentialsProfileStateImportNow) {
                if (currentActivityRef == null || !(currentActivityRef.get() instanceof AppBasedCertImportActivity)) {
                    Intent startIntent = new Intent(context, AppBasedCertImportActivity.class);
                    startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startIntent.putExtra(AppBasedCertImportActivity.STARTED_WITH_UCP, true);
                    startActivity(startIntent);
                }
            }
            if (mUcpUpdateListener != null) {
                if (!pendingIntents.isEmpty() && isMainActivityAvailable) {
                    for (Intent intentItem:pendingIntents) {
                        mUcpUpdateListener.onUCPUpdated(intentItem);
                    }
                } else {
                    mUcpUpdateListener.onUCPUpdated(intent);
                }
            } else {
                pendingIntents.add(intent);
            }
        }
    }

    public static AppBasedCertImportApplication getInstance() {
	    return instance;
    }

    public List<CredentialsProfile> getUcpList() {
        return ucpList;
    }

    public synchronized void setUcpUpdateListener(UcpUpdateListener listener) {
	    mUcpUpdateListener = listener;
    }

    public interface UcpUpdateListener {
	    void onUCPUpdated(Intent intent);
    }

    public boolean isAppAuthorized(){
	    return isAuthorized;
    }
}
