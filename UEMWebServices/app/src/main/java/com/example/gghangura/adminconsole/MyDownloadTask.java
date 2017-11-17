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

import com.good.gd.apache.http.HttpEntity;
import com.good.gd.apache.http.HttpResponse;
import com.good.gd.apache.http.client.methods.HttpGet;
import com.good.gd.apache.http.client.methods.HttpPost;
import com.good.gd.apache.http.entity.ByteArrayEntity;
import com.good.gd.apache.http.message.BasicHeader;
import com.good.gd.net.GDHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by gghangura on 2017-03-22.
 */

public class MyDownloadTask extends AsyncTask {

    @Override
    //make Rest call
    protected Object doInBackground(Object[] params) {

        Global.restApiIndentifier iden = (Global.restApiIndentifier) params[3];

        GDHttpClient httpclient = new GDHttpClient();
        HttpPost requestPost = null;
        HttpGet requestGet = null;

        if (iden == Global.restApiIndentifier.getHeader) {
            requestPost = new HttpPost((String) params[0]);
        } else {
            requestGet = new HttpGet((String) params[0]);
        }

        BasicHeader head;
        BasicHeader[] headers = new BasicHeader[0];

        if (iden == Global.restApiIndentifier.with) {
            head = new BasicHeader("Authorization", (String) params[2]);
            headers = new BasicHeader[]{head};
        } else if (iden == Global.restApiIndentifier.checkServer) {

        } else {
            String xml = (String) params[2];

            HttpEntity entity = new ByteArrayEntity(xml.getBytes());
            head = new BasicHeader("Content-Type", "application/vnd.blackberry.authorizationrequest-v1+json");
            headers = new BasicHeader[]{head};
            requestPost.setEntity(entity);
        }

        if (iden == Global.restApiIndentifier.with) {
            requestGet.setHeaders(headers);
        } else if (iden == Global.restApiIndentifier.checkServer) {

        } else {
            requestPost.setHeaders(headers);
        }

        try {

            HttpResponse response;

            if (iden == Global.restApiIndentifier.getHeader) {
                response = httpclient.execute(requestPost);
            } else {
                response = httpclient.execute(requestGet);
            }


            InputStream stream = null;
            stream = response.getEntity().getContent();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;


            while ((length = stream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }

            ResponseData res = (ResponseData) params[1];

            res.returnedData(new StringBuffer(baos.toString()));
        } catch (Exception e) {
            Global.getInstance().DismissProgress();
            Global.getInstance().showAlert("Try Again!", e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    protected void onPreExecute() {
        //display progress dialog.

    }

    protected void onPostExecute(Void result) {
        // dismiss progress dialog and update ui
    }
}

