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
