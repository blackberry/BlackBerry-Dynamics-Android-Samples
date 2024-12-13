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

package com.good.gd.example.securestore.common_lib;

import com.good.gd.GDStateListener;

import static com.good.gd.example.securestore.common_lib.utils.AppLogUtils.DEBUG_LOG;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * App GD State listener used to receive GD Auth events and send to all App registered components
 *
 * Any app component which needs to be aware of GD State to call GD APIs registers with state control
 */
public class AppGDStateControl implements GDStateListener {

    private static AppGDStateControl _instance;

    public enum State {
        GD_NotAuthorized, // App is not GD authorised so no GD API can be used. This is initial state at startup
        GD_Authorized,  // App is GD authorised, any GD API can be used
        GD_Locked // App is GD UI locked, background tasks can continue but UI cannot be updated
    }

    private State mCurrentState;

    Set<AppGDStateControlListener> mStateListeners;

    public static AppGDStateControl createInstance() {

        if (_instance == null) {
            _instance = new AppGDStateControl();
        }

        return _instance;

    }

    public static AppGDStateControl getInstance() {

        return _instance;

    }

    private AppGDStateControl() {

        //By default we are in not authorized state until we hear from GD
        mCurrentState = State.GD_NotAuthorized;

        mStateListeners = new HashSet<AppGDStateControlListener>();

    }

    public void addAppStateListener(AppGDStateControlListener aListener){

        mStateListeners.add(aListener);
    }

    public void removeAppStateListener(AppGDStateControlListener aListener){

        mStateListeners.remove(aListener);
    }

    public State getCurrentState(){
        return mCurrentState;
    }

    private void updateListeners() {

        for(AppGDStateControlListener l : mStateListeners){
            l.onAppGDStateChanged(mCurrentState);
        }
    }

    @Override
    public void onAuthorized() {

        DEBUG_LOG("onAuthorized");

        mCurrentState = State.GD_Authorized;

        updateListeners();
    }

    @Override
    public void onLocked() {

        DEBUG_LOG("onLocked");

        mCurrentState = State.GD_Locked;

        updateListeners();
    }

    @Override
    public void onWiped() {

        mCurrentState = State.GD_NotAuthorized;

        updateListeners();

    }

    @Override
    public void onUpdateConfig(Map<String, Object> settings) {

    }

    @Override
    public void onUpdatePolicy(Map<String, Object> policyValues) {

    }

    @Override
    public void onUpdateServices() {

    }

    @Override
    public void onUpdateEntitlements() {

    }
}
