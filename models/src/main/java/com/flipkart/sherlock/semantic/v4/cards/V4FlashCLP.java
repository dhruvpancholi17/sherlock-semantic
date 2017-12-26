package com.flipkart.sherlock.semantic.v4.cards;


import com.flipkart.sherlock.semantic.v4.V4FlashContentType;
import com.flipkart.sherlock.semantic.v4.V4FlashSuggestion;

/**
 * Created by dhruv.pancholi on 11/12/17.
 */
public class V4FlashCLP extends V4FlashSuggestion {
    private String clickUrl;
    private V4FlashContentType contentType;
    private String title;
    private String subTitle;
    private String imageUrl;

    @java.beans.ConstructorProperties({"clickUrl", "contentType", "title", "subTitle", "imageUrl"})
    public V4FlashCLP(String clickUrl, V4FlashContentType contentType, String title, String subTitle, String imageUrl) {
        this.clickUrl = clickUrl;
        this.contentType = contentType;
        this.title = title;
        this.subTitle = subTitle;
        this.imageUrl = imageUrl;
    }

    public V4FlashCLP() {
    }

    public String getClickUrl() {
        return this.clickUrl;
    }

    public V4FlashContentType getContentType() {
        return this.contentType;
    }

    public String getTitle() {
        return this.title;
    }

    public String getSubTitle() {
        return this.subTitle;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setClickUrl(String clickUrl) {
        this.clickUrl = clickUrl;
    }

    public void setContentType(V4FlashContentType contentType) {
        this.contentType = contentType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
