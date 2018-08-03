/* Copyright (c) 2018 BlackBerry Ltd.
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

package com.example.jahawkins.webviewspike;


import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import com.good.gd.apache.http.Header;
import com.good.gd.apache.http.HttpEntity;
import com.good.gd.apache.http.HttpResponse;
import com.good.gd.net.GDHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

// Kind of an extra class to work around the fact that a WebResourceResponse can't be created
// with a 3xx status code.
class ResponseBuilder {
    private static final String TAG = ResponseBuilder.class.getSimpleName();

    private static final String headerContentLength = "Content-Length";

    private String logStr(String value) {
        if (value == null) {
            return " null";
        }
        return " \"" + value + "\"";
    }
    private String logURI(Uri value) {
        return logStr(value.toString());
    }

    public int statusCode;
    public String reasonPhrase;
    public HashMap<String, String> headers = null;
    public String contentType = null;
    public String contentEncoding = null;
    public WebInputStream stream = null;
    public GDHttpClient getGDHttpClient() {
        return (this.stream == null) ? null : this.stream.httpClient;
    }

    public Context context = null;

    private String injectAsset = null;
    private long injectLength = -1;
    public boolean setInjectAsset(String assetFilename) {
        InputStream inputStream = null;
        boolean ok = true;
        if (assetFilename != null && (
            this.injectAsset == null ||
                !this.injectAsset.equals(assetFilename))
        ) {
            this.injectAsset = new String(assetFilename);
            try {
                inputStream = this.context.getAssets().open(assetFilename);

            }
            catch (IOException exception) {
                Log.e(TAG, "setInjectAsset(" + assetFilename + ") open failed " +
                    exception.toString() + ".");
                inputStream = null;
                ok = false;
                assetFilename = null;
            }
        }
        if (inputStream != null) {
            this.injectLength = 0;
            byte[] bytes = new byte[1024];
            int read;
            try {
                read = inputStream.read(bytes);
                while (read >= 0) {
                    this.injectLength += read;
                    read = inputStream.read(bytes);
                }
                inputStream.close();
            }
            catch (IOException exception) {
                Log.e(TAG, "setInjectAsset(" + assetFilename + ") read failed " +
                    exception.toString() + ".");
                ok = false;
                assetFilename = null;
            }
        }

        if (assetFilename == null) {
            this.injectAsset = null;
            this.injectLength = -1;
        }

        return ok;
    }

    public ResponseBuilder(int statusCode, String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }
    public ResponseBuilder(int statusCode, Exception exception) {
        this.statusCode = statusCode;
        this.setFromException(exception);
    }
    private void setFromException(Exception exception) {
        this.reasonPhrase = exception.toString();
    }
    public ResponseBuilder() {}

    public ResponseBuilder build(WebResourceRequest resourceRequest,
                                 HttpResponse httpResponse,
                                 GDHttpClient httpClient
    ) {
        String trimmedContentType = this.setFromHttpResponse(httpResponse);

        InputStream stream = null;
        String injectedAsset = null;
        long contentLength = -1;

        final HttpEntity httpEntity = httpResponse.getEntity();
        if (httpEntity != null) {
            contentLength = httpEntity.getContentLength();
            // The next line can throw IOException. It's in the getContent method signature.
            // I don't know in what circumstance that would occur.
            try {
                stream = httpEntity.getContent();
            }
            catch (IOException exception) {
                this.statusCode = 404;
                this.setFromException(exception);
                stream = null;
            }
        }
        if (stream != null) {
            InputStream[] inputStreams = null;
            if (this.injectAsset == null) {
                Log.d(TAG, "Injection switched off" + logURI(resourceRequest.getUrl()) + ".");
            }
            else {
                Log.d(TAG,
                    "Injection conditions statusCode:" + this.statusCode +
                        " isForMain:" + resourceRequest.isForMainFrame() + logStr(this.contentType)
                );
                if ((this.statusCode < 300 || this.statusCode >= 400) &&
                    resourceRequest.isForMainFrame() &&
                    this.contentType != null &&
                    this.contentType.startsWith("text/html"))
                {
                    try {
                        InputStream injectStream = this.context.getAssets().open(this.injectAsset);
                        inputStreams = new InputStream[]{injectStream, stream};
                        injectedAsset = this.injectAsset;
                    }
                    catch (IOException exception) {
                        Log.e(TAG,
                            "Injection failed" + logStr(this.injectAsset) + " " +
                            exception.toString() + ".");
                        inputStreams = null;
                    }
                }
            }
            if (inputStreams == null) {
                inputStreams = new InputStream[]{stream};
            }
            this.stream = new WebInputStream(resourceRequest.getUrl(), httpClient, inputStreams);
        }

        if (injectedAsset == null) {
            Log.d(TAG, "No injected asset" + logURI(resourceRequest.getUrl()));
        }
        else {
            boolean hasKey = this.headers.containsKey(headerContentLength);
            Log.d(TAG,
                "Injected asset" + logStr(injectedAsset) + " length:" + this.injectLength +
                logStr(headerContentLength) + ":" + hasKey + " original:" + contentLength + ".");
            if (contentLength >= 0) {
                if (hasKey) {
                    this.headers.put(headerContentLength,
                        String.format("%d", contentLength + this.injectLength));
                }
                else {
                    Log.e(TAG, "No header" + logStr(headerContentLength) + " in:\n" +
                        this.headers.toString());
                }
            }
        }

        String statusLine = httpResponse.getStatusLine().toString();
        Log.d(TAG, "Response building" + logURI(resourceRequest.getUrl()) + logStr(statusLine) +
            logStr(this.contentType) + logStr(trimmedContentType) + logStr(injectedAsset) +
            logStr(this.contentEncoding) + "\n" + this.headers.toString());

        return this;
    }

    private String setFromHttpResponse(HttpResponse httpResponse) {
        this.statusCode = httpResponse.getStatusLine().getStatusCode();
        this.reasonPhrase = httpResponse.getStatusLine().getReasonPhrase();
        final Header headers[] = httpResponse.getAllHeaders();
        this.headers = new HashMap<String, String>();
        for (Header header : headers) {
            String name = header.getName();
            if (Settings.getInstance().getSetting("stripContentSecurityPolicy") &&
                name.equalsIgnoreCase("Content-Security-Policy")
                ) {
                Log.d(TAG,
                    "Skipping response header" + logStr(name) + logStr(header.getValue()) + ".");
            }
            else {
                this.headers.put(header.getName(), header.getValue());
            }
        }

        String trimmedContentType = null;
        final HttpEntity httpEntity = httpResponse.getEntity();
        if (httpEntity != null) {
            Header contentTypeHeader = httpEntity.getContentType();
            if (contentTypeHeader != null) {
                String contentTypeHeaderValue = contentTypeHeader.getValue();
                // Next part is questionable. It will truncate off any charset or other addition
                // to just the MIME type and subtype. The untruncated content type would still
                // be in the headers.
                int semicolon = contentTypeHeaderValue.indexOf(';');
                if (semicolon >= 0) {
                    trimmedContentType = contentTypeHeaderValue.substring(semicolon);
                    this.contentType = contentTypeHeaderValue.substring(0, semicolon).trim();
                } else {
                    this.contentType = contentTypeHeaderValue;
                }
            }
            Header contentEncodingHeader = httpEntity.getContentEncoding();
            if (contentEncodingHeader != null) {
                this.contentEncoding = contentEncodingHeader.getValue();
            }
        }
        return trimmedContentType;
    }

    public WebResourceResponse toWebResponse() {
        if (this.stream == null) {
            // No stream yet so put in an EOF placeholder.
            this.stream = new WebInputStream();
        }
        return new WebResourceResponse(
            this.contentType, this.contentEncoding, this.statusCode, this.reasonPhrase,
            this.headers, this.stream);
    }
}
