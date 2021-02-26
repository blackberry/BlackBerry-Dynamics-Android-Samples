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

import android.provider.BaseColumns


class ColorContract {

     /* Inner class that defines the table contents */
        abstract class ColorTable: BaseColumns {
            companion object {
             const val TABLE_NAME = "FavoriteColors"
             const val COLUMN_NAME_COLOR_ID = "colorId"
             const val COLUMN_NAME_ISFAVORITE = "isFavorite"


             const val SQL_CREATE_FAVORITECOLORS = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_NAME_COLOR_ID + " INTEGER PRIMARY KEY," +
             COLUMN_NAME_ISFAVORITE + " INTEGER DEFAULT 0)"

             const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"

             val SQL_POPULATE_DATA = arrayOf("INSERT INTO " + TABLE_NAME + " VALUES (" + Constants.COLOR_BLUE + ", 0);", "INSERT INTO " + TABLE_NAME + " VALUES (" + Constants.COLOR_BROWN + ", 0);", "INSERT INTO " + TABLE_NAME + " VALUES (" + Constants.COLOR_GREEN + ", 0);", "INSERT INTO " + TABLE_NAME + " VALUES (" + Constants.COLOR_ORANGE + ", 0);", "INSERT INTO " + TABLE_NAME + " VALUES (" + Constants.COLOR_PINK + ", 0);", "INSERT INTO " + TABLE_NAME + " VALUES (" + Constants.COLOR_PURPLE + ", 0);", "INSERT INTO " + TABLE_NAME + " VALUES (" + Constants.COLOR_RED + ", 0);", "INSERT INTO " + TABLE_NAME + " VALUES (" + Constants.COLOR_YELLOW + ", 0);")
            }
    }
}