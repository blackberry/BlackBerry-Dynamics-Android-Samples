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
import android.widget.AdapterView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.good.gd.GDStateListener;
import com.good.gd.widget.GDSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class ResponseActivity extends AppCompatActivity implements GDSearchView.OnQueryTextListener, AdapterView.OnItemClickListener, GDStateListener {

    ArrayList<String> listdata = new ArrayList<String>();

    ArrayList<Object> restUsers;

    ListView responseListView;
    GDSearchView searchView;

    ResponseData res = new ResponseData() {
        @Override
        public void returnedData(StringBuffer arg1) {
            serializeData(arg1.toString());
        }
    };

    //parse the data or show the search view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);

        Global.getInstance().con = this;

        responseListView = (ListView) findViewById(R.id.responseListView);
        searchView = (GDSearchView) findViewById(R.id.searchView);

        Intent intent = getIntent();
        String message = intent.getStringExtra("Message");
        Global.groupOrApplication identifier = Global.getInstance().gorA;

        if (identifier == Global.groupOrApplication.application || identifier == Global.groupOrApplication.group) {
            serializeData(message,identifier);
            searchView.setVisibility(View.INVISIBLE);
        } else {
            searchView.setVisibility(View.VISIBLE);
            searchView.setOnQueryTextListener(this);
            responseListView.setOnItemClickListener(this);
        }
    }

    //parse data when something is searched
    private void serializeData(String str, Global.groupOrApplication iden) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(str);
        }  catch (JSONException e) {
            Global.getInstance().showAlert("Invalid Data Returned", "Could not convert to JSON object");
            e.printStackTrace();
        }

        JSONArray jsonArray = null;
        if (iden == Global.groupOrApplication.application) {
            try {
                jsonArray = obj.getJSONArray("applications");
            } catch (JSONException e) {
                Global.getInstance().showAlert("Invalid Data Returned", "Could not convert to JSON object");
                e.printStackTrace();
            }

        } else if (iden == Global.groupOrApplication.group){
            try {
                jsonArray = obj.getJSONArray("groups");
            } catch (JSONException e) {
                Global.getInstance().showAlert("Invalid Data Returned", "Could not convert to JSON object");
                e.printStackTrace();
            }


        }
        if (jsonArray != null) {
            for (int i=0;i<jsonArray.length();i++){
                try {
                    JSONObject jsonObj = (JSONObject) jsonArray.get(i);
                    listdata.add((String) jsonObj.get("name"));
                } catch (JSONException e) {
                    Global.getInstance().showAlert("Invalid Data Returned", "Could not convert to JSON object");
                    e.printStackTrace();
                }
            }
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, listdata);
        responseListView.setAdapter(adapter);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                responseListView.setAdapter(adapter);
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    //make the rest api call when the text on the search view is changes
    public boolean onQueryTextChange(String newText) {
        if (!newText.equals("")) {
            String url = Global.getInstance().restUrl;
            url = url.concat("directories/users?includeExistingUsers=true&search=");
            url = url.concat(newText);
            url = url.replace(" ", "%20");
            Global.getInstance().makeRestCall(url, res, Global.restApiIndentifier.with);
        }
        return false;
    }

    //parse data
    private void serializeData(String Str) {
        listdata = new ArrayList<String>();
        JSONObject obj = null;
        try {
            obj = new JSONObject(Str);
        }  catch (JSONException e) {
            Global.getInstance().showAlert("Invalid Data Returned", "Could not convert to JSON object");
            e.printStackTrace();
        }

        JSONArray jsonArray = null;
        try {
            jsonArray = obj.getJSONArray("directoryUsers");
        } catch (JSONException e) {
            Global.getInstance().showAlert("Invalid Data Returned", "Could not convert to JSON object");
            e.printStackTrace();
        }
        restUsers = new ArrayList<Object>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObj = (JSONObject) jsonArray.get(i);
                String lastName = (String) jsonObj.get("lastName");
                if (lastName != null) {
                    RestUser user = new RestUser(jsonObj.get("username"), jsonObj.get("displayName"), jsonObj.get("firstName"), lastName, jsonObj.get("emailAddress"), jsonObj.get("directoryId"));
                    restUsers.add(user);
                }
                listdata.add((String) jsonObj.get("displayName"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listdata);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                responseListView.setAdapter(adapter);

            }
        });

    }

    //show next activity
    private void showIntent(int i) {
        Intent intent = new Intent(this, RestUserDetailActivity.class);
        Global.getInstance().selectedRestUser = (RestUser) restUsers.get(i);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showIntent(position);
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
    protected void onResume() {
        super.onResume();

        Global.getInstance().con = this;
    }
}
