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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * State Machine for connected applications
 */
public class ConnectedApplicationState {

    static private final int STATE_RESET = 0x00;
    static private final int STATE_NO_APP_CONNECTED = 0x01;
    static private final int STATE_APP_PENDING_ACTIVATION = 0x02;
    static private final int STATE_APP_CONNECTED =0x04;
    static private final int STATE_APP_ACTIVATED_NOT_CONNECTED =0x08;
    static private final int STATE_APP_REMOVED = 0x10;

    private int mState = STATE_RESET;

    private Collection<String> mConnectedApps = null;
    private Collection<String> mToActivateApps = null;
    private Collection<String> mActivatedNotConnectedApps = null;
    private Collection<String> mRemovedApps = null;

    //Return state, used to determine if State has changed
    public int getState() {
        return mState;
    }

    // Reset state when updating from new information
    public void resetState() {
        mState = STATE_RESET;
        mConnectedApps = new ArrayList<String>();
        mToActivateApps = new ArrayList<String>();
        mActivatedNotConnectedApps = new ArrayList<String>();
        mRemovedApps = new ArrayList<String>();
    }

    public void setStateNoAppConnected(){
        mState |= STATE_NO_APP_CONNECTED;
    }

    public boolean isNoAppConnected() {
        return (mState & STATE_NO_APP_CONNECTED) == STATE_NO_APP_CONNECTED;

    }

    public void setStateAppConnected(){
        mState |= STATE_APP_CONNECTED;
    }

    public boolean isAppConnected() {
        return (mState & STATE_APP_CONNECTED) == STATE_APP_CONNECTED;

    }

    public void setStateAppPendingActivation(){
        mState |= STATE_APP_PENDING_ACTIVATION;
    }

    public boolean isAppPendingActivation() {
        return (mState & STATE_APP_PENDING_ACTIVATION) == STATE_APP_PENDING_ACTIVATION;

    }

    public void setStateActivatedNotConnected(){
        mState |= STATE_APP_ACTIVATED_NOT_CONNECTED;
    }

    public boolean isActivatedNotConnected() {
        return (mState & STATE_APP_ACTIVATED_NOT_CONNECTED) == STATE_APP_ACTIVATED_NOT_CONNECTED;

    }

    public void setStateRemoved(){
        mState |= STATE_APP_REMOVED;
    }

    public boolean isStateRemoved() {
        return (mState & STATE_APP_REMOVED) == STATE_APP_REMOVED;

    }


    public void clearStateAppPendingActiviation(){
        mState ^= STATE_APP_PENDING_ACTIVATION;
    }

    public Collection<String> getConnectedApps() {
        return mConnectedApps;
    }

    public Collection<String> getAppsToActivate() {
        return mToActivateApps;
    }

    public Collection<String> getActivatedNotConnectedApps( ) {return mActivatedNotConnectedApps;}

    public Collection<String> getRemovedApps() {return mRemovedApps;}

    public void addConnectedApp(String aDeviceName){
        mConnectedApps.add(aDeviceName);
    }

    public void addAppToActivate(String aDeviceName){
        mToActivateApps.add(aDeviceName);
    }

    public void addAppToActivatedNotConnected(String aDeviceName) {mActivatedNotConnectedApps.add(aDeviceName); }

    public void addremovedApp(String aDeviceName) {mRemovedApps.add(aDeviceName); }

    public void removeAppToActivate(String aDeviceName){
        mToActivateApps.remove(aDeviceName);
    }

    public String dumpConnectedApplicationState() {

        String ret = "ConnectedApplicationState isNoAppConnected =" + isNoAppConnected() + " isAppConnected =" + isAppConnected() +
                " isAppPendingActivation = " + isAppPendingActivation() + " isAppActivatedNotConnected = " + isActivatedNotConnected();

        ret += "ConnectedApps = " + Arrays.toString(getConnectedApps().toArray());
        ret += "AppsToActivate =" + Arrays.toString(getAppsToActivate().toArray());
        ret += "AppsActivatedNotConnected =" + Arrays.toString(getActivatedNotConnectedApps().toArray());

        return ret;
    }
}
