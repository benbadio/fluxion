package com.nigelbrown.fluxion;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * The bus communicates store change events ({@link FluxReaction reactions} or
 * {@link StoreChangeError errors}) to all views implementing {@link BaseFluxViewInterface}.
 */
public class FluxBus {
    private static FluxBus sInstance;
    private final Subject<Object, Object> mBus = new SerializedSubject<>(PublishSubject.create());

    private FluxBus() {}

    public synchronized static FluxBus getInstance() {
        if (sInstance == null) {
            sInstance = new FluxBus();
        }
        return sInstance;
    }

    void send(Object o) {mBus.onNext(o);}

    /**
     * @return the FluxBus instance.
     */
    public Observable<Object> get() {return mBus;}

    /**
     * @return true if the bus has observers.
     */
    public boolean hasObservers() {return mBus.hasObservers();}
}
