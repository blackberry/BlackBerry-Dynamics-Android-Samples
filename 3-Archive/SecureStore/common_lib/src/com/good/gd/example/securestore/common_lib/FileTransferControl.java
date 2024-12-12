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

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.good.gd.icc.GDICCForegroundOptions;
import com.good.gd.icc.GDServiceClient;
import com.good.gd.icc.GDServiceErrorCode;
import com.good.gd.icc.GDServiceException;

import java.util.HashSet;
import java.util.Set;

import static com.good.gd.example.securestore.common_lib.utils.AppLogUtils.DEBUG_LOG;
import static com.good.gd.example.securestore.common_lib.utils.AppLogUtils.ERROR_LOG;

/**
 * Singleton used to control File Transfer
 */
public class FileTransferControl {

    private static FileTransferControl _instance;

    private Set<FileTransferListener> mFileTransferListeners;

    private Handler mStateHandler;

    private static final int MSG_SEND_SUCCESS = 1;
    private static final int MSG_SEND_FAILURE = 2;
    private static final int MSG_NUMBER_FILES_RECEIVED = 3;

    public static FileTransferControl createInstance(Looper looper) {

        if (_instance == null) {
            _instance = new FileTransferControl(looper);
        }

        return _instance;
    }

    public static FileTransferControl getInstance() {

        return _instance;

    }

    private FileTransferControl(Looper looper) {

        mFileTransferListeners = new HashSet<FileTransferListener>();

        mStateHandler = new StateHandler(looper);
    }


    public void addFileTransferListener(FileTransferListener aListener){

        mFileTransferListeners.add(aListener);
    }

    public void removeFileTransferListener(FileTransferListener aListener){

        mFileTransferListeners.remove(aListener);
    }

    public void fileReceived(String filename) {

        DEBUG_LOG("File received = " + filename);

        for(FileTransferListener listeners : mFileTransferListeners){

            listeners.fileReceived(filename);
        }

    }

    public void displayFileSentErrorNotification(String aErrorMessage ){
        // GDServiceClient.sendTo( ) failed for some reason, display the reason

        ERROR_LOG("Error Send Notification = " + aErrorMessage);

        for(FileTransferListener listeners : mFileTransferListeners){

            listeners.fileSentError(aErrorMessage);
        }

    }

    public void fileSentErrorNotification( String aErrorCode, String aErrorMessage ){

        String mess = "SEND ERROR: " + aErrorCode + " " + aErrorMessage;

        Message msg = Message.obtain(null, MSG_SEND_FAILURE, 0, 0, mess);
        mStateHandler.sendMessage(msg);

    }

    public void displayFileSentSuccessNotification() {

        DEBUG_LOG("File Send Success");

        for(FileTransferListener listeners : mFileTransferListeners){

            listeners.fileSentSuccess();
        }

    }

    public void fileSentSuccessNotification() {

        Message msg = Message.obtain(null, MSG_SEND_SUCCESS, 0, 0, null);
        mStateHandler.sendMessage(msg);

    }

    public void sendFile(String aFilePath, String aDeviceName) {

        DEBUG_LOG("Send File to remote ConnectedApplication File = " + aFilePath);

        // FilePath to file in Secure Container is passed. First get the connected device address
        String remoteAddress = ConnectedApplicationControl.getInstance().getConnectedApplicationAddress(aDeviceName);

        //Now Send actual file using Device Address & GDServices APIs
        String[] filesToSend = new String[] {aFilePath};

        try {
            String requestID = GDServiceClient.sendTo(remoteAddress, SERVICENAME, SERVICEVERSION, SERVICEMETHODNAME,
                    null, filesToSend, GDICCForegroundOptions.PreferMeInForeground);

            ServicesControl.getInstance().setServicesRequestID(requestID);

        } catch (GDServiceException e) {
            GDServiceErrorCode errorCode = e.errorCode();
            String errorMessage = e.getMessage();
            fileSentErrorNotification(errorCode.name(), errorMessage);
        }

    }


    public void sendNumberFiles(int aNumberFiles, String aDeviceName){

        DEBUG_LOG("Send Number Files to remote ConnectedApplication num = " + aNumberFiles);

        // First get the connected device address
        String remoteAddress = ConnectedApplicationControl.getInstance().getConnectedApplicationAddress(aDeviceName);

        SecureStoreTransferService s = new SecureStoreTransferService();

        s.setNumberFiles(aNumberFiles);

        Object params = s.toMap();

        // We define and use our own private Service to send Number of Files to Connected Application
        try {
            String requestID = GDServiceClient.sendTo(remoteAddress, SecureStoreTransferService.SERVICENAME, SecureStoreTransferService.VERSION, SecureStoreTransferService.SERVICE_NUM_FILES_METHOD,
                    params, null, GDICCForegroundOptions.PreferPeerInForeground);

            ServicesControl.getInstance().setServicesRequestID(requestID);

        } catch (GDServiceException e) {
            GDServiceErrorCode errorCode = e.errorCode();
            String errorMessage = e.getMessage();
            fileSentErrorNotification(errorCode.name(), errorMessage);
        }

    }

    public void numberFilesReceived(Object params) {

        SecureStoreTransferService s = new SecureStoreTransferService();

        s.setMap(params);

        int numberFiles = s.getNumberFiles();

        DEBUG_LOG("Number of Files Recieved from connected application  = " + numberFiles);

        Message msg = Message.obtain(null, MSG_NUMBER_FILES_RECEIVED, 0, 0, numberFiles);
        mStateHandler.sendMessage(msg);

    }

    public void displayNumberFilesNotification(int aNumberFiles ){

        for(FileTransferListener listeners : mFileTransferListeners){

            listeners.numberFilesReceived(aNumberFiles);
        }

    }


    // Handles AppEvent messages which various portions of GDWear SDK can send
    class StateHandler extends Handler {
        public StateHandler(@NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == MSG_SEND_SUCCESS) {
                displayFileSentSuccessNotification();
            } else if(msg.what == MSG_SEND_FAILURE) {
                displayFileSentErrorNotification((String)msg.obj);
            } else if(msg.what == MSG_NUMBER_FILES_RECEIVED){
                displayNumberFilesNotification((Integer) msg.obj);
            } else
            {
                super.handleMessage(msg);
            }

        }
    }


    // We use the standard defined GD Transfer File Service to send file to connected application
    public final static String SERVICEVERSION = "1.0.0.0";
    public final static String SERVICENAME = "com.good.gdservice.transfer-file";
    public final static String SERVICEMETHODNAME = "transferFile";

}