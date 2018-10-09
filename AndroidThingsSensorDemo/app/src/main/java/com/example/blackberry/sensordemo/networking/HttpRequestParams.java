/* Copyright (c) 2018  BlackBerry Limited.
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

package com.example.blackberry.sensordemo.networking;

import com.good.gd.apache.http.entity.StringEntity;
import com.good.gd.apache.http.message.BasicHeader;

import java.util.ArrayList;

//A class to hold HTTP request parameters that are passed to the GDHttpConnector.

public class HttpRequestParams
{
    public static final int POST = 2;
    public static final int GET = 4;
    public static final int PUT = 6;


    private String url;  //Contains the URL to be loaded.
    private ArrayList<BasicHeader> headers;  //Contains the headers for the HTTP request.
    private StringEntity postBody;  //Contains the body of HTTP POST requests.
    private int requestType;  //Contains the request type, either POST or GET.


    public HttpRequestParams(String url, ArrayList<BasicHeader> headers,
                             StringEntity postBody, int requestType)
    {
        this.url = url;
        this.headers = headers;
        this.postBody = postBody;
        this.requestType = requestType;
    }

    public StringEntity getPostBody()
    {
        return postBody;
    }

    public void setPostBody(StringEntity postBody)
    {
        this.postBody = postBody;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public ArrayList<BasicHeader> getHeaders()
    {
        return headers;
    }

    public void setHeaders(ArrayList<BasicHeader> headers)
    {
        this.headers = headers;
    }


    public int getRequestType()
    {
        return requestType;
    }

    public void setRequestType(int requestType)
    {
        this.requestType = requestType;
    }

}
