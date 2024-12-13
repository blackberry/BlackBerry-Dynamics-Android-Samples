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

package com.good.gd.example.securesql;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.authentication.AuthenticationManager;
import com.good.gd.authentication.ReAuthType;
import com.good.gd.authentication.ReAuthResult;
import com.good.gd.example.utils.DbContract;
import com.good.gd.example.utils.Settings;

/**
 * SecureSQL activity - A list of contacts read from the GD secure database
 * through the com.good.gd.database.sqlite package. A cursor is setup and
 * managed by the activity and editing/deleting is supported.
 */
public class SecureSQL extends SampleAppActivity
                       implements
                            GDStateListener,
                            LoaderManager.LoaderCallbacks<Cursor>,
                            OnClickListener {

    public static final String TAG = "SecureSQL";

    private static final int MENU_ITEM_OPEN_ITEM = 1;
    private static final int MENU_ITEM_DELETE_ITEM = 2;
    private static final int MENU_ITEM_EDIT_ITEM = 3;

    private static final int CONTACTS_LIST_CURSOR = 0;

    private ListView listView;

    private SimpleCursorAdapter mAdapter;

    private TextView tv;

    static private boolean canAddAnother500 = true;
    private ExecutorService executor;

    /**
     * onCreate - sets up the core activity members
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GDAndroid.getInstance().activityInit(this);

        executor = Executors.newSingleThreadExecutor();

        setContentView(R.layout.list_contacts);

        setupAppBar(getString(R.string.app_name));

        tv = findViewById(R.id.itemcount);
        listView = findViewById(R.id.list_view);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent,
                                    final View view, final int position, final long id) {
                viewContact(id);
            }
        });

        mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null,
                new String[]{DbContract.CONTACTS_FIELD_SECONDNAME, DbContract.CONTACTS_FIELD_FIRSTNAME},
                new int[]{android.R.id.text1, android.R.id.text2}, 0);

        listView.setAdapter(mAdapter);
        CancellationSignalUtil.getInstance().update(this);

        ViewGroup bottomBar = findViewById(R.id.action_view_menu);
        ViewGroup mainView = findViewById(R.id.bbd_secure_sql_UI);
        ViewGroup contentView = findViewById(R.id.content_layout);

        adjustViewsIfEdgeToEdgeMode(mainView, bottomBar, contentView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * onCreateContextMenu - populates the mContext menu which is shown after a
     * long press on a row
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, MENU_ITEM_OPEN_ITEM, 0, R.string.MENU_STRING_OPEN);
        menu.add(Menu.NONE, MENU_ITEM_DELETE_ITEM, 1, R.string.MENU_STRING_DELETE);
        menu.add(Menu.NONE, MENU_ITEM_EDIT_ITEM, 1, R.string.MENU_STRING_EDIT);
    }

    /**
     * onContextItemSelected - something on the mContext menu was selected
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        if (info == null) {
            return false;
        }

        switch (item.getItemId()) {
            case MENU_ITEM_OPEN_ITEM:
                viewContact(info.id);
                break;
            case MENU_ITEM_DELETE_ITEM:
                deleteContact(info.id);
                break;
            case MENU_ITEM_EDIT_ITEM:
                editContact(info.id);
                break;
        }
        return true;
    }

    private void executeQuery() {
        CancellationSignalUtil.getInstance().start();
    }

    /**
     * viewContact - takes an id and opens the viewer for that contact
     */
    private void viewContact(long id) {
        if (id > 0) {
            Intent i = new Intent(this, ViewContactActivity.class);
            i.putExtra(DbContract.CONTACTS_FIELD_ID, id);
            startActivity(i);
        }
    }

    /**
     * deleteContact - takes an id and deletes that user
     */
    private void deleteContact(long id) {
        if (id > 0) {
            getContentResolver().delete(DbContract.CONTENT_URI, DbContract.CONTACTS_FIELD_ID + "=" + id, null);
        }
    }

    /**
     * editContact - takes an id and opens the editor for that contact
     */
    private void editContact(long id) {
        if (id > 0) {
            Intent i = new Intent(this, EditContactActivity.class);
            i.putExtra(DbContract.CONTACTS_FIELD_ID, id);
            startActivity(i);
        }
    }

    @Override
    public void onAuthorized() {

        // Initialize the loaderManager which will start the process of loading
        // the Cursor

        LoaderManager.getInstance(this).initLoader(CONTACTS_LIST_CURSOR, null, this);
    }

    private void checkIfThereIsItems() {
        if (listView.getAdapter().getCount() != 0) {
            findViewById(R.id.empty).setVisibility(View.GONE);
            findViewById(R.id.list_view).setVisibility(View.VISIBLE);
            findViewById(R.id.itemcount).setVisibility(View.VISIBLE);
            tv.setText("Count = " + listView.getAdapter().getCount());
        } else {
            findViewById(R.id.itemcount).setVisibility(View.GONE);
            findViewById(R.id.list_view).setVisibility(View.GONE);
            findViewById(R.id.empty).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLocked() {
    }

    @Override
    public void onWiped() {
    }

    @Override
    public void onUpdateConfig(final Map<String, Object> settings) {
    }

    @Override
    public void onUpdatePolicy(final Map<String, Object> policyValues) {
    }

    @Override
    public void onUpdateServices() {
    }

    @Override
    public void onUpdateEntitlements() {
    }

    /*
     * Create CursorLoader which is then used to load cursor off the main thread
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        if (loaderID == CONTACTS_LIST_CURSOR) {
            return (new CursorLoader(this, DbContract.CONTENT_URI, null, null, null, null));
        }
        return null;
    }

    /*
     * Async Cursor loading has now finished so now have a valid Cursor to show
     * in the UI
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (mAdapter != null) {
            mAdapter.changeCursor(cursor);
        }
        checkIfThereIsItems();
    }

    /*
     * When called the Cursor which the loader has been using is now invalid so
     * must release references to it as it is about to be closed
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_add:
                startActivityForResult(new Intent(this, EditContactActivity.class),
                        0);
                break;

            case R.id.action_add_500:
                addManyItemsClicked();
                break;

            case R.id.action_delete_all:
                deleteAllClicked();
                break;

            case R.id.action_heavy_query:
                executeQuery();
                break;

            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
    }

    private void addManyItemsClicked() {
        if (canAddAnother500) {
           LongOperation longOperation = new LongOperation(this);
            longOperation.longOperation();
        } else {
            Toast.makeText(this, "Busy Processing Previous Request. Try after some time", Toast.LENGTH_SHORT).show();
        }
        checkIfThereIsItems();
    }

    private void deleteAllClicked() {
        Settings settings = new Settings().loadFromPreferences();
        if(settings.isReauthenticateEnabled()) {
            reauthToDelete();
        } else {
            handleDeleteAll();
        }
    }

    private void reauthToDelete() {
        BroadcastReceiver reauthReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String reauthToken = AuthenticationManager.getReauthenticationToken(intent);
                ReAuthResult reauthResult = AuthenticationManager.getReauthenticationResult(intent);
                ReAuthType reAuthType = AuthenticationManager.getReauthenticationAuthType(intent);
                Log.i(TAG, "onReceive: token:  " + reauthToken);
                Log.i(TAG, "onReceive: result: " + reauthResult);
                Log.i(TAG, "onReceive: type:   " + reAuthType);

                if(reauthResult == ReAuthResult.Success) {
                    Log.i(TAG, "onReceive: Success");
                    handleDeleteAll();
                } else {
                    Log.w(TAG, "onReceive: Rejected");
                }
                GDAndroid.getInstance().unregisterReceiver(this);
            }
        };
        GDAndroid.getInstance().registerReceiver(reauthReceiver
                , new IntentFilter(AuthenticationManager.GD_RE_AUTHENTICATION_EVENT));

        String title, message;

        title = "Contact Manager";
        message = "To delete the selected contacts you must confirm this destructive action.";

        String token = AuthenticationManager.reauthenticate(title,
                message,
                60,
                30,
                false,
                false);
        Log.i(TAG, "reauthToDelete: token: " + token);
    }

    private void handleDeleteAll() {
        getContentResolver().delete(DbContract.CONTENT_URI, null, null);
    }

    private class LongOperation {
        Handler uiHandler = new Handler(getMainLooper());
        Context mContext;

        public LongOperation(Context c) {
            this.mContext = c;
        }

        void longOperation() {
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    doInBackground();
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onPostOperation();
                        }
                    });
                }
            };
            executor.submit(task);
        }

        private void doInBackground() {
            canAddAnother500 = false;
            populateSampleData(mContext);
        }

        private void onPostOperation() {
            canAddAnother500 = true;
        }
    }

    private void populateSampleData(Context ctx) {
        // do a load of insert operations wrapped by a transaction, bulk inserts are many
        // times faster when done in this way
        try {
            // read the file in assets and split out the names adding to each record
            try {
                String word;
                int numAdded = 0;
                BufferedReader br = new BufferedReader(new InputStreamReader(ctx.getAssets().open("names.txt")));

                while ((word = br.readLine()) != null) {
                    String firstName = null, secondName = null;
                    StringTokenizer tok = new StringTokenizer(word);
                    if (tok.hasMoreTokens()) {
                        firstName = tok.nextToken();
                        if (tok.hasMoreTokens()) {
                            secondName = tok.nextToken();
                        }
                    }

                    ContentValues v = new ContentValues();
                    v.put(DbContract.CONTACTS_FIELD_FIRSTNAME, firstName);
                    v.put(DbContract.CONTACTS_FIELD_SECONDNAME, secondName);
                    v.put(DbContract.CONTACTS_FIELD_PHONENUMBER, "+44012312512" + numAdded++);
                    v.put(DbContract.CONTACTS_FIELD_NOTES, "");
                    getContentResolver().insert(DbContract.CONTENT_URI, v);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        CancellationSignalUtil.getInstance().shutdown();
        executor.shutdown();
        super.onDestroy();
    }
}
