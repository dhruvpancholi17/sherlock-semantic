package com.flipkart.sherlock.semantic.autosuggest.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Created by dhruv.pancholi on 02/06/17.
 */
@Getter
@AllArgsConstructor
public class AutoSuggestSolrResponse {
    private final String solrQuery;
    private final List<AutoSuggestDoc> autoSuggestDocs;
}
