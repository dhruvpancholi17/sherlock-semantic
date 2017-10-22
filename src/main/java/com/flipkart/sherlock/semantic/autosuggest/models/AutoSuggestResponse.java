package com.flipkart.sherlock.semantic.autosuggest.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Created by dhruv.pancholi on 02/06/17.
 */
@Getter
@AllArgsConstructor
public class AutoSuggestResponse {
    private final String payloadId;
    private final List<QuerySuggestion> querySuggestions;
    private final List<ProductSuggestion> productSuggestions;
    private final Params params;
    private final String querySolrQuery;
    private final String productSolrQuery;
    private final List<AutoSuggestDoc> autoSuggestDocs;
}
