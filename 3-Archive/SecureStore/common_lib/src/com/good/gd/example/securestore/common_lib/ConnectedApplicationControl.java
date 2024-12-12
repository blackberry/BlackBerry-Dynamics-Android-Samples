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

import android.content.Context;

import com.good.gd.support.GDConnectedApplication;
import com.good.gd.support.GDConnectedApplicationState;
import com.good.gd.support.GDConnectedApplicationSupport;
import com.good.gd.support.GDConnectedApplicationSupportListener;

import static com.good.gd.example.securestore.common_lib.utils.AppLogUtils.DEBUG_LOG;
import static com.good.gd.example.securestore.common_lib.utils.AppLogUtils.ERROR_LOG;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Singleton used to control connected applications
 *
 * If GD Handheld Support Library is not built into the app then the ConnectedApplication APIs
 * cannot be used for interDeviceSupport. In which case App will still work but inter device
 * functionality cannot be used
 *
 * APIs provide abstraction over ConnectedApplication APIs.
 *
 * - Start Connected Application Activation
 * - Get list of connected applications
 * - Determine if application activation is allowed
 */
public class ConnectedApplicationControl implements GDConnectedApplicationSupportListener {

    private static ConnectedApplicationControl _instance;

    private GDConnectedApplicationSupport mConnectedAppSupport;

    private ArrayList<GDConnectedApplication> mConnectedApplications;

    private ConnectedApplicationState mConnectedApplicationState;

    Set<ConnectedApplicationListener> mStateListeners;

    public static ConnectedApplicationControl createInstance(Context aContext) {

        if (_instance == null) {
            _instance = new ConnectedApplicationControl(aContext);
        }

        return _instance;

    }

    public static ConnectedApplicationControl getInstance() {

        return _instance;

    }

    private ConnectedApplicationControl(Context aContext) {

        mConnectedApplicationState = new ConnectedApplicationState();
        mConnectedApplicationState.resetState();

        try {

            mConnectedAppSupport = GDConnectedApplicationSupport.createInstance(aContext, this);

        } catch (com.good.gd.error.GDMissingDependancyError error) {
            // Google Play Services Library is not linked to the app so connectedAppControl will not work
            // All other calls to this class fail silently
            ERROR_LOG("Google Play Services Library not linked. InterDevice support APIs cannot be used");

            return;
        }

        //If GooglePlayServices lib is linked set state to not connected until/if we get information
        mConnectedApplicationState.setStateNoAppConnected();

        mStateListeners = new HashSet<ConnectedApplicationListener>();

    }

    public void addConnectedAppStateListener(ConnectedApplicationListener aListener) {

        if (mConnectedAppSupport == null) {
            return;
        }

        mStateListeners.add(aListener);
    }

    public void removeConnectedAppStateListener(ConnectedApplicationListener aListener) {

        if (mConnectedAppSupport == null) {
            return;
        }

        mStateListeners.remove(aListener);
    }

    public ConnectedApplicationState getCurrentState() {

        return mConnectedApplicationState;
    }

    public void updateConnectedApplications() {

        if (mConnectedAppSupport == null) {
            return;
        }

        mConnectedAppSupport.queryConnectedApplications();

    }

    public String getConnectedApplicationAddress(String aConnectedApplicationDeviceName) {

        // ConnectedApplicationAddress is used with the GDService APIs to send data to remote API

        if (mConnectedAppSupport == null) {
            return null;
        }

        String remoteAddress = null;

        for (GDConnectedApplication d : mConnectedApplications) {
            if (d.getState() == GDConnectedApplicationState.StateActivated && d.getDisplayName().equals(aConnectedApplicationDeviceName)) {
                remoteAddress = d.getAddress();
                break;
            }
        }

        DEBUG_LOG("Connected Application Address =" + remoteAddress);

        return remoteAddress;
    }

    public boolean isConnectedApplicationActivationAllowed() {

        if (mConnectedAppSupport == null) {
            return false;
        }

        boolean allowed =  mConnectedAppSupport.isConnectedApplicationActivationAllowed();

        DEBUG_LOG("isConnectedApplicationActivationAllowed =" + allowed);

        return allowed;
    }

