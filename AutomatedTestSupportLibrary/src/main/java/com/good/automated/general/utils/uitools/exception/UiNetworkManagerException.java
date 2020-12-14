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

package com.good.automated.general.utils.uitools.exception;

/**
 * Exception which indicates issues related to {@link com.good.automated.general.utils.uitools.networking.UiNetworkManager}
 */
public class UiNetworkManagerException extends RuntimeException {
    public UiNetworkManagerException() {
        super();
    }

    public UiNetworkManagerException(String message) {
        super(message);
    }

    public UiNetworkManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public UiNetworkManagerException(Throwable cause) {
        super(cause);
    }

    protected UiNetworkManagerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
