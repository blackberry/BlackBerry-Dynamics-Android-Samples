package com.good.automated.test.screenFinder.parsing;

import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DumpQueue {

    private UiDevice uiDevice;

    private static final int CAPACITY = 15;
    private final Queue queue = new LinkedList<>();

    // lock and condition variables
    private final Lock aLock = new ReentrantLock();
    private final Condition queueNotFull = aLock.newCondition();
    private final Condition queueNotEmpty = aLock.newCondition();

    public DumpQueue() {
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    public void dumpWindow() throws InterruptedException {
        aLock.lock();
        try {
            while (queue.size() == CAPACITY) {
                queueNotFull.await();
            }

            ByteArrayOutputStream viewDumpStream = new ByteArrayOutputStream();

            uiDevice.dumpWindowHierarchy(viewDumpStream);
            InputStream viewDumpToParse = new ByteArrayInputStream(viewDumpStream.toByteArray());

            boolean isAdded = queue.offer(viewDumpToParse);
            if (isAdded) {
                logMessage(new String(viewDumpStream.toByteArray()));
                queueNotEmpty.signalAll();
            }
        } catch (IOException ex) {
            // Could not get dump
        } finally {
            aLock.unlock();
        }
    }

    public InputStream get() throws InterruptedException {
        aLock.lock();
        InputStream resultValue = null;
        try {
            while (queue.size() == 0) {
                queueNotEmpty.await();
            }

            resultValue = (InputStream) queue.poll();

            queueNotFull.signalAll();

        } finally {
            aLock.unlock();
        }

        return resultValue;
    }

    private void logMessage(String msg) {
        int maxLogSize = 1000;
        for(int i = 0; i <= msg.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i+1) * maxLogSize;
            end = end > msg.length() ? msg.length() : end;
            Log.v("TEST_GD", msg.substring(start, end));
        }
    }
}
