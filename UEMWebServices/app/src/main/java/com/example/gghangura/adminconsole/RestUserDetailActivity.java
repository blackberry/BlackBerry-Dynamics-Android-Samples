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

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.good.gd.GDStateListener;
import com.good.gd.widget.GDTextView;

import java.util.Map;

public class RestUserDetailActivity extends AppCompatActivity implements GDStateListener {

    GDTextView dNameText;
    GDTextView fNameText;
    GDTextView lNameText;
    GDTextView uNameText;
    GDTextView eMailText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restuserdetail);

        dNameText = (GDTextView) findViewById(R.id.dNameText);
        fNameText = (GDTextView) findViewById(R.id.fNameText);
        lNameText = (GDTextView) findViewById(R.id.lNameText);
        uNameText = (GDTextView) findViewById(R.id.uNameText);
        eMailText = (GDTextView) findViewById(R.id.eMailText);

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
