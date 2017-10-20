package com.flipkart.sherlock.semantic.autosuggest.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

/**
 * Created by dhruv.pancholi on 01/06/17.
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class QuerySuggestion {
    private String query;
    private List<Store> stores;
}
