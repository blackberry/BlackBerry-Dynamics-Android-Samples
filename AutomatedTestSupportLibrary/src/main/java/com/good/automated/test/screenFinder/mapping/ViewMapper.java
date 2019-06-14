package com.good.automated.test.screenFinder.mapping;

import com.good.automated.test.screenFinder.view.SearchableView;

import java.util.LinkedList;
import java.util.List;

/**
 * Holder for views that could appear on the screen and that we will search for.
 */
public class ViewMapper {

    private List<SearchableView> searchableViewList = new LinkedList<>();

    public void addView(SearchableView view) {
        searchableViewList.add(view);
    }

    public void addViews(List<SearchableView> viewsList) {
        searchableViewList.addAll(viewsList);
    }

    public List<SearchableView> getList() {
        return searchableViewList;
    }
}