    public void startConnectedApplicationActivation(Context aActivityContext, String aConnectedApplicationDeviceName) {

        if (mConnectedAppSupport == null) {
            return;
        }

        DEBUG_LOG("startConnectedApplicationActivation Device Name =" + aConnectedApplicationDeviceName);

        // if has not activated start activation

        String remoteAddress = null;

        for (GDConnectedApplication d : mConnectedApplications) {
            if (d.getState() == GDConnectedApplicationState.StateNotActivated && d.getDisplayName().equals(aConnectedApplicationDeviceName)) {
                remoteAddress = d.getAddress();
                break;
            }
        }

        if (remoteAddress != null) {
            mConnectedAppSupport.startConnectedApplicationActivation(remoteAddress, aActivityContext);
        }
    }

    public void removeConnectedApplication(String aConnectedApplicationDeviceName){

        if (mConnectedAppSupport == null) {
            return;
        }

        DEBUG_LOG("removeConnectedApplication Device Name =" + aConnectedApplicationDeviceName);

        String remoteAddress = null;

        for (GDConnectedApplication d : mConnectedApplications) {
            if (d.getDisplayName().equals(aConnectedApplicationDeviceName)) {
                remoteAddress = d.getAddress();
                break;
            }
        }

        if (remoteAddress != null) {
            mConnectedAppSupport.removeConnectedApplication(remoteAddress);
        }

    }

    private void updateListeners() {

        DEBUG_LOG("updateListeners");

            for (ConnectedApplicationListener l : mStateListeners) {
                l.onConnectedApplicationStateChanged(mConnectedApplicationState);
            }
    }

    @Override
    public void onApplicationsConnected(ArrayList<GDConnectedApplication> aConnectedApplications) {

        DEBUG_LOG("onApplicationsConnected Num Apps Connected =" + aConnectedApplications.size());

        //We store connectedApplications Array because we need details like device address later
        mConnectedApplications = aConnectedApplications;

        //Each time we reset the state and process information again
        mConnectedApplicationState.resetState();

        if(mConnectedApplications.size() ==0){

            //If there are no longer any apps connected set state accordingly
            mConnectedApplicationState.setStateNoAppConnected();

        } else {

            for (GDConnectedApplication d : mConnectedApplications) {

                DEBUG_LOG("Connected Application state =" + d.getState() + "Connected Application Name =" + d.getDisplayName()
                + " Connected Application address = " + d.getAddress());

                // for each connected device check if app is connected or pending activation and update state accordingly
                if (d.getState() == GDConnectedApplicationState.StateNotActivated) {

                    mConnectedApplicationState.setStateAppPendingActivation();
                    mConnectedApplicationState.addAppToActivate(d.getDisplayName());

                } else if (d.getState() == GDConnectedApplicationState.StateActivated) {

                    mConnectedApplicationState.setStateAppConnected();
                    mConnectedApplicationState.addConnectedApp(d.getDisplayName());
                } else if(d.getState() == GDConnectedApplicationState.StateNotConnected) {
                    mConnectedApplicationState.setStateActivatedNotConnected();
                    mConnectedApplicationState.addAppToActivatedNotConnected(d.getDisplayName());
                } else if(d.getState() == GDConnectedApplicationState.StateRemoved) {
                    mConnectedApplicationState.setStateRemoved();
                    mConnectedApplicationState.addremovedApp(d.getDisplayName());
                }

            }

        }

        updateListeners();

    }

    @Override
    public void onApplicationActivationComplete(GDConnectedApplication aGdConnectedApplication) {

        DEBUG_LOG("onApplicationActivationComplete state = " + aGdConnectedApplication.getState());

        if (aGdConnectedApplication.getState() == GDConnectedApplicationState.StateActivated) {

            //Now activated we update the state logic

            mConnectedApplicationState.addConnectedApp(aGdConnectedApplication.getDisplayName());
            mConnectedApplicationState.setStateAppConnected();

            mConnectedApplicationState.removeAppToActivate(aGdConnectedApplication.getDisplayName());

            if(mConnectedApplicationState.getAppsToActivate().size()==0){
                mConnectedApplicationState.clearStateAppPendingActiviation();
            }

            updateListeners();
        }

        if (aGdConnectedApplication.getState() == GDConnectedApplicationState.StateActivationFailed_Error ||
                aGdConnectedApplication.getState() == GDConnectedApplicationState.StateActivationFailed_UserCancelled) {

            //In this case we don't need to do anything because activation failed for some reason but device is still connected
            //otherwise device list would have changed
        }


    }
}