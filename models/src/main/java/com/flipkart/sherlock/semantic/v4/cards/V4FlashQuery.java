package com.flipkart.sherlock.semantic.v4.cards;


import com.flipkart.sherlock.semantic.v4.V4FlashContentType;
import com.flipkart.sherlock.semantic.v4.V4FlashSuggestion;

/**
 * Created by dhruv.pancholi on 11/12/17.
 */
public class V4FlashQuery extends V4FlashSuggestion {
    private String clickUrl;
    private V4FlashContentType contentType;
    private String query;

    @java.beans.ConstructorProperties({"clickUrl", "contentType", "query"})
    public V4FlashQuery(String clickUrl, V4FlashContentType contentType, String query) {
        this.clickUrl = clickUrl;
        this.contentType = contentType;
        this.query = query;
    }

    public V4FlashQuery() {
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

    public void setClickUrl(String clickUrl) {
        this.clickUrl = clickUrl;
    }

    public void setContentType(V4FlashContentType contentType) {
        this.contentType = contentType;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
