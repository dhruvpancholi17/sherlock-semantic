package com.flipkart.sherlock.semantic.autosuggest.models.v4.types;

import com.flipkart.sherlock.semantic.autosuggest.models.v4.V4ContentType;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.V4Suggestion;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.V4SuggestionRow;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.V4SuggestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by dhruv.pancholi on 06/12/17.
 */
@Builder
@Getter
public class V4Query extends V4SuggestionRow {
    private final String clickUrl;
    private final V4ContentType contentType;
    private final String query;
}
