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

package com.good.gd.example.appkinetics;

import static com.good.gd.example.appkinetics.AppKineticsHelpers.BRIDGE_APP_PACKAGE_NAME;

import android.util.Log;

import com.good.gd.icc.GDICCForegroundOptions;
import com.good.gd.icc.GDService;
import com.good.gd.icc.GDServiceClientListener;
import com.good.gd.icc.GDServiceError;
import com.good.gd.icc.GDServiceErrorCode;
import com.good.gd.icc.GDServiceException;
import com.good.gd.icc.GDServiceListener;

/**
 * ICC service listener
 */
public class AppKineticsGDServiceListener implements
        GDServiceClientListener, GDServiceListener {

    // Static Variables -------------------------------------------------------
    private static AppKineticsGDServiceListener instance;

    // Static Methods ---------------------------------------------------------
    public static AppKineticsGDServiceListener getInstance() {
        if (instance == null) {
            synchronized (AppKineticsGDServiceListener.class) {
                instance = new AppKineticsGDServiceListener();
            }
        }
        return instance;
    }

    // Public Methods ---------------------------------------------------------

    // service client listener methods
    @Override
    public void onMessageSent(final String application,
                              final String requestId,
                              final String[] attachments) {
        Log.d(AppKineticsHelpers.LOGTAG, "AppKineticsGDServiceListener.onMessageSent");
    }

    @Override
    public void onReceivingAttachments(String application, int numberOfAttachments, String requestID) {
        Log.d(AppKineticsHelpers.LOGTAG, "AppKineticsGDServiceListener.onReceivingAttachments number of attachments: " + numberOfAttachments + " for requestID: " + requestID + "\n");
    }
    
    @Override
    public void onReceivingAttachmentFile(String application, String path, long size, String requestID) {
        Log.d(AppKineticsHelpers.LOGTAG, "AppKineticsGDServiceListener.onReceivingAttachmentFile attachment: " + path + " size: " + size + " for requestID: " + requestID + "\n");
    }

    // from serviceListener
    @Override
    public void onReceiveMessage(final String application,
                                 final String service,
                                 final String version,
                                 final String method,
                                 final Object params,
                                 final String[] attachments,
                                 final String requestId) {
        Log.d(AppKineticsHelpers.LOGTAG, "AppKineticsGDServiceListener.onReceiveMessage (from serviceListener)");

        GDServiceErrorCode serviceErrorCode = null;
        Integer internalErrorMsg = null;

        boolean isMessageReceivedFromBridgeApp = application.equals(BRIDGE_APP_PACKAGE_NAME);

        if (!service.equals(AppKineticsHelpers.SERVICENAME)) {
            serviceErrorCode = GDServiceErrorCode.GDServicesErrorServiceNotFound;
        } else if (!version.equals(AppKineticsHelpers.VERSION)) {
            serviceErrorCode = GDServiceErrorCode.GDServicesErrorServiceVersionNotFound;
        } else if (!method.equals(AppKineticsHelpers.SERVICEMETHODNAME)) {
            serviceErrorCode = GDServiceErrorCode.GDServicesErrorMethodNotFound;
        // BB Bridge sets params object. So if it's Bridge app we shouldn't consider this as an error.
        } else if (params != null && !isMessageReceivedFromBridgeApp) {
            serviceErrorCode = GDServiceErrorCode.GDServicesErrorInvalidParams;
        } else if (attachments.length != 1) {
            internalErrorMsg = R.string.error_wrong_attachments_no;
        }

        if (serviceErrorCode != null) {
            sendErrorResponse(new GDServiceError(serviceErrorCode), application, requestId);
        } else if (internalErrorMsg != null) {
            AppKineticsModel.getInstance().handleError(internalErrorMsg.intValue());
        } else {
            AppKineticsModel.getInstance().addFilesToPendingSaveList(attachments);
        }
    }

    // from clientListener
    @Override
    public void onReceiveMessage(final String application, final Object params,
                                 final String[] attachments, final String requestId) {
        Log.d(AppKineticsHelpers.LOGTAG,
                "AppKineticsGDServiceListener.onReceiveMessage (from clientListener)");

        if (params instanceof GDServiceError){
            AppKineticsModel.getInstance().handleError((GDServiceError)params);
        }
    }

    private void sendErrorResponse(GDServiceError serviceError, String application, String requestID) {
        try {
            GDService.replyTo(application, serviceError, GDICCForegroundOptions.PreferPeerInForeground, null, requestID);
        } catch (GDServiceException e) {
            Log.d(AppKineticsHelpers.LOGTAG, "- replyTo - Error: " + e.getMessage());
        }
    }
}