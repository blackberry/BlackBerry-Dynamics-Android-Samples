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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class IncomingEventActivity extends BaseActivity {
    private static final String LOG_TAG = "IncomingEventActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreate\n");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.incoming_event);

        Button btnAccept = findViewById(R.id.accept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

                Intent appIntent = new Intent(IncomingEventActivity.this, InCallActivity.class);
                appIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(appIntent);
            }
        });

        Button btnDecline = findViewById(R.id.decline);
        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setSwitchView(R.id.switchView);

        View mainView = findViewById(R.id.main_layout);

        SampleAppActivityUtils.adjustViewsIfEdgeToEdgeMode(mainView, null, null);
    }
}
