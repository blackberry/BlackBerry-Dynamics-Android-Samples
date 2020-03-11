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

package com.good.automated.test.screenFinder.checker;

import com.good.automated.test.screenFinder.view.BBDView;

public class ViewChecker {

    private String packageName;
    private String resourceId;
    private IViewListener listener;

    public ViewChecker(String packageName, String resourceId, IViewListener listener) {
        this.packageName = packageName;
        this.resourceId = resourceId;
        this.listener = listener;
    }

    public boolean compareAndNotify(BBDView view) {
        boolean viewFound = false;

        viewFound = view.getPackageName() != null && view.getPackageName().equals(packageName);

        if (viewFound) {
            viewFound = view.getResourceId() != null && view.getResourceId().equals(resourceId);
        }

        if (viewFound)
            listener.onViewFound();

        return viewFound;
    }

}
