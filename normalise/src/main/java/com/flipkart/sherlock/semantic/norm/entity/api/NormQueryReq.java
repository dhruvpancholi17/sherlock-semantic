package com.flipkart.sherlock.semantic.norm.entity.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by anurag.laddha on 28/11/17.
 */


/**
 * Request object to normalise queries
 */

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class NormQueryReq {
    /**
     * List of queries
     */
    @NotNull
    @JsonProperty
    private List<String> data;

    /**
     * Store/Category context
     */
    @JsonProperty
    private String store;

    /**
     * Marketplace context
     */
    @JsonProperty
    private String marketPlaceId;
}
