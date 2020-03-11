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

package com.good.automated.test.screenFinder;

import android.util.Log;

import com.good.automated.general.utils.UIAutomatorUtilsFactory;
import com.good.automated.general.utils.threadsafe.SafeCommandExecutor;
import com.good.automated.test.screenFinder.checker.ViewListener;
import com.good.automated.test.screenFinder.general.JSONCreator;
import com.good.automated.test.screenFinder.mapping.ViewMapper;
import com.good.automated.test.screenFinder.mapping.ViewMapperBuilder;
import com.good.automated.test.screenFinder.view.BBDView;
import com.good.automated.test.screenFinder.view.BBDViewFactory;
import com.good.automated.test.screenFinder.handlers.ViewHandlerThread;
import com.good.automated.test.screenFinder.view.SearchableView;

import org.json.JSONObject;

import java.util.List;

/**
 * Main orchestrator class for collecting UI info.
 */
public class ViewDiscovery {

    private static ViewDiscovery _instance = null;

    public static ViewDiscovery getInstance() {
        if (_instance == null)
            _instance = new ViewDiscovery();

        return _instance;
    }

    private ViewHandlerThread handlerThread;
    private ViewMapper viewMapper;
    private BBDViewFactory factory;

    public ViewDiscovery() {
        factory = new BBDViewFactory();
    }

    /**
     * Fills {@link BBDViewFactory} searchable list and registers {@link ViewHandlerThread} with it.
     */
    public void initHandler() {
        SafeCommandExecutor executor = UIAutomatorUtilsFactory.getUIAutomatorUtils().getSafeCommandExecutor();
        initHandler(executor);
    }

    public void initHandler(SafeCommandExecutor executor) {
        if (viewMapper != null) {
            factory.setSearchableList(viewMapper.getList());
        } else {
            factory.setSearchableList((new ViewMapperBuilder().build()).getList());
        }
        handlerThread = new ViewHandlerThread();
        handlerThread.register(factory, executor);
    }

    /**
     * Injects views of a specified view mapper to search for them.
     *
     * @param viewMapper    {@link ViewMapper} to use for search
     */
    public void injectViews(ViewMapper viewMapper) {
        this.viewMapper = viewMapper;
    }

    public void addViews(List<SearchableView> views) {
        this.viewMapper.addViews(views);
    }

    /**
     * Starts monitoring by the {@link ViewHandlerThread}.
     */
    public void start() {
        handlerThread.startMonitoring();
    }

    /**
     * Prints collected view stack and deregister the {@link ViewHandlerThread}.
     */
    public void stop() {
        printStack();
        handlerThread.deregister();
    }

    /**
     * Prints collected stack.
     */
    public void printStack() {
        for (BBDView view : handlerThread.getUiStack()) {
            Log.d("TEST_GD", "Received stack: " + view.getPackageName() +
                    " / " + view.getResourceId() +
                    " / " + view.getTimeOnTheTop() + "ms");
        }
    }


    public boolean isScreenShown(String packageName, String resourceId, long delay) {
        return new ViewListener(handlerThread.getHandler()).isScreenShown(packageName, resourceId, delay);
    }

    /**
     * Converts collected UI stack to {@link JSONObject}.
     *
     * @return  UI stack as {@link JSONObject}
     */
    public JSONObject stackToJson() {
        return new JSONCreator(handlerThread.getUiStack()).getViewStackAsJson();
    }

    /**
     * Converts collected UI queue to {@link JSONObject}.
     *
     * @return  UI queue as {@link JSONObject}
     */
    public JSONObject queueToJson() {
        return new JSONCreator(handlerThread.getUiStack()).getViewQueueAsJson();
    }
}
