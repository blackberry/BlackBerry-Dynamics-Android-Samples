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

public class AppKineticsHelpers {

	// Static Constants -------------------------------------------------------

	// version of the file transfer service for AppKinetics
	public final static String VERSION = "1.0.0.0";

	// tag for logging purposes
	public final static String LOGTAG = "AppKinetics";
	
	// substring pattern of file name to treat as transferrable
	public final static String FILENAMEPATTERN = "Sample - ";

	// name of Good service for transferring files
	public final static String SERVICENAME = "com.good.gdservice.transfer-file";

	// name of Good service method for transferring files
	public final static String SERVICEMETHODNAME = "transferFile";

	// for notifying about unsupported file types
	public final static String UNSUPPORTFILETYPE = "Cannot display this file type";

	// new line
	public final static String NEWLINE = System.getProperty("line.separator");

	public final static String BRIDGE_APP_PACKAGE_NAME = "com.blackberry.intune.bridge";
}
