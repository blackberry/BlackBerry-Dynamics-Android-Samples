package com.good.automated.general.utils.uitools.exception;

/**
 * Indicated an issue occurred while attempt to perform some UI interaction.
 */
public class UiInteractionException extends RuntimeException {
    public UiInteractionException() {
        super();
    }

    public UiInteractionException(String message) {
        super(message);
    }

    public UiInteractionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UiInteractionException(Throwable cause) {
        super(cause);
    }

    protected UiInteractionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
