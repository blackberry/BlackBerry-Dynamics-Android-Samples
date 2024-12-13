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

package com.good.gd.example.gdinteraction;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Pair;

import com.good.gd.GDAndroid;
import com.good.gd.GDAppEvent;
import com.good.gd.GDAppEventListener;
import com.good.gd.GDAppEventType;
import com.good.gd.GDAppResultCode;
import com.good.gd.GDStateAction;

import static com.good.gd.GDAppResultCode.GDErrorActivationFailed;
import static com.good.gd.GDAppResultCode.GDErrorAppDenied;
import static com.good.gd.GDAppResultCode.GDErrorBlocked;
import static com.good.gd.GDAppResultCode.GDErrorIdleLockout;
import static com.good.gd.GDAppResultCode.GDErrorPasswordChangeRequired;
import static com.good.gd.GDAppResultCode.GDErrorProvisioningFailed;
import static com.good.gd.GDAppResultCode.GDErrorPushConnectionTimeout;
import static com.good.gd.GDAppResultCode.GDErrorRemoteLockout;
import static com.good.gd.GDAppResultCode.GDErrorSecurityError;
import static com.good.gd.GDAppResultCode.GDErrorWiped;

/**
 * Handles GD events. An instance of this class is passed to authorize(),
 * which will retain it as the callback for incoming GDAppEvents.
 */
public class GDEventHandler implements GDAppEventListener {

    private String TAG = GDEventHandler.class.getSimpleName();

    private static GDEventHandler _instance = null;

    private boolean hasReceivedAuthEvent = false;   // set when we receive an authorized GD event

    private WeakReference<GDInteraction> gdInteractionRef = null;
    private final List<Pair<String, List<String>>> eventLog = new ArrayList<>();
	private final DateFormat _timeFormatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    /**
     * onGDEvent - handles events from the GD library including authorization
     * and withdrawal of authorization.
     *
     * Note: this only gets called if we do GDAndroid.getInstance().setGDAppEventListener(this),
     * see initialize() method. The default is that it doesn't get called.
     *
     * @see com.good.gd.GDAppEventListener#onGDEvent(com.good.gd.GDAppEvent)
     */
    public void onGDEvent(GDAppEvent appEvent) {

        switch (appEvent.getEventType()) {
            case GDAppEventAuthorized:
                hasReceivedAuthEvent = true;
                break;

            default:
                break;
        }

        addEventToLog(appEvent);
    }

    /*
     * This gets called by the GDInteraction at creation time, so we can call it
     * back when we get a new event.
     */
    public void setGDInteractionActivity(GDInteraction gdInteraction) {
        gdInteractionRef = new WeakReference<>(gdInteraction);
    }

    // Event log

    private void addEventToLog(GDAppEvent event) {
        addEventToLog(stringsForEvent(event));
    }

    private void addEventToLog(List<String> eventStrings) {

        eventLog.add(new Pair<String, List<String>>(timeNowString(), eventStrings));

        if (gdInteractionRef != null && gdInteractionRef.get() != null) {
            /*
             * If there is an GDInteraction alive, we should inform it of
             * the new event, so that it can update the view.
             */
          	gdInteractionRef.get().updateFromEventHandler();
        }
    }

    int eventCount() {
        return eventLog.size();
    }

    Pair<String, List<String>> eventAtIndex(int index) {
        return eventLog.get(index);
    }

    // Singleton

    private GDEventHandler() {
        super();
    }

    public static synchronized GDEventHandler getInstance() {
        if (_instance == null) {
            _instance = new GDEventHandler();
        }
        return _instance;
    }

    void initialize() {

        // We use the GDStateAction callback interface for preference now, but you can uncomment
        // this line to use the GDAppEvent interface instead:
        // GDAndroid.getInstance().setGDAppEventListener(this);

        registerGDStateReceiver();
    }

