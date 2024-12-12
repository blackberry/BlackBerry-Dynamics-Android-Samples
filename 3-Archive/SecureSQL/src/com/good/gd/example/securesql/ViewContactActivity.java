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

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;

import com.good.gd.example.utils.ContactsDBUtils.Contact;
import com.good.gd.example.utils.DbContract;

import java.util.Map;

/**
 * ViewContactActivity - views contact fields. Must be passed a contact ID to view.
 */
public class ViewContactActivity extends AppCompatActivity implements GDStateListener {

    private static final String TAG = ViewContactActivity.class.getSimpleName();

    private long _contactRecId = -1;

    /**
     * onCreate - creates the viewer activity based on the passed contact id
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GDAndroid.getInstance().activityInit(this);

        setContentView(R.layout.view_contact);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        if (i != null) {
            _contactRecId = i.getLongExtra(DbContract.CONTACTS_FIELD_ID, -1);
        }
        if (savedInstanceState != null) {
            _contactRecId = savedInstanceState.getLong(DbContract.CONTACTS_FIELD_ID);
        }

        if (_contactRecId > 0) {
        	Contact c = null;
			Cursor cursor = getContentResolver().query(DbContract.CONTENT_URI, null, DbContract.CONTACTS_FIELD_ID + "=" + _contactRecId, null, null);
			cursor.moveToFirst();
	        if (!cursor.isAfterLast()) {
	            String firstName = cursor.getString(1);
	            String secondName = cursor.getString(2);
	            String phoneNumber = cursor.getString(3);
	            String notes = cursor.getString(4);
	            c = new Contact(firstName, secondName, phoneNumber, notes);
	        }
            ((TextView) findViewById(R.id.firstName)).setText(c.getFirstName());
            ((TextView) findViewById(R.id.secondName)).setText(c.getSecondName());
            ((TextView) findViewById(R.id.phoneNumber)).setText(c.getPhoneNumber());
            ((TextView) findViewById(R.id.notes)).setText(c.getNotes());
        }
    }

    /**
     * onSaveInstanceState - preserves the current id for a rotation
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(DbContract.CONTACTS_FIELD_ID, _contactRecId);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onAuthorized() {
        Log.d(TAG, "onAuthorized()");
    }

    @Override
    public void onLocked() {
        Log.d(TAG, "onLocked()");
    }

    @Override
    public void onWiped() {
        Log.d(TAG, "onWiped()");
    }

    @Override
    public void onUpdateConfig(final Map<String, Object> settings) {
        Log.d(TAG, "onUpdateConfig()");
    }

    @Override
    public void onUpdatePolicy(final Map<String, Object> policyValues) {
        Log.d(TAG, "onUpdatePolicy()");
    }

    @Override
    public void onUpdateServices() {
        Log.d(TAG, "onUpdateServices()");
    }

    @Override
    public void onUpdateEntitlements() {
        Log.d(TAG, "onUpdateEntitlements()");
    }
}
