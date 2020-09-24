/* Copyright (c) 2017 - 2020 BlackBerry Limited.
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

package com.good.automated.test.screenFinder.general;

import android.util.Log;

import com.good.automated.test.screenFinder.view.BBDView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.concurrent.BlockingDeque;

/**
 * Class with methods for converting a view stack to {@link JSONObject}.
 */
public class JSONCreator {

    private static final String TAG = JSONCreator.class.getSimpleName();

    private static final String KEY_PACKAGE = "packageName";
    private static final String KEY_RESOURCE_ID = "resourceId";
    private static final String KEY_INTERNAL_ID = "uniqueId";
    private static final String KEY_TIME_VISIBLE = "timeVisible";
    private static final String KEY_VIEW_STACK = "viewStack";
    private static final String KEY_APPEARANCE_TIME = "appearanceTime";

    private BlockingDeque<BBDView> viewStack;

    public JSONCreator(BlockingDeque<BBDView> viewStack) {
        this.viewStack = viewStack;
    }

    /**
     * Returns collected view stack to {@link JSONObject}.
     *
     * @return  view stack as {@link JSONObject}
     */
    public JSONObject getViewStackAsJson() {
        Iterator<BBDView> it = viewStack.iterator();
        return parseStack(it);
    }

    /**
     * Returns collected view stack as queue to {@link JSONObject}.
     *
     * @return  view queue as {@link JSONObject}
     */
    public JSONObject getViewQueueAsJson() {
        Iterator<BBDView> it = viewStack.descendingIterator();
        return parseStack(it);
    }

    /**
     * Parses present view stack to {@link JSONObject}.
     *
     * @param viewIterator  {@link Iterator} of views in stack
     * @return              {@link JSONObject} with stack view
     */
    private JSONObject parseStack(Iterator<BBDView> viewIterator) {
        JSONObject viewMappingJSON = new JSONObject();
        JSONArray viewStackJson = new JSONArray();

        while (viewIterator.hasNext()) {
            BBDView view = viewIterator.next();
            try {
                JSONObject bbdViewJson = new JSONObject();

                bbdViewJson.put(KEY_INTERNAL_ID, view.getId());
                bbdViewJson.put(KEY_PACKAGE, view.getPackageName());
                bbdViewJson.put(KEY_RESOURCE_ID, view.getResourceId());
                bbdViewJson.put(KEY_TIME_VISIBLE, view.getTimeOnTheTop());
                bbdViewJson.put(KEY_APPEARANCE_TIME, view.getAppearanceTime());

                viewStackJson.put(bbdViewJson);
            } catch (JSONException ex) {
                Log.e(TAG, "JSONException while creating json from view stack!", ex);
            }
        }

        try {
            viewMappingJSON.put(KEY_VIEW_STACK, viewStackJson);
        } catch (JSONException ex) {
            Log.e(TAG, "JSONException while adding a view stack to json.", ex);
        }

        return viewMappingJSON;
    }

}
