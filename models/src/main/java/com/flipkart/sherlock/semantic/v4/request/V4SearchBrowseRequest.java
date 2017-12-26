package com.flipkart.sherlock.semantic.v4.request;


import com.flipkart.sherlock.semantic.v4.V4FlashMarketPlace;

/**
 * Created by dhruv.pancholi on 20/12/17.
 */
public class V4SearchBrowseRequest {

    private String query;
    private String store;
    private V4FlashMarketPlace marketPlaceId;
    private long timestamp;
    private String uri;

    public V4SearchBrowseRequest() {
    }

    public V4FlashMarketPlace getMarketPlaceId() {
        return marketPlaceId;
    }

    public void setMarketPlaceId(V4FlashMarketPlace marketPlaceId) {
        this.marketPlaceId = marketPlaceId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

}
