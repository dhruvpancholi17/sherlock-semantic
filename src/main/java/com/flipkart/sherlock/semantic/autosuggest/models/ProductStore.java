package com.flipkart.sherlock.semantic.autosuggest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by dhruv.pancholi on 25/04/17.
 */
@Getter
@AllArgsConstructor
public class ProductStore {

    @JsonProperty("count")
    private final int count;

    @JsonProperty("dd-count")
    private final double ddCount;

    @JsonProperty("contrib")
    private final double contrib;

    @JsonProperty("dd-contrib")
    private final double ddContrib;

    @JsonProperty("store")
    private final String store;
}
