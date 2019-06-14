package com.good.automated.test.screenFinder.mapping;

import com.good.automated.test.screenFinder.view.SearchableView;

/**
 * Class contains methods for creation and filling of a {@link ViewMapper}.
 */
public class ViewMapperBuilder {

    private ViewMapper viewMapper;
    private boolean shouldAddBBDView;

    public ViewMapperBuilder() {
        viewMapper = new ViewMapper();
        shouldAddBBDView = true;
    }

    public ViewMapperBuilder addViewByResourceId(String resourceId) {
        int id = MappingDefaultBBD.getUniqueIdForMapper(viewMapper);
        viewMapper.addView(new SearchableView(id, resourceId));
        return this;
    }

    /**
     * Specifies that we should not search for BBD views therefore they will not be added to {@link ViewMapper}.
     *
     * @return  instance of the current class
     */
    public ViewMapperBuilder dontSearchBBDView() {
        shouldAddBBDView = false;
        return this;
    }

    /**
     * Adds pre-defined BBD views to the specified {@link ViewMapper}.
     *
     * @param viewMapper    {@link ViewMapper} to add views to
     */
    private void addBBDViewToMapper(ViewMapper viewMapper) {
        for (String resId : MappingDefaultBBD.uiElementsList) {
            int id = MappingDefaultBBD.getUniqueIdForMapper(viewMapper);
            viewMapper.addView(new SearchableView(id, resId));
        }
    }

    /**
     * Returns filled {@link ViewMapper} object.
     * If {@link #shouldAddBBDView} set to true - all of the BBD views will be added to the collection.
     *
     * @return  filled instance of the {@link ViewMapper}
     */
    public ViewMapper build() {
        if (shouldAddBBDView) {
            addBBDViewToMapper(viewMapper);
        }

        return viewMapper;
    }


}
