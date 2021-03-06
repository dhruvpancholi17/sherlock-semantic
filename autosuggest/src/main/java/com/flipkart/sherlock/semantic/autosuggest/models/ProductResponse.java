package com.flipkart.sherlock.semantic.autosuggest.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Created by dhruv.pancholi on 01/06/17.
 */
@Getter
@AllArgsConstructor
public class ProductResponse {
    private final AutoSuggestSolrResponse autoSuggestSolrResponse;
    private final List<ProductSuggestion> productSuggestions;
}
