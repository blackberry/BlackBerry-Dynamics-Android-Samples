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

package com.good.gd.example.appkinetics;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.good.gd.GDAndroid;
import com.good.gd.GDServiceProvider;
import com.good.gd.GDServiceType;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * Manages file transfer capable applications
 *
 */
public class FileTransferService {

	// Instance Variables -----------------------------------------------------
	private Vector<GDServiceProvider> appDetails = null;
	String appPackageName = null;
	
	// Constructors -----------------------------------------------------------
	public FileTransferService(Context context) {
		if (null == context) {
			throw new IllegalArgumentException("cannot handle null context!");
		}

        this.appDetails = GDAndroid.getInstance().getServiceProvidersFor(AppKineticsHelpers.SERVICENAME,
                                                                         AppKineticsHelpers.VERSION,
				                                                         GDServiceType.GD_SERVICE_TYPE_APPLICATION);
		this.appPackageName = context.getPackageName();
	}

	// Public Methods -----------------------------------------------------
	/**
	 * getList - get list of file transfer services on this device
	 * 
	 * @return
	 */
	public List<GDServiceProvider> getList() {
		final List<GDServiceProvider> options = new ArrayList<GDServiceProvider>();
        this.appDetails = GDAndroid.getInstance().getServiceProvidersFor(AppKineticsHelpers.SERVICENAME,
                                                                         AppKineticsHelpers.VERSION,
                                                                         GDServiceType.GD_SERVICE_TYPE_APPLICATION);
        
		for (GDServiceProvider detail : this.appDetails) {
			final String servicePackage = detail.getAddress();

			if ( otherApplication(servicePackage)) {
				final String serviceName = detail.getName();
				if (!TextUtils.isEmpty(serviceName)) {
					options.add(detail); 
					if(detail.getIcon() == null) {
						Log.d(AppKineticsHelpers.LOGTAG,
				                "Icon for "+detail.getName()+" is NULL");
					}
					else {
						Log.d(AppKineticsHelpers.LOGTAG,
				                "Icon for "+detail.getName()+" is not NULL");
					}
				}
			}
		}
		return options;
	}

	/**
	 * addressLookup - return the app detail address based on the specified app
	 * name
	 * 
	 * @param name
	 * @return
	 */
	public String addressLookup(String name) {
		if (TextUtils.isEmpty(name) || (null == this.appDetails)) {
			throw new NoSuchElementException(name);
		}

		for (GDServiceProvider detail : this.appDetails) {
			if (name.equals(detail.getName())) {
				return detail.getAddress();
			}
		}
		throw new NoSuchElementException(name);
	}

	// Private Methods --------------------------------------------------------
	/**
	 * otherApplication - checks if string refers to self (this package)
	 * 
	 * @param packageName
	 * @return
	 */
	private boolean otherApplication(final String packageName) {
		return ! this.appPackageName.equalsIgnoreCase(packageName);
	}
}
