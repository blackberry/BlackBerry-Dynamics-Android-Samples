/* Copyright (c) 2021 BlackBerry Limited.
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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.good.gd.GDStateListener;
import com.good.gd.widget.GDTextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DeviceDetails extends AppCompatActivity implements GDStateListener, View.OnClickListener {

    //called when some data is returned by the webservices and then parse the data to check if the device was deleted or not
    ResponseData res = new ResponseData() {
        @Override
        public void returnedData(final StringBuffer arg1) {
            Handler mainHandler = new Handler(con.getMainLooper());

            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {

                    try {
                        DocumentBuilderFactory dbFactory
                                = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

                        InputStream stream = new ByteArrayInputStream(arg1.toString().getBytes(StandardCharsets.UTF_8));
                        Document doc = dBuilder.parse(stream);
                        doc.getDocumentElement().normalize();
                        System.out.println("Root element :"
                                + doc.getDocumentElement().getNodeName());

                        NodeList nreturnStatus = doc.getElementsByTagName("ns2:returnStatus");

                        Node nStatus = nreturnStatus.item(0);

                        if (nStatus.getNodeType() == Node.ELEMENT_NODE) {
                            Element statusEle = (Element) nStatus;

                            if (statusEle.getElementsByTagName("ns2:code").item(0).getTextContent().equals("SUCCESS")) {
                                new AlertDialog.Builder(con)
                                        .setTitle("Device Deleted")
                                        .setMessage("Awesome")
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Global.getInstance().closeActivity = true;
                                                Global.getInstance().userDevice = null;
                                                DeviceDetails.this.finish();

                                            }
                                        })
                                        .show();
                            } else {
                                Global.getInstance().DismissProgress();

                                Global.getInstance().showAlert("Try Again!", statusEle.getElementsByTagName("ns2:message").item(0).getTextContent());
                            }
                        }

                    } catch (Exception e) {
                        Global.getInstance().DismissProgress();
                        Global.getInstance().showAlert("Try Again!",e.getMessage());
                        e.printStackTrace();
                    }

                }
            };
            mainHandler.post(myRunnable);
        }
    };

    Element device;

    GDTextView modelText;
    GDTextView platformText;
    GDTextView itPolicyText;
    GDTextView imeiText;
    GDTextView lastContactedText;

    Button deleteDevice;

    Context con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_details);

        con = this;

        Global.getInstance().con = this;
        Element device = Global.getInstance().userDevice;

        modelText = (GDTextView) findViewById(R.id.modelText);
        platformText = (GDTextView) findViewById(R.id.platformVersionText);
        itPolicyText = (GDTextView) findViewById(R.id.itPolicyText);
        imeiText = (GDTextView) findViewById(R.id.imeiText);
        lastContactedText = (GDTextView) findViewById(R.id.lastContactText);

        Intent intent = getIntent();
        int position = intent.getIntExtra("position",0);


        deleteDevice = (Button) findViewById(R.id.deleteDevice);
        deleteDevice.setOnClickListener(this);

        if (device.getElementsByTagName("ns2:model").item(0) != null) {
            modelText.setText(device.getElementsByTagName("ns2:model")
                    .item(0)
                    .getTextContent());
        }

        if (device.getElementsByTagName("ns2:platformVersion").item(0) != null) {
            platformText.setText(device.getElementsByTagName("ns2:platformVersion")
                    .item(0)
                    .getTextContent());
        }

        if (device.getElementsByTagName("ns2:itPolicyDateSent").item(0) != null) {
            itPolicyText.setText(device.getElementsByTagName("ns2:itPolicyDateSent")
                    .item(0)
                    .getTextContent());
        }

        if (device.getElementsByTagName("ns2:imei").item(0) != null) {
            imeiText.setText(device.getElementsByTagName("ns2:imei")
                    .item(0)
                    .getTextContent());
        }

        if (device.getElementsByTagName("ns2:lastContactDate").item(0) != null) {
            lastContactedText.setText(device.getElementsByTagName("ns2:lastContactDate")
                    .item(0)
                    .getTextContent());
        }

        Global.getInstance().userDeviceUid = device.getElementsByTagName("ns2:uid").item(0).getTextContent();


    }

    @Override
    public void onAuthorized() {

    }

    @Override
    public void onLocked() {

    }

    @Override
    public void onWiped() {

    }

    @Override
    public void onUpdateConfig(Map<String, Object> map) {

    }

    @Override
    public void onUpdatePolicy(Map<String, Object> map) {

    }

    @Override
    public void onUpdateServices() {

    }

    @Override
    public void onUpdateEntitlements() {

    }

    @Override
    public void onClick(View v) {
        Global.getInstance().ShowProgress();
        Global.getInstance().makeSoapCall(res,Global.soapApiIdentifier.wipe);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Global.getInstance().con = this;
    }
}
