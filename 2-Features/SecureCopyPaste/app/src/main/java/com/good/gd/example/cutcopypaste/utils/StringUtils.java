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

package com.good.gd.example.cutcopypaste.utils;

import java.util.Random;

public class StringUtils {

    /**
     *
     * @param length of the string to generate
     * @param chars to choose from to generate the string
     * @return random string containing passed <code>chars<code/> in random order,<br/>
     * <code>""</code>(empty string) if <code>length <= 0<code/> or if <code>chars<code/> argument is <code>null</code> or empty
     */
    public static String randomString(int length,String chars){

        if (length <= 0 || chars == null || chars.isEmpty()) {
            return "";
        }

        final char[] randomChars = new char[length];
        final Random rand = new Random();

        for (int nextChar = 0;nextChar < length;nextChar++) {

            int randomCharIndex = rand.nextInt(chars.length());

            randomChars[nextChar] = chars.charAt(randomCharIndex);
        }
        return new String(randomChars);
    }
}
