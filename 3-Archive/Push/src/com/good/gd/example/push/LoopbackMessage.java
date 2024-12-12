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

package com.good.gd.example.push;

import java.net.URI;

import com.good.gd.apache.http.client.HttpClient;
import com.good.gd.apache.http.client.methods.HttpPost;
import com.good.gd.apache.http.entity.StringEntity;
import com.good.gd.apache.http.impl.client.DefaultHttpClient;

/** 
 * LoopbackMessage - this class allows a message to be sent to the GNP service which simulates
 * what an application server needs to do in order to send a push message to a client device. This 
 * role will in practice be taken by an application server, this class exists merely for the purpose
 * of demonstration.
 */
class LoopbackMessage {

	public synchronized static void send(final String message, final String token, final String nocServerURL) {
		new Thread() {
			public void run() {
		        try {
		            HttpClient httpclient = new DefaultHttpClient();
		        	HttpPost request = new HttpPost();
		        	final String gnpHost = nocServerURL;
		        	
		        	// setup the request URL, headers and body
		        	request.setURI(new URI("https://"+gnpHost+"/GNP1.0?method=notify"));
		        	request.setHeader("X-Good-GNP-Token", token);
		        	request.setEntity(new StringEntity(message));
		        	
		        	// send it!
		        	httpclient.execute(request);
		        } catch (Exception exception) {
		            exception.printStackTrace();
		        }				
			}
		}.start();
    }
}

