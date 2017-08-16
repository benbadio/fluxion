package com.nigelbrown.fluxion;

// TODO: 8/16/2017 Is this still needed?
public class StoreChange {
	private String mStoreId;
    private FluxAction mFluxAction;

    public StoreChange(String storeId, FluxAction fluxAction) {
        this.mStoreId = storeId;
        this.mFluxAction = fluxAction;
    }

    public FluxAction getFluxAction() {
        return mFluxAction;
    }

	public String getStoreId() {
		return mStoreId;
	}
}
