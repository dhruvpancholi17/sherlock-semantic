package com.flipkart.sherlock.semantic.v4;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.flipkart.sherlock.semantic.v4.cards.*;


/**
 * Created by dhruv.pancholi on 11/12/17.
 */
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

    @java.beans.ConstructorProperties({"type"})
    public V4FlashSuggestion(V4FlashSuggestionType type) {
        this.type = type;
    }

    public V4FlashSuggestion() {
    }

    public V4FlashSuggestionType getType() {
        return this.type;
    }

    public void setType(V4FlashSuggestionType type) {
        this.type = type;
    }
}
