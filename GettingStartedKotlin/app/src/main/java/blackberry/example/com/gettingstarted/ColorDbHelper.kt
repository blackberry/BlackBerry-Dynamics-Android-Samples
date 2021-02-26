/* Copyright (c) 2021 BlackBerry Limited.
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

package blackberry.example.com.gettingstarted

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class ColorDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        //Create the table.
        db.execSQL(ColorContract.ColorTable.SQL_CREATE_FAVORITECOLORS)

        //Populate with default color data.
        val numRows = ColorContract.ColorTable.SQL_POPULATE_DATA.size

        for (count in 0 until numRows) {
            db.execSQL(ColorContract.ColorTable.SQL_POPULATE_DATA[count])
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //Not implemented in version 1.
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Colors.db"
    }
}