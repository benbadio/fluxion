package com.nigelbrown.fluxion;

/**
 * Interface definition for callbacks invoked by the {@link FluxDispatcher dispatcher} following
 * {@link FluxStore store} changes or errors.
 * This interface should be implemented by any non-activity class to be considered a "view" in the flux flow.
 * Possible implementors include fragments and services. The application's parent activity should implement {@link FluxViewInterface}.
 */
public interface BaseFluxViewInterface {
    /**
     * Called when a change is posted by a store. When called, the view can react to the change
     *
     * @param fluxReaction Contains the type of action to be reacted upon and any data provided
     *                 by the store to be used when reacting to the store change.
     */
    void onReact(FluxReaction fluxReaction);
    /**
     * Called if an error occurs during the creation of an action.
     *
     * @param error Contains the error ({@link Throwable}) that occurred.
     */
    void onActionError(FluxActionError error);
    /**
     * Called whenever a {@link FluxStore store} posts an error using {@link FluxStore#postChangeError(StoreChangeError) postChangeError}
     *
     * @param error Contains the error ({@link Throwable}) posted.
     */
    void onStoreChangedError(StoreChangeError error);
}
