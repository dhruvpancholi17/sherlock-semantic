package com.flipkart.sherlock.semantic.autosuggest.models.v4;

import com.flipkart.sherlock.semantic.autosuggest.models.AutoSuggestDoc;
import com.flipkart.sherlock.semantic.autosuggest.models.Params;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Created by dhruv.pancholi on 07/11/17.
 */
@Getter
@AllArgsConstructor
public class V4AutoSuggestResponse {

    private final String payloadId;
    private final int coldStartVersion;
    private final List<V4SuggestionRow> suggestions;

    // Debug Fields
    private Params params;
    private String querySolrQuery;
    private String productSolrQuery;
    private List<AutoSuggestDoc> autoSuggestDocs;
}
