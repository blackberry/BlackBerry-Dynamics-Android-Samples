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

import android.content.Intent;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.good.gd.GDStateListener;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class SoapUsers extends AppCompatActivity implements GDStateListener, ListView.OnItemClickListener {

    //called when the data is returned from the soap api
    ResponseData res = new ResponseData() {
        @Override
        public void returnedData(StringBuffer arg1) {

            InputStream stream = new ByteArrayInputStream(arg1.toString().getBytes(StandardCharsets.UTF_8));
            parseXml(stream);
        }
    };

    ArrayList<String> usersArray;
    ArrayList<Element> allData;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soap_users);

        Global.getInstance().con = this;

        listView = (ListView) findViewById(R.id.soapUserListView);

        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Global.getInstance().con = this;

        allData =  new ArrayList<Element>();

        usersArray =  new ArrayList<String>();

        Global.getInstance().ShowProgress();

        Global.getInstance().makeSoapCall(res, Global.soapApiIdentifier.user);
    }

    //parse data
    private void parseXml(InputStream str) {

        try {
            DocumentBuilderFactory dbFactory
                    = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(str);
            doc.getDocumentElement().normalize();
            System.out.println("Root element :"
                    + doc.getDocumentElement().getNodeName());

            NodeList nreturnStatus = doc.getElementsByTagName("ns2:returnStatus");

            Node nStatus = nreturnStatus.item(0);

            if (nStatus.getNodeType() == Node.ELEMENT_NODE) {
                Element statusEle = (Element) nStatus;

                if (statusEle.getElementsByTagName("ns2:code").item(0).getTextContent().equals("SUCCESS")) {
                    NodeList nList = doc.getElementsByTagName("ns2:users");
                    for (int temp = 0; temp < nList.getLength(); temp++) {
                        Node nNode = nList.item(temp);
                        System.out.println("\nCurrent Element :"
                                + doc.getNodeName());
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement = (Element) nNode;
                            usersArray.add(eElement.getElementsByTagName("ns2:displayName")
                                    .item(0)
                                    .getTextContent());
                            allData.add(eElement);
                            System.out.println("Bas Login Name : "
                                    + eElement
                                    .getElementsByTagName("ns2:displayName")
                                    .item(0)
                                    .getTextContent());
                            System.out.println("Bas Login Name : "
                                    + eElement
                                    .getElementsByTagName("ns2:basLoginName")
                                    .item(0)
                                    .getTextContent());

                            NodeList deviceList = eElement.getElementsByTagName("ns2:devices");
                            for (int inttemp = 0; inttemp < deviceList.getLength(); inttemp++) {
                                Node nnNode = deviceList.item(temp);
                                if (nNode.getNodeType() == Node.ELEMENT_NODE){
                                    Element eeElement = (Element) nnNode;
                                    System.out.println("Device Model : "
                                            + eElement
                                            .getElementsByTagName("ns2:model")
                                            .item(0)
                                            .getTextContent());
                                }

                            }
                        }
                    }
                } else  {
                    Global.getInstance().DismissProgress();
                    Global.getInstance().showAlert("Try Again!",statusEle.getElementsByTagName("ns2:message").item(0).getTextContent());
                }
            }
        } catch (Exception e) {
            Global.getInstance().DismissProgress();
            Global.getInstance().showAlert("Try Again!",e.getMessage());
            e.printStackTrace();
        }


        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, usersArray);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Global.getInstance().DismissProgress();
                listView.setAdapter(adapter);

            }
        });
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Global.getInstance().soapUser = allData.get(position);
        Intent intent = new Intent(this, SoapUserDetail.class);
        startActivity(intent);
    }


}
