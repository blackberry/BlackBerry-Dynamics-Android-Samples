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

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Custom Adapter for file list
 *
 */
public class FileListAdapter extends ArrayAdapter<FileModel> {

	//Caching previously checked file
	private static FileModel previousFile;

	// Inner Class ------------------------------------------------------------
	static class ViewHolder {
		protected TextView text;
		protected CheckBox checkbox;
	}

	// Instance Variables -----------------------------------------------------
	private final List<FileModel> list;

	// Constructor ------------------------------------------------------------
	public FileListAdapter(Activity context, List<FileModel> list) {
		super(context, R.layout.filelistrowlayout, list);
		this.list = list;
	}

    public void notifyDataSetChanged(final List<FileModel> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

	// Public Methods ---------------------------------------------------------
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
			view = inflater.inflate(R.layout.filelistrowlayout, parent, false);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = view.findViewById(R.id.label);
			viewHolder.checkbox = view.findViewById(R.id.check);
			viewHolder.checkbox.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View buttonView)
					{
						FileModel element = (FileModel) viewHolder.checkbox.getTag();
						element.setSelected(((CompoundButton) buttonView).isChecked());

						//Emulation of single-choice behaviour for our checkbox list
						if(previousFile != element)
						{
							ListView listview = ((Activity)getContext()).findViewById(R.id.listview);
							int previousPosition = list.indexOf(previousFile);

							if (previousPosition != -1)
							{
								previousFile.setSelected(false);
								//uncheck previous checkbox
								ViewGroup linearLayout = (ViewGroup) listview.getChildAt(previousPosition);
								((CompoundButton) linearLayout.findViewById(R.id.check)).setChecked(false);
							}
							//Make current position to act as previous one in future calls
							previousFile = element;
						}
					}
				});
			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(list.get(position));
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.text.setText(list.get(position).getName());
		holder.checkbox.setChecked(list.get(position).isSelected());
		return view;
	}
}
