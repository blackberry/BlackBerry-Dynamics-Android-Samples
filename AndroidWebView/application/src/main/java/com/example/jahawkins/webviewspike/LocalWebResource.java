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
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class LocalWebResource {
    private static final String TAG = LocalWebResource.class.getSimpleName();

    private final static Charset utf8Charset = Charset.forName("UTF-8");
    private final static String htmlContentType =
        MimeTypeMap.getSingleton().getMimeTypeFromExtension("html");

    public static WebResourceResponse getAsset(WebResourceRequest request, Context context) {
        Uri uri = request.getUrl();
        // Next line should be done with the File interface but it's too cumbersome because it
        // doesn't have a way to get an array into a path.
        // This has the side effect of stripping the leading slash, which is necessary for the asset
        // open(), below, to succeed.
        final String assetPath = TextUtils.join(File.separator, uri.getPathSegments());
        Log.d(TAG, String.format("uri\"%s\" specific\"%s\" fragment\"%s\" path\"%s\" asset:%s",
            uri.toString(), uri.getSchemeSpecificPart(), uri.getFragment(), uri.getPath(), assetPath));

        final String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        // If contentType isn't set, the WebView won't run the JS code. It appears that the
        // MimeTypeMap doesn't map .js to anything, so there's a special case for that.
        String contentType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (contentType == null && extension.equals("js")) {
            contentType = "application/javascript";
        }


        int statusCode = 200;
        String reasonPhrase = "OK";
        InputStream stream = null;
        Exception assetException = null;

        try {
            stream = context.getAssets().open(assetPath);
        } catch (IOException exception) {
            Log.d(TAG, "Asset exception " + exception.toString());
            assetException = exception;
        }

        if (assetException != null) {
            statusCode = 404;
            reasonPhrase = assetException.toString();
            stream = errorPage(contentType, assetException);
        }

        return new WebResourceResponse(contentType, null, statusCode, reasonPhrase, null, stream);
    }

    public static InputStream errorPage(String contentType, Exception exception) {
        if (contentType.equals(htmlContentType)) {
            final String html = String.format(
                "<!DOCTYPE html>\n<html><body><p>Asset&nbsp;Exception: %s.</p></body></html>",
                exception.toString());
            return new ByteArrayInputStream(html.getBytes(utf8Charset));
        }
        else {
            return new InputStream() {
                @Override
                public int read() throws IOException {
                    return -1;
                }
            };
        }
    }

}
