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

package com.good.gd.example.services.greetings.server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.String;

import android.util.Log;

import com.good.gd.icc.GDICCForegroundOptions;
import com.good.gd.icc.GDService;
import com.good.gd.icc.GDServiceListener;
import com.good.gd.icc.GDServiceError;
import com.good.gd.icc.GDServiceErrorCode;
import com.good.gd.icc.GDServiceException;

import com.good.gd.file.File;
import com.good.gd.file.FileOutputStream;

class GreetingsServerGDServiceListener implements GDServiceListener {
	
	private static GreetingsServerGDServiceListener _instance = null;
	private static final String TAG = GreetingsServerGDServiceListener.class.getSimpleName();

	// test files
	private volatile String _file1;
	private volatile String _file2;

	synchronized static GreetingsServerGDServiceListener getInstance() {
		if(_instance == null) {
			_instance = new GreetingsServerGDServiceListener();
		}
		return _instance;
	}

    private boolean consumeFrontRequestService(String serviceID, String application, String method, String version, String requestID) {
        if(serviceID.equals(GDService.GDFrontRequestService) && version.equals("1.0.0.0")) {
            if(method.equals(GDService.GDFrontRequestMethod)) {
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
                    GDService.replyTo(application, serviceError, GDICCForegroundOptions.PreferMeInForeground, null, requestID);
                } catch(GDServiceException e) {
                    Log.d(TAG, "- replyTo - Error: " + e.getMessage());
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
	public void onMessageSent(String application, String requestID, String[] attachments) {
		Log.d(TAG, "Message sent.\n");
	}

    @Override
    public void onReceivingAttachments(String application, int numberOfAttachments, String requestID) {
        Log.d(TAG, "onReceivingAttachments number of attachments: " + numberOfAttachments + " for requestID: " + requestID + "\n");
    }
    
    @Override
    public void onReceivingAttachmentFile(String application, String path, long size, String requestID) {
        Log.d(TAG, "onReceivingAttachmentFile attachment: " + path + " size: " + size + " for requestID: " + requestID + "\n");
    }

    //Service Listener methods
	@Override
	public void onReceiveMessage(String application, String service,
			String version, String method, Object params, String[] attachments,
			String requestID) {
		Log.d(TAG, "+ GreetingsServer.onReceiveMessage application=" + application);

		if (_file1 == null || _file2 == null) {
			int fileSize = 10000;
			_file1 = test_createFile("replyFile1.txt", fileSize);
			_file2 = test_createFile("/\u6c49\u8bed/replyfile2\u65e5\u672c\u56fd.txt", fileSize);
		}
		
		//Prepare for payload
		String message = "";
		String param = null;
		String files[] = null;
		GDServiceError serviceError = null;

		if (params != null) {
			//Build up the reply message and get the param for services below
			//This example is only supporting a String param
			if(params instanceof String){
				message = (String)params;
				param = (String)params;
			}
		}

		//Attachments
		if (attachments == null) {
			Log.d(TAG, "+ GreetingsServer.onReceiveMessage attachments null");
		}
		else {
			Log.d(TAG, "+ GreetingsServer.onReceiveMessage attachments length=" + attachments.length);
			if (attachments.length > 0){
				message += "\nAttachments received:";
				for (String attachment : attachments) {
					message += "\n" + attachment;
				}
			}
		}
		
		Log.d(TAG, "+ GreetingsServer.onReceiveMessage message=" + message);

		try {
			//Prepare for service reply
			Object reply = null;	

			//Blanket check for bad service requests
			if (application == null || service == null || version == null || method == null) {
				serviceError = new GDServiceError(GDServiceErrorCode.GDServicesErrorGeneral);
			}
			else{
				//Check application
				if(application.equalsIgnoreCase("com.good.gd.example.services.greetings.client")){
					Log.d(TAG, "+ GreetingsServer.onReceiveMessage - from greetings client");
					if (attachments != null && attachments.length != 0){
						// For this example we only send a file back if we have been sent files.
						files = new String[2];
						files[0] = _file1;
						files[1] = _file2;
					}

					//Check service
                    if (!consumeFrontRequestService(service, application, method, version, requestID)) {
                        if (service.equalsIgnoreCase("search")) {
                            Log.d(TAG, "+ GreetingsServer.onReceiveMessage - search service");
                            Object result = searchService(param, method, version);
                            if (result instanceof String) {
                                reply = result;
                            }
                            else if (result instanceof GDServiceError) {
                                serviceError = (GDServiceError)result;
                            } else {
                                Log.d(TAG, "+ GreetingsServer.onReceiveMessage - service error");
                                serviceError = new GDServiceError(GDServiceErrorCode.GDServicesErrorGeneral);
                            }
                        }
                        else if (service.equalsIgnoreCase("testService")) {
                            Log.d(TAG, "+ GreetingsServer.onReceiveMessage - test service");
                            //Just give a reply greeting as there's no specific method to look for
                            reply = "Hello from GreetingsServer!";
                        } else {
                            Log.d(TAG, "+ GreetingsServer.onReceiveMessage - service not found");
                            serviceError = new GDServiceError(GDServiceErrorCode.GDServicesErrorServiceNotFound);
                        }
                    }
				} else if (application.equalsIgnoreCase("com.my.other.application")){
					Log.d(TAG, "+ GreetingsServer.onReceiveMessage - from other application");
					if (service.equalsIgnoreCase("search")){
						//Search service not allowed from com.my.other.application
						serviceError = new GDServiceError(GDServiceErrorCode.GDServicesErrorNotAllowed);
					}
				} else {
					Log.d(TAG, "+ GreetingsServer.onReceiveMessage - unknown app");
					//Application not found
					serviceError = new GDServiceError(GDServiceErrorCode.GDServicesErrorApplicationNotFound);
				}
			}

			//Check for errors before replying
			if (serviceError == null) {
				Log.d(TAG, "+ GreetingsServer.onReceiveMessage - send reply");
				//Send service reply
				GDService.replyTo(application, reply, GDICCForegroundOptions.PreferMeInForeground, files, requestID);
			}
			else {
				Log.d(TAG, "+ GreetingsServer.onReceiveMessage - send error reply");
				//Send service reply
				GDService.replyTo(application, serviceError, GDICCForegroundOptions.PreferMeInForeground, files, requestID);
			}
		} catch (GDServiceException e) {
			Log.d(TAG, "- onReceiveMessage - Error: " + e.getMessage());
		}
		Log.d(TAG, "- GreetingsServer.onReceiveMessage");
	}

	//Search service
	private Object searchService(String param, String method, String version) {
		if (!version.equalsIgnoreCase("1.0.0")) {
			//Version not found
			return new GDServiceError(GDServiceErrorCode.GDServicesErrorServiceVersionNotFound);
		}
		
		//Search for match
		if (param.length() > 0) {
			int employeeDBPos = 0;

			//Primitive DB
			String[] names = {"adam","bob","clive","debbie","edward"};
			Integer[] ages = {21,33,31,23,25};
			Integer[] departments = {1,2,1,1,3};

			//Simulate searching DB by name
			boolean found = false;
			for (int i = 0; i < names.length; i++) {
				String name = names[i];
				if(name.equals(param)){
					found = true;
					employeeDBPos = i;
					break;
				}
			}

			//Check they were in DB
			if(!found) {
				//Create a custom 'Name not found' error - pass back the original query as the Details
				return new GDServiceError(1,"Name not found",param);
			} else if (method.equalsIgnoreCase("age")) {
				//Return age
				return ages[employeeDBPos].toString();
			} else if (method.equalsIgnoreCase("department")) {
				//Return department
				return departments[employeeDBPos];
			} else {
				//Method not found
				return new GDServiceError(GDServiceErrorCode.GDServicesErrorMethodNotFound);
			}
		}
		//Params invalid
		return new GDServiceError(GDServiceErrorCode.GDServicesErrorInvalidParams);
	}

	// test method
	private String test_createFile(String filename, int size) {
		Log.d(TAG , "+ createFile: " + filename + "\n");
		File file = new File(filename);
		if (file.isDirectory()) {
			Log.d(TAG , "+ createFile deleting directory");
			boolean isDeleted = file.delete();
			if (!isDeleted) {
				Log.d(TAG, "Cannot delete file" + filename);
			}
		}
		String parent = file.getParent();
		if (parent != null) {
			File parentfile = new File(parent);
			boolean created = parentfile.mkdirs();
			if (!created) {
				Log.d(TAG , "+ createFile: couldn't create directories");
			} else {
				Log.d(TAG , "+ createFile: created directories");
			}
		}

		byte[] data = new byte[size];
		for (int i=0; i<data.length; ++i) {
			data[i] = 2; // just fill the file with a random value
		}

		OutputStream out;
		try {
			Log.d(TAG , "+ createFile" + filename + "\n");
			out = new BufferedOutputStream(new FileOutputStream(file));
			out.write(data);
			out.flush();
			out.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		Log.d(TAG , "- createFile - created\n");
		return filename;
	}
}