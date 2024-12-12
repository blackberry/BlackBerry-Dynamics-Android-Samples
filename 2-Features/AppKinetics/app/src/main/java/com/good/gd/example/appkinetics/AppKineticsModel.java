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

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import androidx.fragment.app.DialogFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.util.Log;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateAction;
import com.good.gd.GDStateListener;
import com.good.gd.file.File;
import com.good.gd.file.FileInputStream;
import com.good.gd.file.FileOutputStream;
import com.good.gd.icc.GDICCForegroundOptions;
import com.good.gd.icc.GDService;
import com.good.gd.icc.GDServiceClient;
import com.good.gd.icc.GDServiceError;
import com.good.gd.icc.GDServiceException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Application Model representation
 */
public class AppKineticsModel implements GDStateListener {

    //key for storing in preferences a flag that indicated if initialize() has been called
    private static final String WAS_INITIALIZED_FLAG_KEY = "wasInitialized";
    // Static Variables -------------------------------------------------------
    // Singleton instance variable
    private static AppKineticsModel instance = null;
    // Instance Variables -----------------------------------------------------
    // authorized state of the app
    private volatile boolean authorized = false;
    // list of files stored in the container
    private HashMap<String, FileModel> files = new HashMap<String, FileModel>();
    //context to get snd save shared preferences and getting assets
    private Context context = null;
    private ErrorMessageModel lastError = null;
    // The variable is to track the activity lifecycle state. Used to check if the activity is in proper state before executing the Fragment transactions
    private boolean isActivityPaused = false;
	//latest resumed "AppKinetics" activity of the app
	volatile WeakReference<AppKinetics> appKineticsActivity = null;
    //latest resumed activity of the app
    volatile WeakReference<FragmentActivity> currentActivityReference = null;

    private AppKineticsModelListener modelListener = null;


    // Static Methods ---------------------------------------------------------
    public static AppKineticsModel getInstance() {
        if (instance == null) {
            synchronized (AppKineticsModel.class) {
                instance = new AppKineticsModel();
                GDAndroid.getInstance().setGDStateListener(instance);
            }
        }
        return instance;
    }

    private AppKineticsModel() {
        final AppKineticsGDServiceListener serviceListener =
                AppKineticsGDServiceListener.getInstance();

        Log.e(AppKineticsHelpers.LOGTAG,
                "AppKineticsModel() service Listener = " + serviceListener + "\n");

        if (serviceListener != null) {

            // set the Client Service Listener to get responses from server
            // (which is another AppKinetics client)
            try {
                GDServiceClient.setServiceClientListener(serviceListener);
                GDService.setServiceListener(serviceListener);
            } catch (GDServiceException gdServiceException) {
                Log.e(AppKineticsHelpers.LOGTAG, "AppKineticsModel() " +
                        "- error setting GDServiceClientListener: " + gdServiceException.getMessage() + "\n"
                );
            }
        }
        registerContainerMigrationReceivers();
    }

    public void setModelListener(AppKineticsModelListener listener) {
        this.modelListener = listener;
    }

