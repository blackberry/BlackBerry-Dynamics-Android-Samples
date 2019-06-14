/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.test.screens;

import static com.good.automated.general.utils.Duration.WAIT_FOR_SCREEN;

import android.util.Log;

import com.good.automated.general.controls.Button;
import com.good.automated.general.controls.EditText;
import com.good.automated.general.controls.TextView;
import com.good.automated.general.controls.impl.ButtonImpl;
import com.good.automated.general.controls.impl.EditTextImpl;
import com.good.automated.general.controls.impl.TextViewImpl;
import com.good.automated.general.utils.Duration;

public class BBDCertificateImportUI extends AbstractBBDUI {

    private static final String SCREEN_ID = "bbd_pkcs_password_view_UI";
    private String packageName;
    private String password = "password"; //default password

    private BBDCertificateImportUIMap controls;
    private String TAG = BBDCertificateImportUI.class.getSimpleName();

    /**
     *
     * @param packageName app under test package name
     */
    public BBDCertificateImportUI(String packageName) {
        this.packageName = packageName;
        this.controls = new BBDCertificateImportUIMap();
    }

    public BBDCertificateImportUI(String packageName, long delay) {
        this.packageName = packageName;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDCertificateImportUIMap();
    }

    /**
     *
     * @param packageName app under test package name
     */
    public BBDCertificateImportUI(String packageName, String password) {
        this.packageName = packageName;
        this.password = password;
        this.controls = new BBDCertificateImportUIMap();
    }

    public BBDCertificateImportUI(String packageName, String password, long delay) {
        this.packageName = packageName;
        this.password = password;
        if (!getUiAutomationUtils().isResourceWithIDShown(packageName, getScreenID(), delay)){
            throw new RuntimeException("Needed screen was not shown within provided time!");
        }
        this.controls = new BBDCertificateImportUIMap();
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
            return controls.getTextMessage().getText();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return null;
        }
    }

    /**
     *
     * @return true if click on button OK was performed successfully, otherwise false
     */
    public boolean clickOK() {
        try {
            return controls.getBtnOK().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * @return true if click on button Cancel was performed successfully, otherwise false
     */
    public boolean clickCancel() {
        try {
            return controls.getBtnCancel().click();
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * @param certPassword password of certificate to be entered
     * @return true if password was entered successfully, otherwise false
     */
    public boolean enterPassword(String certPassword) {
        try {
            return controls.getCertPassword().legacySetText(certPassword);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * @param certPassword password of certificate to be entered
     * @return alert dialog
     */
    public BBDAlertDialogUI setPassword(String certPassword) {
        enterPassword(certPassword);
        clickOK();
        return new BBDAlertDialogUI();
    }

    @Override
    public boolean doAction() {
        return setPassword(password).click();
    }

    private class BBDCertificateImportUIMap {

        public Button getBtnCancel() {
            return ButtonImpl.getByID(packageName, "COM_GOOD_GD_PKCS_PASSSWORD_LATER_BUTTON",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public EditText getCertPassword() {
            return EditTextImpl.getByID(packageName,
                    "COM_GOOD_GD_PKCS_PASSSWORD_VIEW_PASSWORD_FIELD",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public Button getBtnOK() {
            return ButtonImpl.getByID(packageName, "COM_GOOD_GD_PKCS_PASSSWORD_ACCESS_BUTTON",
                    Duration.of(WAIT_FOR_SCREEN));
        }

        public TextView getTextMessage() {
            return TextViewImpl.getByID(packageName, "COM_GOOD_GD_PKCS_PASSSWORD_MESSAGE",
                    Duration.of(WAIT_FOR_SCREEN));
        }
    }
}
