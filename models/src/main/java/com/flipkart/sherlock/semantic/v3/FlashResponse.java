package com.flipkart.sherlock.semantic.v3;

import java.util.List;

/**
 * Created by dhruv.pancholi on 18/12/17.
 */
public class FlashResponse {
    private String payloadId;
    private List<QuerySuggestion> querySuggestions;
    private List<ProductSuggestion> productSuggestions;

    public FlashResponse() {
    }

    public FlashResponse(String payloadId, List<QuerySuggestion> querySuggestions, List<ProductSuggestion> productSuggestions) {
        this.payloadId = payloadId;
        this.querySuggestions = querySuggestions;
        this.productSuggestions = productSuggestions;
    }

    public String getPayloadId() {
        return payloadId;
    }

    public List<QuerySuggestion> getQuerySuggestions() {
        return querySuggestions;
    }

    public List<ProductSuggestion> getProductSuggestions() {
        return productSuggestions;
    }
}
