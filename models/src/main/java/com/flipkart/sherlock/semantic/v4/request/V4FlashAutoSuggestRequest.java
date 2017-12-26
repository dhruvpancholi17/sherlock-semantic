package com.flipkart.sherlock.semantic.v4.request;

import com.flipkart.sherlock.semantic.v4.V4FlashMarketPlace;

import java.util.Arrays;
import java.util.List;

/**
 * Created by dhruv.pancholi on 20/12/17.
 */
public class V4FlashAutoSuggestRequest {

    private String query;
    private V4FlashMarketPlace marketPlaceId;
    private boolean groceryContext;
    private List<String> types = Arrays.asList("QUERY_STORE", "QUERY");
    private int rows = 28;
    private long userTimeStamp;
    private String contextUri;
    private String contextQuery;
    private String contextStore;
    private List<V4SearchBrowseRequest> searchBrowseHistory;

    public V4FlashAutoSuggestRequest() {
    }

    public V4FlashMarketPlace getMarketPlaceId() {
        return marketPlaceId;
    }

    public void setMarketPlaceId(V4FlashMarketPlace marketPlaceId) {
        this.marketPlaceId = marketPlaceId;
    }

    public long getUserTimeStamp() {
        return userTimeStamp;
    }

    public void setUserTimeStamp(long userTimeStamp) {
        this.userTimeStamp = userTimeStamp;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isGroceryContext() {
        return groceryContext;
    }

    public void setGroceryContext(boolean groceryContext) {
        this.groceryContext = groceryContext;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public String getContextUri() {
        return contextUri;
    }

    public void setContextUri(String contextUri) {
        this.contextUri = contextUri;
    }

    public String getContextQuery() {
        return contextQuery;
    }

    public void setContextQuery(String contextQuery) {
        this.contextQuery = contextQuery;
    }

    public String getContextStore() {
        return contextStore;
    }

    public void setContextStore(String contextStore) {
        this.contextStore = contextStore;
    }

    public List<V4SearchBrowseRequest> getSearchBrowseHistory() {
        return searchBrowseHistory;
    }

    public void setSearchBrowseHistory(List<V4SearchBrowseRequest> searchBrowseHistory) {
        this.searchBrowseHistory = searchBrowseHistory;
    }
}