    private void registerContainerMigrationReceivers() {
        IntentFilter intentFilter;

        // register container migration pending broadcast receiver
        intentFilter= new IntentFilter(GDStateAction.GD_STATE_CONTAINER_MIGRATION_PENDING);
        GDAndroid.getInstance().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(AppKineticsHelpers.LOGTAG, "Container migration pending broadcast received");
            }
        }, intentFilter);

        // register container migration completed broadcast receiver
        intentFilter = new IntentFilter(GDStateAction.GD_STATE_CONTAINER_MIGRATION_COMPLETED);
        GDAndroid.getInstance().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(AppKineticsHelpers.LOGTAG, "Container migration completed broadcast received");
            }
        }, intentFilter);
    }


    // Public Methods ---------------------------------------------------------
    public void initialize() {
        files.clear();

        // Get all the files at the root ('/') of the secure storage
        final File directory = new File("/");
        java.io.File[] filesArray = directory.listFiles();

        for (java.io.File file : filesArray) {
            // if it is a file add it to the list
            if (file.isFile()) {
                files.put(file.getName(), new FileModel(file.getName()));
            }
        }

        if (files.isEmpty()) {
            final SharedPreferences storedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(context);

			/*
             * check if flag wasInitialized has been stored in stored preferences.
			 * If it is the first initialization and there is no
			 * files, we should copy default files.
			 */
            if (!storedPreferences.contains(WAS_INITIALIZED_FLAG_KEY)) {
                resetFileList();

                // save flag wasInitialized to the stored preferences
                SharedPreferences.Editor editor = storedPreferences.edit();
                editor.putBoolean(WAS_INITIALIZED_FLAG_KEY, true);
                editor.apply(); // Commit to storage
            }
        } else {
            if(modelListener != null) {
                // the list of files has been changed,
                // notify listener about this
                modelListener.onFilesListChanged();
            }
        }
    }

    public void resetFileList() {
		Log.d(AppKineticsHelpers.LOGTAG, "AppKineticsModel.resetFileList IN");

		List<FileModel> fileList = new ArrayList<FileModel>(this.files.values());

        //delete all the files
        for (final FileModel file : fileList) {
            deleteFile(file.getName());
        }

        // copy sample files

        // make the valid pattern of sample files
        final Pattern pattern = Pattern.compile(AppKineticsHelpers.FILENAMEPATTERN);

        try {
            // get the files from the app assets folder
            final String[] sampleFiles = this.context.getAssets().list("");
            if (sampleFiles == null) {
                return;
            }

            for (final String file : sampleFiles) {
                // check if the file is one of the sample files
                final Matcher matcher = pattern.matcher(file);
                if (!matcher.find()) {
                    continue;
                }

                // copy the file to secure storage
                final InputStream inputStream = context.getAssets().open(file);

                saveFile(file, inputStream);

                inputStream.close();
            }
        } catch (final IOException ioException) {
            Log.e(AppKineticsHelpers.LOGTAG, "AppKineticsModel.resetFileList:" +
                    ioException.getMessage());
        }
		Log.d(AppKineticsHelpers.LOGTAG, "AppKineticsModel.resetFileList OUT");
    }

    public List<FileModel> getFiles() {
        // return a list from the hashmap
        List<FileModel> fileList = new ArrayList<FileModel>(this.files.values());
        return fileList;
    }


	// Add files to the collection of files that will be saved after "AppKinetics" activity is resumed.
	public boolean addFilesToPendingSaveList(final String[] files) {
		Log.d(AppKineticsHelpers.LOGTAG, "AppKineticsModel.addFilesToPendingSaveList IN");

		synchronized (pendingFilesToSaveLock) {
			if ( files.length > 0) {
				ArrayList<String> arrUnsorted = new ArrayList<String>(pendingFilesToSave);

                Collections.addAll(arrUnsorted, files);
				Collections.sort(arrUnsorted);

				pendingFilesToSave.add(arrUnsorted.get(0));

				for ( int i = 1; i < files.length; ++i){
					//add without duplicates
					if (arrUnsorted.get(i).compareTo(arrUnsorted.get(i - 1)) != 0) {
						pendingFilesToSave.add(arrUnsorted.get(i));
					}
				}
			}
		}

		startAppKinetics();

		Log.d(AppKineticsHelpers.LOGTAG, "AppKineticsModel.addFilesToPendingSaveList OUT");
		return true;
	}

	void startAppKinetics() {
        if (this.authorized && !this.getPendingFileList().isEmpty()) {
            Intent i = new Intent(context, AppKinetics.class);
            int intentFlags = Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
            i.setFlags(intentFlags);
            context.startActivity(i);
        }
    }

	//files that will be saved after "AppKinetics" activity is resumed.
	ArrayList<String> pendingFilesToSave = new ArrayList<String>();

	//used for locking of pendingFilesToSave
	Object pendingFilesToSaveLock = new Object();

	ArrayList<String> getPendingFileList() {
		synchronized (pendingFilesToSaveLock) {
			return pendingFilesToSave;
		}
	}

	void savePendingFiles() {
		Log.d(AppKineticsHelpers.LOGTAG, "AppKineticsModel.savePendingFiles IN");

		synchronized (pendingFilesToSaveLock) {
			for (String file : pendingFilesToSave) {
				try {
					// for each file, open as GD input stream and save it

					FileInputStream fis = new FileInputStream(file);
					this.saveFile(file, fis);
				} catch (java.lang.Exception e) {
				}
			}

			pendingFilesToSave.clear();
		}
		Log.d(AppKineticsHelpers.LOGTAG, "AppKineticsModel.savePendingFiles OUT");
	}

    /**
     * sendFile - send files over a Good Inter-Container Communication request
     *
     * @param address
     * @param data
     */
    public void sendFiles(final String address, final List<String> data) {
		Log.d(AppKineticsHelpers.LOGTAG, "AppKineticsModel.sendFiles IN");

        try {
            final List<String> filesToSend = new ArrayList<String>();

            for (final String file : data) {
                filesToSend.add("/" + file); // add the root path to the file names
            }

            // Invoke ICC to send the files
            GDServiceClient.sendTo(address, AppKineticsHelpers.SERVICENAME,
                    AppKineticsHelpers.VERSION,
                    AppKineticsHelpers.SERVICEMETHODNAME, null,
                    filesToSend.toArray(new String[filesToSend.size()]),
                    GDICCForegroundOptions.PreferPeerInForeground);
        } catch (final GDServiceException gdServiceException) {
            Log.e(AppKineticsHelpers.LOGTAG, "AppKineticsModel.sendFiles: unable to transfer file: "
                    + gdServiceException.toString());
        }
		Log.d(AppKineticsHelpers.LOGTAG, "AppKineticsModel.sendFiles OUT");
    }

    public void deleteFile(final String path) {
        Log.e(AppKineticsHelpers.LOGTAG,
                "AppKineticsModel.deleteFile: saved file - " + path);
        final File file = new File("/" + path);
        file.delete();

        files.remove(file.getName());
    }

    /**
     * getFileData - returns a byte array of the file at the specified path
     *
     * @param path
     * @return
     */
    public byte[] getFileData(final String path) {
        byte retData[] = null;
        if (this.authorized) {
            try {
                // open the file as GD input stream
                java.io.InputStream inputStream = new FileInputStream("/" + path);
                // read the file data
                if (inputStream != null && inputStream.available() > 0) {
                    retData = new byte[inputStream.available()];
                    inputStream.read(retData);
                }
                inputStream.close();
            } catch (final IOException ioException) {
                Log.e(AppKineticsHelpers.LOGTAG,
                        "AppKineticsModel.getFileData: " + ioException.toString());
            }
        }
        return retData;
    }

    // GDStateListener Methods ------------------------------------------------
    @Override
    public void onAuthorized() {
		Log.d(AppKineticsHelpers.LOGTAG, "AppKineticsModel.onAuthorized()");

		// initialize the model
		initialize();

		authorized = true;

		startAppKinetics();
    }

    @Override
    public void onLocked() {
        authorized = false;
        Log.d(AppKineticsHelpers.LOGTAG, "AppKineticsModel.onLocked()");
    }

    @Override
    public void onWiped() {
        this.authorized = false;

        //remove wasInitialized flag from stored preferences
        final SharedPreferences storedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = storedPreferences.edit();
        editor.remove(WAS_INITIALIZED_FLAG_KEY);
        editor.apply(); // Commit to storage

        Log.d(AppKineticsHelpers.LOGTAG, "AppKineticsModel.onWiped()");
    }

    @Override
    public void onUpdateConfig(Map<String, Object> settings) {
        Log.d(AppKineticsHelpers.LOGTAG, "AppKineticsModel.onUpdateConfig()");
    }

    @Override
    public void onUpdatePolicy(Map<String, Object> policyValues) {
        Log.d(AppKineticsHelpers.LOGTAG, "AppKineticsModel.onUpdatePolicy()");
    }

    @Override
    public void onUpdateServices() {
        Log.d(AppKineticsHelpers.LOGTAG, "AppKineticsModel.onUpdateServices()");
    }

    @Override
    public void onUpdateEntitlements() {
        Log.d(AppKineticsHelpers.LOGTAG, "AppKineticsModel.onUpdateEntitlements()");
    }

	synchronized private void saveFile(final String file, final InputStream inputStream) {
		Log.d(AppKineticsHelpers.LOGTAG, "AppKineticsModel.saveFile IN");
		try {
			// save the input stream into GD secure storage
			File f = new File(file);
			String fileName = f.getName();
			// get the file stem & extension
			String fileStem = fileName.substring(0, fileName.lastIndexOf('.'));
			String fileExt = fileName.substring(fileName.lastIndexOf('.'));

			// if the same file exists, create a new file like 'stem (n).ext'
			// n is a numerical value
			String outFileName = "/" + fileStem + fileExt;
			File outFile = new File(outFileName);
			final String receivedFileName = outFile.getName();

			if (outFile.exists()) {
				appKineticsActivity.get().showOverwriteDialog(receivedFileName, outFileName, inputStream);
			} else {
				actualSaveFile(outFileName, inputStream);
				inputStream.close();
			}
		} catch (java.lang.Exception e) {
		}
		Log.d(AppKineticsHelpers.LOGTAG, "AppKineticsModel.saveFile OUT");
	}

	void actualSaveFile(final String outFileName, final InputStream inputStream) {
		Log.d(AppKineticsHelpers.LOGTAG, "AppKineticsModel.actualSaveFile IN");
		// open the file output stream for writing
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(outFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// copy the streams
		byte buffer[] = new byte[4 * 1024];
		try {
			while (inputStream.available() > 0) {
				int numRead = inputStream.read(buffer);
				fos.write(buffer, 0, numRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Add it to the hashmap
		File outfile = new File(outFileName);
		this.files.put(outfile.getName(), new FileModel(outfile.getName()));

		Log.e(AppKineticsHelpers.LOGTAG,
                "AppKineticsModel.saveFiles: saved file - " + outfile.getName());
		Log.d(AppKineticsHelpers.LOGTAG, "AppKineticsModel.actualSaveFile OUT");
	}

    private boolean isForeground()
    {
        boolean inForeground = false;

        ActivityManager activityManager = (ActivityManager) this.context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                appProcess.processName.equals(this.context.getPackageName())) {
                inForeground = true;
                break; //break as soon as we have a match to ensure code execution is as fast as possible
            }
        }
        return inForeground;
    }

    public void handleError(GDServiceError aError) {
        String errorTitle = aError.getErrorCode().toString();
        String errorMessage = aError.getMessage();

        if (errorMessage.isEmpty()){
            // handle case when no message was sent in the error
            errorMessage = errorTitle;
            errorTitle = context.getResources().getString(R.string.error);
        }
        handleError(errorTitle, errorMessage);
    }

    public void handleError(int errorStringId) {
        String errorTitle = context.getResources().getString(R.string.error);
        String errorMessage = context.getResources().getString(errorStringId);

        handleError(errorTitle, errorMessage);
    }

    private void handleError(String errorTitle, String errorMessage) {
        Log.e(AppKineticsHelpers.LOGTAG, "AppKineticsModel ICC Error " + errorTitle + " " + errorMessage);

        final String fragmentTAG = "Icc_Error_Message";
        final ErrorMessageFragment userMessage = ErrorMessageFragment.createInstance(errorTitle, errorMessage);

        if ( !showFragmentIfCan(userMessage, fragmentTAG)) {
            Log.d(AppKineticsHelpers.LOGTAG, "fragment added to queue, getCurrentActivity = " + getCurrentActivity() + " isForeground = " + isForeground());
            lastError = new ErrorMessageModel(errorTitle, errorMessage, fragmentTAG);
        }
    }

    /**
     * Show fragment if can(activity != null and is in foreground state) and needed(if fragment is not already presented)
     * @param fragment dialog fragment to show
     * @param fragmentTag tag that linked with fragment
     * @return true if can show fragment, otherwise false
     */
    private boolean showFragmentIfCan(final @NonNull DialogFragment fragment, final @NonNull String fragmentTag) {
        final FragmentActivity currentActivity = getCurrentActivity();

        if (currentActivity != null && !isActivityPaused()) {
            final FragmentManager fm = currentActivity.getSupportFragmentManager();
            // if fragment is not shown then show it
            if (fm.findFragmentByTag(fragmentTag) == null) {
                currentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fragment.show(fm, fragmentTag);
                        fm.executePendingTransactions();
                    }
                });
                return true;
            } else {
                Log.w(AppKineticsHelpers.LOGTAG, "fragment with tag: " + fragmentTag + " already presented");
                return false;
            }
        }
        return false;
    }

    public void processPendingDialog() {
        Log.d(AppKineticsHelpers.LOGTAG, "process pending dialog");
        if (lastError != null) {
            final ErrorMessageFragment fragment = ErrorMessageFragment.createInstance(lastError.title, lastError.message);
            if (showFragmentIfCan(fragment, lastError.tag)) {
                lastError = null;
            }
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

	boolean isAuthorized() {
		return authorized;
	}

    public void onResume() {
        isActivityPaused = false;
    }

    public void onPause() {
        isActivityPaused = true;
    }

    public boolean isActivityPaused() {
        return isActivityPaused;
    }

    public void setAppKineticsActivity(AppKinetics appKineticsActivity2) {
        this.appKineticsActivity = new WeakReference<>(appKineticsActivity2);
    }

    private Activity getAppKineticsActivity() {
        return this.appKineticsActivity.get();
    }

    public void setCurrentActivity(FragmentActivity currentActivity) {
        this.currentActivityReference = new WeakReference<>(currentActivity);
    }

    private @Nullable FragmentActivity getCurrentActivity() {
        // can be executed before setCurrentActivity(Activity)
        // Example: when AppKineticsModel.handleError called before AppKinetics.onPostResume
        if(currentActivityReference != null) {
            return this.currentActivityReference.get();
        } else {
            Log.e(AppKineticsHelpers.LOGTAG, "getCurrentActivity() called but currentActivityReference == null");
            return null;
        }
    }

    private class ErrorMessageModel {
        final String tag;
        final String title;
        final String message;

        ErrorMessageModel(final String title, final String message, final String tag) {
            this.tag = tag;
            this.title = title;
            this.message = message;
        }
    }
}