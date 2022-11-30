/* Copyright (c) 2021  BlackBerry Limited.
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
*/

package com.msohm.blackberry.samples.bemsdirectorylookupsample;

import com.good.gd.apache.http.HttpResponse;
import com.good.gd.apache.http.client.methods.HttpGet;
import com.good.gd.apache.http.client.methods.HttpPost;
import com.good.gd.apache.http.entity.StringEntity;
import com.good.gd.apache.http.message.BasicHeader;
import com.good.gd.net.GDHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class GDHttpConnector {

    public GDHttpConnector(){}

    public String doRequest(HttpRequestParams params) throws IOException {
        InputStream stream = null;
        String str;

        try {
            switch (params.getRequestType()) {
                case HttpRequestParams.POST:  stream = makePostRequest(params);
                    break;
                case HttpRequestParams.GET:  stream = makeGetRequest(params);
                    break;
            }

            str = readIt(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return str;
    }

    /**
     * Given a string representation of a URL, sets up a connection and gets
     * an input stream.
     */
    private InputStream makeGetRequest(HttpRequestParams params)
            throws IOException {

        GDHttpClient httpclient = new GDHttpClient();
        final HttpGet request = new HttpGet(params.getUrl());

        BasicHeader[] headers = new BasicHeader[params.getHeaders().size()];
        headers = params.getHeaders().toArray(headers);

        if (headers != null && headers.length > 0) {
            request.setHeaders(headers);
        }

        HttpResponse response = httpclient.execute(request);
        return response.getEntity().getContent();
    }

    /**
     * Given a string representation of a URL, sets up a connection and gets
     * an input stream.
     */
    private InputStream makePostRequest(HttpRequestParams params) throws IOException {
        GDHttpClient httpclient = new GDHttpClient();
        final HttpPost request = new HttpPost(params.getUrl());

        BasicHeader[] headers = new BasicHeader[params.getHeaders().size()];
        headers = params.getHeaders().toArray(headers);

        if (headers != null && headers.length > 0) {
            request.setHeaders(headers);
        }

        StringEntity entity = params.getPostBody();

        if (entity != null) {
            request.setEntity(entity);
        }

        HttpResponse response = httpclient.execute(request);
        return response.getEntity().getContent();

    }

    /** Reads an InputStream and converts it to a String.
     */
    private String readIt(InputStream stream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;

        while ((length = stream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toString();
    }
}
