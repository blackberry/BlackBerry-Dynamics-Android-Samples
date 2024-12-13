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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import android.util.Log;

import com.good.gd.GDAndroid;

import static com.good.gd.example.gdinteraction.GDInteraction.BLOCK_ID;

public class EventReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "EventReceiver";
    private static final String INTENT_ACTION_AWAKE = "INTENT_ACTION_AWAKE";
    private static final String INTENT_ACTION_UNBLOCK = "INTENT_ACTION_UNBLOCK";
    private static final String INTENT_ACTION_BLOCK = "INTENT_ACTION_BLOCK";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG, "TestReceiver.onReceive\n");

        String action = intent.getAction();

        if (action == null) {
            return;
        }

        switch (action) {

            // adb shell am broadcast -n com.good.gd.example.gdinteraction/.EventReceiver -a INTENT_ACTION_AWAKE
            case INTENT_ACTION_AWAKE:
                processAwakeEvent(context);
                break;

            // adb shell am broadcast -n com.good.gd.example.gdinteraction/.EventReceiver -a INTENT_ACTION_UNBLOCK
            case INTENT_ACTION_UNBLOCK:
                processUnblockEvent();
                break;

            // adb shell am broadcast -n com.good.gd.example.gdinteraction/.EventReceiver -a INTENT_ACTION_BLOCK
            case INTENT_ACTION_BLOCK:
                processBlockEvent();
                break;

        }
    }

    private void processAwakeEvent(Context context) {
        Intent msgIntent = new Intent(context, GDAuthorizationService.class);
        ContextCompat.startForegroundService(context, msgIntent);
    }

    private void processUnblockEvent() {
        Log.v(LOG_TAG,"Calling GDAndroid.executeUnblock() from event");
        GDAndroid.executeUnblock(BLOCK_ID);
    }

    private void processBlockEvent() {
        Log.v(LOG_TAG,"Calling GDAndroid.executeBlock() from event");
        GDAndroid.executeBlock(BLOCK_ID, "Local block", "Your application is blocked locally. Please use the same block id and send broadcast message with action INTENT_ACTION_UNBLOCK to unblock it.\n" +
                "You can run 'adb shell am broadcast -n com.good.gd.example.gdinteraction/.EventReceiver -a INTENT_ACTION_UNBLOCK'");
    }
}
