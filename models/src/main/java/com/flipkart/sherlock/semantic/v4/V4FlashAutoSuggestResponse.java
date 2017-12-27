package com.flipkart.sherlock.semantic.v4;

import java.util.List;

/**
 * Created by dhruv.pancholi on 11/12/17.
 */
public class V4FlashAutoSuggestResponse {
    private String payloadId;
    private int coldStartVersion;
    private List<V4FlashSuggestion> suggestions;

    @java.beans.ConstructorProperties({"payloadId", "coldStartVersion", "suggestions"})
    public V4FlashAutoSuggestResponse(String payloadId, int coldStartVersion, List<V4FlashSuggestion> suggestions) {
        this.payloadId = payloadId;
        this.coldStartVersion = coldStartVersion;
        this.suggestions = suggestions;
    }

    public V4FlashAutoSuggestResponse() {
    }

    public String getPayloadId() {
        return this.payloadId;
    }

    public int getColdStartVersion() {
        return this.coldStartVersion;
    }

    public List<V4FlashSuggestion> getSuggestions() {
        return this.suggestions;
    }

    public void setPayloadId(String payloadId) {
        this.payloadId = payloadId;
    }

    public void setColdStartVersion(int coldStartVersion) {
        this.coldStartVersion = coldStartVersion;
    }

    public void setSuggestions(List<V4FlashSuggestion> suggestions) {
        this.suggestions = suggestions;
    }
}
