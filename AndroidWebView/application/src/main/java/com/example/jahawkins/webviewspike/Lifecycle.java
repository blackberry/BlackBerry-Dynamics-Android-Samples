/* Copyright (c) 2018 BlackBerry Ltd.
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

package com.example.jahawkins.webviewspike;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.good.gd.GDAndroid;
import com.good.gd.GDAppServer;
import com.good.gd.GDStateAction;

import java.util.List;
import java.util.Map;

public class Lifecycle implements ActivityLifecycleCallbacks {
    private static final String TAG = Lifecycle.class.getSimpleName();

    private static final Lifecycle sharedInstance = new Lifecycle();
    public static Lifecycle getInstance() {
        return sharedInstance;
    }
    private Lifecycle() {
        super();
    }

    private Boolean authorisationState = false;
    private MainActivity currentActivity = null;

    private Boolean isMainActivity(Activity activity, Boolean makeCurrent) {
        if (activity.getClass() != MainActivity.class) {
            if (makeCurrent) {
                this.currentActivity = null;
            }
            return false;
        }

        if (makeCurrent) {
            this.currentActivity = (MainActivity) activity;
        }
        return true;
    }
    private Boolean isMainActivity(Activity activity) {
        return isMainActivity(activity, false);
    }

    public Boolean getAuthorisationState() { return authorisationState; }

    private GDAndroid becomeBBDReceiver(Context context) {
        final GDAndroid gdAndroid = GDAndroid.getInstance();
        gdAndroid.applicationInit(context.getApplicationContext());

        final IntentFilter filter = new IntentFilter(GDStateAction.GD_STATE_AUTHORIZED_ACTION);
        filter.addAction(GDStateAction.GD_STATE_LOCKED_ACTION);
        filter.addAction(GDStateAction.GD_STATE_WIPED_ACTION);
        filter.addAction(GDStateAction.GD_STATE_UPDATE_POLICY_ACTION);

        gdAndroid.registerReceiver(new BroadcastReceiver() {
            // Following attributes are used for diagnostic logging only.
            private int calls = 0;
            private Context lastContext = null;
            private String lastIntentAction = null;

            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if (lastIntentAction != action || lastContext != context) {
                    calls = 0;
                    lastContext = context;
                    lastIntentAction = action;
                }
                calls++;
                Log.d(TAG, String.format("Broadcast onReceive(%s,%s) %d.",
                    context.getClass().getSimpleName(), action, calls));
                switch (action) {
                    case GDStateAction.GD_STATE_AUTHORIZED_ACTION:
                        Lifecycle.this.authorisationState = true;
                        Lifecycle.this.setLinks();
                        break;
                    case GDStateAction.GD_STATE_LOCKED_ACTION:
                    case GDStateAction.GD_STATE_WIPED_ACTION:
                        Lifecycle.this.authorisationState = false;
                        break;
                    case GDStateAction.GD_STATE_UPDATE_POLICY_ACTION:
                        Log.d(TAG, "Policy update " +
                            GDAndroid.getInstance().getApplicationPolicyString());
                        break;
                    case GDStateAction.GD_STATE_UPDATE_CONFIG_ACTION:
                        Lifecycle.this.setLinks();
                    default:
                        // Other actions aren't used by this application.
                        break;
                }
            }
        }, filter);

        return gdAndroid;
    }

    private void setLinks() {
        Settings settings = Settings.getInstance();
        settings.resetLinks();

        Map<String, Object> configMap = GDAndroid.getInstance().getApplicationConfig();

        List<GDAppServer> servers = (List<GDAppServer>) configMap.get(
            GDAndroid.GDAppConfigKeyServers);
        for (GDAppServer server : servers) {
            Uri.Builder link = new Uri.Builder();
            link.scheme("http");
            link.encodedAuthority(server.server + ":" + server.port);
            settings.addLinks(link.build().toString());
        }

        String config = (String) configMap.get(GDAndroid.GDAppConfigKeyConfig);
        settings.addLinks(config);

        if (this.currentActivity != null) {
            WebView webView = this.currentActivity.findViewById(MainActivity.WEB_VIEW_ID);
            if (webView != null) {
                webView.reloadIfUserInterface();
            }
        }
    }


    public void initialise(Context context) {
        final Application application = (Application) context.getApplicationContext();
        final GDAndroid bbdRuntime = GDAndroid.getInstance();
        final Boolean activated = bbdRuntime.isActivated(application);

        Log.d(TAG, String.format("initialise(%s) Application:%s isActivated:%b.",
            context.getClass().getSimpleName(), application.getClass().getSimpleName(), activated));

        application.registerActivityLifecycleCallbacks(this);
        this.becomeBBDReceiver(context).activityInit((Activity)context);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        Log.d(TAG, String.format("onActivityCreated(%s) package:%s.",
            activity.getClass().getSimpleName(), activity.getClass().getPackage().getName()));

        // Near here might have to add safety code to prevent activityInit before applicationInit.
        // So far it hasn't been an issue, perhaps because this class only gets initialised during
        // the MainActivity onCreate. This callback would have been invoked before
        // MainActivity onCreate, but it can't have been registered at that time.
        if (isMainActivity(activity)) {
            GDAndroid.getInstance().activityInit(activity);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        final StringBuilder message = new StringBuilder(
            String.format("onActivityStarted(%s)", activity.getClass().getSimpleName()));
        // https://stackoverflow.com/questions/37856407/can-activity-getintent-ever-return-null
        final Intent intent = activity.getIntent();
        final Uri intentData = (intent == null ? null : intent.getData());
        message.append((intent == null ? " intent:null" : (
            intentData == null ?
                " intentData:null" :
                String.format(" intentData:\"%s\"", intentData.toString())
        )));
        message.append(isMainActivity(activity, true) ? " Main" : " Not main");
        Log.d(TAG, message.append(".").toString());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d(TAG, String.format("onActivityResumed(%s) %s.", activity.getClass().getSimpleName(),
            isMainActivity(activity, true) ? "Main" : "Not main"));
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d(TAG, String.format("onActivityPaused(%s).", activity.getClass().getSimpleName()));
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d(TAG, String.format("onActivityStopped(%s).", activity.getClass().getSimpleName()));
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        Log.d(TAG, String.format("onActivitySaveInstanceState(%s,%s).",
            activity.getClass().getSimpleName(), String.valueOf(bundle)));
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d(TAG, String.format("onActivityDestroyed(%s).", activity.getClass().getSimpleName()));
        if (activity == currentActivity) {
            currentActivity = null;
        }
    }
}
