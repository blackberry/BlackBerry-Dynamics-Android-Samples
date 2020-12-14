/*
 * Copyright (c) 2020 BlackBerry Limited.
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

package com.good.gd.webview_V2.bbwebview.devtools;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.good.gd.webview_V2.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WebSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WebSettingsFragment extends Fragment implements WebSettingsAccess{

    private static final String TAG = "GDWebView-" +  WebSettingsFragment.class.getSimpleName();

    private WebSettings webSettings;

    public WebSettingsFragment() {
        // Required empty public constructor
    }


    public static WebSettingsFragment newInstance() {
        return new WebSettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web_settings, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        WebSettingsListAdapter adapter = new WebSettingsListAdapter();
        adapter.webSettingsItems = new ArrayList<>();

        listWebSettings(getWebSettings(),adapter.webSettingsItems);

        ListView lv = getView().findViewById(R.id.web_settings_items);
        lv.setAdapter(adapter);

    }

    public static void listWebSettings(WebSettings webSettings, List<? super WebSetting> outList){

        try {
            Method[] declaredMethods = webSettings.getClass().getDeclaredMethods();

            for (Method declaredMethod : declaredMethods) {
                Class<?> retType = declaredMethod.getReturnType();
                declaredMethod.setAccessible(true);

                if (declaredMethod.getName().startsWith("get") && declaredMethod.getParameterTypes().length == 0) {
                    Object methRes = declaredMethod.invoke(webSettings);

                    WebSetting webSetting = new WebSetting(declaredMethod.getName(), retType, methRes);

                    outList.add(webSetting);

                    Log.d(TAG, "listWebSettings getter: " + retType + " " + declaredMethod.getName() + " = " + methRes);
                }

            }

        } catch (Exception e) {
            Log.e(TAG, "listWebSettings", e);
        }

    }

    @Override
    public void setWebSettings(WebSettings ws) {
        Log.i(TAG, "setWebSettings: " + ws);
        this.webSettings = ws;
    }

    @Override
    public WebSettings getWebSettings() {
        return webSettings;
    }

    @Override
    public void onDetach() {
        Log.i(TAG, "> onDetach");
        setWebSettings(null);
        super.onDetach();
        Log.i(TAG, "< onDetach");
    }

    public static class WebSetting<T> {

        private String methodName;
        private Class<T> retType;
        private T retValue;

        private String settingName = null;

        public WebSetting(String name, Class<T> retType, T retValue) {
            this.methodName = name;
            this.retType = retType;
            this.retValue = retValue;

        }

        public T getValue(){
            return retValue;
        }

        public String getSettingName(){
            if(settingName == null) {
                //remove "get" prefix
                settingName = methodName.replaceAll("^get", "");
            }
            return settingName;
        }

        @NonNull
        @Override
        public String toString() {
            return methodName + " = " + getValue();
        }
    }


    public static class WebSettingsListAdapter extends BaseAdapter {

        List<WebSetting> webSettingsItems;

        @Override
        public int getCount() {
            return webSettingsItems.size();
        }

        @Override
        public Object getItem(int position) {
            return webSettingsItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2,parent,false);
            }

            TextView name = convertView.findViewById(android.R.id.text1);
            TextView val = convertView.findViewById(android.R.id.text2);

            WebSetting item = (WebSetting) getItem(position);

            name.setText(item.getSettingName());
            val.setText(item.getValue()+"");

            return convertView;
        }
    }

}
