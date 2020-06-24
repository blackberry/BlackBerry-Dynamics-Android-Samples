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

import com.good.gd.apache.http.Header;
import com.good.gd.apache.http.HeaderElement;
import com.good.gd.apache.http.HttpEntity;

import java.util.Arrays;

public class HttpResponseParser {
    public HttpResponseParser() {
    }

    public String parseContentEncoding(Header[] allHeaders, HttpEntity responseEntity) {
        String contentEncoding = null;
        for (int i = 0; i < allHeaders.length; i++) {
            Header header = allHeaders[i];

            if ("content-encoding".equalsIgnoreCase(header.getName())) {
                contentEncoding = header.getValue();
            }
        }

        Header contentEncodingElements = responseEntity.getContentEncoding();
        if (contentEncodingElements != null) {
            HeaderElement[] elements = contentEncodingElements.getElements();
            for (int i = 0; i < elements.length; i++) {
                HeaderElement element = elements[i];
                if (element != null && element.getValue() != null) {
                    if (Arrays.asList("gzip", "deflate", "br").contains(element.getValue().toLowerCase())) {
                        contentEncoding = element.getValue().toLowerCase();
                        break;
                    }
                }
            }
        }



        return contentEncoding;
    }
}