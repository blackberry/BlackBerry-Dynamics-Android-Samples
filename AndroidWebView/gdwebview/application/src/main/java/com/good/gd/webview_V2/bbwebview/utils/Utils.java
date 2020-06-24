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

import android.net.Uri;
import android.util.Log;

import java.util.List;

import static com.good.gd.webview_V2.bbwebview.utils.Utils.Strings.*;

public class Utils {

    static public class Strings {
        static public final boolean isNullOrEmpty(String str) {
            return str == null || str.isEmpty();
        }
    }

    static public class URL{
        static public String encodeUrl(String urlDecoded) {

            try {
                Uri urlObj = Uri.parse(urlDecoded);
                final String scheme = urlObj.getScheme();
                final String schemeSuffix = "://";
                final String domain = urlObj.getAuthority();// port included
                StringBuilder path = new StringBuilder();


                List<String> pathSegments = urlObj.getPathSegments();
                for (String pathSegment : pathSegments) {
                    if(!isNullOrEmpty(pathSegment)){
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
                if(!isNullOrEmpty(queryDecoded)) {

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
                if(!isNullOrEmpty(fragmentDecoded)) {
                    fragment = fragmentDecoded;
                }

                StringBuilder urlBuilder = new StringBuilder();


                urlBuilder.append(scheme)
                        .append(schemeSuffix)
                        .append(domain)
                        .append(path.toString());

                String fragmentPrefix = null;

                if(!isNullOrEmpty(query)){
                    urlBuilder.append(queryPrefix)
                            .append(query);
                    fragmentPrefix = "#";
                } else {
                    fragmentPrefix = "#";
                }

                if(!isNullOrEmpty(fragment)){
                    //urlBuilder.append(fragmentPrefix)
                            //.append(fragment);
                }

                Log.i("Utils.URL", "encodeUrl " + urlBuilder);

                return urlBuilder.toString();


            } catch (Exception e){
                Log.e("Utils.URL", "ERROR encodeUrl " + urlDecoded, e);
            }

            Log.e("Utils.URL", "ERROR encodeUrl couldn't decode: " + urlDecoded);
            return urlDecoded;
        }



    }
}
