package com.good.gd.webview_V2;

import android.content.Intent;
import android.os.Bundle;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;

import static com.good.gd.webview_V2.BrowserActivity.EXTRA_URL;

public class HomeActivity extends AppCompatActivity implements GDStateListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        GDAndroid.getInstance().activityInit(this);
        final HomeActivity homeActivity = this;

        EditText editTextUrl = findViewById(R.id.url);

        Button btn = findViewById(R.id.button_home);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = editTextUrl.getText().toString();
                if (url.isEmpty()) {
                    Toast.makeText(HomeActivity.this, "Url field is empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!URLUtil.isValidUrl(url)) {
                    Toast.makeText(HomeActivity.this, "Entered url is not valid", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(homeActivity, BrowserActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra(EXTRA_URL, url);
                homeActivity.startActivity(intent);
            }
        });
    }

    @Override
    public void onAuthorized() {}

    @Override
    public void onLocked() {}

    @Override
    public void onWiped() {}

    @Override
    public void onUpdateConfig(Map<String, Object> map) {}

    @Override
    public void onUpdatePolicy(Map<String, Object> map) {}

    @Override
    public void onUpdateServices() {}

    @Override
    public void onUpdateEntitlements() {}

}
