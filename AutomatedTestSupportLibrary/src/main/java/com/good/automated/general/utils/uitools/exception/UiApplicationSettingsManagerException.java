package com.good.automated.general.utils.uitools.exception;

import com.good.automated.general.utils.uitools.applicationsettings.UiApplicationSettingsManager;

/**
 * Exception which indicates issues related to {@link UiApplicationSettingsManager}
 */
public class UiApplicationSettingsManagerException extends RuntimeException {
    public UiApplicationSettingsManagerException() {
        super();
    }

    public UiApplicationSettingsManagerException(String message) {
        super(message);
    }

    public UiApplicationSettingsManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public UiApplicationSettingsManagerException(Throwable cause) {
        super(cause);
    }

    protected UiApplicationSettingsManagerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
