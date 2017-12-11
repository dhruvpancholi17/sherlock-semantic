package com.flipkart.sherlock.semantic.autosuggest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Created by dhruv.pancholi on 06/10/16.
 */
@Getter
@AllArgsConstructor
public class DecayedProductObj {

    @JsonProperty("dd-contrib")
    private final double ddContrib;

    @JsonProperty("contrib")
    private final double contrib;

    @JsonProperty("product-id")
    private final String productId;

    @JsonProperty("count")
    private final int count;

    @JsonProperty("dd-count")
    private final double ddCount;

    @JsonProperty("leafPaths")
    private final List<String> leafPaths;

    @JsonProperty("store")
    private final String store;
}
