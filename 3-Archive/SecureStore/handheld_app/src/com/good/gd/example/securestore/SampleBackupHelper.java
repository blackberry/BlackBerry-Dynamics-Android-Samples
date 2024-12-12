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

package com.good.gd.example.securestore;

import java.io.IOException;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.good.gd.example.securestore.common_lib.utils.BackupUtils;
import com.good.gd.backup.GDFileBackupHelper;
/**
 * Represents a BackupAgentHelper subclass used to exemplify how the GDFileBackupHelper is used to backup
 * files from the application secure container.
 */
public class SampleBackupHelper extends BackupAgentHelper {

	private static final String FILES_BACKUP_KEY = "BACKUP_KEY";
	private static final String TAG = "SampleBackupHelper";
	
	public void onCreate() {
		Log.i(TAG, "[SampleBackupHelper::onCreate]");
		
		// We create a GDFileBackupHelper instance which should be used instead of the default
		// FileBackupHelper provided by android. We should indicate which files do we want to backup/restore.
		// An important thing to mention is that we are using the BackupUtils to return a list of files
		// which should be backed up. This list can be obtained from anywhere in real-life apps.
		GDFileBackupHelper helper = new GDFileBackupHelper(this, BackupUtils.list());
		addHelper(FILES_BACKUP_KEY, helper);
	}
	
	@Override
	public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
			 ParcelFileDescriptor newState) throws IOException {
		Log.i(TAG, "[SampleBackupHelper::onBackup]");
		super.onBackup(oldState, data, newState);
	}

	@Override
	public void onRestore(BackupDataInput data, int appVersionCode,
			ParcelFileDescriptor newState) throws IOException {
		Log.i(TAG, "[SampleBackupHelper::onRestore]");
		super.onRestore(data, appVersionCode, newState);
	}
}