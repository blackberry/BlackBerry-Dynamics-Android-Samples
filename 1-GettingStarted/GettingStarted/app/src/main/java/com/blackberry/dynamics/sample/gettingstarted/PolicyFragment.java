/* Copyright (c) 2021 BlackBerry Limited.
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

package com.blackberry.dynamics.sample.gettingstarted;

import android.content.Context;
import android.content.RestrictionsManager;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.util.Date;

public class PolicyFragment extends Fragment {

    Boolean[] checkValues = new Boolean[4];
    String updateTime = "";
    String policyString = "";
    Boolean viewCreated = false;
    private static PolicyFragment instance = null;

    TextView policyUpdated;
    TextView policyTV;
    CheckBox checkBoxPersonnel;
    CheckBox checkBoxPress;
    CheckBox checkBoxProjects;
    CheckBox checkBoxSales;

    public PolicyFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to retrieve an instance of this fragment.
     * A new instance is created if one does not already exist.
     * @return An instance of fragment PolicyFragment.
     */
    public static PolicyFragment newInstance()
    {
        if (instance == null) {
            instance = new PolicyFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_policy, container, false);

        policyUpdated = view.findViewById(R.id.policyTime);
        policyTV = view.findViewById(R.id.policyContents);
        checkBoxPersonnel = view.findViewById(R.id.checkPersonnel);
        checkBoxPress = view.findViewById(R.id.checkPress);
        checkBoxProjects = view.findViewById(R.id.checkProjects);
        checkBoxSales = view.findViewById(R.id.checkSales);

        checkValues[0] = false;
        checkValues[1] = false;
        checkValues[2] = false;
        checkValues[3] = false;
        viewCreated = true;

//        updatePolicy();

        return view;
    }

    public void updatePolicy()
    {
        RestrictionsManager restrictionsMgr = (RestrictionsManager)
                getActivity().getSystemService(Context.RESTRICTIONS_SERVICE);

        Bundle appRestrictions = restrictionsMgr.getApplicationRestrictions();

        Log.d("Policy", appRestrictions.toString());
        policyString = appRestrictions.toString();

        if (appRestrictions.containsKey("Pers")) {
            checkValues[0] = appRestrictions.getBoolean("Pers");
        }
        if (appRestrictions.containsKey("Press")) {
            checkValues[1] = appRestrictions.getBoolean("Press");
        }
        if (appRestrictions.containsKey("Proj")) {
            checkValues[2] = appRestrictions.getBoolean("Proj");
        }
        if (appRestrictions.containsKey("Sales")) {
            checkValues[3] = appRestrictions.getBoolean("Sales");
        }

        DateFormat df = DateFormat.getDateInstance();
        updateTime = "Policy last updated: " + df.format(new Date());

        refreshUI();
    }

    private void refreshUI()
    {
        if (viewCreated) {
            policyTV.setText(policyString);
            policyUpdated.setText(updateTime);

            // jumpDrawablesToCurrentState necessary to circumvent visual bugs with CheckBoxes
            checkBoxPersonnel.setChecked(checkValues[0]);
            checkBoxPersonnel.jumpDrawablesToCurrentState();
            checkBoxPress.setChecked(checkValues[1]);
            checkBoxPress.jumpDrawablesToCurrentState();
            checkBoxProjects.setChecked(checkValues[2]);
            checkBoxProjects.jumpDrawablesToCurrentState();
            checkBoxSales.setChecked(checkValues[3]);
            checkBoxSales.jumpDrawablesToCurrentState();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        refreshUI();
    }
}
