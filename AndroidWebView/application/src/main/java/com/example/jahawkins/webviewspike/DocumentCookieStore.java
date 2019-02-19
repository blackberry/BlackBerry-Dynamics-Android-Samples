/* Copyright (c) 2018 BlackBerry Ltd.
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

package com.example.jahawkins.webviewspike;

import android.content.ContentValues;
import android.util.Log;
import android.webkit.JavascriptInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.good.gd.database.SQLException;
import com.good.gd.database.sqlite.SQLiteDatabase;
import com.good.gd.database.sqlite.SQLiteStatement;
import com.good.gd.file.File;

import android.database.Cursor;

public class DocumentCookieStore {
    private static final String TAG = DocumentCookieStore.class.getSimpleName();
    private String logStr(String value) {
        if (value == null) {
            return " null";
        }
        return " \"" + value + "\"";
    }

    private static final SimpleDateFormat expiresFormat =
        new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");

    public Boolean dumpExpiringCookies = false;

    private DocumentCookieStore() {}
    private String databasePath = null;
    public DocumentCookieStore(String databasePath) {
        super();
        this.databasePath = new String(databasePath);
        try {
            Date testDate = expiresFormat.parse("Fri, 31 Dec 9999 23:59:59 GMT");
            Log.d(TAG, "Format check" + logStr(expiresFormat.format(testDate)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public class DocumentCookieStoreException extends Exception {

        public DocumentCookieStoreException(String message) {
            super(message);
        }
    }

    private class JavaScriptBridge {
        @JavascriptInterface
        public String getDocumentCookie(String host, String path) {
            return DocumentCookieStore.this.getDocumentCookie(host, path);
        }

        @JavascriptInterface
        public void setDocumentCookie(String cookie, String host, String path) {
            try {
                DocumentCookieStore.this.setDocumentCookie(cookie, host, path);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
    private JavaScriptBridge bridge = null;

    public JavaScriptBridge getBridge() {
        if (this.bridge == null) {
            this.bridge = new DocumentCookieStore.JavaScriptBridge();
        }
        return this.bridge;
    }
    public String getBridgeName() {
        // Must return the same as the name of the var in the JS file in assets.
        return "DocumentCookieStoreBridge";
    }
    public String getBridgeAsset() {
        return "DocumentCookieStore.js";
    }

    public boolean deleteDatabase() throws DocumentCookieStoreException {
        if (this.database != null) {
            this.database.close();
            this.database = null;
        }

        if (Lifecycle.getInstance().getAuthorisationState()) {
            return SQLiteDatabase.deleteDatabase(new File(this.databasePath));
        }
        else {
            throw new DocumentCookieStoreException(
                "Cannot delete database until authorised; file is in the secure store.");
        }
    }

    public int deleteSessionCookies() throws DocumentCookieStoreException {
        if (Lifecycle.getInstance().getAuthorisationState()) {
            return this.expireCookies(true);
        }
        else {
            throw new DocumentCookieStoreException("Cannot delete session document cookies until" +
                " authorised; database is in the secure store.");
        }
    }

    private SQLiteDatabase database = null;
    private SQLiteDatabase getDatabase() throws DocumentCookieStoreException {
        if (this.database == null) {
            if (!Lifecycle.getInstance().getAuthorisationState()) {
                throw new DocumentCookieStoreException("Cannot open database until authorised.");
            }

            this.database = SQLiteDatabase.openOrCreateDatabase(this.databasePath, null);

            Cursor cursor = this.database.rawQuery(
                "select sqlite_version() AS sqlite_version", null);
            if (cursor.moveToNext()) {
                Log.d(TAG, String.format("Opened database at path:\"%s\" SQLite version:\"%s\"",
                    this.databasePath, cursor.getString(0)));
            }
            else {
                Log.e(TAG, "Couldn't retrieve SQLite version.");
            }
            cursor.close();

            SQLiteStatement countStatement = this.getCookieCounter();
            if (countStatement == null) {
                SQLiteStatement createStatement = this.database.compileStatement(
                    DocumentCookieStoreSQL.TABLE_DDL);
                createStatement.execute();
                createStatement.close();
                countStatement = this.getCookieCounter();
            }

            if (countStatement == null) {
                Log.e(TAG, "Couldn't create cookie table.");
            }
            else {
                long cookieCount = countStatement.simpleQueryForLong();
                Log.d(TAG, "Cookie count:" + cookieCount);
                countStatement.close();
            }
        }

        return this.database;
    }
    private SQLiteStatement getCookieCounter() {
        SQLiteStatement statement;
        try {
            statement = this.database.compileStatement(DocumentCookieStoreSQL.SELECT_COUNT);
        }
        catch (SQLException exception) {
            Log.d(TAG, "Couldn't count cookies: " + exception.toString() + ".");
            statement = null;
        }
        return statement;
    }

    public String getDocumentCookie(String host, String path) {
        Log.d(TAG, String.format("getDocumentCookie(%s,%s)", host, path));

        this.expireCookies(false);

        Cursor cursor = null;
        try {
            cursor = this.getDatabase().query(
                false, DocumentCookieStoreSQL.TABLE_NAME, DocumentCookieStoreSQL.COLUMNS_NAME_VALUE,
                DocumentCookieStoreSQL.CONDITION_HOST_PATH, new String[]{host, path},
                null, null, null, null);
        } catch (DocumentCookieStoreException exception) {
            Log.d(TAG, "Returning empty document cookie before authorisation.");
            return "";
        }

        String result[][] = new String[cursor.getCount()][cursor.getColumnCount()];
        for (int rowIndex=0; rowIndex<result.length; rowIndex++) {
            if (!cursor.moveToPosition(rowIndex)) {
                Log.e(TAG, "Failed to moveToPosition(" + rowIndex + "), skipping.");
                continue;
            }
            for (int columnIndex=0; columnIndex<result[rowIndex].length; columnIndex++) {
                result[rowIndex][columnIndex] = cursor.getString(columnIndex);
                if (result[rowIndex][columnIndex] == null) {
                    result[rowIndex][columnIndex] = new String("");
                }
                // There isn't any escaping or encoding here. The only special character is = and it
                // isn't allowed in a key.
                // The split, later, sets a maximum of two items so everything after the first = is
                // the value, even if the value includes a second = sign.
            }
        }

        StringBuilder cookie = new StringBuilder("");
        // It'd be nice to do this with a fancy iterator but this works.
        for (int rowIndex=0; rowIndex<result.length; rowIndex++) {
            if (rowIndex > 0) {
                // https://developer.mozilla.org/en-US/docs/Web/API/Document/cookie
                // The RFC "mandates a single space after each semicolon".
                cookie.append("; ");
            }
            for (int columnIndex = 0; columnIndex < result[rowIndex].length; columnIndex++) {
                if (columnIndex > 0) {
                    cookie.append("=");
                }
                cookie.append(result[rowIndex][columnIndex]);
            }
        }

        return cookie.toString();
    }

    public void setDocumentCookie(String cookie, String host, String path) throws ParseException {
        Log.d(TAG, String.format("setDocumentCookie(%s,%s,%s)", cookie, host, path));

        String pairs[][] = DocumentCookieStore.parsePairs(cookie);
        if (pairs.length == 0) {
            return;
        }
        Date expires = null;
        Boolean isSecure = false;
        for (int index=1; index<pairs.length; index++) {
            if (pairs[index][0].equals("expires")) {
                expires = expiresFormat.parse(pairs[index][1]);
                continue;
            }
            if (pairs[index][0].equals("secure")) {
                isSecure = true;
                continue;
            }
            Log.e(TAG, String.format("Unsupported cookie attribute in setting \"%s\"=\"%s\"",
                pairs[index][0], pairs[index][1]));
        }
        Log.d(TAG, String.format("Parsed cookie expires:%s%s%s secure:%s.",
            expires == null ? "" : "\"",
            expires == null ? "null" : expiresFormat.format(expires),
            expires == null ? "" : "\"",
            isSecure ? "True" : "False"));

        Date creationTime = new Date();
        ContentValues contentValues = new ContentValues();
        contentValues.put("creation_utc", creationTime.getTime());
        contentValues.put("host_key", host);
        contentValues.put("name", pairs[0][0]);
        contentValues.put("value", pairs[0][1]);
        contentValues.put("path", path);
        contentValues.put("expires_utc", expires == null ? 0 : expires.getTime());
        contentValues.put("is_secure", isSecure ? 1 : 0);
        contentValues.put("is_httponly", 0);
        contentValues.put("last_access_utc", creationTime.getTime());
        contentValues.put("has_expires", expires == null ? 0 : 1);
        contentValues.put("is_persistent", expires == null ? 0 : 1);
        contentValues.put("priority", 1);
        contentValues.put("encrypted_value", "");
        contentValues.put("firstpartyonly", 0);

        long inserted;
        try {
            inserted = this.getDatabase().insertWithOnConflict(
                DocumentCookieStoreSQL.TABLE_NAME, null, contentValues,
                SQLiteDatabase.CONFLICT_REPLACE);
        } catch (DocumentCookieStoreException exception) {
            Log.d(TAG, "Not setting document cookie before authorisation.");
            inserted = -1;
        }
        Log.d(TAG, "Cookie " + (inserted == -1 ? "failed to insert" : "inserted OK") +
            ". Content:" + contentValues);
    }
    private static String[][] parsePairs(String cookie) {
        String pairs[] = cookie.split(";");
        String settings[][] = new String[pairs.length][];
        for (int index=0; index<pairs.length; index++) {
            String keyValue[] = pairs[index].split("=", 2);
            // First item in each setting is the cookie name, or an attribute name. Attribute names
            // are converted to lower case.
            // Second item is the value or empty string if there was no = sign.
            settings[index] = new String[]{
                index <= 0 ? keyValue[0].trim() : keyValue[0].trim().toLowerCase(),
                keyValue.length > 1 ? keyValue[1].trim() : new String("")
            };
        }
        return settings;
    }

    private int expireCookies(Boolean session) {
        // Deletes all expired cookies, not just those for the current host and path.

        // Same clause can be used for the dump and the delete.
        String expiryClause = session ?
            DocumentCookieStoreSQL.CONDITION_SESSION_COOKIE :
            DocumentCookieStoreSQL.conditionExpiry(new Date());

        if (this.dumpExpiringCookies) {
            this.dumpCookies();
            this.dumpCookies(expiryClause);
        }

        int deleted = 0;
        try {
            deleted = this.getDatabase().delete(
                DocumentCookieStoreSQL.TABLE_NAME, expiryClause, null);
        } catch (DocumentCookieStoreException exception) {
            Log.d(TAG, "Not expiring document cookies before authorisation.");
            deleted = 0;
        }
        Log.d(TAG, "Expired cookies deleted:" + deleted + ".");

        if (deleted > 0 && this.dumpExpiringCookies) {
            this.dumpCookies(null);
        }
        return deleted;
    }

    private int dumpCookies(String whereClause) {
        String description = (whereClause == null ? "All" : "\"" + whereClause + "\"");
        int count = 0;
        Cursor cursor;
        try {
            cursor = this.getDatabase().rawQuery(DocumentCookieStoreSQL.SELECT_DUMP +
                (whereClause == null ? "" : " WHERE " + whereClause), null);
        } catch (DocumentCookieStoreException exception) {
            description = description + " before authorisation";
            cursor = null;
        }
        if (cursor == null) {
            Log.d(TAG, description + " cursor null.");
        }
        else {
            if (cursor.moveToFirst()) {
                Log.d(TAG, description + " cursor has " + cursor.getCount() + ":");
                while (true) {
                    count++;
                    Boolean expires = (cursor.getInt(2) == 1);
                    Date expiryTime = (expires ? new Date(cursor.getLong(3)) : null);
                    Log.d(TAG, String.format(
                        "[%d] host:\"%s\" path:\"%s\" name:\"%s\" value:\"%s\" expires:%s.",
                        count, cursor.getString(4), cursor.getString(5),
                        cursor.getString(0), cursor.getString(1),
                        expires ? expiresFormat.format(expiryTime) : "Session"));
                    if (!cursor.moveToNext()) {
                        break;
                    }
                }
            } else {
                Log.d(TAG, description + " cursor empty.");
            }
            cursor.close();
        }
        return count;
    }
    private int dumpCookies() {
        return this.dumpCookies(null);
    }
}