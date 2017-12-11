package com.flipkart.sherlock.semantic.autosuggest.models.v4;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by dhruv.pancholi on 11/12/17.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "QUERY_STORE", value = V4QueryStore.class),
        @JsonSubTypes.Type(name = "QUERY", value = V4Query.class),
        @JsonSubTypes.Type(name = "PRODUCT", value = V4Product.class),
        @JsonSubTypes.Type(name = "RICH", value = V4Rich.class),
        @JsonSubTypes.Type(name = "CLP", value = V4CLP.class),
        @JsonSubTypes.Type(name = "PARTITION", value = V4Partition.class)
})
public class V4Suggestion {
    private V4SuggestionType type;
}
