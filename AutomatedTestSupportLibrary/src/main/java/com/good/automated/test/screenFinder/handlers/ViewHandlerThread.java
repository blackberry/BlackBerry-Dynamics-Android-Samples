package com.good.automated.test.screenFinder.handlers;

import android.os.HandlerThread;

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
    public synchronized void register(BBDViewFactory factory) {
        start();
        handler = new ViewHandler(getLooper());
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
