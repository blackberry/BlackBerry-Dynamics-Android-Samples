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

package com.good.gd.example.cutcopypaste.dlp;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Custom callback, removes custom menu items from selection context menu if DLP is on
public class SecureActionModeCallback implements ActionMode.Callback {

    private SecureActionModeCallback(){
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
   /*
            Here if DLP option is on (That is GC Policy Option "Prevent copy from GD apps to non-GD apps") then we filter the menu offered to the user to ensure that only
            CUT, COPY, PASTE, SELECT_ALL options are provided

            In Android N there are other options added to the list, items like SHARE and the ability for 3rd party apps to offer themselves as option usage for TEXT (an example
            is the Translate option offered by Google Translate App). Any app which offers its self with an intent filter of android.intent.action.PROCESS_TEXT would show in the list

            We filter all out from the menu provided to the user
             */

        if (DLPPolicies.getInstance().isOutboundDlpEnabled()) {

            List<Integer> allowedMenuItems = Arrays.asList(android.R.id.copy,
                                                            android.R.id.paste,
                                                            android.R.id.cut,
                                                            android.R.id.selectAll);

            List<Integer> menuItemsToRemove = new ArrayList<>();

            // Check through list of Menu Items that have been built into the Menu, if Item is not standard Cut/Copy/Paste/Select All record it to be removed
            for (int i = 0; i < menu.size(); i++) {
                MenuItem mI = menu.getItem(i);

                if (!allowedMenuItems.contains(mI.getItemId())) {
                    menuItemsToRemove.add(mI.getItemId());
                }
            }

            // Now remove all non standard MenuItems and return we have updated the menu
            for (int toRemoveItem : menuItemsToRemove) {
                menu.removeItem(toRemoveItem);
            }
            return true;
        }
        // If DLP is off we allow all these menu items
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
    }

    public static ActionMode.Callback newCallback(){
        return new SecureActionModeCallback();
    }
}
