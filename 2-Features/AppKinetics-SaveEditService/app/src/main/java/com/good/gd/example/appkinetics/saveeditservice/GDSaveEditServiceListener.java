/* Copyright 2024 BlackBerry Ltd.
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

/**
 * GDSaveEditServiceListener.java
 */
package com.good.gd.example.appkinetics.saveeditservice;

import android.util.Log;

import com.good.gd.icc.GDServiceListener;

public class GDSaveEditServiceListener implements GDServiceListener {

    private static GDSaveEditServiceListener instance;

    private OnReceiveAttachmentsEventListener onReceiveAttachmentsEventListener;

    private String[] pendingAttachments;

    private String requestedApplication;

    public static GDSaveEditServiceListener getInstance() {
        if (instance == null) {
            synchronized (GDSaveEditServiceListener.class) {
                instance = new GDSaveEditServiceListener();
            }
        }
        return instance;
    }

    /**
     * Used to set new OnReceiveMessageEventListener for handling event,
     * when application received files for editing from SaveEdit Client application.
     *
     * @param listener OnReceiveMessageEventListener instance.
     */
    public void setOnReceiveAttachmentsEventListener(final OnReceiveAttachmentsEventListener listener) {
        this.onReceiveAttachmentsEventListener = listener;
    }

    /**
     * Removes listener if it was previously registered using .
     */
    public void clearListener() {
        this.onReceiveAttachmentsEventListener = null;
    }

    /**
     * Used to get pending attachments.
     *
     * @return String array with pending attachments.
     */
    public String[] getPendingAttachments() {
        return pendingAttachments;
    }

    public String getRequestedApplication() {
        return requestedApplication;
    }

    @Override
    public void onMessageSent(final String application, final String requestID,
                              final String[] attachments) {
        Log.d(SaveEditService.TAG, "onMessageSent");
    }

    @Override
    public void onReceivingAttachments(String application, int numberOfAttachments, String requestID) {
        Log.d(SaveEditService.TAG, "onReceivingAttachments number of attachments: " +
                                    numberOfAttachments + " for requestID: " + requestID + "\n");
    }
    
    @Override
    public void onReceivingAttachmentFile(String application, String path, long size, String requestID) {
        Log.d(SaveEditService.TAG, "onReceivingAttachmentFile attachment: " +
                                    path + " size: " + size + " for requestID: " + requestID + "\n");
    }
    
    @Override
    public void onReceiveMessage(final String application, final String service,
                                 final String version, final String method, final Object params,
                                 final String[] attachments, final String requestID) {
        this.requestedApplication = application;

        if (onReceiveAttachmentsEventListener != null) {
            onReceiveAttachmentsEventListener.onReceiveAttachments(attachments);
        } else {
            pendingAttachments = attachments;
        }
    }

    /**
     * Public interface, which is used to handle received attachments.
     */
    public interface OnReceiveAttachmentsEventListener {
        /**
         * Called when attachments received in the GDServiceClientListener.
         *
         * @param attachments Array with attachments.
         */
        void onReceiveAttachments(final String[] attachments);
    }
}
