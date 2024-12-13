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

package com.good.gd.example.securestore;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.good.gd.GDAndroid;
import com.good.gd.example.securestore.common_lib.AppGDStateControl;
import com.good.gd.example.securestore.common_lib.AppGDStateControlListener;

public class ConnectedApplicationManagerActivity extends FragmentActivity implements AppGDStateControlListener {

    private final String FRAGMENT_TAG = "ConnectedApplicationManagement";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GDAndroid.getInstance().activityInit(this);

        setContentView(R.layout.mainfragment);
    }


    private void loadUIFragmentIfNeeded() {

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.findFragmentByTag(FRAGMENT_TAG) == null) {

            //UI is not already loaded so we load it now
            ConnectedApplicationManagerListFragment m_fragment = new ConnectedApplicationManagerListFragment();

            FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();
            fragmentTransaction.replace(R.id.listFragmentSpace,
                    m_fragment, FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
    }

    private void removeUIFragment() {

        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment f = fragmentManager.findFragmentByTag(FRAGMENT_TAG);

        if (f != null) {

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(f);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //This specific Activity is only interested in Auth events when it is in the foreground
        AppGDStateControl.getInstance().addAppStateListener(this);

        if (AppGDStateControl.getInstance().getCurrentState() == AppGDStateControl.State.GD_Authorized) {
            //We are already authorized so we can show our UI
            loadUIFragmentIfNeeded();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        AppGDStateControl.getInstance().removeAppStateListener(this);

        /*
        We remove the Fragment here because otherwise the system remembers it was loaded and if it kills the process
        for memory reasons when it is restarted it will automatically attempt to add the Fragment when we would be in an
        non Authorized state
         */
        removeUIFragment();
    }


    @Override
    public void onAppGDStateChanged(AppGDStateControl.State aNewState) {

        if (AppGDStateControl.getInstance().getCurrentState() == AppGDStateControl.State.GD_Authorized) {
            //We are already authorized so we can show our UI
            loadUIFragmentIfNeeded();
        } else if (AppGDStateControl.getInstance().getCurrentState() == AppGDStateControl.State.GD_NotAuthorized) {
            //We remove our Fragment because UI uses GD APIs
            removeUIFragment();
        }
    }
}
