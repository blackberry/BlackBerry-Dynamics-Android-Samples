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
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.good.gd.GDAndroid;

public class Settings implements Parcelable {

    public final static String SECURE_SQL_SP = "com.good.gd.example.SecureSQLSharedPreferences";

    private final static String SP_REAUTH_KEY         = "com.good.gd.example.reauthenticate";

    //defaults
    private boolean reauthenticate = false;

    private SharedPreferences sharedPreferences;

    public Settings() {
    }

    public Settings loadFromPreferences() {
        sharedPreferences =
                GDAndroid.getInstance().getGDSharedPreferences(SECURE_SQL_SP, Context.MODE_PRIVATE);

        reauthenticate = sharedPreferences.getBoolean(SP_REAUTH_KEY, reauthenticate);

        return this;
    }

    public void saveToPreferences() {
        sharedPreferences.edit().putBoolean(SP_REAUTH_KEY, reauthenticate).apply();
    }

    public boolean isReauthenticateEnabled() {
        return reauthenticate;
    }

    public void setReauthenticate(boolean reauthenticate) {
        this.reauthenticate = reauthenticate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(reauthenticate ? 1 : 0);
    }

    private Settings(Parcel in) {
        reauthenticate = (in.readInt() == 1);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Settings createFromParcel(Parcel in) {
            return new Settings(in);
        }

        public Settings[] newArray(int size) {
            return new Settings[size];
        }
    };
}
