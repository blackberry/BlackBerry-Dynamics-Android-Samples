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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.good.gdwearable.GDAndroid;
import com.good.gd.example.securestore.common_lib.FileBrowserBaseFragment;

import com.good.gd.example.securestore.common_lib.iconifiedlist.IconifiedText;
import com.good.gd.example.securestore.common_lib.utils.BaseFileUtils;
import com.good.gd.example.securestore.common_lib.utils.ListUtils;
import com.good.gd.example.securestore.iconifiedlist.IconifiedTextListAdapter;
import com.good.gd.example.securestore.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.good.gd.example.securestore.common_lib.utils.AppLogUtils.DEBUG_LOG;


/**
 * AndroidFileBrowser - This is based on the FileBrowserFragment from the Handheld app with
 * pieces removed/tweaked which aren't relevant to wearables
 */
public class FileBrowserFragment extends FileBrowserBaseFragment implements View.OnClickListener{

	public FileBrowserFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.main, container, false);

        v.findViewById(R.id.gd_app_back).setOnClickListener(this);

        mView = v;

		return v;

	}

	@Override
	public void onStart() {
		super.onStart();

		getListView().setLongClickable(true);
		registerForContextMenu(getListView());

		/*
		 * Set the path the very first time the fragment is created. As fragment
		 * addition to the activity is asynchronous the FileBrowser activity
		 * will not call back directly in case the callback triggers any action
		 * that requires the fragment to be in the started state (typically view
		 * related operations). Therefore we can action the callback here
		 * instead if this is the first start after creation and we can tell
		 * this because the mCurrentPath member is null.
		 */
		if (mCurrentPath == null) {
			mCurrentPath = FileUtils.getInstance().getCurrentRoot();
			// now we've set the path update authorized state as we know it must
			// now be true
			onAuthorizeStateChange(true);
		}
	}


	/**
	 * onListItemClick - something on the list was clicked (not a long click)
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		String browseTo = !mCurrentPath.equals("/") ? mCurrentPath + "/"
				+ directoryEntries.get(position).getText() : mCurrentPath
				+ directoryEntries.get(position).getText();
		browseToPath(browseTo);
	}


	private void onAuthorizeStateChange(boolean authorized) {
		m_authorized = authorized;

		if (authorized) {

			// browse to path
			browseToPath(mCurrentPath);
		}
	}

	/**
	 * upOneLevel - switch the current working directory one layer up
	 */
	private void upOneLevel() {
		File parentFile = FileUtils.getInstance().getParentFile(mCurrentPath);
		if (parentFile != null
				&& FileUtils.getInstance().canGoUpOne(mCurrentPath)) {
			browseToPath(parentFile.getPath());
		}
	}

	/**
	 * browseToPath - load the file browser at the specified path
	 */
    @Override
	protected void browseToPath(String path) {
		if (m_authorized && (path != null)) {
			File file = FileUtils.getInstance().getFileFromPath(path);
			if (file.isDirectory()) {
				mCurrentPath = file.getPath();
				populateList(file.listFiles());
				updateButtonsAndTitle();
			} else {
				FileUtils.getInstance().openItem(getActivity(), path);
			}
		}
	}

    @Override
    protected void deleteItem(String aFilePath) {
        FileUtils.getInstance().deleteItem(aFilePath);
    }

    @Override
    protected void copyToContainer(String aFilePath) {
		//Not implemented on Wearable. We only support GD Container on Wearable
    }

    @Override
    protected int getFileMode() {
        return BaseFileUtils.MODE_CONTAINER;
    }

    @Override
    protected boolean isDir(String aFilePath) {
        return FileUtils.getInstance().isDir(aFilePath);
    }

    @Override
    protected int getNumberFilesInCurrentPath() {
        File file = FileUtils.getInstance().getFileFromPath(mCurrentPath);
        File[] listFiles = file.listFiles();
        return listFiles.length;
    }

	@Override
	protected boolean doesSampleDataExist() {

		SharedPreferences sp = GDAndroid.getInstance().getGDSharedPreferences(SECURE_STORE_SHARED_PREFS,
				android.content.Context.MODE_PRIVATE);

		boolean ret = sp.getBoolean(SECURE_STORE_SAMPLE_DATA_KEY, false);

		DEBUG_LOG("doesSampleDataExist = " + ret);

		return ret;
	}

	@Override
	protected void setSampleDataExists() {

		SharedPreferences sp = GDAndroid.getInstance().getGDSharedPreferences(SECURE_STORE_SHARED_PREFS,
				android.content.Context.MODE_PRIVATE);

		sp.edit().putBoolean(SECURE_STORE_SAMPLE_DATA_KEY, true).apply();

	}

	/**
	 * populateList - takes a set of files and writes into the list adapter
	 */
	private void populateList(File[] files) {
		this.directoryEntries.clear();
		List<String> folderLst = new ArrayList<>();
		List<String> fileLst = new ArrayList<>();

		if (files != null) {
			for (File currentFile : files) {
				if (currentFile.isDirectory()) {
					ListUtils.insertAsc(folderLst, currentFile.getName());
				} else {
					ListUtils.insertAsc(fileLst, currentFile.getName());
				}
			}

			Drawable folderIcon = ContextCompat.getDrawable(getActivity(), R.drawable.fb_folder);
			Drawable fileIcon = (FileUtils.getInstance().getMode() == FileUtils.MODE_SDCARD) ? ContextCompat
					.getDrawable(getActivity(),R.drawable.fb_file) : ContextCompat
					.getDrawable(getActivity(),R.drawable.fb_file_secure);
			for (String str : folderLst) {
				this.directoryEntries.add(new IconifiedText(str, folderIcon));
			}
			for (String str : fileLst) {
				this.directoryEntries.add(new IconifiedText(str, fileIcon));
			}
		}

		IconifiedTextListAdapter itla = new IconifiedTextListAdapter(
				getActivity());
		itla.setListItems(this.directoryEntries);
		setListAdapter(itla);
		setSelection(0);
	}

	/**
	 * updateButtonsAndTitle - redraw the toggle buttons, update the title and
	 * toggle the padlock
	 */
	private void updateButtonsAndTitle() {

		if (FileUtils.getInstance().getMode() == FileUtils.MODE_CONTAINER) {
			mView.findViewById(R.id.fb_padlock)
					.setVisibility(View.VISIBLE);
		} else {
			mView.findViewById(R.id.fb_padlock)
					.setVisibility(View.INVISIBLE);
		}

		// setTitle(mCurrentPath);
	}

	/**
	 * showNewDirUI - show some UI which gets a new directory name
	 */
	public void showNewDirUI() {
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle(R.string.DIR_NAME);
		final EditText input = new EditText(getActivity());
		alert.setView(input);
		alert.setPositiveButton(R.string.OK,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String dirName = input.getText().toString();
						if (dirName.length() > 0) {
							FileUtils.getInstance().makeNewDir(mCurrentPath,
									dirName);
							browseToPath(mCurrentPath);
						}
					}
				});
		alert.setNegativeButton("Cancel", null);
		alert.show();
	}

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.gd_app_back){

            File parentFile = FileUtils.getInstance().getParentFile(mCurrentPath);
            if (parentFile != null
                    && FileUtils.getInstance().canGoUpOne(mCurrentPath)) {
                browseToPath(parentFile.getPath());
            }
        }
    }

    @Override
    public void fileReceived(String aFilePath) {

        Runnable r = new Runnable() {
            @Override
            public void run() {
                //When we receive a file we refresh the current UI, incase the receipt of the file alters the UI (i.e adds new file or dir into current view)
                browseToPath(mCurrentPath);
            }
        };

        //We need to queue a Runnable to run on UI thread because it will update the UI
        getActivity().runOnUiThread(r);
    }

}
