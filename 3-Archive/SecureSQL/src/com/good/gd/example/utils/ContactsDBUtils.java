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

package com.good.gd.example.utils;

import android.content.Context;

import com.good.gd.database.sqlite.SQLiteDatabase;
import com.good.gd.database.sqlite.SQLiteOpenHelper;

//Notes: porting from android.database.sqlite requires switching to the GD
//package for the .sqlite package classes


/**
 * ContactDBUtils - provides utility methods for manipulating the contacts database.
 */
public class ContactsDBUtils extends SQLiteOpenHelper {

    public static class Contact {
        public Contact(String firstName, String secondName, String phoneNumber, String notes) {
            _firstName = firstName;
            _secondName = secondName;
            _phoneNumber = phoneNumber;
            _notes = notes;
        }

        public String getFirstName() {
            return _firstName;
        }

        public String getSecondName() {
            return _secondName;
        }

        public String getPhoneNumber() {
            return _phoneNumber;
        }

        public String getNotes() {
            return _notes;
        }

        private String _firstName;
        private String _secondName;
        private String _phoneNumber;
        private String _notes;
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		
            db.execSQL("CREATE TABLE IF NOT EXISTS " + DbContract.CONTACTS_TABLE_NAME + " (" +
            		DbContract.CONTACTS_FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            		DbContract.CONTACTS_FIELD_FIRSTNAME + " TEXT," +
            		DbContract.CONTACTS_FIELD_SECONDNAME + " TEXT," +
            		DbContract.CONTACTS_FIELD_PHONENUMBER + " TEXT," +
            		DbContract.CONTACTS_FIELD_NOTES + " TEXT)");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists"+ DbContract.CONTACTS_TABLE_NAME + ";");
		onCreate(db);
	}
	
	public ContactsDBUtils (Context context) {
		super(context, DbContract.CONTACTS_TABLE_NAME, null, DbContract.CONTACTS_DB_VERSION);
	}
}
