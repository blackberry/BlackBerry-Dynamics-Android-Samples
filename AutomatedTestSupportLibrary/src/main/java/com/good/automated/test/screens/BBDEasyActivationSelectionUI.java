/* Copyright (c) 2017 - 2020 BlackBerry Limited.
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

package com.good.automated.test.screens;

import static com.good.automated.general.utils.Duration.WAIT_FOR_SCREEN;

import android.util.Log;

import com.good.automated.general.controls.Component;
import com.good.automated.general.controls.ImageView;
import com.good.automated.general.controls.ListView;
import com.good.automated.general.controls.TextView;
import com.good.automated.general.controls.impl.ImageViewImpl;
import com.good.automated.general.controls.impl.ListViewImpl;
import com.good.automated.general.controls.impl.TextViewImpl;
import com.good.automated.general.utils.Duration;

import java.util.LinkedList;
import java.util.List;

public class BBDEasyActivationSelectionUI extends AbstractBBDUI {

    private static final String SCREEN_ID = "bbd_activation_delegate_view_UI";

    private static final String TAG = BBDEasyActivationSelectionUI.class.getSimpleName();
    private String appToSelectPackageName;
    private String packageName;
    private String appToSelect;
    private BBDEasyActivationSelectionUI.ActivationDelegationUIMap controls;

    /**
     * @param packageName app under test packageName
     */
    public BBDEasyActivationSelectionUI(String packageName) {
        this.packageName = packageName;
        this.controls = new BBDEasyActivationSelectionUI.ActivationDelegationUIMap();
    }

    public BBDEasyActivationSelectionUI(String packageName, long delay) {
        this.packageName = packageName;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDEasyActivationSelectionUI.ActivationDelegationUIMap();
    }

    /**
     * @param packageName            app under test packageName
     * @param appToSelectPackageName Easy Activator packageName
     * @param appToSelect            Easy Activator app Name
     */
    public BBDEasyActivationSelectionUI(String packageName, String appToSelectPackageName, String appToSelect) {
        this.packageName = packageName;
        this.appToSelect = appToSelect;
        this.appToSelectPackageName = appToSelectPackageName;
        this.controls = new BBDEasyActivationSelectionUI.ActivationDelegationUIMap();
    }

    /**
     *
     * @param packageName            app under test packageName
     * @param appToSelectPackageName Easy Activator packageName
     * @param appToSelect            Easy Activator app Name
     * @param delay                  Wait for screen
     */
    public BBDEasyActivationSelectionUI(String packageName, String appToSelectPackageName, String
            appToSelect, long delay) {
        this.packageName = packageName;
        this.appToSelect = appToSelect;
        this.appToSelectPackageName = appToSelectPackageName;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDEasyActivationSelectionUI.ActivationDelegationUIMap();
    }

    public static String getScreenID() {
        return SCREEN_ID;
    }

    /**
     *
     * @return title of the screen
     */
    public String getTitle() {
        try {
            return controls.getActivationHelp().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    /**
     *
     * @return true if click on button Setup Using Access Key was performed successfully, otherwise
     * false
     */
    public boolean clickSetupUsingAccessKey() {
        try {
            return controls.getSetupUsingAccessKey().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * @param appName app name to be checked
     * @return true if specified app name is available for Easy Activation, otherwise false
     */
    public boolean checkAppExists(String appName) {
        try {
            return controls.getActivationDelegationList().checkAppExists(appName);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * @param appName app name to be selected
     * @return true if app was selected for Easy Activation, otherwise false
     */
    public boolean selectApp(String appName) {
        try {
            return controls.getActivationDelegationList().selectApp(appName);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * @return true if click on button Learn More was performed successfully, otherwise false
     */
    public boolean clickOnLearnMore() {
        try {
            return controls.getLearnMore().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * @return true is all elements are shown on the screen, otherwise false
     */
    public boolean isShown() {
        return controls.getActivationHelp() != null &&
                controls.getActivationDelegationList() != null &&
                controls.getSetupUsingAccessKey() != null &&
                controls.getLearnMore() != null;
    }

    /**
     *
     * @param label label to be checked
     * @return true if labels match, otherwise false
     */
    public boolean checkDebugLabel(String label) {
        try {
            return controls.getGdSimulationLabel().getText().equals(label);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * @return list of apps available for Easy Activation
     */
    public List<TextView> getAppList() {
        if (controls.getActivationDelegationList() != null) {
            return controls.getActivationDelegationList().getListOfActivationDelegationElements();
        }
        return null;
    }

    public String getListOfAvailableActivators() {
        List<TextView> appList = getAppList();
        if (appList != null && appList.size() > 0) {
            StringBuilder res = new StringBuilder(100);
            for (TextView tView : appList) {
                res.append(tView.getText()).append(" | ");
            }
            Log.d(TAG, "List of available activators: " + res.toString());
            return res.toString();
        }
        return null;
    }

    @Override
    public boolean doAction() {
        if (appToSelectPackageName != null) {
            Log.d(TAG, "Try to select app: " + appToSelect);
            return selectApp(appToSelect);
        }
        Log.d(TAG, "Try to click \"Setup Using Access Key\" button");
        return clickSetupUsingAccessKey();
    }

    private class ActivationDelegationUIMap {

        public TextView getActivationHelp() {
            return TextViewImpl.getByID(packageName, "gd_easy_activation_help",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getSetupUsingAccessKey() {
            return TextViewImpl.getByID(packageName, "activationKeyLink",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public ImageView getLearnMore() {
            return ImageViewImpl.getByID(packageName, "gd_help",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getGdSimulationLabel() {
            return TextViewImpl.getByID(packageName, "gd_simulation_label",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public ActivationDelegationListUIMap getActivationDelegationList() {
            return new ActivationDelegationListUIMap("List");
        }

        private class ActivationDelegationListUIMap {
            private final ListView list;
            private List<TextView> listOfElements;

            public ActivationDelegationListUIMap(String list) {
                this.list = ListViewImpl.getByID(packageName, list);
                this.listOfElements = listOfActivationDelegationElements();
            }

            private List<TextView> listOfActivationDelegationElements() {
                listOfElements = new LinkedList<TextView>();
                int i = 0;
                while (i < list.getChildCount()) {
                    Component elem = new TextViewImpl(list.getChildElement(i));
                    if (elem.getClassName().equals("android.widget.TextView") && elem.getText().contains("can be set up using the password for one of the following applications or by using the Access Key provided by an Administrator")) {
                        //For case when was opened ActivationDelegation screen
                        Log.d(TAG, "Looking for EA elements. For case when was opened "
                                + "ActivationDelegation screen");
                    } else if (elem.getClassName().equals("android.widget.TextView") && elem.getText().contains("Application for")) {
                        //For case when screen was scrolled (a lot of elements to select)
                        listOfElements.add(new TextViewImpl(list.getChildElement(i)));
                        Log.d(TAG, "Looking for EA elements. For case when screen was scrolled (a lot of elements to select)");
                    } else {
                        //The list of Activation Delegation apps is a pretty long
                        listOfElements.add(new TextViewImpl(list.getChildElement(i).getChildElement(1).getChildElement(1)));
                        Log.d(TAG, "Looking for EA elements. The list of Activation Delegation apps is a pretty long");
                    }
                    i++;
                }
                return listOfElements;

            }

            public boolean checkAppExists(String appName) {
                return getRequiredApp(appName) != null;
            }

            public boolean selectApp(String appName) {
                try {
                    return getRequiredApp(appName).click();
                } catch (NullPointerException e) {
                    Log.d(TAG, "NullPointerException: " + e.getMessage());
                }
                return false;
            }

            private TextView getRequiredApp(String appName) {
                for (TextView element : listOfElements) {
                    try {
                        if (element.getText().equals(appName)) {
                            Log.d(TAG, "Easy Activator was found: " + element.getText());
                            return element;
                        }
                    } catch (NullPointerException e) {
                        Log.d(TAG, "NullPointerException: " + e.getMessage());
                    }
                }
                Log.d(TAG, "Easy Activator app wasn't found!");
                Log.d(TAG, "List of elements shown on Easy Activation Selection screen: " + getListOfAvailableActivators());
                return null;
            }

            public List<TextView> getListOfActivationDelegationElements() {
                return listOfElements;
            }
        }
    }
}
