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

package com.good.gd.example.appbasedcertimport;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.good.gd.pki.CredentialsProfile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

public class ProfilesFragment extends Fragment {
    public static final String TAG = ProfilesFragment.class.getName();

    private ProfilesListener mProfilesListener;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<CredentialsProfile> credentialsProfiles;
    private List<String> idsListToMark = new Vector<>();
    private boolean isImportNow = true;
    private Button importCertButton;

    public ProfilesFragment() {
        credentialsProfiles = new ArrayList<>();
        mAdapter = new ProfilesAdapter(credentialsProfiles);
    }

    public void setProfilesListener(ProfilesListener profilesListener) {
        mProfilesListener = profilesListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_proffiles, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView = getView().findViewById(R.id.profiles_list);
        mLayoutManager = new LinearLayoutManager(getView().getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        importCertButton = getActivity().findViewById(R.id.btn_cert_import);
        importCertButton.setEnabled(credentialsProfiles.size()>0);

        final CheckBox cbImportNow = getActivity().findViewById(R.id.cb_importNaw);
        final Button finishImportButton = getActivity().findViewById(R.id.btn_finish_import);
        cbImportNow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isImportNow = isChecked;
                finishImportButton.setEnabled(!isChecked);
            }
        });
        finishImportButton.setEnabled(!cbImportNow.isChecked());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (credentialsProfiles.size() > 0) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public boolean isImportNow() {
        return isImportNow;
    }

    public void refreshData(List<CredentialsProfile> data) {
        credentialsProfiles.clear();
        credentialsProfiles.addAll(data);
        mAdapter.notifyDataSetChanged();
        importCertButton.setEnabled(credentialsProfiles.size()>0);
    }

    public void markImportRequired(String ucp_id) {
        idsListToMark.add(ucp_id);
    }

    public void unmarkImportRequired(String ucp_id) {
        idsListToMark.remove(ucp_id);
    }

    public void resetImportRequiredMarkers() {
        idsListToMark.clear();
        mAdapter.notifyDataSetChanged();
    }

    public class ProfilesAdapter extends RecyclerView.Adapter<ProfilesAdapter.ViewHolder> {
        private Collection<CredentialsProfile> mDataset;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public View mView;
            public TextView mHeader;
            public TextView mSettings;
            public TextView mType;
            public TextView mStatus;
            public TextView mRequiredStatus;

            public ViewHolder(View itemView, TextView mHeader, TextView mSettings, TextView type,
                              TextView mStatus, TextView requiredStatus) {
                super(itemView);
                this.mView = itemView;
                this.mHeader = mHeader;
                this.mSettings = mSettings;
                this.mType = type;
                this.mStatus = mStatus;
                this.mRequiredStatus = requiredStatus;
            }
        }

        public ProfilesAdapter(Collection<CredentialsProfile> credentials) {
            mDataset = credentials;
        }

        @Override
        public ProfilesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.profile_item_view, parent, false);
            TextView header = v.findViewById(R.id.tv_profile_header);
            TextView settings = v.findViewById(R.id.tv_profile_settings);
            TextView type = v.findViewById(R.id.tv_profile_type);
            TextView status = v.findViewById(R.id.tv_profile_status);
            TextView requiredStatus = v.findViewById(R.id.tv_profile_required_status);
            ViewHolder vh = new ViewHolder(v, header, settings, type, status, requiredStatus);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int itemPosition = mRecyclerView.getChildLayoutPosition(v);
                    CredentialsProfile item = (CredentialsProfile) mDataset.toArray()[itemPosition];
                    mProfilesListener.onProfileSelected(item);
                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Object[] data = mDataset.toArray();
            CredentialsProfile item = (CredentialsProfile) data[position];
            holder.mHeader.setText(item.getName());
            holder.mSettings.setText(item.getProviderSettings());
            holder.mType.setText(item.getTypeEnum().name());
            holder.mStatus.setText(item.getState().name());
            holder.mRequiredStatus.setText(item.getRequired().toString());
            if (idsListToMark.contains(item.getId())) {
                holder.itemView.setBackgroundColor(Color.LTGRAY);
            }
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    public interface ProfilesListener {
        void onProfileSelected(CredentialsProfile credentialsProfile);
    }
}
