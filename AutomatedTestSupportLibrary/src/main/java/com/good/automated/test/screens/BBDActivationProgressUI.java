/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.test.screens;

import static com.good.automated.general.utils.Duration.WAIT_FOR_SCREEN;

import android.util.Log;

import com.good.automated.general.controls.TextView;
import com.good.automated.general.controls.impl.TextViewImpl;
import com.good.automated.general.utils.Duration;

public class BBDActivationProgressUI extends AbstractBBDUI {

    private static final String SCREEN_ID = "bbde_provision_progress_view_UI";
    private String packageName;

    private BBDActivationProgressUIMap controls;
    private String TAG = BBDActivationProgressUI.class.getSimpleName();

    /**
     * @param packageName           app under test packageID
     * @param isUsedInProvisionFlow put true if class is used in provision flow,
     *                              for extended testing of this screen put false
     */
    public BBDActivationProgressUI(String packageName, boolean isUsedInProvisionFlow) {
        this.packageName = packageName;
        this.controls = new BBDActivationProgressUIMap();
    }

    public BBDActivationProgressUI(String packageName, boolean isUsedInProvisionFlow, long delay) {
        this.packageName = packageName;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)) {
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDActivationProgressUIMap();
    }

    public static String getScreenID() {
        return SCREEN_ID;
    }

    /**
     * @return true if click on Report A Problem button was successful, otherwise false
     */
    public boolean reportAProblem() {
        try {
            return controls.getReportAProblem().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     * @param label label to be checked
     * @return true if label matches expected one, otherwise false
     */
    public boolean checkSimulatedOrDebugLabel(String label) {
        try {
            return controls.getGdSimulationLabel().getText().equals(label);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean doAction() {
        return getUiAutomationUtils().waitUntilElementGoneFromUI(packageName, getScreenID(),
                Duration.of(Duration.PROVISIONING));
    }

    private class BBDActivationProgressUIMap {

        public TextView getInfrastructureActivation() {
            return TextViewImpl.getByText(packageName, "Infrastructure Activation",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getEnterpriseActivation() {
            return TextViewImpl.getByText(packageName, "Enterprise Activation",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getSecureDataTransferViaAppKinetics() {
            return TextViewImpl.getByText(packageName, "Secure Data Transfer via AppKinetics",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getSecuringCommunications() {
            return TextViewImpl.getByText(packageName, "Securing Communications",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getCommunicationsSecured() {
            return TextViewImpl.getByText(packageName, "Communications Secured",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getRetrievingPolicies() {
            return TextViewImpl.getByText(packageName, "Retrieving Policies",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getPoliciesRetrieved() {
            return TextViewImpl.getByText(packageName, "Policies Retrieved",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getReportAProblem() {
            return TextViewImpl.getByID(packageName, "gd_bottom_line_action_label",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getGdSimulationLabel() {
            return TextViewImpl.getByID(packageName, "gd_simulation_label",
                    Duration.of(WAIT_FOR_SCREEN));
        }
    }
}
