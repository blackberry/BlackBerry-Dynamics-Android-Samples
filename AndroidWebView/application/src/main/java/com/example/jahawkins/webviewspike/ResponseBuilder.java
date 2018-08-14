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
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.HashMap;

// Kind of an extra class to work around the fact that a WebResourceResponse can't be created
// with a 3xx status code.
class ResponseBuilder {
    private static final String TAG = ResponseBuilder.class.getSimpleName();

    private static final String headerContentLength = "Content-Length";
    private static final String headerCSP = "Content-Security-Policy";

    private static final char onceChars[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

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
    private byte[] injectOnce = null;
    private int injectOnceIndex = -1;
    private final static String onceDelimiter = "\"";
    private final static String oncePrefix = "nonce=" + onceDelimiter;
    public boolean setInjectAsset(String assetFilename) {
        InputStream inputStream = null;
        boolean ok = true;
        if (assetFilename != null &&
            (this.injectAsset == null || !this.injectAsset.equals(assetFilename))
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
            this.injectOnce = null;
            this.injectOnceIndex = -1;
            byte[] bytes = new byte[1024];
            // Assume the injected asset file is encoded in UTF-8.
            Charset charset = Charset.forName("UTF-8");
            StringBuilder beforeOnce = new StringBuilder("");
            int read;
            try {
                read = inputStream.read(bytes);
                while (read >= 0) {
                    if (beforeOnce != null) {
                        beforeOnce.append(new String(bytes, charset));
                        int prefixIndex = beforeOnce.indexOf(oncePrefix);
                        if (prefixIndex >= 0) {
                            int endIndex = beforeOnce.indexOf(
                                onceDelimiter, prefixIndex + oncePrefix.length());
                            if (endIndex < 0) {
                                Log.d(TAG, "Found once prefix at " + prefixIndex +
                                    " but no end delimiter.");
                            }
                            else {
                                this.injectOnceIndex = prefixIndex + oncePrefix.length();
                                this.injectOnce = this.onceValue(
                                    endIndex - this.injectOnceIndex, charset);
                                Log.d(TAG, "Found once prefix at " + prefixIndex +
                                    " and end delimiter at " + endIndex +
                                    logStr(beforeOnce.substring(
                                        prefixIndex, endIndex + onceDelimiter.length())) +
                                    logStr(new String(this.injectOnce, charset)) + " " +
                                    this.injectOnce.length);
                                beforeOnce = null;
                            }
                        }
                    }
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
            this.injectOnce = null;
            this.injectOnceIndex = -1;
        }

        return ok;
    }

    private byte[] onceValue(int length, Charset charset) {
        StringBuilder once = new StringBuilder(length);
        SecureRandom rng = new SecureRandom();
        while (length > 0) {
            once.append(onceChars[rng.nextInt(onceChars.length)]);
            length--;
        }
        return once.toString().getBytes(charset);
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
                        OnceStream injectStream = new OnceStream(
                            this.context.getAssets().open(this.injectAsset),
                            this.injectOnceIndex,
                            this.injectOnce);

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

        this.updateHeaders();

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
                    if (this.injectOnceIndex < 0) {
                        Log.d(TAG, "CSP header has directive" + logStr(directive) +
                            " but there isn't a once value to inject.");
                    }
                    else {
                        endIndex += directive.length();
                        try {
                            final String sourceEnd = new String(this.injectOnce, "UTF-8");
                            this.headers.put(headerCSP,
                                value.substring(0, endIndex) + " 'nonce-" + sourceEnd + "'" +
                                    value.substring(endIndex));
                            Log.d(TAG, "Modified CSP\n" + value + "\n" +
                                this.headers.get(headerCSP));
                        } catch (UnsupportedEncodingException exception) {
                            Log.d(TAG, "Couldn't generate nonce- source for" +
                                logStr(this.injectOnce.toString()) + " " + exception.toString());
                        }
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
