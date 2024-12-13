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

package com.good.gd.example.securestore.common_lib.utils;

import android.util.Log;

/**
 * App Log Utils - Simple class for debug logging. Can macro out logs or route to new destimation
 */
public class AppLogUtils {

    private final static String LOG_TAG = "SecureStore";

    public static void DEBUG_LOG(String aLogMessage){

        Log.d(LOG_TAG, aLogMessage);

    }

    public static void ERROR_LOG(String aLogMessage){

        Log.e(LOG_TAG, aLogMessage);

    }

}
