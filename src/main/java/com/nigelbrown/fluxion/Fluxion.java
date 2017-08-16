package com.nigelbrown.fluxion;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Fluxion implements Application.ActivityLifecycleCallbacks {

    public static String TAG = "Fluxion";
    private static Fluxion sInstance;
    private final FluxBus mFluxBus;
    private final FluxDispatcher mDispatcher;
    private final SubscriptionManager mSubscriptionManager;

    private Fluxion(Application application) {
        this.mFluxBus = FluxBus.getInstance();
        this.mDispatcher = FluxDispatcher.getInstance(mFluxBus);
        this.mSubscriptionManager = SubscriptionManager.getInstance();
        application.registerActivityLifecycleCallbacks(this);
    }

    /**
     * Initialize the Fluxion framework, this method must be called on application creation and only once.
     *
     * @param application the application upon which the Fluxion framework will be initialized.
     * @return The singleton {@link Fluxion} framework instance.
     */
    public static Fluxion init(Application application) {
        if (sInstance != null)
            return sInstance;
        return sInstance = new Fluxion(application);
    }

    /**
     * Call this method to stop the Fluxion framework and clear all its subscriptions and registrations.
     * In a single activity view hierarchy, this would be called in the main activity's {@link Activity#onDestroy() onDestroy} method.
     */
    public static void shutdown() {
        if (sInstance == null) return;
        sInstance.mSubscriptionManager.clear();
        sInstance.mDispatcher.unregisterAll();
    }

    /**
     * @return the singleton instance of the {@link FluxBus}
     */
    public FluxBus getFluxBus() {
        return mFluxBus;
    }

    /**
     * @return the singleton instance of the {@link FluxDispatcher}
     */
    public FluxDispatcher getDispatcher() {
        return mDispatcher;
    }

    /**
     * @return the singleton instance of the {@link SubscriptionManager}
     */
    public SubscriptionManager getSubscriptionManager() {
        return mSubscriptionManager;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        if (activity instanceof FluxViewInterface) {
            ((FluxViewInterface) activity).onRegisterStores();
            mDispatcher.registerReaction((FluxViewInterface) activity);
            ((AppCompatActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                @Override
                public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
                    if (f instanceof BaseFluxViewInterface) {
                        mDispatcher.registerReaction((BaseFluxViewInterface) f);
                    }
                }

                @Override
                public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                    if (f instanceof BaseFluxViewInterface) {
                        mDispatcher.unregister((BaseFluxViewInterface) f);
                    }
                }

                @Override
                public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
                    super.onFragmentAttached(fm, f, context);
                    if (f instanceof BaseFluxViewInterface) {
                        mDispatcher.registerReaction((BaseFluxViewInterface) f);
                    }
                }

                @Override
                public void onFragmentDetached(FragmentManager fm, Fragment f) {
                    if (f instanceof BaseFluxViewInterface) {
                        mDispatcher.unregister((BaseFluxViewInterface) f);
                    }
                    super.onFragmentDetached(fm, f);
                }
            }, true);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
