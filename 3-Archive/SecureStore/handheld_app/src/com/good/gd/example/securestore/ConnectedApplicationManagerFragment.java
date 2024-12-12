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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.good.gd.example.securestore.common_lib.ConnectedApplicationControl;
import com.good.gd.example.securestore.common_lib.ConnectedApplicationListener;
import com.good.gd.example.securestore.common_lib.ConnectedApplicationState;

import static com.good.gd.example.securestore.common_lib.utils.AppLogUtils.DEBUG_LOG;

public class ConnectedApplicationManagerFragment extends Fragment implements ConnectedApplicationListener {

    @Override
    public void onPause() {
        super.onPause();

        ConnectedApplicationControl.getInstance().removeConnectedAppStateListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        ConnectedApplicationControl.getInstance().addConnectedAppStateListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.connected_application_management, container, false);

        Button b = v.findViewById(R.id.remove_button);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeApp();
            }
        });

        return v;
    }

    @Override
    public void onConnectedApplicationStateChanged(ConnectedApplicationState aState) {
    }

    private void removeApp() {

        ConnectedApplicationState state = ConnectedApplicationControl.getInstance().getCurrentState();

        String name = null;

        if (state.isAppConnected()) {

            name = state.getConnectedApps().iterator().next();

        } else if(state.isActivatedNotConnected()) {

            name = state.getActivatedNotConnectedApps().iterator().next();
        }

        DEBUG_LOG("ConnectedApplicationManagerFragment remove Device Name =" + name);

        if (name != null) {
            ConnectedApplicationControl.getInstance().removeConnectedApplication(name);
        }
    }
}
