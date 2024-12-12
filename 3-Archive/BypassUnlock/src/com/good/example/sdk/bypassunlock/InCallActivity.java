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

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class InCallActivity extends BaseActivity {
    private static final String LOG_TAG = "InCallTestActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreate\n");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.in_call);
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
