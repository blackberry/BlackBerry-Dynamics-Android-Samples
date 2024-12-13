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

import static com.good.gd.example.securestore.common_lib.utils.AppLogUtils.DEBUG_LOG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.good.gd.GDAndroid;
import com.good.gd.example.securestore.common_lib.AppGDStateControl;
import com.good.gd.example.securestore.common_lib.AppGDStateControlListener;
import com.good.gd.example.securestore.common_lib.ConnectedApplicationControl;
import com.good.gd.example.securestore.common_lib.ConnectedApplicationListener;
import com.good.gd.example.securestore.common_lib.ConnectedApplicationState;
/**
 * SecureStore activity - a basic file browser list which supports multiple modes
 * (Container and insecure SDCard). Files can be deleted, moved to the container
 * and if they're .txt files they can be opened and viewed.
 */
public class SecureStore extends SampleAppActivity implements OnClickListener, AppGDStateControlListener, ConnectedApplicationListener {

    private FileBrowserFragment m_fragment = null;

    private final String FRAGMENT_TAG = "Secure_Store_UI";

    boolean permissionFlag;
    private final int MANAGE_ALL_FILES_ACCESS_PERMISSION = 101;

    /*
     * onCreate - sets up the core activity members
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GDAndroid.getInstance().activityInit(this);
        setContentView(R.layout.mainfragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        updateConnectedApplicationButtons(ConnectedApplicationControl.getInstance().getCurrentState());
        return super.onCreateOptionsMenu(menu);
    }

    public void onConnectionAction(int id) {

        if (id == R.id.action_activate_application) {

            if(ConnectedApplicationControl.getInstance().isConnectedApplicationActivationAllowed()) {
                //If ConnectedApplicationActivation is allowed then start it

                ConnectedApplicationState state = ConnectedApplicationControl.getInstance().getCurrentState();

                // Here we simply get the first connected application which is pending activation and activate that
                //If there were multiple then it would be possible to create a UI picker to show user which one to activate
                String address = state.getAppsToActivate().iterator().next();

                ConnectedApplicationControl.getInstance().startConnectedApplicationActivation(this,address);
            }

        } else if(id == R.id.action_manage_connected_apps) {

            //Start Activity to show connected application management
            Intent i = new Intent();
            i.setClass(this, ConnectedApplicationManagerActivity.class);
            startActivity(i);
        }
    }

    private void loadUIFragmentIfNeeded() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if(fragment == null) {
            //UI is not already loaded so we load it now
            m_fragment = new FileBrowserFragment();

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
        permissionFlag = false;
        //This specific Activity is only interested in Auth events when it is in the foreground
        AppGDStateControl.getInstance().addAppStateListener(this);

        if (AppGDStateControl.getInstance().getCurrentState() == AppGDStateControl.State.GD_Authorized) {
            //We are already authorized so we can show our UI
            loadUIFragmentIfNeeded();
        }

        ConnectedApplicationControl.getInstance().addConnectedAppStateListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        AppGDStateControl.getInstance().removeAppStateListener(this);
        ConnectedApplicationControl.getInstance().removeConnectedAppStateListener(this);

        /*
        We remove the Fragment here because otherwise the system remembers it was loaded and if it kills the process
        for memory reasons when it is restarted it will automatically attempt to add the Fragment when we would be in an
        non Authorized state
         */

        //If application is being paused for permission, don't remove fragment
        if (!permissionFlag) {
            removeUIFragment();
        }
    }

    public void requestAllFilesPermission() {
        permissionFlag = true;
        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        Uri uri = Uri.parse("package:" + getPackageName());
        intent.setData(uri);
        startActivityForResult(intent, MANAGE_ALL_FILES_ACCESS_PERMISSION);
    }

    @Override
    public void onClick(View v) {
        if (m_fragment != null) {
            m_fragment.onClick(v.getId());
            switch (v.getId()) {
                case R.id.action_btn_container:
                    updateBtns(R.id.action_btn_container, R.id.action_btn_sdcard);
                    break;
                case R.id.action_btn_sdcard:
                    updateBtns(R.id.action_btn_sdcard, R.id.action_btn_container);
                    break;
            }
        }

        onConnectionAction(v.getId());
    }

    private void updateBtns(int enabledButton, int disabledButton) {
        ((ToggleButton) findViewById(enabledButton)).setChecked(true);
        ((ToggleButton) findViewById(disabledButton)).setChecked(false);
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

    private void updateConnectedApplicationButtons(ConnectedApplicationState aState){

        View appBarLayout = findViewById(R.id.app_bottom_bar);

        View promptActivate = appBarLayout.findViewById(R.id.action_activate_application);
        View connectedApp = appBarLayout.findViewById(R.id.action_application_connected);
        View activateDisallowed = appBarLayout.findViewById(R.id.action_activate_application_disallowed);
        View manageConnectedApplications = appBarLayout.findViewById(R.id.action_manage_connected_apps);

        DEBUG_LOG("updateConnectedApplicationButtons ConnectedApplicationState = " + aState.dumpConnectedApplicationState());

        //If No App Connected then we show neither icon
        if (aState.isNoAppConnected()) {
            promptActivate.setVisibility(View.GONE);
            connectedApp.setVisibility(View.GONE);
            manageConnectedApplications.setVisibility(View.GONE);
        } else {
            // It is possible to have multiple connected applications so could have both applications pending activation and connected

            if (aState.isAppPendingActivation()) {

                if (ConnectedApplicationControl.getInstance().isConnectedApplicationActivationAllowed()) {
                    promptActivate.setVisibility(View.VISIBLE);
                } else {
                    activateDisallowed.setVisibility(View.VISIBLE);
                }
            } else {
                promptActivate.setVisibility(View.GONE);
                activateDisallowed.setVisibility(View.GONE);
            }

            if (aState.isAppConnected()) {
                connectedApp.setVisibility(View.VISIBLE);
            } else {
                connectedApp.setVisibility(View.GONE);
            }

            manageConnectedApplications.setVisibility(View.VISIBLE);
        }
    }

    /* @Override */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (m_fragment != null) {
            // Fragment has its own request permission API, but since we remove FileBrowserFragment with onPause callback,
            // this fragment won't be attached again to parent Activity so we can't relay on that API.
            m_fragment.onPermissionResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onConnectedApplicationStateChanged(ConnectedApplicationState aState) {
        updateConnectedApplicationButtons(aState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MANAGE_ALL_FILES_ACCESS_PERMISSION) {
            permissionFlag = false;
            m_fragment.refreshUI();
        }
    }
}