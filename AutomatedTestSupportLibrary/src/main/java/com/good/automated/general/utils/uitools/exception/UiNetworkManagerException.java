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
