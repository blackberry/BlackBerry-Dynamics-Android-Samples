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

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;

import com.good.gd.example.utils.ContactsDBUtils.Contact;
import com.good.gd.example.utils.DbContract;

import java.util.Map;

/**
 * EditContactActivity - used for editing existing contacts and adding new ones.
 * Must be passed the contact ID in order to edit it, otherwise it will add as
 * new.
 */
public class EditContactActivity extends SampleAppActivity implements GDStateListener, OnClickListener {

    private static final String TAG = EditContactActivity.class.getSimpleName();

	private long _contactRecId = -1;

	/**
	 * onCreate - creates an editor based on the passed contact id.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        GDAndroid.getInstance().activityInit(this);

		setContentView(R.layout.edit_contact);

        setupAppBarAndEnabledBackButton(getString(R.string.app_name));

		Intent i = getIntent();
		if (i != null) {
			_contactRecId = i.getLongExtra(DbContract.CONTACTS_FIELD_ID,
					-1);
		}
		if (savedInstanceState != null) {
			_contactRecId = savedInstanceState
					.getLong(DbContract.CONTACTS_FIELD_ID);
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
			((EditText) findViewById(R.id.firstName)).setText(c.getFirstName());
			((EditText) findViewById(R.id.secondName)).setText(c
					.getSecondName());
			((EditText) findViewById(R.id.phoneNumber)).setText(c
					.getPhoneNumber());
			((EditText) findViewById(R.id.notes)).setText(c.getNotes());
		}

		ViewGroup mainView = findViewById(R.id.scroller);
		ViewGroup contentView = findViewById(R.id.content_layout);
		ViewGroup bottomBar = findViewById(R.id.action_view_menu);

		adjustViewsIfEdgeToEdgeMode(mainView, bottomBar, contentView);
	}

	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.action_save:
			actionSave();
			finish();
			break;
		case R.id.action_cancel:
			finish();
			break;
		}
	}

	private void actionSave() {
		String firstName = ((EditText) findViewById(R.id.firstName)).getText()
				.toString();
		String secondName = ((EditText) findViewById(R.id.secondName))
				.getText().toString();
		String phoneNum = ((EditText) findViewById(R.id.phoneNumber)).getText()
				.toString();
		String notes = ((EditText) findViewById(R.id.notes)).getText()
				.toString();

		if ((firstName.trim().length() > 0 || secondName.trim().length() > 0)
				|| (phoneNum.length() > 0 || notes.trim().length() > 0)) {
			ContentValues v = new ContentValues();
	        v.put(DbContract.CONTACTS_FIELD_FIRSTNAME, firstName);
	        v.put(DbContract.CONTACTS_FIELD_SECONDNAME, secondName);
	        v.put(DbContract.CONTACTS_FIELD_PHONENUMBER, phoneNum);
	        v.put(DbContract.CONTACTS_FIELD_NOTES, notes);
			if (_contactRecId > 0) {
				// updating an existing contact
				getContentResolver().update(DbContract.CONTENT_URI, v, DbContract.CONTACTS_FIELD_ID + "= ?", new String[]{Long.toString(_contactRecId)});
			} else {
				// saving a new contact
				getContentResolver().insert(DbContract.CONTENT_URI, v);
			}
		}
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
