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

import android.content.Context;

import com.good.gd.GDAndroid;
import com.good.gd.example.securestore.common_lib.AppGDStateControl;
import com.good.gd.example.securestore.common_lib.AppGDStateControlListener;
import com.good.gd.example.securestore.common_lib.ConnectedApplicationControl;
import com.good.gd.example.securestore.common_lib.ConnectedApplicationListener;
import com.good.gd.example.securestore.common_lib.ConnectedApplicationState;
import com.good.gd.example.securestore.common_lib.FileTransferControl;
import com.good.gd.example.securestore.common_lib.ServicesControl;

/**
 * Secure Store Handheld App Manager
 */
public class AppStateManager implements AppGDStateControlListener, ConnectedApplicationListener {

    private static AppStateManager _instance;

    public static AppStateManager createInstance(Context aContext) {

        if (_instance == null) {
            _instance = new AppStateManager(aContext);
        }

        return _instance;
    }


    private AppStateManager(Context aContext) {

        //We first do GD Application Init which will load the basic GD functionality
        GDAndroid.getInstance().applicationInit(aContext);

        //Next we create Singleton State Listener and set on GD
        AppGDStateControl listener = AppGDStateControl.createInstance();
        GDAndroid.getInstance().setGDStateListener(listener);

        // Next we set Singletons which handle our InterDevice Communication
        ServicesControl.createInstance();
        FileTransferControl.createInstance(aContext.getMainLooper());
        ConnectedApplicationControl.createInstance(aContext);

        AppGDStateControl.getInstance().addAppStateListener(this);
        ConnectedApplicationControl.getInstance().addConnectedAppStateListener(this);
    }

    @Override
    public void onAppGDStateChanged(AppGDStateControl.State aNewState) {

        if (aNewState == AppGDStateControl.State.GD_Authorized) {
            // We are now authorized so we check if there are any Connected Applications
            ConnectedApplicationControl.getInstance().updateConnectedApplications();
        }
    }

    @Override
    public void onConnectedApplicationStateChanged(ConnectedApplicationState aState) {
    }
}
