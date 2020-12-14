/* Copyright (c) 2017 - 2020 BlackBerry Limited.
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

package com.good.automated.test.screenFinder.view;

import com.good.automated.test.screenFinder.mapping.MappingDefaultBBD;

public class BBDView {

    private SearchableView searchableView;
    private String packageName;
    private long timeOnTheTop = 0;
    private String appearanceTime;

    public BBDView() {
        searchableView = MappingDefaultBBD.unknownView;
    }

    public int getId() {
        return searchableView.getId();
    }

    public void associateWithView(SearchableView searchableView) {
        this.searchableView = searchableView;
    }

    public String getResourceId() {
        return searchableView.getResourceId();
    }

    public String getPackageName() {
        return packageName == null ? "unknown" : packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getTimeOnTheTop() {
        return timeOnTheTop;
    }

    public void setTimeOnTheTop(long timeOnTheTop) {
        this.timeOnTheTop = timeOnTheTop;
    }

    public String getAppearanceTime() {
        return appearanceTime;
    }

    public void setAppearanceTime(String appearanceTime) {
        this.appearanceTime = appearanceTime;
    }

    @Override
    public String toString() {
        return packageName + " / " + getResourceId();
    }
}
