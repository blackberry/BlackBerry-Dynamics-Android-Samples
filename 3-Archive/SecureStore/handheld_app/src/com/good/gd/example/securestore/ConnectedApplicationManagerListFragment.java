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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.ListFragment;

import com.good.gd.example.securestore.common_lib.ConnectedApplicationControl;
import com.good.gd.example.securestore.common_lib.ConnectedApplicationListener;
import com.good.gd.example.securestore.common_lib.ConnectedApplicationState;

import java.util.ArrayList;
import java.util.List;

public class ConnectedApplicationManagerListFragment extends ListFragment implements ConnectedApplicationListener {

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

    @Override
    public void onConnectedApplicationStateChanged(ConnectedApplicationState aState) {
        populateListView();
    }


    @Override
    public void onStart() {
        super.onStart();

        populateListView();
    }

    private void populateListView() {

        ConnectedApplicationState state = ConnectedApplicationControl.getInstance().getCurrentState();

        List<ConnectedApp> appsList = new ArrayList<>();


        if (state.isAppConnected()) {

            for (String name : state.getConnectedApps()) {
                ConnectedApp app = new ConnectedApp();
                app.mDisplayName = name;
                app.mCanRemove = true;
                app.mConnected = true;
                app.mCanActivate = false;
                appsList.add(app);
            }
        }

        if (state.isActivatedNotConnected()) {

            for (String name : state.getActivatedNotConnectedApps()) {
                ConnectedApp app = new ConnectedApp();
                app.mDisplayName = name;
                app.mCanRemove = true;
                app.mConnected = false;
                app.mCanActivate = false;
                appsList.add(app);
            }
        }

        if (state.isAppPendingActivation()) {

            for (String name : state.getAppsToActivate()) {
                ConnectedApp app = new ConnectedApp();
                app.mDisplayName = name;
                app.mCanRemove = false;
                app.mConnected = true;
                app.mCanActivate = true;
                appsList.add(app);
            }
        }

        if (state.isStateRemoved()) {
            for (String name : state.getRemovedApps()) {
                ConnectedApp app = new ConnectedApp();
                app.mDisplayName = name;
                app.mCanRemove = false;
                app.mConnected = false;
                app.mCanActivate = false;
                appsList.add(app);
            }
        }

        ConnectedAppsListAdapter adapter = new ConnectedAppsListAdapter(
                getActivity());
        adapter.setListContent(appsList);
        setListAdapter(adapter);
        setSelection(0);
    }


    class ConnectedApp {

        public String mDisplayName;
        public boolean mCanActivate;
        public boolean mCanRemove;
        public boolean mConnected;
    }

    class ActivateButtonListener implements View.OnClickListener {

        final String mDisplayName;
        final Context mContext;

        public ActivateButtonListener(Context aActivityContext, String aDisplayName) {

            mDisplayName = aDisplayName;
            mContext = aActivityContext;
        }

        @Override
        public void onClick(View view) {

            if (mDisplayName != null) {
                ConnectedApplicationControl.getInstance().startConnectedApplicationActivation(mContext, mDisplayName);
            }
        }
    }

    class RemoveButtonListener implements View.OnClickListener {

        final String mDisplayName;

        public RemoveButtonListener(String aDisplayName) {

            mDisplayName = aDisplayName;
        }

        @Override
        public void onClick(View view) {

            if (mDisplayName != null) {
                ConnectedApplicationControl.getInstance().removeConnectedApplication(mDisplayName);
            }
        }
    }

    class ConnectedAppsListAdapter extends BaseAdapter {

        private final Context mContext;

        private List<ConnectedApp> mItems = new ArrayList<>();

        ConnectedAppsListAdapter(Context context) {
            mContext = context;
        }

        void setListContent(List<ConnectedApp> aApps) {
            mItems = aApps;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int i) {
            return mItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View item_view = convertView;

            if (item_view == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                item_view = inflater.inflate(R.layout.connected_application_list_item, parent, false);
            }

            ConnectedApp a = mItems.get(position);

            ((TextView) item_view.findViewById(R.id.connected_application_name)).setText(a.mDisplayName);

            //Update activate & remove buttons to show correct user options
            Button activate_button = item_view.findViewById(R.id.button_activate);

            if (a.mCanActivate) {

                activate_button.setVisibility(View.VISIBLE);

                activate_button.setOnClickListener(new ActivateButtonListener(getActivity(), a.mDisplayName));
            } else {

                activate_button.setVisibility(View.GONE);
            }

            Button remove_button = item_view.findViewById(R.id.button_remove);

            if (a.mCanRemove) {

                remove_button.setVisibility(View.VISIBLE);

                remove_button.setOnClickListener(new RemoveButtonListener(a.mDisplayName));

            } else {

                remove_button.setVisibility(View.GONE);
            }

            // Update state based on application flags
            TextView state = item_view.findViewById(R.id.connected_application_state);

            if (!a.mCanActivate && !a.mCanRemove && !a.mConnected) {
                state.setText(R.string.removed);
            } else if (a.mCanActivate) {
                state.setText(R.string.not_activated);
            } else {
                if (a.mConnected) {
                    state.setText(R.string.connected);
                } else {
                    state.setText(R.string.not_connected);
                }
            }
            return item_view;
        }
    }
}
