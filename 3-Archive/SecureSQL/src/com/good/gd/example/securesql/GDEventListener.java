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


 /*
 *  This file contains Good Sample Code subject to the Good Dynamics SDK Terms and Conditions.
 *  (c) 2013 Good Technology Corporation. All rights reserved.
 */

package com.good.gd.example.securesql;

import android.content.Context;
import android.content.Intent;

import com.good.gd.GDAppEvent;
import com.good.gd.GDAppEventListener;
import com.good.gd.GDAppEventType;

/**
 * Handles GD events. Notice that an instance of this class is passed to authorize(),
 * which will retain it as the callback.
 */
public class GDEventListener implements GDAppEventListener {

	private Intent _uiIntent = null;
	private Context _context = null;
	private boolean _uiLaunched = false;
	
    /** onGDEvent - handles events from the GD library including authorization
     * and withdrawal of authorization.
     * 
     * @see com.good.gd.GDAppEventListener#onGDEvent(com.good.gd.GDAppEvent)
     */
    public void onGDEvent(GDAppEvent anEvent) {
    	GDAppEventType eventType = anEvent.getEventType();
        if (eventType == GDAppEventType.GDAppEventAuthorized) {
        	if (!_uiLaunched && _uiIntent != null && _context != null) {
        		_context.startActivity(_uiIntent);
        		_uiLaunched = true;
        	}
        }
    }

    /** setUILaunchIntent - specifies the intent that is sent in order to launch the
     * application UI once the application is authorized.
     */
	public void setUILaunchIntent(Intent i, Context ctx) {		
		_uiIntent = i;
		_context = ctx;
	}
}
