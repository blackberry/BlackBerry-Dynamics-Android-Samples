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

package com.blackberry.dynamics.sample.gettingstarted

import android.content.Context
import android.content.RestrictionsManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.text.DateFormat
import java.util.*

class PolicyFragment : Fragment() {

    var checkValues: BooleanArray = BooleanArray(4)
    var updateTime: String = ""
    var policyString: String = ""
    var viewCreated: Boolean = false

    lateinit var policyUpdated: TextView
    lateinit var policyTV: TextView
    lateinit var checkBoxPersonnel: CheckBox
    lateinit var checkBoxPress: CheckBox
    lateinit var checkBoxProjects: CheckBox
    lateinit var checkBoxSales: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_policy, container, false)

        policyUpdated = view.findViewById(R.id.policyTime)
        policyTV = view.findViewById(R.id.policyContents)
        checkBoxPersonnel = view.findViewById(R.id.checkPersonnel)
        checkBoxPress = view.findViewById(R.id.checkPress)
        checkBoxProjects = view.findViewById(R.id.checkProjects)
        checkBoxSales = view.findViewById(R.id.checkSales)
        viewCreated = true

        updatePolicy()

        return view
    }

    fun updatePolicy() {
        val restrictionsMgr = activity?.
            getSystemService(Context.RESTRICTIONS_SERVICE) as RestrictionsManager

        val appRestrictions: Bundle = restrictionsMgr.applicationRestrictions

        Log.d("Policy", appRestrictions.toString())
        policyString = appRestrictions.toString()

        if (appRestrictions.containsKey("Pers")) {
            checkValues[0] = appRestrictions.getBoolean("Pers")
        }
        if (appRestrictions.containsKey("Press")) {
            checkValues[1] = appRestrictions.getBoolean("Press")
        }
        if (appRestrictions.containsKey("Proj")) {
            checkValues[2] = appRestrictions.getBoolean("Proj")
        }
        if (appRestrictions.containsKey("Sales")) {
            checkValues[3] = appRestrictions.getBoolean("Sales")
        }

        val df = DateFormat.getDateInstance()
        updateTime = "Policy last updated: " + df.format(Date())

        refreshUI()
    }

    private fun refreshUI() {
        if (viewCreated) {
            policyTV.text = policyString
            policyUpdated.text = updateTime

            // jumpDrawablesToCurrentState necessary to circumvent visual bugs with CheckBoxes
            checkBoxPersonnel.isChecked = checkValues[0]
            checkBoxPersonnel.jumpDrawablesToCurrentState()
            checkBoxPress.isChecked = checkValues[1]
            checkBoxPress.jumpDrawablesToCurrentState()
            checkBoxProjects.isChecked = checkValues[2]
            checkBoxProjects.jumpDrawablesToCurrentState()
            checkBoxSales.isChecked = checkValues[3]
            checkBoxSales.jumpDrawablesToCurrentState()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshUI()
    }

    companion object {
        /**
         * Use this factory method to retrieve an instance of this fragment.
         * A new instance is created if one does not already exist.
         * @return An instance of fragment PolicyFragment.
         */
        var instance: PolicyFragment? = null

        fun newInstance(): PolicyFragment {
            if (instance == null) {
                instance = PolicyFragment()
            }
            return instance!!
        }
    }
}