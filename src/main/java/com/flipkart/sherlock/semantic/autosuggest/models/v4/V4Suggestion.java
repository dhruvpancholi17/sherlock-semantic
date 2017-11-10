package com.flipkart.sherlock.semantic.autosuggest.models.v4;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by dhruv.pancholi on 07/11/17.
 */
@Builder
@Getter
@AllArgsConstructor
public class V4Suggestion {
    private final String titleText;
    private final String subTitleText;
    private final V4ContentType contentType;
    private final V4Store store;

    private final String pid;
    private final String lid;
    private final String imageUrl;

    private final String iteratorUrl;
    private final String clickUrl;
}
