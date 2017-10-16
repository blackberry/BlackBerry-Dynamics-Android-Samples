package com.example.gghangura.adminconsole;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.good.gd.GDStateListener;

import java.util.Map;

public class RestUserDetailActivity extends AppCompatActivity implements GDStateListener {

    TextView dNameText;
    TextView fNameText;
    TextView lNameText;
    TextView uNameText;
    TextView eMailText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restuserdetail);

        dNameText = (TextView) findViewById(R.id.dNameText);
        fNameText = (TextView) findViewById(R.id.fNameText);
        lNameText = (TextView) findViewById(R.id.lNameText);
        uNameText = (TextView) findViewById(R.id.uNameText);
        eMailText = (TextView) findViewById(R.id.eMailText);

        RestUser user = Global.getInstance().selectedRestUser;

        dNameText.setText(user.DisplayName());
        fNameText.setText(user.FirstName());
        lNameText.setText(user.LastName());
        uNameText.setText(user.UserName());
        eMailText.setText(user.Email());

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
}
