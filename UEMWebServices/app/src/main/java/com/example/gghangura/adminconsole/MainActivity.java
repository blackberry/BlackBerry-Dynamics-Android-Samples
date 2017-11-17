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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.ndkproxy.GDStartupController;
import com.good.gd.widget.GDEditText;

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

//Main Activity
public class MainActivity extends AppCompatActivity implements View.OnClickListener, GDStateListener {

    //TextViews
    GDEditText urlText;
    GDEditText providerText;
    GDEditText authText;
    GDEditText tenantText;
    GDEditText userText;
    GDEditText passText;

    String responseFromServer;

    //Button
    Button goButton;

    //Global Url
    String url;

    String serverResponse;

    //Variable to check if the BD has authorized the app or not
    boolean hasStarted = false;

    //Response Handler to get the auth header
    ResponseData res2 = new ResponseData() {
        @Override
        public void returnedData(StringBuffer arg1) {

            Global.getInstance().soapUrl = String.format("%senterprise/admin/util/ws",urlText.getText());
            Global.getInstance().userName = String.valueOf(userText.getText());


            Global.getInstance().restHeader = arg1.toString();

            Global.getInstance().makeSoapCall(res3, Global.soapApiIdentifier.getHeader);


        }
    };

    //Response handler to get the encoded username from soap api
    ResponseData res3 = new ResponseData() {
        @Override
        public void returnedData(StringBuffer arg1) {

            Global.getInstance().DismissProgress();

            InputStream stream = new ByteArrayInputStream(arg1.toString().getBytes(StandardCharsets.UTF_8));
            String encodedUser = parseXml(stream);

            if (encodedUser != null) {
                Global.getInstance().soapAuthToken = String.format("Basic %s",Base64.encodeToString(String.format("%s:%s",encodedUser,passText.getText()).getBytes(), Base64.NO_WRAP));
            }

            saveToGlobal();
            showIntent();
        }
    };

    //Response Handler to check the server is running
    ResponseData res = new ResponseData() {
        @Override
        public void returnedData(StringBuffer arg1) {
            String[] splitArray = arg1.toString().split("\\s+");

            String localurl = url + "util/authorization";

            String pass = String.valueOf(passText.getText());

            Global.getInstance().restHeader = String.format("{\"provider\" : \"%s\", \"username\" : \"%s\", \"password\" : \"%s\"}", providerText.getText(), userText.getText(), Base64.encodeToString(pass.getBytes(),0));

            Global.getInstance().makeRestCall(localurl,res2, Global.restApiIndentifier.getHeader);

            serverResponse = arg1.toString();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Call BD to authorize the app
        GDAndroid.getInstance().activityInit(this);

    }

    //connect the the server
    @Override
    public void onClick(View v) {

        //Showing the progress
        Global.getInstance().ShowProgress();

        //adding '/' at the end of the URL
        if (urlText.getText().charAt(urlText.getText().length() - 1) != '/') {
            urlText.setText(String.format("%s/",urlText.getText()));
        }

        //Making the URL
        String url = String.format("%s%s/api/v1/util/ping", urlText.getText(), tenantText.getText());

        this.url = String.format("%s%s/api/v1/", urlText.getText(), tenantText.getText());

        //Making REST call
        Global.getInstance().makeRestCall(url, res, Global.restApiIndentifier.checkServer);

    }

    //show about activity
    public void showAboutScreen(View v) {
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    //Save the values to Global class
    private void saveToGlobal() {

        String[] values = {String.valueOf(urlText.getText()), String.valueOf(providerText.getText()), String.valueOf(authText.getText()), String.valueOf(tenantText.getText()), String.valueOf(userText.getText()), String.valueOf(passText.getText())};

        Global.getInstance().saveToPreferneces(values);


        Global.getInstance().restUrl = this.url;

        Global.getInstance().soapUrl = String.format("%senterprise/admin/ws",urlText.getText());
    }

    // Show new activity
    private void showIntent() {
        Intent intent = new Intent(MainActivity.this, ConnectedActivity.class);
        intent.putExtra("Message", serverResponse);
        startActivity(intent);

    }

    //parse the xml and get the encoded username
    private String parseXml(InputStream str) {
        String returned = null;

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
                    NodeList nList = doc.getElementsByTagName("ns2:GetEncodedUsernameResponse");

                    Node nNode = nList.item(0);

                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        returned = eElement .getElementsByTagName("ns2:encodedUsername") .item(0) .getTextContent();
                    }

                } else {
                    Global.getInstance().DismissProgress();
                    Global.getInstance().showAlert("Try Again!", statusEle.getElementsByTagName("ns2:message").item(0).getTextContent());
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            Global.getInstance().DismissProgress();
            Global.getInstance().showAlert("Try Again!", e.getMessage());
            e.printStackTrace();
        }
        return returned;
    }

    //Function called when BD authorize the app
    @Override
    public void onAuthorized() {

        //Initialzing the variables

        hasStarted = true;

        urlText = (GDEditText) findViewById(R.id.urlText);
        providerText = (GDEditText) findViewById(R.id.providerText);
        authText = (GDEditText) findViewById(R.id.authText);
        tenantText = (GDEditText) findViewById(R.id.tenantText);
        userText = (GDEditText) findViewById(R.id.userText);
        passText = (GDEditText) findViewById(R.id.passwordText);

        goButton = (Button) findViewById(R.id.gobutton);

        goButton.setOnClickListener(this);

        Global.getInstance().con = this;

        //Getting the values from BD Shared Prefernces and setting them
        String[] values = Global.getInstance().getFromPreferneces();

        urlText.setText(values[0]);
        providerText.setText(values[1]);
        authText.setText(values[2]);
        tenantText.setText(values[3]);
        userText.setText(values[4]);
        passText.setText(values[5]);
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
    protected void onResume() {
        super.onResume();
        if (hasStarted) {
            Global.getInstance().con = this;
        }

    }
}
