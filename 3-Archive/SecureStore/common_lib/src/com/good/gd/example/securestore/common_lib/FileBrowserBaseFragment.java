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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.backup.BackupManager;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.Toast;

import androidx.fragment.app.ListFragment;

import com.good.gd.example.securestore.common_lib.utils.BackupUtils;
import com.good.gd.example.securestore.common_lib.iconifiedlist.IconifiedText;
import com.good.gd.example.securestore.common_lib.utils.BaseFileUtils;

/**
 * AndroidFileBrowser - a basic file browser list which supports multiple modes
 * (Container and insecure SDCard). Files can be deleted, moved to the container
 * and if they're .txt files they can be opened and viewed.
 */
public abstract class FileBrowserBaseFragment extends ListFragment implements FileTransferListener {

	public final static String STATE_CURRENT_PATH = "curr_path";

	protected List<IconifiedText> directoryEntries = new ArrayList<IconifiedText>();
	protected String mCurrentPath = null;

	private static final int MENU_ITEM_COPY_TO_CONTAINER = 1;
	private static final int MENU_ITEM_OPEN_ITEM = 2;
	private static final int MENU_ITEM_DELETE_ITEM = 3;
    private static final int MENU_ITEM_COPY_TO_CONNECTED_APPLICATION =4;
    private static final int MENU_ITEM_SEND_NUMBER_FILES_TO_CONNECTED_APPLICATION =5;

	protected BackupManager mBackupManager = null;
	protected boolean m_authorized = false;

    protected static final String SECURE_STORE_SHARED_PREFS = "SharedPrefs";
    protected static final String SECURE_STORE_SAMPLE_DATA_KEY = "SampleDataWritten";

    private HashMap<Integer, String> mDeviceNameHashMap =null;


    protected View mView;

	public FileBrowserBaseFragment() {

	}

    @Override
    public void onResume() {
        super.onResume();

        FileTransferControl.getInstance().addFileTransferListener(this);

    }

    @Override
    public void onPause() {
        super.onPause();

        FileTransferControl.getInstance().removeFileTransferListener(this);
    }

    /**
	 * onCreate - sets up the core activity members
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			mCurrentPath = savedInstanceState.getString(STATE_CURRENT_PATH);
		}

        if (!doesSampleDataExist()) {
            // create the sample data if doesn't already exist
            BackupUtils.create();
            setSampleDataExists();
        }
	}

	/**
	 * onSaveInstanceState - saves the path for restore during transitions
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString(STATE_CURRENT_PATH, mCurrentPath);
	}

	/**
	 * onCreateContextMenu - populates the context menu which is shown after a
	 * long press on a row
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        String fullFilePath = getFullFilePath(info.position);

        boolean isFile = !isDir(fullFilePath);

        int order = 0;

        mDeviceNameHashMap = new HashMap<Integer, String>();

		menu.add(Menu.NONE, MENU_ITEM_OPEN_ITEM, order, R.string.MENU_STRING_OPEN);
		menu.add(Menu.NONE, MENU_ITEM_DELETE_ITEM, ++order,
				R.string.MENU_STRING_DELETE);
		if (getFileMode() == BaseFileUtils.MODE_SDCARD) {
			menu.add(Menu.NONE, MENU_ITEM_COPY_TO_CONTAINER, ++order,
					R.string.MENU_STRING_COPY_TO_CONTAINER);
		}
        if(ConnectedApplicationControl.getInstance().getCurrentState().isAppConnected() && isFile){

            ConnectedApplicationState state = ConnectedApplicationControl.getInstance().getCurrentState();

            for(String device_name : state.getConnectedApps()) {

                //We have a connected application
                menu.add(Menu.NONE, MENU_ITEM_COPY_TO_CONNECTED_APPLICATION, ++order,
                        "Copy File to " + device_name);

                canUseICC(menu, order);
                mDeviceNameHashMap.put(order, device_name);

                menu.add(Menu.NONE, MENU_ITEM_SEND_NUMBER_FILES_TO_CONNECTED_APPLICATION, ++order,
                        "Send Number Files to " + device_name);

                canUseICC(menu, order);
                mDeviceNameHashMap.put(order, device_name);

            }


        }
	}

    private void canUseICC(ContextMenu menu, int order){

        if(ServicesControl.getInstance().isServicesSystemBusy()){

            //If currently still sending previous message then don't allow user to send next one
            menu.getItem(order).setEnabled(false);

        } else {

            menu.getItem(order).setEnabled(true);

        }

    }

    private String getFullFilePath(int position){
       return !mCurrentPath.equals("/") ? mCurrentPath + "/"
                + directoryEntries.get(position).getText() : mCurrentPath
                + directoryEntries.get(position).getText();
    }

	/**
	 * onContextItemSelected - something on the context menu was selected
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		if (info == null) {
			return false;
		}

		String fullFilePath = getFullFilePath(info.position);
		switch (item.getItemId()) {
		case MENU_ITEM_COPY_TO_CONTAINER:
			copyToContainer(fullFilePath);
			break;

		case MENU_ITEM_OPEN_ITEM:
			browseToPath(fullFilePath);
			break;

		case MENU_ITEM_DELETE_ITEM:
			deleteItem(fullFilePath);
			browseToPath(mCurrentPath);
			break;
        case MENU_ITEM_COPY_TO_CONNECTED_APPLICATION:
            FileTransferControl.getInstance().sendFile(fullFilePath, getDeviceNameFromOrder(item.getOrder()));
            break;
        case MENU_ITEM_SEND_NUMBER_FILES_TO_CONNECTED_APPLICATION:
            FileTransferControl.getInstance().sendNumberFiles(getNumberFilesInCurrentPath(), getDeviceNameFromOrder(item.getOrder()));
            break;
		}
		return true;
	}

    private String getDeviceNameFromOrder(int aOrder) {

        return mDeviceNameHashMap.get(aOrder);


    }


    protected abstract int getNumberFilesInCurrentPath();
    protected abstract void browseToPath(String aFilePath);
    protected abstract void deleteItem(String aFilePath);
    protected abstract void copyToContainer(String aFilePath);
    protected abstract int getFileMode();
    protected abstract boolean isDir(String aFilePath);
    protected abstract boolean doesSampleDataExist();
    protected abstract void setSampleDataExists();


    @Override
    public void fileSentSuccess() {

        Toast.makeText(getActivity(), "SEND SUCCESS", Toast.LENGTH_LONG).show();
    }

    @Override
    public void fileSentError(String aErrorMessage) {

        //We have decided to not show the exact error message because it can be very verbose
        // (espicially on a wearable which has a small screen)
        // So we decide to only show SEND ERROR not the full error
        Toast.makeText(getActivity(), "SEND ERROR", Toast.LENGTH_LONG).show();
    }

    @Override
    public void numberFilesReceived(int aNumberFiles) {

        Toast.makeText(getActivity(), "ConnectedApp has " + aNumberFiles + " Files", Toast.LENGTH_LONG).show();
    }
}
