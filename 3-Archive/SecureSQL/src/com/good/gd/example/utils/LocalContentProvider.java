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

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;

import com.good.gd.database.sqlite.SQLiteDatabase;
import com.good.gd.database.sqlite.SQLiteQueryBuilder;

public class LocalContentProvider extends ContentProvider {

    private ContactsDBUtils dbHelper;

    private static final int CONTACTS = 100;
    private static final int CONTACTS_HEAVY_QUERY = 101;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DbContract.AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, DbContract.CONTACTS_TABLE_NAME, CONTACTS);
        matcher.addURI(authority, DbContract.CONTACTS_LONG_QUERY, CONTACTS_HEAVY_QUERY);
        return matcher;
    }



    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal) {

        Cursor retCursor;

        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)) {
            case CONTACTS: {
                qb.setTables(DbContract.CONTACTS_TABLE_NAME);
                retCursor = qb.query(readableDatabase, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case CONTACTS_HEAVY_QUERY:{
                final String tables = DbContract.CONTACTS_TABLE_NAME + " as a LEFT JOIN " + DbContract.CONTACTS_TABLE_NAME + " as b ON ( 1 = 1)";
                qb.setTables(tables);


                retCursor = qb.query(readableDatabase, projection, selection, selectionArgs, null, null, sortOrder, null, cancellationSignal);

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Context c = getContext();
        if (c != null) {
            ContentResolver cr = c.getContentResolver();
            retCursor.setNotificationUri(cr, uri);
        }

        return retCursor;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sort) {
        return query(uri, projection, selection, selectionArgs, sort, null);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        count = db.delete(DbContract.CONTACTS_TABLE_NAME, where, whereArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public String getType(Uri uri) {

        final int match = uriMatcher.match(uri);

        switch (match) {
            case CONTACTS:
                return DbContract.CONTACTS_CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        Uri result = null;
        // TODO validate the Uri
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowID = db.insert(DbContract.CONTACTS_TABLE_NAME, null,
                initialValues);
        if (rowID > 0) {
            result = ContentUris.withAppendedId(DbContract.CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(result, null);
        }
        return result;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new ContactsDBUtils(context);
        return dbHelper != null;
    }



    @Override
    public int update(Uri uri, ContentValues values, String where,
                      String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        count = db.update(DbContract.CONTACTS_TABLE_NAME, values, where,
                whereArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

}