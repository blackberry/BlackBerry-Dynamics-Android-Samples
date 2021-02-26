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

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.good.gd.GDStateListener;
import com.good.gd.widget.GDTextView;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Map;

public class SoapUserDetail extends AppCompatActivity implements GDStateListener, ListView.OnItemClickListener, Button.OnClickListener {

    GDTextView dNameText;
    GDTextView lNameText;
    GDTextView eMailText;

    Button getApplications;

    ListView deviceListView;

    ArrayList<String> deviceForListView = new ArrayList<String>();

    ArrayList<Element> allDevices = new ArrayList<Element>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soap_user_detail);

        Global.getInstance().con = this;

        dNameText = (GDTextView) findViewById(R.id.dNameText);
        lNameText = (GDTextView) findViewById(R.id.lNameText);
        eMailText = (GDTextView) findViewById(R.id.eMailText);

        getApplications = (Button) findViewById(R.id.applicationButton);

        getApplications.setOnClickListener(this);

        deviceListView = (ListView) findViewById(R.id.deviceListView);

        deviceListView.setOnItemClickListener(this);

        showData();
    }

    //parse data
    private void showData() {
        Element ele = Global.getInstance().soapUser;

        if (ele.getElementsByTagName("ns2:displayName")
                .item(0) != null) {
            dNameText.setText(ele.getElementsByTagName("ns2:displayName")
                    .item(0)
                    .getTextContent());
        }

        if (ele.getElementsByTagName("ns2:basLoginName")
                .item(0) != null) {
            lNameText.setText(ele.getElementsByTagName("ns2:basLoginName")
                    .item(0)
                    .getTextContent());
        }

        if (ele.getElementsByTagName("ns2:emailAddresses")
                .item(0) != null) {
            eMailText.setText(ele.getElementsByTagName("ns2:emailAddresses")
                    .item(0)
                    .getTextContent());
        }

        final NodeList deviceList = ele.getElementsByTagName("ns2:devices");

        if (deviceList.getLength() > 0) {


        for (int inttemp = 0; inttemp < deviceList.getLength(); inttemp++) {
            Node nnNode = deviceList.item(inttemp);
            if (nnNode.getNodeType() == Node.ELEMENT_NODE){
                Element eeElement = (Element) nnNode;
                allDevices.add(eeElement);
                deviceForListView.add(ele
                        .getElementsByTagName("ns2:model")
                        .item(inttemp)
                        .getTextContent());
            }

        }
        }

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceForListView);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                deviceListView.setAdapter(adapter);

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
    //show device detail when clicked on a device
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Global.getInstance().userDevice = allDevices.get(position);
        Intent intent = new Intent(this, DeviceDetails.class);
        intent.putExtra("position",position);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Global.getInstance().con = this;

        if (Global.getInstance().closeActivity ) {
            Global.getInstance().closeActivity = false;
            SoapUserDetail.this.finish();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, SoapUserApplications.class);
        startActivity(intent);
    }
}
