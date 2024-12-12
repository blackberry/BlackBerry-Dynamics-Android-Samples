/* Copyright (c) 2023 BlackBerry Ltd.
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

package com.good.gd.example.appkinetics;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.good.gd.GDServiceProvider;

public class IconAndTextListAdapter extends ArrayAdapter<GDServiceProvider> {

    public IconAndTextListAdapter(Activity activity, List<GDServiceProvider> gdServiceProviders) {
        super(activity, 0, gdServiceProviders);
    }

    @SuppressLint("ViewHolder")
	@SuppressWarnings("deprecation")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Activity activity = (Activity) getContext();
        LayoutInflater inflater = activity.getLayoutInflater();

        // Inflate the views from XML
        View rowView = inflater.inflate(R.layout.listviewlayout, parent, false);
        GDServiceProvider gdServiceProvider = getItem(position);

        // Load the image and set it on the ImageView
        ImageView imageView = rowView.findViewById(R.id.image);
        imageView.setImageDrawable(new BitmapDrawable((gdServiceProvider.getIcon())));

        // Set the text on the TextView
        TextView textView = rowView.findViewById(R.id.text);
        textView.setText(gdServiceProvider.getName());

        return rowView;
    }
}