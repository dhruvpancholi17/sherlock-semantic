package com.flipkart.sherlock.semantic.core.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * Created by anurag.laddha on 23/05/17.
 */
@AllArgsConstructor
@Getter
@ToString
public class SearchResponse {

    private String solrQuery;
    private List<Map<String, Object>> results;

}
