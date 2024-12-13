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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.good.gd.GDAndroid;
import com.good.gd.example.securestore.common_lib.FileBrowserBaseFragment;
import com.good.gd.example.securestore.common_lib.iconifiedlist.IconifiedText;
import com.good.gd.example.securestore.common_lib.utils.BaseFileUtils;
import com.good.gd.example.securestore.common_lib.utils.ListUtils;
import com.good.gd.example.securestore.iconifiedlist.IconifiedTextListAdapter;
import com.good.gd.example.securestore.utils.FileUtils;
import com.good.gd.widget.GDEditText;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.good.gd.example.securestore.common_lib.utils.AppLogUtils.DEBUG_LOG;

/**
 * AndroidFileBrowser - a basic file browser list which supports multiple modes
 * (Container and insecure SDCard). Files can be deleted, moved to the container
 * and if they're .txt files they can be opened and viewed.
 */
public class FileBrowserFragment extends FileBrowserBaseFragment {
    private Context mContext;

    private final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private AlertDialog newFolderAlert;

    private String emulatedDirectory = "/";

    public FileBrowserFragment() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            emulatedDirectory = Environment.getStorageDirectory() + "/emulated/0/";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.main, container, false);
        mView = v;

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((SampleAppActivity) getActivity()).setupAppBar(getString(R.string.app_name));

        View mainView = view.findViewById(R.id.bbd_secure_store_UI);
        View contentView = view.findViewById(R.id.content_layout);
        View bottomBar = view.findViewById(R.id.app_bottom_bar);

        ((SampleAppActivity) getActivity()).adjustViewsIfEdgeToEdgeMode(mainView, bottomBar, contentView);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
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
            onAuthorizeStateChange();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (newFolderAlert != null) newFolderAlert.dismiss();
    }

    /**
     * onListItemClick - something on the list was clicked (not a long click)
     */
    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String browseTo = !mCurrentPath.equals("/") ? mCurrentPath + "/"
                + directoryEntries.get(position).getText() : mCurrentPath
                + directoryEntries.get(position).getText();
        browseToPath(browseTo);
    }


    private void onAuthorizeStateChange() {
        m_authorized = true;
        int permission = ContextCompat.checkSelfPermission(mContext
                , Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // This permission is required only for SDCARD.
        if ((mCurrentPath.equals(FileUtils.SDCARD_ROOT)) &&
                (permission != PackageManager.PERMISSION_GRANTED)) {
            return;
        }
        // create backup manager if needed
        if (mBackupManager == null) {
            mBackupManager = new BackupManager(mContext);
        }
        // handle any backup
        mBackupManager.dataChanged();

        // browse to path
        browseToPath(mCurrentPath);

    }

    @SuppressLint("UNUSED_PARAMETER")
    public void onPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE == requestCode) {
            // Check if this fragment is attached to host Activity.
            // If yes, we can directly ask to show file list.
            // If no, this is because parent Activity has removed it with onPause callback,
            // and with onResume callback the new fragment instance will be created.
            // Fragment.onStart callback will show the needed file list, because we store FileUtils mode in an singleton.
            if (mContext != null) {
                //We don't check grantResult. If user doesn't grant permission, the No files message will be shown
                browseToPath(mCurrentPath);
            }
        }
    }

    private void handleSDCardAction() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                ((SecureStore) requireActivity()).requestAllFilesPermission();
            } else if (Environment.isExternalStorageEmulated()) {
                mCurrentPath = emulatedDirectory;
                browseToPath(mCurrentPath);
            }
        } else {
            // With Android N and above, we need to check and request permissions in runtime
            int permission = ContextCompat.checkSelfPermission(mContext
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission == PackageManager.PERMISSION_GRANTED && mCurrentPath != null) {
                Log.d("onClick", "Permission is granted\n");
                browseToPath(mCurrentPath);
            } else {
                Log.w("onClick", "Permission is denied\n");
                ActivityCompat.requestPermissions(requireActivity()
                        , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        , PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    /**
     * onClick - one of the buttons was pressed
     */
    @SuppressLint("NonConstantResourceId")
    public void onClick(int id) {
        switch (id) {
            case R.id.action_back:
                Log.d("onClick", "action_back");
                upOneLevel();
                break;

            case R.id.action_create_folder:
                Log.d("onClick", "action_create_folder");
                showNewDirUI();
                break;

            case R.id.action_btn_container:
                Log.d("onClick", "action_btn_container");
                FileUtils.getInstance().setMode(FileUtils.MODE_CONTAINER);
                mCurrentPath = FileUtils.CONTAINER_ROOT;
                browseToPath(mCurrentPath);
                break;

            case R.id.action_btn_sdcard:
                Log.d("onClick", "action_btn_sdcard");
                FileUtils.getInstance().setMode(FileUtils.MODE_SDCARD);
                mCurrentPath = FileUtils.SDCARD_ROOT;
                handleSDCardAction();
                break;

            case R.id.action_lock:
                Log.d("onClick", "action_lock");
                remoteLock();
                break;
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
        createSomeDummyFilesOnSDCard();
        if (m_authorized && (path != null)) {
            File file = FileUtils.getInstance().getFileFromPath(path);
            if (file.isDirectory()) {
                mCurrentPath = file.getPath();
                populateList(file.listFiles());
            } else {
                FileUtils.getInstance().openItem(mContext, path);
            }
        }
    }

    private void createSomeDummyFilesOnSDCard() {
        File sdcard;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            sdcard = new File(emulatedDirectory);
        } else {
            sdcard = mContext.getExternalFilesDir(null);
        }
        if (sdcard != null && sdcard.isDirectory()) {
            BaseFileUtils.SDCARD_ROOT = sdcard.getAbsolutePath();
            boolean sampleExists = false;
            File[] files = sdcard.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isFile() && f.getName().equals("sample.txt")) {
                        sampleExists = true;
                    }
                }
            }
            if (!sampleExists) {
                createSampleTextFile(BaseFileUtils.SDCARD_ROOT);
            }

            // Create sample directories if they do not exist
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                createEmulatedDirectory(Environment.DIRECTORY_ALARMS);
                createEmulatedDirectory(Environment.DIRECTORY_DCIM);
                createEmulatedDirectory(Environment.DIRECTORY_DOCUMENTS);
                createEmulatedDirectory(Environment.DIRECTORY_DOWNLOADS);
                createEmulatedDirectory(Environment.DIRECTORY_MOVIES);
                createEmulatedDirectory(Environment.DIRECTORY_MUSIC);
                createEmulatedDirectory(Environment.DIRECTORY_NOTIFICATIONS);
                createEmulatedDirectory(Environment.DIRECTORY_PICTURES);
                createEmulatedDirectory(Environment.DIRECTORY_PODCASTS);
                createEmulatedDirectory(Environment.DIRECTORY_RINGTONES);
            } else {
                mContext.getExternalFilesDir(Environment.DIRECTORY_ALARMS);
                mContext.getExternalFilesDir(Environment.DIRECTORY_DCIM);
                mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                mContext.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
                mContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
                mContext.getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS);
                mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                mContext.getExternalFilesDir(Environment.DIRECTORY_PODCASTS);
                mContext.getExternalFilesDir(Environment.DIRECTORY_RINGTONES);
            }
        }
    }

    private void createEmulatedDirectory(String directory) {
        File dir = new File(emulatedDirectory + "/" + directory);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                Log.e("Directory", "Error creating " + directory + " directory");
            }
        }
    }

    private void createSampleTextFile(String path) {
        File file = new File(path, "sample.txt");
        String text = "This is a sample text file for Secure Store, which can " +
                "be securely copied to the container.";

        try {
            if (file.createNewFile()) {
                FileWriter w = new FileWriter(file);
                w.append(text);
                w.flush();
                w.close();
            }
        } catch (IOException e) {
            Log.e("createSampleTextFile", e.getMessage());
        }
    }


    private void remoteLock() {
        GDAndroid.executeRemoteLock();
    }

    @Override
    protected void deleteItem(String aFilePath) {
        FileUtils.getInstance().deleteItem(aFilePath);
    }

    @Override
    protected void copyToContainer(String aFilePath) {
        FileUtils.getInstance().copyToContainer(aFilePath);
    }

    @Override
    protected int getFileMode() {
        return FileUtils.getInstance().getMode();
    }

    @Override
    protected boolean isDir(String aFilePath) {
        return FileUtils.getInstance().isDir(aFilePath);
    }

    @Override
    protected int getNumberFilesInCurrentPath() {
        File file = FileUtils.getInstance().getFileFromPath(mCurrentPath);
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            return listFiles.length;
        }
        return 0;
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
            Drawable folderIcon = ContextCompat.getDrawable(mContext, R.drawable.fb_folder);
            Drawable fileIcon = (FileUtils.getInstance().getMode() == FileUtils.MODE_SDCARD) ? ContextCompat.getDrawable(mContext, R.drawable.fb_file) :
                    ContextCompat.getDrawable(mContext, R.drawable.fb_file_secure);

            for (String str : folderLst) {
                this.directoryEntries.add(new IconifiedText(str, folderIcon));
            }
            for (String str : fileLst) {
                this.directoryEntries.add(new IconifiedText(str, fileIcon));
            }
        }
        IconifiedTextListAdapter adapter = new IconifiedTextListAdapter(mContext);
        adapter.setListItems(this.directoryEntries);
        setListAdapter(adapter);
        setSelection(0);
    }

    /**
     * showNewDirUI - show some UI which gets a new directory name
     */
    private void showNewDirUI() {
        newFolderAlert = createNewFolderDialog();
        newFolderAlert.show();
        Toast.makeText(mContext.getApplicationContext(), "Hello World",
                Toast.LENGTH_LONG).show();
    }


    @NonNull
    private AlertDialog createNewFolderDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setTitle(R.string.DIR_NAME);
        final GDEditText input = new GDEditText(mContext);
        alert.setView(input);
        alert.setPositiveButton(R.string.OK,
                (dialog, whichButton) -> {
                    String dirName = input.getText().toString();
                    if (dirName.length() > 0) {
                        FileUtils.getInstance().makeNewDir(mCurrentPath,
                                dirName);
                        if (isAdded()) {
                            browseToPath(mCurrentPath);
                        }
                    }
                });
        alert.setNegativeButton("Cancel", null);
        return alert.create();
    }

    @Override
    public void fileReceived(String aFilePath) {
        Runnable r = () -> {
            /*
            When we receive a file we refresh the current UI, in case the receipt of the file
            alters the UI (i.e adds new file or dir into current view)
            */
            browseToPath(mCurrentPath);
        };

        //We need to queue a Runnable to run on UI thread because it will update the UI
        requireActivity().runOnUiThread(r);
    }

    public void refreshUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager() && Environment.isExternalStorageEmulated()) {
                mCurrentPath = emulatedDirectory;
                browseToPath(mCurrentPath);
            }
        }
    }
}