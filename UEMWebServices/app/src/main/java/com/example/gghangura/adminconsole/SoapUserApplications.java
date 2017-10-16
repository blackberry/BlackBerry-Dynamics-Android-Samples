package com.example.gghangura.adminconsole;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.good.gd.GDStateListener;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class SoapUserApplications extends AppCompatActivity implements GDStateListener {

    ArrayList<String> applicationArray;

    //called when all the applications are returned
    ResponseData res = new ResponseData() {
        @Override
        public void returnedData(StringBuffer arg1) {
            Global.getInstance().DismissProgress();
            InputStream stream = new ByteArrayInputStream(arg1.toString().getBytes(StandardCharsets.UTF_8));
            parseXml(stream);
        }
    };

    ListView applicationListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soap_user_applications);

        Global.getInstance().con = this;

        applicationListView = (ListView) findViewById(R.id.applicationTextView);

        Global.getInstance().ShowProgress();
        Global.getInstance().makeSoapCall(res, Global.soapApiIdentifier.applications);
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

    //parse data
    private void parseXml(InputStream str) {

        applicationArray = new ArrayList<String>();

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
                    NodeList nList = doc.getElementsByTagName("ns2:applications");
                    for (int temp = 0; temp < nList.getLength(); temp++) {
                        Node nNode = nList.item(temp);
                        System.out.println("\nCurrent Element :"
                                + doc.getNodeName());
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement = (Element) nNode;
                            applicationArray.add(eElement.getElementsByTagName("ns2:name")
                                    .item(0)
                                    .getTextContent());

                        }


                    }

                    final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, applicationArray);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Global.getInstance().DismissProgress();
                            applicationListView.setAdapter(adapter);

                        }
                    });


                } else {
                    Global.getInstance().DismissProgress();
                    Global.getInstance().showAlert("Try Again!", statusEle.getElementsByTagName("ns2:message").item(0).getTextContent());
                }
            }
        } catch (Exception e) {
            Global.getInstance().DismissProgress();
            Global.getInstance().showAlert("Try Again!", e.getMessage());
            e.printStackTrace();
        }
    }
}
