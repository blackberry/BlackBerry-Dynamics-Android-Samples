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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.good.gd.Activity;

public class ContactsActivity extends Activity {
    private static final String LOG_TAG = "ContactsActivity";

    @Override
    protected void onCreate(Bundle bundle) {
        Log.i(LOG_TAG, "onCreate\n");

        super.onCreate(bundle);

        setContentView(R.layout.contacts);

        String names[] = {
            "Mary Stansberry",
            "Daniel Drummond",
            "Virginia Rush",
            "Kathryn Dooley",
            "William Raper",
            "Peggy Dale",
            "Deloris Bromley",
            "Eric Morgan",
            "Jan Anderson",
            "Georgianna Catlin",
            "Eduardo Johnson",
            "Christian Kittinger",
            "Kerry Boggess",
            "Harold Stephens",
            "Wilma Burke",
            "Thomas Williams",
            "Gloria Hafford",
            "Charlene Hernandez",
            "Reginald Pauling",
            "William Bouchard"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                names);
        ListView listview = findViewById(R.id.listview);
        listview.setAdapter(adapter);

        View mainView = findViewById(R.id.main_layout);

        SampleAppActivityUtils.setupAppBar(mainView, getString(R.string.app_name), false);
        SampleAppActivityUtils.adjustViewsIfEdgeToEdgeMode(mainView, null, listview);
    }
}
