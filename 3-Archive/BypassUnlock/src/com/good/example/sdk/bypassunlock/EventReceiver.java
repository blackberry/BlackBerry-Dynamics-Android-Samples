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

package com.good.example.sdk.bypassunlock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.good.gd.GDAndroid;

public class EventReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "EventReceiver";
    private static final String INTENT_ACTION_UNBLOCK = "INTENT_ACTION_UNBLOCK";
    public static final String BLOCK_ID = "BLOCK_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG, "TestReceiver.onReceive\n");

        String action = intent.getAction();

        if (action != null && action.equals(INTENT_ACTION_UNBLOCK)) {
            GDAndroid.executeUnblock(BLOCK_ID);
        } else {
            startIncomingEventActivity(context);
        }
    }

    private void startIncomingEventActivity(Context context) {
        Intent appIntent = new Intent(context, IncomingEventActivity.class);
        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(appIntent);
    }
}
