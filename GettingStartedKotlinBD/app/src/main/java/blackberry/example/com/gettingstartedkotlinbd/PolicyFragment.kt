/* Copyright (c) 2021 BlackBerry Limited
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

package blackberry.example.com.gettingstartedkotlinbd

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.good.gd.GDAndroid
import com.good.gd.error.GDNotAuthorizedError
import com.good.gd.widget.GDTextView
import java.text.DateFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 * Use the [PolicyFragment.newInstance] factory method to
 * create/retrieve an instance of this fragment.
 */
class PolicyFragment : Fragment() {

    val gdAndroid: GDAndroid = GDAndroid.getInstance()
    var updateTime: String = ""
    var policyString: String = ""
    var policyMap: Map<String, Any> = HashMap()
    var checkValues: BooleanArray = BooleanArray(4)
    var viewCreated: Boolean = false

    lateinit var policyUpdated: GDTextView
    lateinit var policyTV: GDTextView
    lateinit var checkBoxPersonnel: CheckBox
    lateinit var checkBoxPress: CheckBox
    lateinit var checkBoxProjects: CheckBox
    lateinit var checkBoxSales: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_policy, container, false)

        // Initialize UI elements
        policyUpdated = view.findViewById(R.id.policyTime)
        policyTV = view.findViewById(R.id.policyContents)
        checkBoxPersonnel = view.findViewById(R.id.checkPersonnel)
        checkBoxPress = view.findViewById(R.id.checkPress)
        checkBoxProjects = view.findViewById(R.id.checkProjects)
        checkBoxSales = view.findViewById(R.id.checkSales)

        // Flag the view as created for refreshUI
        viewCreated = true

        // Retrieve the latest policy
        updatePolicy()

        return view
    }

    /**
     * Retrieves policy information from GDAndroid and sets values accordingly.
     * refreshUI is then called to reflect changes.
     */
    fun updatePolicy() {
        synchronized(this) {
            // Fetch the policy
            try {
                policyString = gdAndroid.applicationPolicyString
                policyMap = gdAndroid.applicationPolicy
                Log.d("Policy", policyString)
            } catch (e: GDNotAuthorizedError) {
                Log.e("Policy", "Not authorized policy")
                e.printStackTrace()
                policyString = ""
                policyMap = HashMap()
            }
        }

        // Reset CheckBox values
        checkValues[0] = false
        checkValues[1] = false
        checkValues[2] = false
        checkValues[3] = false

        // Get display policy from the Policy Map
        val display: Map<String, Any>? = policyMap["display"] as Map<String, Any>?

        // Iterate through the display policy for each CheckBox value
        if (display != null) {
            val tabs: Vector<Any>? = display["tabs"] as Vector<Any>?

            if (tabs != null) {
                for (tab: Any in tabs) {
                    when (tab as String) {
                        "Pers" -> checkValues[0] = true
                        "Press" -> checkValues[1] = true
                        "Proj" -> checkValues[2] = true
                        "Sales" -> checkValues[3] = true
                    }
                }
            } else {
                Log.e("Policy", "No tabs settings in policy")
            }
        } else {
            Log.e("Policy", "No display policy")
        }

        val df = DateFormat.getDateInstance()
        updateTime = "Policy last updated: " + df.format(Date())

        // Update UI elements with new values
        refreshUI()
    }

    /**
     * Updates the values of all UI elements if the view has been created.
     */
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

    /**
     * Ensures UI elements are current when the fragment is put in view.
     */
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