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

import java.util.Comparator;

/**
 * Represents file data object
 * 
 */
public class FileModel {

	// Inner Class -----------------------------------------------------

	/**
	 * Comparator to sort the objects based on the file name
	 * 
	 */
	static public class NameComparator implements Comparator<FileModel> {
		@Override
		public int compare(FileModel o1, FileModel o2) {
			return o1.getName().compareToIgnoreCase(o2.getName());
		}
	}

	// Instance Variables -----------------------------------------------------
	private String name;
	private boolean selected;

	// Constructors -----------------------------------------------------------
	public FileModel(String name) {
		this.name = name;
		selected = false;
	}

	// Public Methods ---------------------------------------------------------
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
