/* Copyright (c) 2020 BlackBerry Limited
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

package blackberry.example.com.gettingstartedbd;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.fragment.app.Fragment;

import com.good.gd.GDAndroid;
import com.good.gd.error.GDNotAuthorizedError;
import com.good.gd.widget.GDTextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PolicyFragment#newInstance} factory method to
 * create/retrieve an instance of this fragment.
 */
public class PolicyFragment extends Fragment
{

    GDAndroid gdAndroid = GDAndroid.getInstance();
    String updateTime = "";
    String policyString = "";
    Map<String, Object> policyMap = new HashMap<>();
    Boolean[] checkValues = new Boolean[4];
    Boolean viewCreated = false;
    private static PolicyFragment instance = null;

    GDTextView policyUpdated;
    GDTextView policyTV;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_policy, container, false);

        // Initialize UI elements
        policyUpdated = view.findViewById(R.id.policyTime);
        policyTV = view.findViewById(R.id.policyContents);
        checkBoxPersonnel = view.findViewById(R.id.checkPersonnel);
        checkBoxPress = view.findViewById(R.id.checkPress);
        checkBoxProjects = view.findViewById(R.id.checkProjects);
        checkBoxSales = view.findViewById(R.id.checkSales);

        // Flag the view as created for refreshUI
        viewCreated = true;

        // Retrieve the latest policy
        updatePolicy();

        return view;
    }

    /**
     * Retrieves policy information from GDAndroid and sets values accordingly.
     * refreshUI is then called to reflect changes.
     */
    public void updatePolicy()
    {
        synchronized (this) {
            // Fetch the policy
            try {
                policyString = gdAndroid.getApplicationPolicyString();
                policyMap = gdAndroid.getApplicationPolicy();
            } catch (GDNotAuthorizedError e) {
                e.printStackTrace();
                policyString = "";
                policyMap = new HashMap<>();
            }
        }

        // Reset CheckBox values
        checkValues[0] = false;
        checkValues[1] = false;
        checkValues[2] = false;
        checkValues[3] = false;

        // Get display poicy for the Policy Map
        Map<String, Object> display = (Map<String, Object>) policyMap.get("display");

        // Iterate through the display policy for each CheckBox value
        if (display != null) {
            Vector<Object> tabs = (Vector<Object>) display.get("tabs");

            if (tabs != null) {
                for (Object tab : tabs) {
                    switch((String)tab) {
                        case "Pers":
                            checkValues[0] = true;
                            break;
                        case "Press":
                            checkValues[1] = true;
                            break;
                        case "Proj":
                            checkValues[2] = true;
                            break;
                        case "Sales":
                            checkValues[3] = true;
                            break;
                    }
                }
            } else {
                Log.e("Policy", "No tabs settings in policy");
            }
        } else {
            Log.e("Policy", "No display policy");
        }

        updateTime = "Policy last updated: " +
                DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);

        // Update UI elements with new values
        refreshUI();
    }

    /**
     * Updates the values of all UI elements if the view has been created.
     */
    private void refreshUI()
    {
        if (viewCreated) {
            policyUpdated.setText(updateTime);
            policyTV.setText(policyString);

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

    /**
     * Ensures UI elements are current when the fragment is put in view.
     */
    @Override
    public void onResume()
    {
        super.onResume();
        refreshUI();
    }
}
