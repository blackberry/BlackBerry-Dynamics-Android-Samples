/* Copyright (c) 2017 BlackBerry Ltd.
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
package com.example.gghangura.adminconsole;

import android.os.AsyncTask;
import android.util.Xml;

import com.good.gd.apache.http.client.methods.HttpPost;
import com.good.gd.apache.http.entity.StringEntity;
import com.good.gd.apache.http.message.BasicHeader;
import com.good.gd.net.GDHttpClient;

import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import com.good.gd.apache.http.HttpResponse;
import com.good.gd.apache.http.client.methods.HttpGet;
import com.good.gd.net.GDHttpClient;

/**
 * Created by gghangura on 2017-03-27.
 */

public class SoapTask extends AsyncTask {

    @Override
    //make soap api call
    protected Object doInBackground(Object[] params) {

        StringWriter xmlstr = null;

        if (params[3] == Global.soapApiIdentifier.user) {
            xmlstr = writeUserXml();
        } else if (params[3] == Global.soapApiIdentifier.wipe) {
            xmlstr = writeDeviceDetailXml((String) params[4]);
        } else if (params[3] == Global.soapApiIdentifier.applications)  {
            xmlstr = writeApplicationXml((String) params[4]);
        } else if (params[3] == Global.soapApiIdentifier.getHeader)  {
            xmlstr = writeEncodedUsernameXml((String) params[4]);
        }

        GDHttpClient httpclient = new GDHttpClient();
        final HttpPost request = new HttpPost((String) params[0]);


        BasicHeader head1 = new BasicHeader("Accept-Encoding","Accept-Encoding");
        BasicHeader head3 = new BasicHeader("Content-Type","text/xml; charset=utf-8");

        BasicHeader[] headers = {head1,head3};

        if (params[3] != Global.soapApiIdentifier.getHeader) {
            BasicHeader head2 = new BasicHeader("Authorization",(String) params[2]);
            headers = new BasicHeader[]{head1, head2, head3};
        }

            if (headers != null && headers.length > 0)
        {
            request.setHeaders(headers);
        }

        StringEntity entity = null;
        try {
            entity = new StringEntity(xmlstr.toString());


            if (entity != null)
            {
                request.setEntity(entity);
            }

            HttpResponse response = httpclient.execute(request);

        InputStream stream = null;
            stream = response.getEntity().getContent();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;


            while ((length = stream.read(buffer)) != -1)
            {
                baos.write(buffer, 0, length);
            }

            ResponseData res = (ResponseData) params[1];

            res.returnedData(new StringBuffer(baos.toString()));
        } catch (Exception e) {
            Global.getInstance().DismissProgress();
            Global.getInstance().showAlert("Try Again!",e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    //xml to get all users
    private StringWriter writeUserXml(){
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("","soapenv:Envelope");
            serializer.attribute("","xmlns:adm","http://ws.rim.com/enterprise/admin");
            serializer.attribute("","xmlns:soapenv","http://schemas.xmlsoap.org/soap/envelope/");
            serializer.startTag("","soapenv:Header");
            serializer.endTag("","soapenv:Header");
            serializer.startTag("","soapenv:Body");
            serializer.startTag("","adm:GetUsersRequest");
            serializer.startTag("","adm:metadata");
            serializer.startTag("","adm:locale");
            serializer.text("en_US");
            serializer.endTag("","adm:locale");
            serializer.startTag("","adm:clientVersion");
            serializer.text("12");
            serializer.endTag("","adm:clientVersion");
            serializer.startTag("","adm:organizationUid");
            serializer.text("0");
            serializer.endTag("","adm:organizationUid");
            serializer.endTag("","adm:metadata");
            serializer.startTag("","adm:sortBy");
            serializer.startTag("","adm:DISPLAY_NAME");
            serializer.text("true");
            serializer.endTag("","adm:DISPLAY_NAME");
            serializer.endTag("","adm:sortBy");
            serializer.startTag("","sortAscedning");
            serializer.text("true");
            serializer.endTag("","sortAscedning");
            serializer.endTag("","adm:GetUsersRequest");
            serializer.endTag("","soapenv:Body");
            serializer.endTag("","soapenv:Envelope");
            serializer.endDocument();
            return writer;
        } catch (Exception e) {
            Global.getInstance().DismissProgress();
            Global.getInstance().showAlert("Try Again!", String.valueOf(e.getCause()));
            return null;
        }
    }

    //xml to get the device details
    private StringWriter writeDeviceDetailXml(String deviceUID){
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("","soapenv:Envelope");
            serializer.attribute("","xmlns:adm","http://ws.rim.com/enterprise/admin");
            serializer.attribute("","xmlns:soapenv","http://schemas.xmlsoap.org/soap/envelope/");
            serializer.startTag("","soapenv:Header");
            serializer.endTag("","soapenv:Header");
            serializer.startTag("","soapenv:Body");
            serializer.startTag("","adm:SetDevicesWipeRequest");
            serializer.startTag("","adm:metadata");
            serializer.startTag("","adm:locale");
            serializer.text("en_US");
            serializer.endTag("","adm:locale");
            serializer.startTag("","adm:clientVersion");
            serializer.text("12");
            serializer.endTag("","adm:clientVersion");
            serializer.startTag("","adm:organizationUid");
            serializer.text("0");
            serializer.endTag("","adm:organizationUid");
            serializer.endTag("","adm:metadata");
            serializer.startTag("","adm:devices");
            serializer.startTag("","adm:uid");
            serializer.text(deviceUID);
            serializer.endTag("","adm:uid");
            serializer.endTag("","adm:devices");
            serializer.startTag("","adm:organizationWipeOnly");
            serializer.text("true");
            serializer.endTag("","adm:organizationWipeOnly");
            serializer.startTag("","adm:offboardingType");
            serializer.startTag("","adm:DISABLE_AND_REMOVE_USER_STATE");
            serializer.text("true");
            serializer.endTag("","adm:DISABLE_AND_REMOVE_USER_STATE");
            serializer.endTag("","adm:offboardingType");
            serializer.endTag("","adm:SetDevicesWipeRequest");
            serializer.endTag("","soapenv:Body");
            serializer.endTag("","soapenv:Envelope");
            serializer.endDocument();
            return writer;
        } catch (Exception e) {
            Global.getInstance().DismissProgress();
            Global.getInstance().showAlert("Try Again!",e.getMessage());
            return null;
        }
    }

    //xml to get the applications according to a user
    private StringWriter writeApplicationXml(String userUid){
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("","soapenv:Envelope");
            serializer.attribute("","xmlns:adm","http://ws.rim.com/enterprise/admin");
            serializer.attribute("","xmlns:soapenv","http://schemas.xmlsoap.org/soap/envelope/");
            serializer.startTag("","soapenv:Header");
            serializer.endTag("","soapenv:Header");
            serializer.startTag("","soapenv:Body");
            serializer.startTag("","adm:GetUsersDetailRequest");
            serializer.startTag("","adm:metadata");
            serializer.startTag("","adm:locale");
            serializer.text("en_US");
            serializer.endTag("","adm:locale");
            serializer.startTag("","adm:clientVersion");
            serializer.text("12");
            serializer.endTag("","adm:clientVersion");
            serializer.startTag("","adm:organizationUid");
            serializer.text("0");
            serializer.endTag("","adm:organizationUid");
            serializer.endTag("","adm:metadata");
            serializer.startTag("","adm:users");
            serializer.startTag("","adm:uid");
            serializer.text(userUid);
            serializer.endTag("","adm:uid");
            serializer.endTag("","adm:users");
            serializer.startTag("","adm:loadApplications");
            serializer.text("true");
            serializer.endTag("","adm:loadApplications");
            serializer.endTag("","adm:GetUsersDetailRequest");
            serializer.endTag("","soapenv:Body");
            serializer.endTag("","soapenv:Envelope");
            serializer.endDocument();

            return writer;
        } catch (Exception e) {
            Global.getInstance().DismissProgress();
            Global.getInstance().showAlert("Try Again!",e.getMessage());
            return null;
        }
    }

