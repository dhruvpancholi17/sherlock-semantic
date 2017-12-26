package com.flipkart.sherlock.semantic.v4.cards;


import com.flipkart.sherlock.semantic.v4.V4FlashSuggestion;

/**
 * Created by dhruv.pancholi on 11/12/17.
 */
public class V4FlashPartition extends V4FlashSuggestion {
    private String title;

    @java.beans.ConstructorProperties({"title"})
    public V4FlashPartition(String title) {
        this.title = title;
    }

    public V4FlashPartition() {
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
