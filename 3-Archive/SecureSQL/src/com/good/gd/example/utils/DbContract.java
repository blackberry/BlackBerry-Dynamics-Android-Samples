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

import android.content.ContentResolver;
import android.net.Uri;


public final class DbContract {

    private DbContract() {
    }

    public static final String AUTHORITY = "com.good.gd.example.utils.provider";

    public static final String CONTACTS_DB_NAME = "contacts.db";
    public static final String CONTACTS_TABLE_NAME = "contacts";
    public static final String CONTACTS_FIELD_ID = "_id";
    public static final String CONTACTS_FIELD_FIRSTNAME = "firstName";
    public static final String CONTACTS_FIELD_SECONDNAME = "secondName";
    public static final String CONTACTS_FIELD_PHONENUMBER = "phoneNumber";
    public static final String CONTACTS_FIELD_NOTES = "notes";
    public static final String CONTACTS_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + CONTACTS_TABLE_NAME;
    public static final String CONTACTS_LONG_QUERY = "contactsLongQuery";

    public static final int CONTACTS_DB_VERSION = 1;

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/contacts");
    public static final Uri CONTENT_CONTACTS_HEAVY_QUERY_URI = BASE_CONTENT_URI.buildUpon().appendPath(CONTACTS_LONG_QUERY).build();

    /**
     * The date the message was posted, in milliseconds since the epoch
     * <P>
     * Type: INTEGER (long)
     * </P>
     */
    public static final String CREATED_AT = "createdAt";

    /**
     * The default sort order for this table
     */
    public static final String DEFAULT_SORT_ORDER = "DESC";

}

