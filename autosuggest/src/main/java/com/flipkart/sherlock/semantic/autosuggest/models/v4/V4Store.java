package com.flipkart.sherlock.semantic.autosuggest.models.v4;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by dhruv.pancholi on 07/11/17.
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class V4Store {
    private final String title;
    private final V4MarketPlace marketPlaceId;
    private final String storePath;
}
