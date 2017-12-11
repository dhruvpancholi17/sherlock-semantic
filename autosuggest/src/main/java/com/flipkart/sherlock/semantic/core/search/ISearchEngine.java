package com.flipkart.sherlock.semantic.core.search;

import java.util.Map;

/**
 * Created by anurag.laddha on 23/05/17.
 */
public interface ISearchEngine {

    enum SearchParam {
        CORE,
        EXPERIMENT,
        HOST,
        PORT
    }

    /**
     * Query engine for searching relevant documents
     * @return
     */
    SearchResponse query(SearchRequest request, Map<SearchParam, String> params);

    /**
     * Query search engine for spell correction
     * @param request: spell check request
     * @return
     */
    SpellResponse spellQuery(SearchRequest request, Map<SearchParam, String> params);
}
