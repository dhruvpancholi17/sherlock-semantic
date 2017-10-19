package com.flipkart.sherlock.semantic.mocks.autosuggest.dao;

import com.flipkart.sherlock.semantic.autosuggest.dao.AutoSuggestDisabledQueriesDao;
import com.flipkart.sherlock.semantic.autosuggest.utils.IOUtils;
import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.flipkart.sherlock.semantic.common.dao.mysql.CompleteTableDao;
import com.flipkart.sherlock.semantic.common.dao.mysql.CompleteTableDao.AutoSuggestDisabled;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhruv.pancholi on 16/10/17.
 */
public class MockAutoSuggestDisabledQueriesDao {

    @Mock
    private CompleteTableDao completeTableDao;

    public AutoSuggestDisabledQueriesDao getAutoSuggestDisabledQueriesDao() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(completeTableDao.getAutoSuggestDisabledQueries()).thenReturn(getAutoSuggestDisabledQueries());
        return new AutoSuggestDisabledQueriesDao(completeTableDao, JsonSeDe.getInstance());
    }

    private List<AutoSuggestDisabled> getAutoSuggestDisabledQueries() {
        List<AutoSuggestDisabled> autoSuggestDisabledList = new ArrayList<>();
        List<String> lines = IOUtils.openFromResource("autosuggest_disabled_queries.txt").readLines();
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            autoSuggestDisabledList.add(new AutoSuggestDisabled(line));
        }
        return autoSuggestDisabledList;
    }
}
