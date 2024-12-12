/* Copyright (c) 2022 BlackBerry Limited
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

package com.blackberry.dynamics.sample.okhttp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.blackberry.okhttpsupport.interceptor.BBCustomInterceptor;
import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AsyncUseCaseActivity extends AppCompatActivity implements GDStateListener {

    private static final String TAG = AsyncUseCaseActivity.class.getName();

    private OkHttpClient client;
    private HashMap<String, String> requestResponseMap;
    private TextView requestUI;
    private TextView requestsResponseStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GDAndroid.getInstance().activityInit(this);
        setContentView(R.layout.activity_async_use_case);

        requestUI = findViewById(R.id.requestui);
        requestsResponseStatus = findViewById(R.id.requestscount);
        requestResponseMap = new HashMap<>();
    }

    private void makeHttpCall(int i) {

        Request request = new Request.Builder().url(urls[i]).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "onFailure invoked in callback -  " + e.toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                Log.d(TAG, "Response received : code : " + response.code());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.body() != null) {
                            try {
                                String sResponse = response.body().string();

                                Log.d(TAG, "Response is empty = " + sResponse.isEmpty());

                                URL url = new URL(response.request().url().toString());

                                if (response.isSuccessful()){
                                    requestResponseMap.put(url.getPath(),"Success");
                                } else {
                                    requestResponseMap.put(url.getPath(),"Failure");
                                }

                                updateUI();
                            } catch (IOException ioException) {
                                Log.e(TAG, "Exception while collecting responses for URL number "
                                        + i  + ". Exception: " + ioException.toString());
                            }
                        }
                    }
                });
            }
        });
    }

    private void updateUI() {
        StringBuilder sb = new StringBuilder();

        sb.append("Number of requests submitted: ");
        sb.append(urls.length);
        sb.append("\n\nNumber of responses received: ");
        sb.append(requestResponseMap.size());
        requestsResponseStatus.setText(sb.toString());


        sb.delete(0, sb.length() - 1);

        for (Map.Entry<String,String> entry : requestResponseMap.entrySet()) {
            entry.getKey();
            entry.getValue();
            sb.append(entry.getKey());
            sb.append(" -- ");
            sb.append(entry.getValue());
            sb.append('\n');
        }

        requestUI.setText(sb.toString());
    }

    // A bunch of images URLs from the BlackBerry Developer web site.
    private static final String[] urls = {
        "https://developers.blackberry.com/us/en/_jcr_content/root/responsivegrid/producttile.img.png/1637347076675/blackberry-dynamics-hero-bridge-blue.png",
        "https://developers.blackberry.com/us/en/_jcr_content/root/responsivegrid/producttile_copy_217557980.img.png/1637347076698/watchdox-hero-banner.png",
        "https://developers.blackberry.com/us/en/_jcr_content/root/responsivegrid/producttile_copy_cop.img.png/1637347076721/blackberry-bbm-enterprise-sdk-hero-banner.png",
        "https://developers.blackberry.com/us/en/_jcr_content/root/responsivegrid/producttile_copy_1232746501.img.png/1637347076745/blackberry-analytics-hero-banner.png",
        "https://developers.blackberry.com/us/en/_jcr_content/root/responsivegrid/producttile_copy_cop_769967751.img.png/1637347076768/blackberry-enterprise-identity-hero.png",
        "https://developers.blackberry.com/us/en/_jcr_content/root/responsivegrid/producttile_copy_cop_1208362981.img.png/1637347076797/blackberry-2fa-hero-banner.png",
        "https://developers.blackberry.com/us/en/_jcr_content/root/responsivegrid/producttile_copy_cop_375518605.img.png/1637347076820/community-generic-1366-x-463-abstract3.png",
        "https://developers.blackberry.com/us/en/_jcr_content/root/responsivegrid/producttile_copy_cop_966268069.img.jpeg/1637347076843/spark-core-sdk-1280x433.jpeg",
        "https://developers.blackberry.com/us/en/_jcr_content/root/responsivegrid/producttile_copy_cop_1620431494.img.png/1629413942725/blackberry-enterprise-identity-hero.png",
        "https://developers.blackberry.com/us/en/resources/get-started/_jcr_content/root/responsivegrid/producttile_copy_cop_2082244065.img.png/1629413947921/blackberry-dynamics-hero-bridge-blue.png",
        "https://developers.blackberry.com/us/en/resources/get-started/_jcr_content/root/responsivegrid/producttile_copy_cop.img.png/1629413943712/blackberry-uem-hero-banner.png",
        "https://developers.blackberry.com/us/en/resources/get-started/_jcr_content/root/responsivegrid/producttile_copy_cop_2080422143.img.png/1629413941475/watchdox-hero-banner.png",
        "https://developers.blackberry.com/us/en/resources/get-started/_jcr_content/root/responsivegrid/producttile_copy_cop_2117087119.img.png/1629413942725/blackberry-enterprise-identity-hero.png",
        "https://developers.blackberry.com/us/en/resources/get-started/_jcr_content/root/responsivegrid/producttile_copy_cop_1394854588.img.png/1629413945146/blackberry-2fa-hero-banner.png",
        "https://developers.blackberry.com/us/en/resources/get-started/_jcr_content/root/responsivegrid/producttile_copy_cop_1176965776.img.jpeg/1629413801338/spark-core-sdk-1280x433.jpeg",
        "https://developers.blackberry.com/us/en/resources/get-started/_jcr_content/root/responsivegrid/producttile_copy_cop_1155081893.img.png/1629413940420/blackberry-bbm-enterprise-sdk-hero-banner.png",
        "https://developers.blackberry.com/us/en/resources/get-started/blackberry-dynamics-getting-started/_jcr_content/root/responsivegrid/wizard/wizardStep1/accordion_1260831343/accordionItem1/image.img.png/1635526734810/dynamics-architecture.png",
        "https://developers.blackberry.com/us/en/resources/get-started/blackberry-dynamics-getting-started/_jcr_content/root/responsivegrid/wizard/wizardStep3/platformdiv/platformDivLayoutContainer/tabs/TabPanelResponsive1/accordion/accordionItem1/image.img.png/1635526737225/screen-shot-2019-11-14-at-8.56.45-am.png",
        "https://developers.blackberry.com/us/en/resources/get-started/blackberry-dynamics-getting-started/_jcr_content/root/responsivegrid/wizard/wizardStep3/platformdiv/platformDivLayoutContainer/tabs/TabPanelResponsive1/accordion_475765876/accordionItem1/image.img.png/1635526737370/simulation-mode.png",
        "https://developers.blackberry.com/us/en/resources/isv-partner-resources/_jcr_content/root/responsivegrid/responsivegrid/authorizationcontain/gridContent-nologin/image.img.png/1613575003380/bnr-isv-partner-600-298.png"
    };

    @Override
    public void onAuthorized() {
        //Start the networking requests after BlackBerry Dynamics authorization is complete.

        BBCustomInterceptor customInterceptor = new BBCustomInterceptor();
        client = new OkHttpClient().newBuilder().addInterceptor(customInterceptor).build();

        for (int i = urls.length - 1; i >= 0; i--) {
            makeHttpCall(i);
        }
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