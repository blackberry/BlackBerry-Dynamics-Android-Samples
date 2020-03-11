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

import android.os.HandlerThread;

import com.good.automated.general.utils.threadsafe.SafeCommandExecutor;
import com.good.automated.test.screenFinder.view.BBDView;
import com.good.automated.test.screenFinder.view.BBDViewFactory;

import java.util.concurrent.BlockingDeque;

public class ViewHandlerThread extends HandlerThread {

    private static final String TAG = ViewHandlerThread.class.getSimpleName();

    private ViewHandler handler;

    public ViewHandlerThread() {
        super(TAG);
    }

    /**
     * Starts a view handler thread and {@link ViewHandler} in a looper (setting {@link BBDViewFactory} to it).
     *
     * @param factory   {@link BBDViewFactory} to set to {@link ViewHandler}
     */
    public synchronized void register(BBDViewFactory factory, SafeCommandExecutor executor) {
        start();
        handler = new ViewHandler(getLooper(), executor);
        handler.setViewFactory(factory);
    }

    public synchronized void startMonitoring() {
        if (handler != null) {
            handler.sendEmptyMessage(0);
        }
    }

    /**
     * Gets already collected UI stack.
     *
     * @return  collected UI stack as {@link BlockingDeque} or null if {@link ViewHandler} is not initialized
     */
    public synchronized BlockingDeque<BBDView> getUiStack() {
        if (handler != null) {
            return handler.getViewStack();
        }

        return null;
    }

    /**
     * Stops {@link ViewHandler} and quits current thread.
     */
    public synchronized void deregister() {
        if (handler != null) {
            handler.stop();
        }
        quitSafely();
    }

    public ViewHandler getHandler() {
        return handler;
    }

}
