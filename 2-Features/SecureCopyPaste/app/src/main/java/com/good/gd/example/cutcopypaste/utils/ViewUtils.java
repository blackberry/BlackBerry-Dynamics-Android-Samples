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

package com.good.gd.example.cutcopypaste.utils;

import android.view.View;
import android.view.ViewGroup;

public class ViewUtils {

    /**
     *
     * @param parent root ViewGroup
     * @param viewAction action to perform on each child of the <b><code>parent</code></b> ViewGroup
     */
    static public void recursiveLoopChildren(ViewGroup parent, ViewAction viewAction) {
        for (int i = parent.getChildCount() - 1; i >= 0; i--) {
            final View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                recursiveLoopChildren((ViewGroup) child,viewAction);
            } else {
                if (child != null) {
                    viewAction.execute(child);
                }
            }
        }
    }

    public interface ViewAction {
        void execute(View view);
    }
}
