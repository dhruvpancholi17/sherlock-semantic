package com.flipkart.sherlock.semantic.autosuggest.models.v4;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.types.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by dhruv.pancholi on 07/11/17.
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @Type(name = "QUERY_STORE", value = V4QueryStore.class),
        @Type(name = "QUERY", value = V4Query.class),
        @Type(name = "PRODUCT", value = V4Product.class),
        @Type(name = "RICH", value = V4Rich.class),
        @Type(name = "CLP", value = V4CLP.class),
        @Type(name = "PARTITION", value = V4Partition.class)
})
public class V4SuggestionRow {
    private V4SuggestionType type;
}
