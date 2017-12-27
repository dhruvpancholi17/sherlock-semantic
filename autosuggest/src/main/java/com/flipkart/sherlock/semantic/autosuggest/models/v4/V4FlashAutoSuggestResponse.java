package com.flipkart.sherlock.semantic.autosuggest.models.v4;

import com.flipkart.sherlock.semantic.autosuggest.models.AutoSuggestDoc;
import com.flipkart.sherlock.semantic.autosuggest.models.Params;
import com.flipkart.sherlock.semantic.v4.V4FlashSuggestion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by dhruv.pancholi on 11/12/17.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class V4FlashAutoSuggestResponse {
    private String payloadId;
    private int coldStartVersion;
    private List<V4FlashSuggestion> suggestions;

    // Debug Fields
    private Params params;
    private String querySolrQuery;
    private String productSolrQuery;
    private List<AutoSuggestDoc> autoSuggestDocs;
}
