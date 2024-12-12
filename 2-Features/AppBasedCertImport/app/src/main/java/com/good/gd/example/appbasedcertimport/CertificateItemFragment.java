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

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.good.gd.pki.Certificate;


public class CertificateItemFragment extends Fragment {
    public static final String TAG = CertificateItemFragment.class.getName();

    private Certificate mCertificate;

    private TextView mSubjectName;
    private TextView mSubjectAlternativeName;
    private TextView mIssuer;
    private TextView mKeyUsage;
    private TextView mExtendedKeyUsage;
    private TextView mNotBeforeDate;
    private TextView mNotAfterDate;
    private TextView mSerialNumber;
    private TextView mVersion;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_certificate_item, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        mSubjectName = view.findViewById(R.id.tv_subject_name);
        mSubjectAlternativeName = view.findViewById(R.id.tv_subject_alternative_name);
        mIssuer = view.findViewById(R.id.tv_issuer);
        mKeyUsage = view.findViewById(R.id.tv_key_usage);
        mExtendedKeyUsage = view.findViewById(R.id.tv_extended_key_usage);
        mNotBeforeDate = view.findViewById(R.id.tv_not_before_date);
        mNotAfterDate = view.findViewById(R.id.tv_not_after_date);
        mSerialNumber = view.findViewById(R.id.tv_serial_number);
        mVersion = view.findViewById(R.id.tv_version);
        setData();
    }

    public void setData(Certificate certificate) {
        mCertificate = certificate;
    }

    private void setData() {
        mSubjectName.setText(mCertificate.getSubjectName());
        mSubjectAlternativeName.setText(mCertificate.getSubjectAlternativeName());
        mIssuer.setText(mCertificate.getIssuer());
        mKeyUsage.setText(mCertificate.getKeyUsage());
        mExtendedKeyUsage.setText(mCertificate.getExtendedKeyUsage());
        mNotBeforeDate.setText(mCertificate.getNotBeforeDate().toString());
        mNotAfterDate.setText(mCertificate.getNotAfterDate().toString());
        mSerialNumber.setText(mCertificate.getSerialNumber());
        mVersion.setText(String.valueOf(mCertificate.getVersion()));
    }
}
