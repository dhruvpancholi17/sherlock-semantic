package com.flipkart.sherlock.semantic.autosuggest.models.v4;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.cards.*;
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
        @JsonSubTypes.Type(name = "QUERY_STORE", value = V4FlashQueryStore.class),
        @JsonSubTypes.Type(name = "QUERY", value = V4FlashQuery.class),
        @JsonSubTypes.Type(name = "PRODUCT", value = V4FlashProduct.class),
        @JsonSubTypes.Type(name = "RICH", value = V4FlashRich.class),
        @JsonSubTypes.Type(name = "CLP", value = V4FlashCLP.class),
        @JsonSubTypes.Type(name = "PARTITION", value = V4FlashPartition.class)
})
public class V4FlashSuggestion {
    private V4FlashSuggestionType type;
}
