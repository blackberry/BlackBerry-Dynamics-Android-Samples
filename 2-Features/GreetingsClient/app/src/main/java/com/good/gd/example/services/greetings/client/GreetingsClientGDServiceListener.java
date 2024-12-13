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


 /*
 * This file contains sample code that is licensed according to the BlackBerry Dynamics SDK terms and conditions.
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */

package com.good.gd.example.services.greetings.client;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.util.Log;

import com.good.gd.icc.GDServiceClientListener;
import com.good.gd.icc.GDServiceListener;
import com.good.gd.icc.GDService;
import com.good.gd.icc.GDServiceException;
import com.good.gd.icc.GDServiceError;
import com.good.gd.icc.GDServiceErrorCode;

import com.good.gd.icc.GDICCForegroundOptions;

class GreetingsClientGDServiceListener implements GDServiceClientListener, GDServiceListener {
	
	private static GreetingsClientGDServiceListener _instance = null;
	private static final String TAG = GreetingsClientGDServiceListener.class.getSimpleName();

	// state
	private boolean _active;
	private Builder _storedDialog;
	
	private GreetingsClient _currentActivity;
    
    private boolean consumeFrontRequestService(String serviceID, String application, String method, String version, String requestID) {
        if(serviceID.equals(GDService.GDFrontRequestService) && version.equals("1.0.0.0")){
            if(method.equals(GDService.GDFrontRequestMethod)){
                // bring to front
                try {
                    GDService.bringToFront(application);
                } catch (GDServiceException e) {
                    Log.d(TAG, "- bringToFront - Error: " + e.getMessage());
                }
                
            } else {
                GDServiceError serviceError = new GDServiceError(GDServiceErrorCode.GDServicesErrorMethodNotFound);
				//Send service reply
                try {
                    GDService.replyTo(application, serviceError, GDICCForegroundOptions.NoForegroundPreference, null, requestID);
                } catch(GDServiceException e) {
                    Log.d(TAG, "- replyTo - Error: " + e.getMessage());
                }
            }
            return true;
        }
        return false;
    }
	
	synchronized static GreetingsClientGDServiceListener getInstance() {
		if(_instance == null) {
			_instance = new GreetingsClientGDServiceListener();
		}
		return _instance;
	}

	void setCurrentContext(GreetingsClient client) {
		if (client != null) {			
			_currentActivity = client;
			//Logic for showing a dialog later
			if (_storedDialog != null){
				Log.d(TAG, "  onStart - show stored dialog");
				_storedDialog.show();
				_storedDialog = null;
			}
			_active = true;
		} else {
			_active = false;
		}
	}
	
	//Service Client Listener methods
	@Override 
	public void onMessageSent(String application, String requestID, String[] attachments) {
		Log.d(TAG, "+ onMessageSent");
	}
    
    // from serviceListener for front request service
    @Override
	public void onReceiveMessage(String application, String service,
                                 String version, String method, Object params, String[] attachments,
                                 String requestID)
    {
        if (!consumeFrontRequestService(service, application, method, version, requestID)) {
            GDServiceError serviceError = new GDServiceError(GDServiceErrorCode.GDServicesErrorServiceNotFound);
            //Send service reply
            try {
                GDService.replyTo(application, serviceError, GDICCForegroundOptions.NoForegroundPreference, null, "");
            } catch(GDServiceException e) {
                Log.d(TAG, "- replyTo - Error: " + e.getMessage());
            }
        }
    }

    @Override
    public void onReceivingAttachments(String application, int numberOfAttachments, String requestID) {
        Log.d(TAG, "onReceivingAttachments number of attachments: " + numberOfAttachments + " for requestID: " + requestID + "\n");
    }
    
    @Override
    public void onReceivingAttachmentFile(String application, String path, long size, String requestID) {
        Log.d(TAG, "onReceivingAttachmentFile attachment: " + path + " size: " + size + " for requestID: " + requestID + "\n");
    }
    
