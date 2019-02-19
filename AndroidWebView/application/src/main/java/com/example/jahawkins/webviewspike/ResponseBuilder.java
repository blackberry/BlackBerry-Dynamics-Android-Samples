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
    private static final String headerCSP = "Content-Security-Policy";

    private String logStr(String value) {
        if (value == null) {
            return " null";
        }
        return " \"" + value + "\"";
    }
    private String logURI(Uri value) {
        return logStr(value.toString());
    }
    private String logStrArray(String values[]) {
        if (values == null) {
            return " null";
        }
        StringBuilder builder = new StringBuilder(" [");
        for (int index = 0; index<values.length; index++) {
            if (index > 0) {
                builder.append(", ");
            }
            builder.append("\"").append(values[index]).append("\"");
        }
        return builder.append("]").toString();
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

    private String injectAssets[] = null;
    JavaScriptAssetStream injectStreams[] = null;

    public void setInjectAssets(String... assetFilenames) {
        if (assetFilenames == null) {
            this.injectAssets = null;
            return;
        }

        this.injectAssets = new String[assetFilenames.length];
        for (int index=0; index<assetFilenames.length; index++) {
            this.injectAssets[index] = new String(assetFilenames[index]);
        }
    }

    public ResponseBuilder(int statusCode, String reasonPhrase) {
        this.statusCode = statusCode;
        // ToDo: Create an HTML page on the fly.
        this.reasonPhrase = reasonPhrase;
    }
    public ResponseBuilder(int statusCode, Exception exception) {
        this.statusCode = statusCode;
        this.setFromException(exception);
    }
    private void setFromException(Exception exception) {
        // ToDo: Create an HTML page from the stack trace.
        this.reasonPhrase = exception.toString();
    }
    public ResponseBuilder() {}

    public ResponseBuilder build(WebResourceRequest resourceRequest,
                                 HttpResponse httpResponse,
                                 GDHttpClient httpClient,
                                 final Context context
    ) {
        String trimmedContentType = this.setFromHttpResponse(httpResponse);

        InputStream stream = null;
        Boolean injectedAssets = false;
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
            if (this.injectAssets == null) {
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
                    this.injectStreams = new JavaScriptAssetStream[this.injectAssets.length];
                    inputStreams = new InputStream[this.injectStreams.length + 1];
                    int inputIndex = 0;
                    for (;inputIndex < this.injectAssets.length; inputIndex++) {
                        this.injectStreams[inputIndex] = new JavaScriptAssetStream(
                            context, this.injectAssets[inputIndex])
                            .setAddDOCTYPE(inputIndex == 0).setAddScriptNOnce(true);
                        inputStreams[inputIndex] = this.injectStreams[inputIndex];
                    }
                    inputStreams[inputIndex] = stream;
                    injectedAssets = true;
                }
                else {
                    this.injectStreams = new JavaScriptAssetStream[0];
                }
            }
            if (inputStreams == null) {
                inputStreams = new InputStream[]{stream};
            }
            this.stream = new WebInputStream(resourceRequest.getUrl(), httpClient, inputStreams);
        }

        this.updateHeaders();

        if (injectedAssets) {
            boolean hasKey = this.headers.containsKey(headerContentLength);
            long injectLength = 0;
            for (JavaScriptAssetStream injectStream : this.injectStreams) {
                try {
                    injectLength += injectStream.getLength();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to get asset stream length for" +
                        logStr(injectStream.getName() + ". " + e.toString()));
                    e.printStackTrace();
                }
            }
            Log.d(TAG,
                "Injected assets" + logStrArray(this.injectAssets) + " length:" + injectLength +
                    logStr(headerContentLength) + ":" + hasKey + " original:" + contentLength + ".");
            if (contentLength >= 0) {
                if (hasKey) {
                    this.headers.put(headerContentLength,
                        String.format("%d", contentLength + injectLength));
                }
                else {
                    Log.e(TAG, "No header" + logStr(headerContentLength) + " in:\n" +
                        this.headers.toString());
                }
            }
        }
        else {
            Log.d(TAG, "No injected asset" + logURI(resourceRequest.getUrl()));
        }

        String statusLine = httpResponse.getStatusLine().toString();
        Log.d(TAG, "Response building" + logURI(resourceRequest.getUrl()) + logStr(statusLine) +
            logStr(this.contentType) + logStr(trimmedContentType) +
            logStrArray(injectedAssets ? this.injectAssets : null) +
            logStr(this.contentEncoding) + "\n" + this.headers.toString());

        return this;
    }

    static final String directive = "script-src";
    private String setFromHttpResponse(HttpResponse httpResponse) {
        this.statusCode = httpResponse.getStatusLine().getStatusCode();
        this.reasonPhrase = httpResponse.getStatusLine().getReasonPhrase();
        final Header headers[] = httpResponse.getAllHeaders();
        this.headers = new HashMap<String, String>();
        for (Header header : headers) {
            this.headers.put(header.getName(), header.getValue());
        }

        String trimmedContentType = null;
        final HttpEntity httpEntity = httpResponse.getEntity();
        if (httpEntity != null) {
            Header contentTypeHeader = httpEntity.getContentType();
            if (contentTypeHeader != null) {
                String contentTypeHeaderValue = contentTypeHeader.getValue();
                // Next part is questionable. It will truncate off any charset or other addition to
                // just the MIME type and subtype. The untruncated content type would still be in
                // the headers.
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

    private void updateHeaders() {
        String value = this.headers.get(headerCSP);
        if (value != null) {
            if (Settings.getInstance().getSetting("stripContentSecurityPolicy")) {
                Log.d(TAG, "Removing response header" + logStr(headerCSP) + " " +
                    logStr(value) + ".");
                this.headers.remove(headerCSP);
            }
            else {
                int endIndex = value.indexOf(directive);
                if (endIndex < 0) {
                    Log.d(TAG, "CSP header" + logStr(value) + " doesn't have directive" +
                        logStr(directive) + ".");
                }
                else {
                    StringBuilder sources = new StringBuilder("");
                    for (JavaScriptAssetStream injectStream : this.injectStreams) {
                        sources.append(String.format(
                            " 'nonce-%s'", injectStream.getOnceValue()));
                    }
                    if (sources.length() <= 0) {
                        Log.d(TAG, "CSP header has directive" + logStr(directive) +
                            " but there isn't a once value to inject.");
                    }
                    else {
                        endIndex += directive.length();
                        this.headers.put(headerCSP,
                            value.substring(0, endIndex) + sources  + value.substring(endIndex));
                        Log.d(TAG, "Modified CSP\n" + value + "\n" + this.headers.get(headerCSP));
                    }
                }
            }
        }

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
