package com.flipkart.sherlock.semantic.v3;

/**
 * Created by dhruv.pancholi on 18/12/17.
 */
public class Store {
    private String store;
    private String title;
    private String marketPlaceId;

    public Store() {
    }

    public Store(String store, String title, String marketPlaceId) {

        this.store = store;
        this.title = title;
        this.marketPlaceId = marketPlaceId;
    }

    public String getStore() {
        return store;
    }

    public String getTitle() {
        return title;
    }

    public String getMarketPlaceId() {
        return marketPlaceId;
    }
}
