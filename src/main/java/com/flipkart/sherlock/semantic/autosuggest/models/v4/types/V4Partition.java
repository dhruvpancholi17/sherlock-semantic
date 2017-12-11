package com.flipkart.sherlock.semantic.autosuggest.models.v4.types;

import com.flipkart.sherlock.semantic.autosuggest.models.v4.V4SuggestionRow;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by dhruv.pancholi on 06/12/17.
 */
@Builder
@Getter
public class V4Partition extends V4SuggestionRow {
    private final String title;
}
