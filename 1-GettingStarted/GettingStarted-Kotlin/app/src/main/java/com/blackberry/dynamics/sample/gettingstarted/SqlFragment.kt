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

package com.blackberry.dynamics.sample.gettingstarted


import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox


/**
 * A simple [Fragment] subclass.
 * Use the [SqlFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SqlFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sql, container, false)

        val blueBox = view.findViewById(R.id.checkBlue) as CheckBox
        val brownBox = view.findViewById(R.id.checkBrown) as CheckBox
        val greenBox = view.findViewById(R.id.checkGreen) as CheckBox
        val orangeBox = view.findViewById(R.id.checkOrange) as CheckBox
        val pinkBox = view.findViewById(R.id.checkPink) as CheckBox
        val purpleBox = view.findViewById(R.id.checkPurple) as CheckBox
        val redBox = view.findViewById(R.id.checkRed) as CheckBox
        val yellowBox = view.findViewById(R.id.checkYellow) as CheckBox

        val clearButton = view.findViewById(R.id.clearButton) as Button
        clearButton.setOnClickListener {
            blueBox.isChecked = false
            brownBox.isChecked = false
            greenBox.isChecked = false
            orangeBox.isChecked = false
            pinkBox.isChecked = false
            purpleBox.isChecked = false
            redBox.isChecked = false
            yellowBox.isChecked = false
        }

        val saveButton = view.findViewById(R.id.saveButton) as Button
        saveButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {

                val dbHelper = ColorDbHelper(context!!)
                val db = dbHelper.writableDatabase

                val size = Constants.COLORS_ALL.size

                //Update the database for each color.
                for (count in 0 until size) {
                    when (Constants.COLORS_ALL[count]) {
                        Constants.COLOR_BLUE -> updateDb(db, Constants.COLOR_BLUE, blueBox.isChecked)
                        Constants.COLOR_BROWN -> updateDb(db, Constants.COLOR_BROWN, brownBox.isChecked)
                        Constants.COLOR_GREEN -> updateDb(db, Constants.COLOR_GREEN, greenBox.isChecked)
                        Constants.COLOR_ORANGE -> updateDb(db, Constants.COLOR_ORANGE, orangeBox.isChecked)
                        Constants.COLOR_PINK -> updateDb(db, Constants.COLOR_PINK, pinkBox.isChecked)
                        Constants.COLOR_PURPLE -> updateDb(db, Constants.COLOR_PURPLE, purpleBox.isChecked)
                        Constants.COLOR_RED -> updateDb(db, Constants.COLOR_RED, redBox.isChecked)
                        Constants.COLOR_YELLOW -> updateDb(db, Constants.COLOR_YELLOW, yellowBox.isChecked)
                    }
                }

                db.close()
            }

            //Update the database with the user's chosen colors.
            private fun updateDb(db: SQLiteDatabase, theColor: Int, checked: Boolean) {
                val values = ContentValues()

                //Update the is favorite column based on the user's selection.
                if (checked) {
                    values.put(ColorContract.ColorTable.COLUMN_NAME_ISFAVORITE, 1)
                } else {
                    values.put(ColorContract.ColorTable.COLUMN_NAME_ISFAVORITE, 0)
                }

                //Update the row for the current color.
                val selection = ColorContract.ColorTable.COLUMN_NAME_COLOR_ID + " LIKE ?"
                val selectionArgs = arrayOf(theColor.toString())

                db.update(ColorContract.ColorTable.TABLE_NAME, values, selection, selectionArgs)
            }
        })

        val loadButton = view.findViewById(R.id.loadButton) as Button
        loadButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {

                val dbHelper = ColorDbHelper(context!!)
                val db = dbHelper.writableDatabase

                val size = Constants.COLORS_ALL.size

                //Read the database for each color.
                for (count in 0 until size) {
                    when (Constants.COLORS_ALL[count]) {
                        Constants.COLOR_BLUE -> blueBox.isChecked = isFavourite(db, Constants.COLOR_BLUE)
                        Constants.COLOR_BROWN -> brownBox.isChecked = isFavourite(db, Constants.COLOR_BROWN)
                        Constants.COLOR_GREEN -> greenBox.isChecked = isFavourite(db, Constants.COLOR_GREEN)
                        Constants.COLOR_ORANGE -> orangeBox.isChecked = isFavourite(db, Constants.COLOR_ORANGE)
                        Constants.COLOR_PINK -> pinkBox.isChecked = isFavourite(db, Constants.COLOR_PINK)
                        Constants.COLOR_PURPLE -> purpleBox.isChecked = isFavourite(db, Constants.COLOR_PURPLE)
                        Constants.COLOR_RED -> redBox.isChecked = isFavourite(db, Constants.COLOR_RED)
                        Constants.COLOR_YELLOW -> yellowBox.isChecked = isFavourite(db, Constants.COLOR_YELLOW)
                    }
                }

                db.close()
            }

            private fun isFavourite(db: SQLiteDatabase, theColor: Int): Boolean {
                //Define the columns we want returned.
                val projection = arrayOf(
                    ColorContract.ColorTable.COLUMN_NAME_COLOR_ID,
                    ColorContract.ColorTable.COLUMN_NAME_ISFAVORITE
                )

                //Define the columns for the where clause.
                val selection = ColorContract.ColorTable.COLUMN_NAME_COLOR_ID + " = " + theColor


                val cur = db.query(
                    ColorContract.ColorTable.TABLE_NAME,
                    projection,
                    selection,
                    null, null, null, null
                )

                cur.moveToFirst()

                val fav = cur.getInt(1)
                cur.close()

                return when (fav)  {
                    0 -> false
                    1 -> true
                    else -> false
                }
            }
        })

        return view
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SQLFragment.
         */
        fun newInstance(): SqlFragment {
            return SqlFragment()
        }
    }
}
