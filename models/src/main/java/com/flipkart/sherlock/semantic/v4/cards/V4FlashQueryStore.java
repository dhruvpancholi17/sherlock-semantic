package com.flipkart.sherlock.semantic.v4.cards;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.flipkart.sherlock.semantic.v4.V4FlashContentType;
import com.flipkart.sherlock.semantic.v4.V4FlashMarketPlace;
import com.flipkart.sherlock.semantic.v4.V4FlashSuggestion;

/**
 * Created by dhruv.pancholi on 11/12/17.
 */
public class V4FlashQueryStore extends V4FlashSuggestion {
    private String clickUrl;
    private V4FlashContentType contentType;
    private String query;
    private String store;
    private V4FlashMarketPlace marketPlaceId;

    @java.beans.ConstructorProperties({"clickUrl", "contentType", "query", "store", "marketPlaceId"})
    public V4FlashQueryStore(String clickUrl, V4FlashContentType contentType, String query, String store, V4FlashMarketPlace marketPlaceId) {
        this.clickUrl = clickUrl;
        this.contentType = contentType;
        this.query = query;
        this.store = store;
        this.marketPlaceId = marketPlaceId;
    }

    public V4FlashQueryStore() {
    }

    public String getClickUrl() {
        return this.clickUrl;
    }

    public V4FlashContentType getContentType() {
        return this.contentType;
    }

    public String getQuery() {
        return this.query;
    }

    public String getStore() {
        return this.store;
    }

    public V4FlashMarketPlace getMarketPlaceId() {
        return this.marketPlaceId;
    }

    public void setClickUrl(String clickUrl) {
        this.clickUrl = clickUrl;
    }

    public void setContentType(V4FlashContentType contentType) {
        this.contentType = contentType;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public void setMarketPlaceId(V4FlashMarketPlace marketPlaceId) {
        this.marketPlaceId = marketPlaceId;
    }
}