    // from clientListener
	@Override
	public void onReceiveMessage(String application,
			Object params,
			String[] attachments,
			String requestID)
	{

		Log.d(TAG, "+ onReceiveMessage");
		
		String reply = "Reply: ";

		//Reply from server
		if (params != null) {
			if (params instanceof String) {
				reply += (String)params;
				Log.d(TAG, "+ onReceiveMessage reply=" + reply);
			}
			else if (params instanceof Integer) {
				reply += "AGE: " + params.toString();
				Log.d(TAG, "+ onReceiveMessage reply=" + reply);
			}
			else if (params instanceof GDServiceError) {
				//GDServiceError has a GDServiceErrorCode, and may also hold a Message, CustomServiceError, and a Details object 
				GDServiceError serviceError = (GDServiceError)params;

				//Optional Error message
				String errorMessage = serviceError.getMessage();
				if (errorMessage != null) {
					reply += "\nGDServiceError: " + errorMessage;
				}

				//Optional Details object
				Object errorDetails = serviceError.getDetails();
				if (errorDetails != null) {
					reply += "\nGDServiceError Details: " + errorDetails.toString();
				}

				//Service Error code
				GDServiceErrorCode serviceErrorCode = serviceError.getErrorCode();

				//Handle custom service errors
				if (serviceErrorCode == GDServiceErrorCode.GDServicesErrorCustom) {
					int customErrorCode = serviceError.getCustomErrorCode();

					//These should match the custom error codes that can be returned by the server - use an enum
					switch (customErrorCode) {
						//Name not found error
						case 1:
							reply += "\nGDServiceError - Custom Service Error: Name Not Found";
							break;

						default:
							reply += "\nGDServiceError - Custom Error Code: " + customErrorCode;
					}
				}
				else {
					//Handle non-custom GDServiceErrors
					switch (serviceErrorCode) {

						case GDServicesErrorGeneral :
							reply += "\nGDServiceError: General Error";
							break;

						case GDServicesErrorApplicationNotFound :
							reply += "\nGDServiceError: Application Not Found";
							break;

						case GDServicesErrorServiceNotFound :
							reply += "\nGDServiceError: Service Not Found";
							break;

						case GDServicesErrorServiceVersionNotFound :
							reply += "\nGDServiceError: Service Version Not Found";
							break;

						case GDServicesErrorMethodNotFound :
							reply += "\nGDServiceError: Method Not Found";
							break;

						case GDServicesErrorNotAllowed :
							reply += "\nGDServiceError: Not Allowed";
							break;

						case GDServicesErrorInvalidParams :
							reply += "\nGDServiceError: Invalid Parameters";
							break;

						case GDServicesErrorInUse :
							reply += "\nGDServiceError: In Use";
							break;

						case GDServicesErrorEnterpriseUserNotMatch :
							reply += "\nGDServiceError: Enterprise User Not Match";
							break;

						default:
							reply += "\nGDServiceError: Unhandled Error";
							break;
					}
				}
			}
		}

		//Attachments
		if (attachments != null && attachments.length > 0) {
			reply += "\nAttachments received: ";
			for (int i=0; i<attachments.length; ++i) {
				reply += "\n" + (i+1) + " " + attachments[i];
			}
		}
		else {
			reply += "\nNo attachments.";
		}

		reply += "\nRequestID: " + requestID;
		
		long currentTime = System.currentTimeMillis();
		long timeTaken = currentTime - GreetingsClient._startTime;
		reply += "\nTime taken: " + timeTaken + "ms";

		if (_currentActivity != null) {
			final String finalReply = reply;
			_currentActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					final Builder dialog = new AlertDialog.Builder(_currentActivity)
							.setTitle("Reply received:")
							.setMessage(finalReply)
							.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int value) {
									//Just dismiss the dialog
								}
							});
					if (_active) {
						Log.d(TAG, "+ onReceiveMessage - displaying dialog");
						if (!_currentActivity.isFinishing()) {
							dialog.show();
						}
					}
					else {
						// Defer showing the dialog until the Activity is active
						Log.d(TAG, "+ onReceiveMessage - saving dialog");
						_storedDialog = dialog;
					}
				}
			});
		} else {
			Log.e(TAG,"Received message when application's UI is not available\nMessage:" +
					reply);
		}
		Log.d(TAG, "- onReceiveMessage");
	}
}