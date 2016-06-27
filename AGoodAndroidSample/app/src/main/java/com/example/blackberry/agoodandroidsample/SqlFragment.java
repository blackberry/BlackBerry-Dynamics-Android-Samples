/* Copyright (c) 2016 BlackBerry Ltd.
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

package com.example.blackberry.agoodandroidsample;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SqlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SqlFragment extends Fragment
{

    public SqlFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SQLFragment.
     */
    public static SqlFragment newInstance()
    {
        return new SqlFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_sql, container, false);

        final CheckBox blueBox = (CheckBox) view.findViewById(R.id.checkBlue);
        final CheckBox brownBox = (CheckBox) view.findViewById(R.id.checkBrown);
        final CheckBox greenBox = (CheckBox) view.findViewById(R.id.checkGreen);
        final CheckBox orangeBox = (CheckBox) view.findViewById(R.id.checkOrange);
        final CheckBox pinkBox = (CheckBox) view.findViewById(R.id.checkPink);
        final CheckBox purpleBox = (CheckBox) view.findViewById(R.id.checkPurple);
        final CheckBox redBox = (CheckBox) view.findViewById(R.id.checkRed);
        final CheckBox yellowBox = (CheckBox) view.findViewById(R.id.checkYellow);

        final Button clearButton = (Button) view.findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                blueBox.setChecked(false);
                brownBox.setChecked(false);
                greenBox.setChecked(false);
                orangeBox.setChecked(false);
                pinkBox.setChecked(false);
                purpleBox.setChecked(false);
                redBox.setChecked(false);
                yellowBox.setChecked(false);
            }
        });

        final Button saveButton = (Button) view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                ColorDbHelper dbHelper = new ColorDbHelper(getContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                int size = Constants.COLORS_ALL.length;

                //Update the database for each color.
                for (int count = 0; count < size; count++)
                {
                    switch(Constants.COLORS_ALL[count])
                    {
                        case Constants.COLOR_BLUE:
                            updateDb(db, Constants.COLOR_BLUE, blueBox.isChecked());
                            break;

                        case Constants.COLOR_BROWN:
                            updateDb(db, Constants.COLOR_BROWN, brownBox.isChecked());
                            break;

                        case Constants.COLOR_GREEN:
                            updateDb(db, Constants.COLOR_GREEN, greenBox.isChecked());
                            break;

                        case Constants.COLOR_ORANGE:
                            updateDb(db, Constants.COLOR_ORANGE, orangeBox.isChecked());
                            break;

                        case Constants.COLOR_PINK:
                            updateDb(db, Constants.COLOR_PINK, pinkBox.isChecked());
                            break;

                        case Constants.COLOR_PURPLE:
                            updateDb(db, Constants.COLOR_PURPLE, purpleBox.isChecked());
                            break;

                        case Constants.COLOR_RED:
                            updateDb(db, Constants.COLOR_RED, redBox.isChecked());
                            break;

                        case Constants.COLOR_YELLOW:
                            updateDb(db, Constants.COLOR_YELLOW, yellowBox.isChecked());
                            break;
                    }
                }

                db.close();
            }

            //Update the database with the user's chosen colors.
            private void updateDb(SQLiteDatabase db, int theColor, boolean checked)
            {
                ContentValues values = new ContentValues();

                //Update the is favorite column based on the user's selection.
                if (checked)
                {
                    values.put(ColorContract.ColorTable.COLUMN_NAME_ISFAVORITE, 1);
                }
                else
                {
                    values.put(ColorContract.ColorTable.COLUMN_NAME_ISFAVORITE, 0);
                }

                //Update the row for the current color.
                String selection = ColorContract.ColorTable.COLUMN_NAME_COLOR_ID + " LIKE ?";
                String[] selectionArgs = {String.valueOf(theColor)};

                db.update(ColorContract.ColorTable.TABLE_NAME, values, selection, selectionArgs);
            }
        });

        final Button loadButton = (Button) view.findViewById(R.id.loadButton);
        loadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                ColorDbHelper dbHelper = new ColorDbHelper(getContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                int size = Constants.COLORS_ALL.length;

                //Read the database for each color.
                for (int count = 0; count < size; count++)
                {
                    switch(Constants.COLORS_ALL[count])
                    {
                        case Constants.COLOR_BLUE:
                            blueBox.setChecked(isFavourite(db, Constants.COLOR_BLUE));
                            break;

                        case Constants.COLOR_BROWN:
                            brownBox.setChecked(isFavourite(db, Constants.COLOR_BROWN));
                            break;

                        case Constants.COLOR_GREEN:
                            greenBox.setChecked(isFavourite(db, Constants.COLOR_GREEN));
                            break;

                        case Constants.COLOR_ORANGE:
                            orangeBox.setChecked(isFavourite(db, Constants.COLOR_ORANGE));
                            break;

                        case Constants.COLOR_PINK:
                            pinkBox.setChecked(isFavourite(db, Constants.COLOR_PINK));
                            break;

                        case Constants.COLOR_PURPLE:
                            purpleBox.setChecked(isFavourite(db, Constants.COLOR_PURPLE));
                            break;

                        case Constants.COLOR_RED:
                            redBox.setChecked(isFavourite(db, Constants.COLOR_RED));
                            break;

                        case Constants.COLOR_YELLOW:
                            yellowBox.setChecked(isFavourite(db, Constants.COLOR_YELLOW));
                            break;
                    }
                }

                db.close();
            }

            private boolean isFavourite(SQLiteDatabase db, int theColor)
            {
                //Define the columns we want returned.
                String[] projection = {ColorContract.ColorTable.COLUMN_NAME_COLOR_ID,
                    ColorContract.ColorTable.COLUMN_NAME_ISFAVORITE};

                //Define the columns for the where clause.
                String selection = ColorContract.ColorTable.COLUMN_NAME_COLOR_ID + " = " + theColor;


                Cursor cur = db.query(
                    ColorContract.ColorTable.TABLE_NAME,
                    projection,
                    selection,
                    null,
                    null,
                    null,
                    null
                );

                cur.moveToFirst();

                int fav = cur.getInt(1);
                cur.close();

                if (fav == 0)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
        });

        return view;
    }

}
