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

import java.util.List;

/** ListUtils - some basic utils to aid with the list
 */
public class ListUtils {
    /** Insert string into list with ASC sorting. Upper case put after lower case.
     */
    public static int insertAsc(List<String> lst, String val) {
        int pos = -1;
        for (int i = 0; i < lst.size(); i++) {
            if (compareString(lst.get(i), val) > 0) {
                pos = i;
                break;
            }
        }

        if (pos != -1) {
            lst.add(pos, val);
        } else {
            lst.add(val);
            pos = lst.size() - 1;
        }

        return pos;
    }

    /** Compare strings with following rules:
     * 1. compareString("g", "g") == 0
     * 2. compareString("g", "G") == -1
     * 3. compareString("g", "h") == -1
     * 4. compareString("g", "H") == -1
     * 5. compareString("h", "g") == 1
     */
    public static int compareString(String str1, String str2) {
        int compVal = str1.compareToIgnoreCase(str2);
        if (compVal != 0) {
            return compVal;
        } else {
            return str2.compareTo(str1);
        }
    }
}
