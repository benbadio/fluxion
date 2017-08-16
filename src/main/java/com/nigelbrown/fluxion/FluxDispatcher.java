package com.nigelbrown.fluxion;

import android.support.v4.util.ArrayMap;

import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * The dispatcher sends {@link FluxAction actions} to all {@link FluxStore stores}
 * via registered callbacks. Stores must be registered with the dispatcher to receive actions.
 */
public class FluxDispatcher {
    private static FluxDispatcher sInstance;
    private final FluxBus mBus;
    private ArrayMap<String, Subscription> mFluxionActionMap;
    private ArrayMap<String, Subscription> mFluxionStoreMap;

    private FluxDispatcher(FluxBus bus) {
        this.mBus = bus;
        this.mFluxionActionMap = new ArrayMap<>();
        this.mFluxionStoreMap = new ArrayMap<>();
    }

    static synchronized FluxDispatcher getInstance(FluxBus fluxBus) {
        if (sInstance == null) { sInstance = new FluxDispatcher(fluxBus); }
        return sInstance;
    }

    public <T extends FluxActionInterface> void registerFluxionAction(final T object) {
        final String tag = object.getClass().getSimpleName();
        Subscription subscription = mFluxionActionMap.get(tag);
        if (subscription == null || subscription.isUnsubscribed()) {
            mFluxionActionMap.put(tag, mBus.get().filter(new Func1<Object, Boolean>() {
                @Override
                public Boolean call(Object o) {
                    return o instanceof FluxAction;
                }
            }).subscribe(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    object.onFluxionAction((FluxAction) o);
                }
            }));
        }
    }

    public <T extends BaseFluxViewInterface> void registerReaction(final T object) {
        final String tag = object.getClass().getSimpleName();
        Subscription subscription = mFluxionStoreMap.get(tag);
        if (subscription == null || subscription.isUnsubscribed()) {
            mFluxionStoreMap.put(tag, mBus.get().filter(new Func1<Object, Boolean>() {
                @Override
                public Boolean call(Object o) {
                    return o instanceof FluxReaction;
                }
            }).subscribe(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    object.onReact((FluxReaction) o);
                }
            }));
        }
        registerFluxionStoreError(object);
    }

    public <T extends BaseFluxViewInterface> void registerFluxionStoreError(final T object) {
        final String tag = object.getClass().getSimpleName() + "_error";
        Subscription subscription = mFluxionStoreMap.get(tag);
        if (subscription == null || subscription.isUnsubscribed()) {
            mFluxionStoreMap.put(tag, mBus.get().filter(new Func1<Object, Boolean>() {
                @Override
                public Boolean call(Object o) {
                    return o instanceof StoreChangeError;
                }
            }).subscribe(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    object.onStoreChangedError((StoreChangeError) o);
                }
            }));
        }
        registerFluxionError(object);
    }

    public <T extends BaseFluxViewInterface> void registerFluxionError(final T object) {
        final String tag = object.getClass().getSimpleName() + "_error";
        Subscription subscription = mFluxionActionMap.get(tag);
        if (subscription == null || subscription.isUnsubscribed()) {
            mFluxionActionMap.put(tag, mBus.get().filter(new Func1<Object, Boolean>() {
                @Override
                public Boolean call(Object o) {
                    return o instanceof FluxActionError;
                }
            }).subscribe(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    object.onActionError((FluxActionError) o);
                }
            }));
        }
    }

    public <T extends FluxActionInterface> void unregisterFluxionAction(final T object) {
        String tag = object.getClass().getSimpleName();
        Subscription subscription = mFluxionActionMap.get(tag);
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            mFluxionActionMap.remove(tag);
        }
    }

    private <T extends BaseFluxViewInterface> void unregisterFluxionStore(final T object) {
        String tag = object.getClass().getSimpleName();
        Subscription subscription = mFluxionStoreMap.get(tag);
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            mFluxionStoreMap.remove(tag);
        }
        unregisterFluxionError(object);
        unregisterFluxionStoreError(object);
    }

    private <T extends BaseFluxViewInterface> void unregisterFluxionError(final T object) {
        String tag = object.getClass().getSimpleName() + "_error";
        Subscription subscription = mFluxionActionMap.get(tag);
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            mFluxionActionMap.remove(tag);
        }
    }

    private <T extends BaseFluxViewInterface> void unregisterFluxionStoreError(final T object) {
        String tag = object.getClass().getSimpleName() + "_error";
        Subscription subscription = mFluxionStoreMap.get(tag);
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            mFluxionStoreMap.remove(tag);
        }
    }

    /**
     * Unregisters all views from the dispatcher.
     */
    public synchronized void unregisterAll() {
        for (Subscription subscription : mFluxionActionMap.values()) {
            subscription.unsubscribe();
        }
        for (Subscription subscription : mFluxionStoreMap.values()) {
            subscription.unsubscribe();
        }
        mFluxionActionMap.clear();
        mFluxionStoreMap.clear();
    }

    /**
     * Unregister a {@link BaseFluxViewInterface} view from the dispatcher
     *
     * @param object the view to be unregistered.
     */
    public synchronized <T extends BaseFluxViewInterface> void unregister(final T object) {
        unregisterFluxionError(object);
        unregisterFluxionStore(object);
        unregisterFluxionStoreError(object);
    }

    void postFluxionAction(final FluxAction action) {
        mBus.send(action);
    }

    void postFluxionActionError(final FluxActionError actionError) {
        mBus.send(actionError);
    }

    void postReaction(final FluxReaction fluxReaction) {
        mBus.send(fluxReaction);
    }

    void postFluxionStoreChangeError(final StoreChangeError storeChange) {
        mBus.send(storeChange);
    }
}
