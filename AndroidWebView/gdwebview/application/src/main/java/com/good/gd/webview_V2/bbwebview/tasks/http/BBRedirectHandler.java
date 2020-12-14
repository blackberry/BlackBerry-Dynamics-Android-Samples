/*
 * Copyright (c) 2020 BlackBerry Limited.
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
package com.good.gd.webview_V2.bbwebview.tasks.http;

import android.util.Log;

import com.good.gd.apache.http.HttpRequest;
import com.good.gd.apache.http.HttpResponse;
import com.good.gd.apache.http.ProtocolException;
import com.good.gd.apache.http.client.RedirectHandler;
import com.good.gd.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import com.good.gd.apache.http.impl.client.DefaultRedirectHandler;
import com.good.gd.apache.http.impl.client.RequestWrapper;
import com.good.gd.apache.http.protocol.ExecutionContext;
import com.good.gd.apache.http.protocol.HttpContext;

import java.net.URI;

public class BBRedirectHandler implements RedirectHandler {

    private final String TAG = "GDWebView-" + BBRedirectHandler.class.getSimpleName() +hashCode();
    private RedirectHandler defaultRedirect = new DefaultRedirectHandler();

        @Override
        public boolean isRedirectRequested(HttpResponse httpResponse, HttpContext httpContext) {

            boolean isRedirectRequested = defaultRedirect.isRedirectRequested(httpResponse, httpContext);
            RequestWrapper requestWrapper = (RequestWrapper) httpContext.getAttribute(ExecutionContext.HTTP_REQUEST);

            Log.i(TAG, "isRedirectRequested, status line: " + httpResponse.getStatusLine() + " result: " + isRedirectRequested);
            Log.i(TAG, "isRedirectRequested, requestUri: " + requestWrapper.getURI());

            return isRedirectRequested;
        }

        @Override
        public URI getLocationURI(HttpResponse httpResponse, HttpContext httpContext) throws
        ProtocolException {

            URI locationURI = defaultRedirect.getLocationURI(httpResponse, httpContext);

            try {

                Log.i(TAG, "getLocationURI, redirect locationURI: " + locationURI);

                String locationWithoutFragmentPart = locationURI.toString().replaceAll("#.*$", "");

                Log.i(TAG, "getLocationURI, redirect locationURI without fragment " + locationWithoutFragmentPart);

                locationURI = URI.create(locationWithoutFragmentPart);
                httpContext.setAttribute("webview.redirect.url",locationURI);

            } catch (Exception e) {
                Log.e(TAG,"getLocationURI, ", e);
                throw new RuntimeException("redirect mess");
            }

            RequestWrapper requestWrapper = (RequestWrapper) httpContext.getAttribute(ExecutionContext.HTTP_REQUEST);
            requestWrapper.removeHeaders("Content-Type");
            requestWrapper.removeHeaders("Origin");

            HttpRequest original = requestWrapper.getOriginal();
            original.removeHeaders("Content-Type");
            original.removeHeaders("Origin");

            String hostPortStrRep = "";
            int port = locationURI.getPort();
            if (port != -1) {
                hostPortStrRep = ":" + port;
            }

            original.setHeader("Host",locationURI.getHost() + hostPortStrRep);

            Log.i(TAG, "getLocationURI, set a new host: " + original.getFirstHeader("Host").getValue());

            if(original instanceof HttpEntityEnclosingRequestBase){
                HttpEntityEnclosingRequestBase postReqOrig = (HttpEntityEnclosingRequestBase) original;
                postReqOrig.setEntity(null);
            }

            return locationURI;
        }
    }
