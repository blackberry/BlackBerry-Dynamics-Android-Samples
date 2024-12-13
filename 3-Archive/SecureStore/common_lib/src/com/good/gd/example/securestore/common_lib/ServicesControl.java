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

package com.good.gd.example.securestore.common_lib;

import com.good.gd.icc.GDService;
import com.good.gd.icc.GDServiceClient;
import com.good.gd.icc.GDServiceClientListener;
import com.good.gd.icc.GDServiceError;
import com.good.gd.icc.GDServiceErrorCode;
import com.good.gd.icc.GDServiceException;
import com.good.gd.icc.GDServiceListener;

import static com.good.gd.example.securestore.common_lib.utils.AppLogUtils.DEBUG_LOG;

/**
 * Singleton used to control interaction with GDServices APIs
 */
public class ServicesControl implements GDServiceListener, GDServiceClientListener {

    private static ServicesControl _instance;

    private String mCurrentRequestID;

    public static ServicesControl createInstance() {

        if (_instance == null) {
            _instance = new ServicesControl();
        }

        return _instance;

    }

    public static ServicesControl getInstance() {

        return _instance;

    }

    private ServicesControl() {

        try {
            GDService.setServiceListener(this);
            GDServiceClient.setServiceClientListener(this);
        } catch (GDServiceException e) {
            e.printStackTrace();
        }
    }

    /*
    We store the request ID of any Request which has been accepted by the Good Inter-Container Communication (ICC)
    system. We know that one of two callbacks will be received, either -

    onMessageSent - Denotes that the ICC message has been successfully received by the remote side
    onnReceiveMessage - Denotes that the ICC message has encountered an error at some stage in transmission to remote side
     */
    public void setServicesRequestID(String aRequestID){

        mCurrentRequestID = aRequestID;

    }

    public boolean isServicesSystemBusy() {

        boolean busy = false;

        if(mCurrentRequestID!=null){
            busy = true;
        }

        DEBUG_LOG("Services subsystem Busy = " + busy);

        return busy;
    }

    @Override
    public void onReceivingAttachments(String application, int numberOfAttachments, String requestID) {

    }

    @Override
    public void onReceivingAttachmentFile(String application, String path, long size, String requestID) {

    }

    //GDService Listener
    @Override
    public void onReceiveMessage(String application, String service, String version, String method, Object params, String[] attachments, String requestID) {

        //GDService messages for file transfer service are handled by FileTrasferControl
        if(FileTransferControl.SERVICENAME.equals(service))
        {
            FileTransferControl.getInstance().fileReceived(attachments[0]);
        } else if(SecureStoreTransferService.SERVICENAME.equals(service)){
            FileTransferControl.getInstance().numberFilesReceived(params);
        }

    }

    //GDServiceClient listener
    @Override
    public void onMessageSent(String application, String requestID, String[] attachments) {

        if(mCurrentRequestID.equals(requestID)) {

            DEBUG_LOG("Message Received Success ID = " + requestID);

            mCurrentRequestID = null;

            // Called when the message we requested to be sent has been sent
            FileTransferControl.getInstance().fileSentSuccessNotification();
        } else {
            DEBUG_LOG("Error requestID does not match returned = " + requestID + " current = " + mCurrentRequestID);
        }

    }

    @Override
    public void onReceiveMessage(String application, Object params, String[] attachments, String requestID) {

        DEBUG_LOG("onReceiveMessage requestID =" + requestID + " params = " + params.toString());

        // Called when a message has been received in response to a message we have sent

        if(mCurrentRequestID.equals(requestID)){

            if(params instanceof GDServiceError){
                //GDServiceError has a GDServiceErrorCode, and may also hold a Message, CustomServiceError, and a Details object
                GDServiceError serviceError = (GDServiceError)params;

                String errorMessage =null;

                //Optional Error message
                String error = serviceError.getMessage();
                if(error != null){
                    errorMessage += "\nGDServiceError: " + error;
                }

                //Optional Details object
                Object errorDetails = serviceError.getDetails();
                if(errorDetails != null){
                    errorMessage += "\nGDServiceError Details: " + errorDetails.toString();
                }

                //Service Error code
                GDServiceErrorCode serviceErrorCode = serviceError.getErrorCode();

                //Handle custom service errors
                if(serviceErrorCode == GDServiceErrorCode.GDServicesErrorCustom){
                    int customErrorCode = serviceError.getCustomErrorCode();

                    //These should match the custom error codes that can be returned by the server - use an enum
                    switch (customErrorCode)
                    {
                        //Name not found error
                        case 1:
                        {
                            errorMessage += "\nGDServiceError - Custom Service Error: Name Not Found";
                        }
                        break;

                        default:{
                            errorMessage += "\nGDServiceError - Custom Error Code: " + serviceError.getErrorCode();
                        }
                    }
                }
                else{
                    //Handle non-custom GDServiceErrors
                    switch (serviceErrorCode)
                    {
                        case GDServicesErrorGeneral : {
                            errorMessage += "\nGDServiceError: General Error";
                        }
                        break;

                        case GDServicesErrorApplicationNotFound : {
                            errorMessage += "\nGDServiceError: Application Not Found";
                        }
                        break;

                        case GDServicesErrorServiceNotFound : {
                            errorMessage += "\nGDServiceError: Service Not Found";
                        }
                        break;

                        case GDServicesErrorServiceVersionNotFound : {
                            errorMessage += "\nGDServiceError: Service Version Not Found";
                        }
                        break;

                        case GDServicesErrorMethodNotFound : {
                            errorMessage += "\nGDServiceError: Method Not Found";
                        }
                        break;

                        case GDServicesErrorNotAllowed : {
                            errorMessage += "\nGDServiceError: Not Allowed";
                        }
                        break;

                        case GDServicesErrorInvalidParams : {
                            errorMessage += "\nGDServiceError: Invalid Parameters";
                        }
                        break;

                        case GDServicesErrorInUse : {
                            errorMessage += "\nGDServiceError: In Use";
                        }
                        break;

                        default:{
                            errorMessage += "\nGDServiceError: Unhandled Error";
                        }
                        break;
                    }
                }

                FileTransferControl.getInstance().fileSentErrorNotification(serviceError.getErrorCode().name(), errorMessage);

                mCurrentRequestID = null;
            }

        }

    }

}
