/* Copyright (c) 2017 BlackBerry Ltd.
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

package com.msohm.blackberry.samples.presencedemo;


public class AppConstants {

    //TODO:  Create a random unique value for your subscription
    // Note:  This value should be unique for every subscription.  This application only
    //has a single active subscription at a time, so a single notify key is used.  If your
    //application will have multiple subscriptions it will need a notify key for each subscription.
    public static final String NOTIFY_KEY = "320ac69e-b8c2-11e6-80f5-76304dec7eb7";

    //TODO:  Update the email addresses below to match some of your contacts.
    public static final String CONTACT_ADDRESSES = "\"you@yourDomain.com\", " +
            "\"someoneElse@yourDomain.com\"";


    // JSON Node names
    public static final String TAG_CONTACTS = "contacts";
    public static final String TAG_AVAILABILITY = "availability";
    public static final String TAG_ID = "id";
    public static final String TAG_SEQUENCE = "sequence";

    public static final String TAG_AVAILABILITY_IMAGE = "availabilityImage";

    public static final long TIMER_DELAY = 150000;  //2.5 minutes  Default timeout on BEMS is 3 minutes.  You may wish to make this configurable in your app.

}
