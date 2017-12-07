package com.flipkart.sherlock.semantic.autosuggest.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * Created by dhruv.pancholi on 07/12/17.
 */
@Getter
@AllArgsConstructor
public class UserInsightResponse {
    private final Map<String, Integer> storeWeight;
}