    //xml to get the encoded username
    private StringWriter writeEncodedUsernameXml(String ad){
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("","soapenv:Envelope");
            serializer.attribute("","xmlns:adm","http://ws.rim.com/enterprise/admin");
            serializer.attribute("","xmlns:soapenv","http://schemas.xmlsoap.org/soap/envelope/");
            serializer.startTag("","soapenv:Header");
            serializer.endTag("","soapenv:Header");
            serializer.startTag("","soapenv:Body");
            serializer.startTag("","adm:GetEncodedUsernameRequest");
            serializer.startTag("","adm:metadata");
            serializer.startTag("","adm:locale");
            serializer.text("en_US");
            serializer.endTag("","adm:locale");
            serializer.startTag("","adm:clientVersion");
            serializer.text("12");
            serializer.endTag("","adm:clientVersion");
            serializer.startTag("","adm:organizationUid");
            serializer.text("0");
            serializer.endTag("","adm:organizationUid");
            serializer.endTag("","adm:metadata");
            serializer.startTag("","adm:username");
            serializer.text(ad);
            serializer.endTag("","adm:username");
            serializer.startTag("","adm:authenticator");
            serializer.startTag("","adm:uid");
            serializer.text("BlackBerry Administration Service");
            serializer.endTag("","adm:uid");
            serializer.startTag("","adm:authenticatorType");
            serializer.startTag("","adm:INTERNAL");
            serializer.text("true");
            serializer.endTag("","adm:INTERNAL");
            serializer.endTag("","adm:authenticatorType");
            serializer.startTag("","adm:name");
            serializer.text("BlackBerry Administration Service");
            serializer.endTag("","adm:name");
            serializer.endTag("","adm:authenticator");
            serializer.startTag("","adm:credentialType");
            serializer.startTag("","adm:PASSWORD");
            serializer.text("true");
            serializer.endTag("","adm:PASSWORD");
            serializer.endTag("","adm:credentialType");
            serializer.endTag("","adm:GetEncodedUsernameRequest");
            serializer.endTag("","soapenv:Body");
            serializer.endTag("","soapenv:Envelope");
            serializer.endDocument();

            return writer;
        } catch (Exception e) {
            Global.getInstance().DismissProgress();
            Global.getInstance().showAlert("Try Again!",e.getMessage());
            return null;
        }
    }
}

