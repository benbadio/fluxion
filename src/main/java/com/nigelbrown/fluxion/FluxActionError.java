package com.nigelbrown.fluxion;

/**
 * A wrapper for an exception that occurs in the process of posting an action.
 */
public class FluxActionError {
    private Throwable mThrowable;

    public FluxActionError(Throwable throwable) {
        this.mThrowable = throwable;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }
}
