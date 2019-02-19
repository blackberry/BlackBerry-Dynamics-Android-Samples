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

// Cookie table schema copied from a file like this one:
// https://android.googlesource.com/platform/external/chromium/+/ics-mr0/chrome/browser/net/sqlite_persistent_cookie_store.cc
// The license is as follows.
//
// Copyright (c) 2010 The Chromium Authors. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
//    * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above
// copyright notice, this list of conditions and the following disclaimer
// in the documentation and/or other materials provided with the
// distribution.
//    * Neither the name of Google Inc. nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package com.example.jahawkins.webviewspike;

import java.util.Date;

public class DocumentCookieStoreSQL {
    final static String TABLE_NAME = "cookies";
    final static String COLUMNS_NAME_VALUE[] = {"name", "value"};
    final static String CONDITION_HOST_PATH = "host_key = ? AND path = ?";
    final static String CONDITION_SESSION_COOKIE = "has_expires == 0";

    final static String TABLE_DDL =
        "CREATE TABLE " + TABLE_NAME + " (creation_utc INTEGER NOT NULL," +
        "host_key TEXT NOT NULL," +
        "name TEXT NOT NULL," +
        "value TEXT NOT NULL," +
        "path TEXT NOT NULL," +
        "expires_utc INTEGER NOT NULL," +
        "is_secure INTEGER NOT NULL," +
        "is_httponly INTEGER NOT NULL," +
        "last_access_utc INTEGER NOT NULL," +
        "has_expires INTEGER NOT NULL DEFAULT 1," +
        "is_persistent INTEGER NOT NULL DEFAULT 1," +
        "priority INTEGER NOT NULL DEFAULT 1," +
        "encrypted_value BLOB DEFAULT ''," +
        "firstpartyonly INTEGER NOT NULL DEFAULT 0," +
        "UNIQUE (host_key, name, path))";

    final static String SELECT_DUMP =
        "SELECT name, value, has_expires, expires_utc, host_key, path FROM " + TABLE_NAME;

    final static String SELECT_COUNT = "SELECT count(0) FROM " + TABLE_NAME;

    static String conditionExpiry(Date expiryDate) {
        return String.format("has_expires = 1 AND expires_utc < %d", expiryDate.getTime());
    }
}
