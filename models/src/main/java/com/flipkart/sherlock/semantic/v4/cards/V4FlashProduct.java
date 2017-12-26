package com.flipkart.sherlock.semantic.v4.cards;


import com.flipkart.sherlock.semantic.v4.V4FlashContentType;
import com.flipkart.sherlock.semantic.v4.V4FlashSuggestion;

/**
 * Created by dhruv.pancholi on 11/12/17.
 */
public class V4FlashProduct extends V4FlashSuggestion {
    private String clickUrl;
    private V4FlashContentType contentType;
    private String title;
    private String subTitle;
    private String imageUrl;

    private String pid;
    private String lid;

    @java.beans.ConstructorProperties({"clickUrl", "contentType", "title", "subTitle", "imageUrl", "pid", "lid"})
    public V4FlashProduct(String clickUrl, V4FlashContentType contentType, String title, String subTitle, String imageUrl, String pid, String lid) {
        this.clickUrl = clickUrl;
        this.contentType = contentType;
        this.title = title;
        this.subTitle = subTitle;
        this.imageUrl = imageUrl;
        this.pid = pid;
        this.lid = lid;
    }

    public V4FlashProduct() {
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

    public String getPid() {
        return this.pid;
    }

    public String getLid() {
        return this.lid;
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

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setLid(String lid) {
        this.lid = lid;
    }
}
