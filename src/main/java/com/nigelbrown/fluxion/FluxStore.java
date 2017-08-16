package com.nigelbrown.fluxion;

import android.os.Bundle;

/**
 * Stores are responsible for containing and updating application state. After
 * stores update themselves in response to an action, they emit a change event
 * (either {@link FluxReaction} or {@link StoreChangeError}). Views implementing
 * {@link BaseFluxViewInterface} listen for and react to such events, and
 * provide the new data to the entire tree of child views.
 */
public abstract class FluxStore implements FluxActionInterface {
    private final FluxDispatcher mDispatcher;

    public FluxStore(FluxDispatcher dispatcher) {
        this.mDispatcher = dispatcher;
    }

    /**
     * Registers the store to the {@link FluxDispatcher}. Call this method during initialization of a {@link BaseFluxViewInterface view}
     * (for example, in {@link android.app.Activity#onCreate(Bundle) Activity.onCreate()}).
     */
    public void register() {
        mDispatcher.registerFluxionAction(this);
    }

    private FluxReaction newReaction(String reactionId, Object... data) {
        if (reactionId.isEmpty()) {
            throw new IllegalArgumentException("Type must not be empty");
        }
        if (data.length % 2 != 0) {
            throw new IllegalArgumentException("Data must be a valid list of key,value pairs");
        }
        FluxReaction.Builder reactionBuilder = FluxReaction.type(reactionId);
        int i = 0;
        while (i < data.length) {
            String key = (String) data[i++];
            Object value = data[i++];
            reactionBuilder.bundle(key, value);
        }
        return reactionBuilder.build();
    }

    /**
     * Post a store change to be reacted on by any views that have registered this store.
     *
     * @param reactionId An ID for the reaction. In most situations this would be the same ID from
     *                   {@link FluxAction} that originated th reaction.
     * @param data       (<i>Optional</i>) Data to be passed to views when the reaction is posted. Data
     *                   parameters should be entered as key/value pairs.
     *                   <p>Example: If the store handled a GET_USERS action it would post a reaction
     *                   like the following:
     *                   </p>
     *                   <pre> {@code
     *                                      postReaction(Actions.GET_USERS, Keys.USERS, users);
     *                                     } </pre>
     */
    protected void postReaction(String reactionId, Object... data) {
        mDispatcher.postReaction(newReaction(reactionId, data));
    }

    /**
     * When an error occurs while handling an action, use this to notify the registered views that an
     * error has occurred. Once posted the views can react on the error.
     *
     * @param error The error to be posted on the bus. Consider using custom exceptions to differentiate
     *              between errors.
     */
    protected void postChangeError(StoreChangeError error) {
        mDispatcher.postFluxionStoreChangeError(error);
    }
}
