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

package com.good.automated.general.utils.threadsafe;

import android.support.test.uiautomator.UiDevice;
import android.util.Pair;

import com.good.automated.general.utils.threadsafe.actions.primary.ActionExecuteShellCommand;
import com.good.automated.general.utils.threadsafe.actions.secondary.ActionFindBySelector;
import com.good.automated.general.utils.threadsafe.actions.secondary.ActionFindUiSelector;
import com.good.automated.general.utils.threadsafe.actions.secondary.ActionGetCurrentPackage;
import com.good.automated.general.utils.threadsafe.actions.ActionPriority;
import com.good.automated.general.utils.threadsafe.actions.secondary.ActionSearchFailed;
import com.good.automated.general.utils.threadsafe.actions.IAction;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class is used to add more synchronization to UiDevice object.
 * All methods in UiDevice except executeShellCommand() are thread-safe.
 * UiDevice.findObject() and UiDevice.executeShellCommand() parallel execution in different threads
 * can lead to the RuntimeException.
 *
 * This class executes actions with 2 types of priorities: primary and secondary.
 *
 * Primary priority   - action that is called from TestCase thread
 *                      and that is important for TestCase execution (e.g. executeShellCommand).
 * Secondary priority - action that is executed in background thread (helper).
 *                      Such actions are used to gather additional statistics during TestCase execution
 *                      and don't affect TestCases.
 */
public class SafeCommandExecutor {

    private ReadWriteLock rwLock;
    private Lock readLock;
    private Lock writeLock;

    public SafeCommandExecutor() {
        rwLock = new ReentrantReadWriteLock();
        readLock = rwLock.readLock();
        writeLock = rwLock.writeLock();
    }

    // ---- Primary priority

    public String executeShellCommand(UiDevice uiDevice, String command) {

        IAction<String> executeShellAction = new ActionExecuteShellCommand(uiDevice, command);

        Pair<Boolean, String> executionResult = executeAction(executeShellAction);

        return executionResult.first ? executionResult.second : "";
    }


    // ---- Secondary priority

    /**
     * Check if object is present on the screen.
     *
     * The search on the screen can be done in 2 ways: UiSelector, BySelector.
     *
     * Pass "package, separator, resourceId" to initialize the search via UiSelector.
     * Pass "package, resourceId" to initialize the search via BySelector.
     *
     * @param uiDevice
     * @param params - "package, separator, resourceId" for UiSelector
     *                 or "package, resourceId" for BySelector.
     * @return pair, where first - false if action cannot be done (e.g. shell command is in progress), otherwise true
     *                     second - result of operation.
     */
    public Pair<Boolean, Boolean> hasUiObject(UiDevice uiDevice, String... params) {
        IAction<Boolean> action;
        if (params.length > 2) {
            action = new ActionFindUiSelector(uiDevice, params);
        } else if (params.length == 2) {
            action = new ActionFindBySelector(uiDevice, params);
        } else {
            action = new ActionSearchFailed();
        }
        return executeAction(action);
    }


    public Pair<Boolean, String> getCurrentPackageName(UiDevice uiDevice) {
        IAction<String> action = new ActionGetCurrentPackage(uiDevice);
        return executeAction(action);
    }



    // Reader-writer problem

    private <T> Pair<Boolean, T> executeAction(IAction<T> action) {
        ActionPriority priority = action.getPriority();

        if (priority == ActionPriority.PRIORITY_PRIMARY) {
            return executePrimaryAction(action);
        }

        if (priority == ActionPriority.PRIORITY_SECONDARY) {
            return executeSecondaryAction(action);
        }

        // Should not happen
        return Pair.create(false, null);
    }

    private <T> Pair<Boolean, T> executePrimaryAction(IAction<T> action) {
        Pair<Boolean, T> result = Pair.create(false, null);
        try {
            writeLock.lock();
            result = action.doAction();
        } catch (Exception ex) {

        } finally {
            writeLock.unlock();
        }
        return result;
    }

    private <T> Pair<Boolean, T> executeSecondaryAction(IAction<T> action) {
        Pair<Boolean, T> result = Pair.create(false, null);

        // Primary command is in progress
        if (!readLock.tryLock())
            return result;

        try {
            result = action.doAction();
        } catch (Exception ex) {

        } finally {
            readLock.unlock();
        }

        return result;
    }

}
