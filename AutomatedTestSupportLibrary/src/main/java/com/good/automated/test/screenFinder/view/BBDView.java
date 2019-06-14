package com.good.automated.test.screenFinder.view;

import com.good.automated.test.screenFinder.mapping.MappingDefaultBBD;

public class BBDView {

    private SearchableView searchableView;
    private String packageName;
    private long timeOnTheTop = 0;

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
        return packageName;
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

    @Override
    public String toString() {
        return packageName + " / " + getResourceId();
    }
}
