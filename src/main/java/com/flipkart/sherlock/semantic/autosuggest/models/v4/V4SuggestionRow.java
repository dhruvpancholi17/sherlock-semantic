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
public class V4SuggestionRow {
    private final V4SuggestionType type;
    private final V4Suggestion content;
}
