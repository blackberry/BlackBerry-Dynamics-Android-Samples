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

import android.support.test.uiautomator.UiObject2;

import com.good.automated.test.screenFinder.mapping.MappingDefaultBBD;

import java.util.LinkedList;
import java.util.List;

/**
 * Class for mapping different representations of UI to {@link BBDView}.
 */
public class BBDViewFactory {

    private List<SearchableView> searchableViewList;

    public void setSearchableList(List<SearchableView> list) {
        searchableViewList = list;
    }

    /**
     * Gets a {@link BBDView} that corresponds to the passed package name and resource id.
     * If package name is null - view treated as unknown view.
     * If resource id is null - view treated as application main ui.
     *
     * @param packageName   package name of the app owner of the view
     * @param resourceId    resource id of the view
     * @return              {@link BBDView} with the specified parameters
     */
    public BBDView getViewForResourceName(String packageName, String resourceId) {
        BBDView view = new BBDView();

        if (packageName == null) {
            view.associateWithView(MappingDefaultBBD.unknownView);
            return view;
        }

        view.setPackageName(packageName);

        if (resourceId == null) {
            view.associateWithView(MappingDefaultBBD.unknownAppUI);
            return view;
        }

        for (SearchableView searchableView : searchableViewList) {
            if (resourceId.contains(searchableView.getResourceId())) {
                view.associateWithView(searchableView);
                return view;
            }
        }

        view.associateWithView(MappingDefaultBBD.unknownView);

        return view;
    }

    /**
     * Gets {@link BBDView} that corresponds to the specified {@link UiObject2}.
     * If object passed is null - view treated as unknown view.
     *
     * @param uiObject2 {@link UiObject2} instance to search corresponding {@link BBDView}
     * @return          {@link BBDView} that corresponds to the specified ui object
     */
    public BBDView getViewForUiObject(UiObject2 uiObject2) {
        BBDView view = new BBDView();

        if (uiObject2 == null) {
            view.associateWithView(MappingDefaultBBD.unknownView);
            return view;
        }

        String packageName = uiObject2.getApplicationPackage();
        String resourceId = uiObject2.getResourceName();


        return getViewForResourceName(packageName, resourceId);
    }

    /**
     * Converts {@link List} of {@link SearchableView} to the {@link List} of corresponding resource ids.
     *
     * @return  list of resource ids of available searchable views
     */
    public List<String> getSearchableViewResources() {
        // TODO: 12/3/18 could be replaced with stream() once Java 1.8 is supported
        List<String> allowedResources = new LinkedList<>();

        for (SearchableView view : searchableViewList) {
            allowedResources.add(view.getResourceId());
        }

        return allowedResources;
    }


//    // TODO: 12/3/18 check and finish this in GD-40041
//    private boolean isAlertShown(UiObject2 uiObject) {
//
//        List<UiObject2> objects = getHierarchy(uiObject);
//
//        UiObject2 title = uiObject.findObject(By.res("android", "alertTitle"));
//
//        objects.forEach(o ->  {
//            if (o.getResourceName().contains("alertTitle"))
//            Log.d("TEST_GD", "Resource: " + o.getResourceName());
//
//        });
//        return title != null;
//    }
}
