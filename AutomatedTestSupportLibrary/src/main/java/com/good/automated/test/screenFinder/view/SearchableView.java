package com.good.automated.test.screenFinder.view;

public class SearchableView {

    private final Integer uniqueIDInternal;

    private final String resourceId;

    public SearchableView(int id, String resId) {
        uniqueIDInternal = id;
        resourceId = resId;
    }

    public int getId() {
        return uniqueIDInternal;
    }

    public String getResourceId() {
        return resourceId;
    }
}