    private BroadcastReceiver gdStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            addEventToLog(Arrays.asList("onReceive: " + intent));
            if (intent.getAction().equals(GDStateAction.GD_STATE_AUTHORIZED_ACTION)) {

                hasReceivedAuthEvent = true;

            } else if (intent.getAction().equals(GDStateAction.GD_STATE_LOCKED_ACTION)) {

                GDAppResultCode extra = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                        ? intent.getSerializableExtra(GDStateAction.GD_STATE_LOCKED_ACTION, GDAppResultCode.class)
                        : (GDAppResultCode) intent.getSerializableExtra(
                                                            GDStateAction.GD_STATE_LOCKED_ACTION);
                addEventToLog(Arrays.asList("onReceive: LOCKED: extra = " + extra));
                handleLockedEventSpecial(extra);
            }
        }
    };

    // If we get a "locked" event and have not already been authorized, the implication is that
    // we can go ahead and use the dynamics APIs, because we must have been authorized in order
    // for the runtime to determine that we are blocked. We are basically in the same state as
    // if we had been running as authorized and then blocked. So, test use of the APIs.
    private void handleLockedEventSpecial(GDAppResultCode code) {
        if (code == GDErrorBlocked && !hasReceivedAuthEvent) {
            addEventToLog(Arrays.asList("Got GDErrorBlocked at startup"));
            if (gdInteractionRef != null && gdInteractionRef.get() != null) {
                gdInteractionRef.get().actionBackgroundFileContent();
            }
        }
    }

    private void registerGDStateReceiver() {
        IntentFilter intentFilter = new IntentFilter();

        // Register either all state actions or only particular one per one Broadcast receiver.
        // State action can be then received from the Broadcast Intent.
        // Note, here we are only registering for the LOCKED action, we can add more later
        intentFilter.addAction(GDStateAction.GD_STATE_AUTHORIZED_ACTION);
        intentFilter.addAction(GDStateAction.GD_STATE_LOCKED_ACTION);
        intentFilter.addAction(GDStateAction.GD_STATE_WIPED_ACTION);
        intentFilter.addAction(GDStateAction.GD_STATE_UPDATE_POLICY_ACTION);
        intentFilter.addAction(GDStateAction.GD_STATE_UPDATE_SERVICES_ACTION);
        intentFilter.addAction(GDStateAction.GD_STATE_UPDATE_CONFIG_ACTION);
        intentFilter.addAction(GDStateAction.GD_STATE_UPDATE_ENTITLEMENTS_ACTION);

        GDAndroid.getInstance().registerReceiver(gdStateReceiver, intentFilter);
    }

    private List<String> stringsForEvent(GDAppEvent gdAppEvent) {

        List<String> results = new ArrayList<>();

        GDAppEventType eventType = gdAppEvent.getEventType();

        if (eventType == GDAppEventType.GDAppEventRemoteSettingsUpdate) {

            results.add("GDAppEventListener Received SettingsUpdate event: " + gdAppEvent);

            Map<String, Boolean> protocols =
                (Map<String, Boolean>) GDAndroid.getInstance()
                                                .getApplicationConfig()
                                                .get(GDAndroid.GDAppConfigKeyCommunicationProtocols);

            results.add("Allowed communication protocols:");
            results.add(GDAndroid.GDProtocolsKeyTLSv1_0 + ": " + protocols.get(GDAndroid.GDProtocolsKeyTLSv1_0));
            results.add(GDAndroid.GDProtocolsKeyTLSv1_1 + ": " + protocols.get(GDAndroid.GDProtocolsKeyTLSv1_1));
            results.add(GDAndroid.GDProtocolsKeyTLSv1_2 + ": " + protocols.get(GDAndroid.GDProtocolsKeyTLSv1_2));

        } else if (eventType == GDAppEventType.GDAppEventAuthorized) {

            results.add("GDAppEventListener Received authorized event: " + gdAppEvent);

        } else if (eventType == GDAppEventType.GDAppEventNotAuthorized) {

            results.add("GDAppEventListener Received unauthorized event: " + gdAppEvent +
                                    " - " + explanationForResultCode(gdAppEvent.getResultCode()));

        } else if (eventType == GDAppEventType.GDAppEventServicesUpdate) {

            results.add("GDAppEventListener Received service update event: " + gdAppEvent);

        } else {
            results.add("GDAppEventListener Got unknown event: " + gdAppEvent);
        }

        return results;
    }

    String timeNowString() {
        return _timeFormatter.format(new Date());
    }

    private String explanationForResultCode(GDAppResultCode code) {
        return
            (code == GDErrorActivationFailed)       ? "activation not completed" :
            (code == GDErrorProvisioningFailed)     ? "activation not completed" :
            (code == GDErrorPushConnectionTimeout)  ? "activation could not be completed" :
            (code == GDErrorIdleLockout)            ? "application blocked till unlocked" :
            (code == GDErrorRemoteLockout)          ? "application blocked till unlocked" :
            (code == GDErrorPasswordChangeRequired) ? "application blocked till password changed" :
            (code == GDErrorSecurityError)          ? "secure store could not be unlocked" :
            (code == GDErrorBlocked)                ? "application blocked" :
            (code == GDErrorAppDenied)              ? "container is wiped" :
            (code == GDErrorWiped)                  ? "container is wiped" :
                                                      "INVALID RESULT CODE";
    }
}
