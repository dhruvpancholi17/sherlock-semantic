package com.flipkart.sherlock.semantic.mocks.autosuggest.helpers;

import com.flipkart.sherlock.semantic.autosuggest.dao.AutoSuggestDisabledQueriesDao;
import com.flipkart.sherlock.semantic.autosuggest.helpers.AutoSuggestQueryAnalyzer;
import com.google.common.collect.ImmutableMap;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Created by dhruv.pancholi on 16/10/17.
 */
public class MockAutoSuggestQueryAnalyzer {

    @Mock
    private AutoSuggestDisabledQueriesDao autoSuggestDisabledQueriesDao;

    @InjectMocks
    private AutoSuggestQueryAnalyzer autoSuggestQueryAnalyzer;


    public AutoSuggestQueryAnalyzer getAutoSuggestQueryAnalyzer() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(autoSuggestDisabledQueriesDao.getMap()).thenReturn(ImmutableMap.of("pan", ""));
        return autoSuggestQueryAnalyzer;
    }
}
