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
package com.good.gd.webview_V2.bbwebview.utils;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.good.gd.apache.http.Header;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class Utils {

    private static final String TAG = "GDWebView-" + Utils.class.getSimpleName();

    private static String[] imageExtensions = {"jpg", "jpeg", "png", "gif", "JPG", "JPEG", "PNG", "GIF"};

    public static boolean isImage(String url){
        for (String extension: imageExtensions){
            if (url.endsWith(extension)){
                return true;
            }
        }
        return false;
    }

    public static void debugLogHeaders(Header[] headers) {
        Log.i(TAG, "debugLogHeaders >>");
        int i = 1;
        for (Header header : headers) {
            Log.i(TAG, "debugLogHeaders - header[" + i++ + "] " + header.getName() + ":" + header.getValue());
        }
        Log.i(TAG, "debugLogHeaders >>");
    }

    public static void debugLogHeaders(Map<String, String> headers) {
        Log.i(TAG, "debugLogHeaders >>");
        int i = 1;
        for (Map.Entry<String, String> header : headers.entrySet()) {
            Log.i(TAG, "debugLogHeaders - header[" + i++ + "] " + header.getKey() + ":" + header.getValue());
        }
        Log.i(TAG, "debugLogHeaders >>");
    }

    public static String encodeUrl(String urlDecoded) {

        try {
            Uri urlObj = Uri.parse(urlDecoded);
            final String scheme = urlObj.getScheme();
            final String schemeSuffix = "://";
            final String domain = urlObj.getAuthority();// port included
            StringBuilder path = new StringBuilder();


            List<String> pathSegments = urlObj.getPathSegments();
            for (String pathSegment : pathSegments) {
                if(!TextUtils.isEmpty(pathSegment)){
                    path.append("/");
                    path.append(Uri.encode(pathSegment));
                }
            }

            if(pathSegments.size() > 0 && urlDecoded.replaceAll("#.*","").endsWith("/")){
                path.append("/");
            }

            final String queryPrefix = "?";
            String queryDecoded = urlObj.getEncodedQuery();

            String query = "";
            if(!TextUtils.isEmpty(queryDecoded)) {

                boolean firstParamAppended = false;
                StringBuilder queryBuilder = new StringBuilder();
                String[] params = queryDecoded.split("&");
                for (int i = 0; i < params.length; i++) {
                    String param = params[i];
                    String[] paramKeyVal = param.split("=");

                    if(!firstParamAppended){
                        firstParamAppended = true;
                    }else {
                        queryBuilder.append("&");
                    }

                    queryBuilder.append(paramKeyVal[0]).append("=");
                    if(paramKeyVal.length == 2) {
                        queryBuilder.append(paramKeyVal[1]);
                    }
                }

                query = queryBuilder.toString();
            }

            String fragmentDecoded = urlObj.getEncodedFragment();
            String fragment = null;
            if(!TextUtils.isEmpty(fragmentDecoded)) {
                fragment = fragmentDecoded;
            }

            StringBuilder urlBuilder = new StringBuilder();


            urlBuilder.append(scheme)
                    .append(schemeSuffix)
                    .append(domain)
                    .append(path.toString());

            String fragmentPrefix = null;

            if(!TextUtils.isEmpty(query)){
                urlBuilder.append(queryPrefix)
                        .append(query);
                fragmentPrefix = "#";
            } else {
                fragmentPrefix = "#";
            }

            if(!TextUtils.isEmpty(fragment)){
                //urlBuilder.append(fragmentPrefix)
                        //.append(fragment);
            }

            Log.i(TAG, "encodeUrl " + urlBuilder);

            return urlBuilder.toString();


        } catch (Exception e){
            Log.e(TAG, "ERROR encodeUrl " + urlDecoded, e);
        }

        Log.e(TAG, "ERROR encodeUrl couldn't decode: " + urlDecoded);
        return urlDecoded;
    }

    public static String getFileContent(int resId, Context context) throws IOException {

        InputStream is = context.getResources().openRawResource(resId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = reader.readLine();
        StringBuilder script = new StringBuilder(line);
        while ((line = reader.readLine()) != null) {
            script.append(line).append("\n");
        }

        reader.close();
        is.close();

        return script.toString();
    }

}
