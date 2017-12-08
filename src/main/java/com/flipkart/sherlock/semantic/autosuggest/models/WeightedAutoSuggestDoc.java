package com.flipkart.sherlock.semantic.autosuggest.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by dhruv.pancholi on 07/12/17.
 */
@Getter
@AllArgsConstructor
public class WeightedAutoSuggestDoc {
    private final AutoSuggestDoc autoSuggestDoc;
    private final double solrWeight;
    private final double uieWeight;
    private final double combinedWeight;
    private final String topStoreBucket;
}
