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

package com.good.automated.test.screenFinder.handlers;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.util.Log;
import android.util.Pair;

import com.good.automated.general.utils.threadsafe.SafeCommandExecutor;
import com.good.automated.test.screenFinder.checker.ViewChecker;
import com.good.automated.test.screenFinder.mapping.MappingDefaultBBD;
import com.good.automated.test.screenFinder.parsing.UIParsingFacade;
import com.good.automated.test.screenFinder.view.BBDView;
import com.good.automated.test.screenFinder.view.BBDViewFactory;

import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static com.good.automated.test.screenFinder.general.Constants.RESOURCE_ID_SEPARATOR;

public class ViewHandler extends Handler {

    private boolean isRunning = false;

    private BlockingDeque<BBDView> viewStack;

    private BBDViewFactory viewFactory;

    private List<String> resourceToSearch;

    private ViewChecker listener;

    private UIParsingFacade facade;

    private SafeCommandExecutor executor;

    public ViewHandler(Looper looper, SafeCommandExecutor executor) {
        super(looper);
        this.viewStack = new LinkedBlockingDeque<>();
        this.facade = new UIParsingFacade();
        this.executor = executor;
    }

    public void setChecker(ViewChecker listener) {
        this.listener = listener;
    }

    public void setViewFactory(BBDViewFactory factory) {
        this.viewFactory = factory;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        if (viewFactory == null ) {
            Log.d("TEST_GD", "There is nothing to search");
            return;
        }

        resourceToSearch = viewFactory.getSearchableViewResources();

        if (resourceToSearch == null ) {
            Log.d("TEST_GD", "There is nothing to search");
            return;
        }

        isRunning = true;

        BBDView currentUi = null;
        BBDView previousUi;
        boolean viewChanged;

        while (isRunning) {
            previousUi = currentUi;
            viewChanged = false;

            long uiTimeStart = System.currentTimeMillis();

            while (!viewChanged && isRunning) {
                currentUi = getCurrentUI();
                viewChanged = hasViewChanged(previousUi, currentUi);
            }

            if (viewChanged) {
                currentUi.setTimeOnTheTop(System.currentTimeMillis() - uiTimeStart);

                if (currentUi.getResourceId().equals(MappingDefaultBBD.unknownAppUI.getResourceId())) {
                    // dump UI xml
                    facade.createDumpAndMap();
                }

                Log.d("TEST_GD", "Pushed: " + currentUi.getPackageName() + "/" + currentUi.getResourceId());
                viewStack.push(currentUi);

                if (listener != null && listener.compareAndNotify(currentUi))
                    listener = null;
            }
        }
    }

    /**
     * Detects whether view has changed.
     * View change is detected if:
     *  - one of the passed views is null
     *  - previous view id is not the same as the current one's
     *  - package name of the previous view differs from the current one's
     *
     * @param previousView  the last view on the screen before the current one as {@link BBDView}
     * @param currentView   the current view on the screen as {@link BBDView}
     * @return              true - if the view was changed
     *                      false - otherwise
     */
    private boolean hasViewChanged(BBDView previousView, BBDView currentView) {
        if (previousView == null || currentView == null)
            return true;

        if (previousView.getId() != currentView.getId())
            return true;

        if (previousView.getPackageName() == null || currentView.getPackageName() == null)
            return false;

        return !previousView.getPackageName().equals(currentView.getPackageName());
    }

    /**
     * Gets current UI as {@link BBDView} (from the provided list of views) based on what is on screen at the moment.
     *
     * @return  {@link BBDView} of the current UI if it is present in the list of expected views
     *          or {@link MappingDefaultBBD#unknownView} if it doesn't
     */
    private BBDView getCurrentUI() {
        BBDView currentView = null;
        try {
            UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            Pair<Boolean, String> packageResult = executor.getCurrentPackageName(uiDevice);

            if (packageResult != null && packageResult.first) {
                String currentPackage = packageResult.second;

                String resIdShown = getUiElementShown(uiDevice, currentPackage, resourceToSearch);

                currentView = viewFactory.getViewForResourceName(currentPackage, resIdShown);
            }

        } catch (IllegalStateException ex) {
            Log.e("ViewDiscovery", "Could not catch view");
        }

        return currentView != null ? currentView : viewFactory.getViewForUiObject(null);
    }

    /**
     * Checks which element from the passed {@link List} is shown on the screen.
     *
     * @param uiDevice      actual {@link UiDevice}
     * @param packageName   package name of the app to search resource of
     * @param uiElements    {@link List} of ui elements ids to search on the screen
     * @return              id of the element shown or null if none of the passed list elements is on the screen
     */
    public String getUiElementShown(UiDevice uiDevice, String packageName, List<String> uiElements) {
        Pair<Boolean, Boolean> searchResult;
        for (String res : uiElements) {
            searchResult = executor.hasUiObject(uiDevice, packageName, RESOURCE_ID_SEPARATOR, res);

            if (!searchResult.first) // Shell command is in progress.
                return null;

            if (searchResult.second) {
                return res;
            }

        }
        return null;
    }

    public void stop() {
        isRunning = false;
    }

    public BlockingDeque<BBDView> getViewStack() {
        return viewStack;
    }
}
