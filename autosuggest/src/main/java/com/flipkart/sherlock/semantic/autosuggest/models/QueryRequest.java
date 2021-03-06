package com.flipkart.sherlock.semantic.autosuggest.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by dhruv.pancholi on 02/06/17.
 */
@Getter
@AllArgsConstructor
public class QueryRequest {
    private final Params params;
    private final AutoSuggestSolrResponse autoSuggestSolrResponse;
}
