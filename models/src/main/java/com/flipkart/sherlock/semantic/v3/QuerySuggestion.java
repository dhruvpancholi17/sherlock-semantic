package com.flipkart.sherlock.semantic.v3;

import java.util.List;

/**
 * Created by dhruv.pancholi on 18/12/17.
 */
public class QuerySuggestion {
    private String query;
    private List<Store> stores;

    public String getQuery() {
        return query;
    }

    public List<Store> getStores() {
        return stores;
    }

    public QuerySuggestion() {

    }

    public QuerySuggestion(String query, List<Store> stores) {

        this.query = query;
        this.stores = stores;
    }
}
