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
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.good.gd.pki.Certificate;
import com.good.gd.pki.CertificateHandler;
import com.good.gd.pki.CertificateListener;
import com.good.gd.pki.Credential;
import com.good.gd.pki.CredentialsProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CertificatesListFragment extends Fragment implements CertificateListener {

    public static final String TAG = CertificatesListFragment.class.getName();

    private List<Certificate> mCertificatesList;
    private RecyclerView mRecyclerView;
    private CertificatesListRecyclerViewAdapter mAdapter;
    private UCPListener mUCPListener;
    private String userCredentialsProfileId;

    public CertificatesListFragment() {
        mCertificatesList = new ArrayList<>();
        mAdapter = new CertificatesListRecyclerViewAdapter(mCertificatesList);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_certificateslist_list, container, false);

        Context context = view.getContext();
        mRecyclerView = view.findViewById(R.id.certificates_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button benImportToUPC = getView().findViewById(R.id.btn_import_to_ucp);
        benImportToUPC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUCPListener.onImportToUCP();
            }
        });
        Button undoImportButton = getView().findViewById(R.id.btn_undo_import);
        undoImportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUCPListener.onUndoImport();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Subscribe to the events about adding or removing the certificate.
        CertificateHandler.getInstance().addCertificateListener(this);
        if (mCertificatesList.size() > 0) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Remove the subscription for the events about adding or removing the certificate.
        CertificateHandler.getInstance().removeCertificateListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setListener(UCPListener UCPListener) {
        mUCPListener = UCPListener;
    }

    @Override
    public void onCertificatedAdded(final Certificate certificate) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCertificatesList.add(certificate);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onCertificateRemoved(Certificate certificate) {
        for (Certificate item:mCertificatesList) {
            if (Arrays.equals(item.getBinaryX509DER(), certificate.getBinaryX509DER())) {
                mCertificatesList.remove(item);
                break;
            }
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    public class CertificatesListRecyclerViewAdapter extends RecyclerView.Adapter<CertificatesListRecyclerViewAdapter.ViewHolder> {

        private final List<Certificate> mItems;

        public CertificatesListRecyclerViewAdapter(List<Certificate> items) {
            mItems = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.certificate_list_item, parent, false);
            TextView certItemView = v.findViewById(R.id.tv_cert_item);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int itemPosition = mRecyclerView.getChildLayoutPosition(v);
                    Certificate certificate = mCertificatesList.get(itemPosition);
                    mUCPListener.onCertificateSelected(certificate);
                }
            });
            ViewHolder viewHolder = new ViewHolder(v, certItemView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mItems.get(position);
            holder.mCertificateNameView.setText(mItems.get(position).getSubjectName());
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mCertificateNameView;
            public Certificate mItem;

            public ViewHolder(View view, TextView certificateNameView) {
                super(view);
                mView = view;
                mCertificateNameView = certificateNameView;
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mCertificateNameView.getText() + "'";
            }
        }
    }

    public void refreshData(CredentialsProfile credentialsProfile) {
        List<Credential> items = credentialsProfile.getCredentials();
        userCredentialsProfileId = credentialsProfile.getId();
        mCertificatesList.clear();
        for (Credential item:items) {
            mCertificatesList.add(item.getUserCertificate());
        }
        mAdapter.notifyDataSetChanged();
    }

    public String getUserCredentialsProfileId() {
        return userCredentialsProfileId;
    }

    public interface UCPListener {
        void onCertificateSelected(Certificate certificate);
        void onImportToUCP();
        void onUndoImport();
    }
}
