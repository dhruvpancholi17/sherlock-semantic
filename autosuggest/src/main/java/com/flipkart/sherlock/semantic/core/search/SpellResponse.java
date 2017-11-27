package com.flipkart.sherlock.semantic.core.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * Created by anurag.laddha on 23/05/17.
 */
@AllArgsConstructor
@Getter
@ToString
public class SpellResponse {

    @AllArgsConstructor
    @Getter
    public static class SpellSuggestion{
        private String original;
        private List<String> suggestions;
    }

    private String solrQuery;

    private List<SpellSuggestion> spellSuggestions;

}
