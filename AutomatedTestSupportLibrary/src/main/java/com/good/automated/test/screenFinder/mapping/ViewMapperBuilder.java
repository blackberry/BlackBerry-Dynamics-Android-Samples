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
