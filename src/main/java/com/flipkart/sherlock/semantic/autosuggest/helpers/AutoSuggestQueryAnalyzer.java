package com.flipkart.sherlock.semantic.autosuggest.helpers;

import com.flipkart.sherlock.semantic.autosuggest.dao.AutoSuggestDisabledQueriesDao;
import com.google.inject.Inject;

import java.util.Map;

/**
 * Created by dhruv.pancholi on 31/05/17.
 */
public class AutoSuggestQueryAnalyzer {

    @Inject
    private AutoSuggestDisabledQueriesDao autoSuggestDisabledQueriesDao;

    public static String getCleanQuery(String query) {
        if (query == null) return query;
        query = query.replaceAll("[+,]", " ");
        query = query.replaceAll("[^A-Za-z0-9&/*_ .'-]", "");
        query = query.trim();
        return query;
    }

    public boolean isDisabled(String query) {
        if (query == null) return false;
        if (query.length() > 48) return true;
        query = query.toLowerCase();

        Map<String, String> negativeMap = autoSuggestDisabledQueriesDao.getMap();
        if (negativeMap == null) return false;

        boolean isDisabled = false;
        for (int i = 1; i <= query.length(); i++) {
            String prefix = query.substring(0, i);
            isDisabled = negativeMap.containsKey(prefix.toLowerCase());
            if (isDisabled) break;
        }
        return isDisabled;
    }
}
