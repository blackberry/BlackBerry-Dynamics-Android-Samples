package com.example.gghangura.adminconsole;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;

import com.good.gd.GDStateListener;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Map;

public class SoapUserDetail extends AppCompatActivity implements GDStateListener, ListView.OnItemClickListener, Button.OnClickListener {

    TextView dNameText;
    TextView lNameText;
    TextView eMailText;

    Button getApplications;

    ListView deviceListView;

    ArrayList<String> deviceForListView = new ArrayList<String>();

    ArrayList<Element> allDevices = new ArrayList<Element>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soap_user_detail);

        Global.getInstance().con = this;

        dNameText = (TextView) findViewById(R.id.dNameText);
        lNameText = (TextView) findViewById(R.id.lNameText);
        eMailText = (TextView) findViewById(R.id.eMailText);

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
