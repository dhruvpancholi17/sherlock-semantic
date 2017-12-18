package com.flipkart.sherlock.semantic.autosuggest.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Created by dhruv.pancholi on 01/06/17.
 */
@Getter
@AllArgsConstructor
public class QueryResponse {
    private final AutoSuggestSolrResponse autoSuggestSolrResponse;
    private final List<QuerySuggestion> querySuggestions;
    private final boolean isSolrSpellCorrectionUsed;
    private final String solrSpellCorrectionOutput;
}
