package com.msohm.blackberry.samples.bemsdirectorylookupsample;

import com.good.gd.apache.http.entity.StringEntity;
import com.good.gd.apache.http.message.BasicHeader;

import java.util.ArrayList;

/**
 * Created by msohm on 11/29/2016.
 */

public class HttpRequestParams
{
    public static final int POST = 2;
    public static final int GET = 4;


    private String url;
    private ArrayList<BasicHeader> headers;
    private StringEntity postBody;
    private int requestType;


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
