package com.example.gghangura.adminconsole;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.good.gd.GDStateListener;
import com.good.gd.widget.GDTextView;

import java.io.Serializable;
import java.util.Map;

public class ConnectedActivity extends AppCompatActivity implements View.OnClickListener, GDStateListener {

    //textview
    GDTextView statusView;

    //buttons
    Button groupsButton;
    Button applicationsButton;
    Button usersButton;

    //called when the webservices return some data
    ResponseData res = new ResponseData() {
        @Override
        public void returnedData(StringBuffer arg1) {
            Global.getInstance().DismissProgress();
            showIntent(arg1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        statusView = (GDTextView) findViewById(R.id.statusTextView);

        groupsButton = (Button) findViewById(R.id.groupsButton);
        applicationsButton = (Button) findViewById(R.id.applicationsButton);
        usersButton = (Button) findViewById(R.id.usersButton);

        groupsButton.setOnClickListener(this);
        applicationsButton.setOnClickListener(this);
        usersButton.setOnClickListener(this);

        Intent intent = getIntent();
        String message = intent.getStringExtra("Message");

        statusView.setText(message);

        Global.getInstance().con = this;
    }

    //Check what button was clicked and get the response according to that from the server
    @Override
    public void onClick(View v) {
        if (v == groupsButton) {
            String url = Global.getInstance().restUrl;
            url = url.concat("groups");
            Global.getInstance().ShowProgress();
            Global.getInstance().makeRestCall(url,res, Global.restApiIndentifier.with);
            Global.getInstance().gorA = Global.groupOrApplication.group;
        } else {
            if (v == applicationsButton) {
                Global.getInstance().ShowProgress();
                String url = Global.getInstance().restUrl;
                url = url.concat("applications");
                Global.getInstance().makeRestCall(url, res, Global.restApiIndentifier.with);
                Global.getInstance().gorA = Global.groupOrApplication.application;
            } else {
                PopupMenu popup = new PopupMenu(this, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.restApi) {
                            Global.getInstance().gorA = Global.groupOrApplication.nothing;
                            showIntent(new StringBuffer());
                        } else {
                            showSoapintent();
                        }
                        return true;
                    }
                });

                popup.show();
            }
        }
    }

    //show next activity if rest user was selected
    private void showIntent(StringBuffer str) {
        Intent intent = new Intent(this, ResponseActivity.class);
        intent.putExtra("Message" , (Serializable) str);
        startActivity(intent);
    }

    //show next activity if soap user was selected
    private void showSoapintent() {
        Intent intent = new Intent(this, SoapUsers.class);
        startActivity(intent);
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
