package com.flipkart.sherlock.semantic.autosuggest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by dhruv.pancholi on 01/06/17.
 */
@Getter
@AllArgsConstructor
public class TransformedCtrObj {

    @JsonProperty("impressions")
    private final int impressions;

    @JsonProperty("p-hits")
    private final double pHits;

    @JsonProperty("state-hits")
    private final double stateHits;

    @JsonProperty("ctr")
    private final double ctr;
}
