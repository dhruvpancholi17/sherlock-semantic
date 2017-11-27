package com.flipkart.sherlock.semantic.autosuggest.dao;

import com.flipkart.sherlock.semantic.mocks.autosuggest.dao.MockAutoSuggestDisabledQueriesDao;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by dhruv.pancholi on 16/10/17.
 */
public class AutoSuggestDisabledQueriesDaoTest {

    @Test
    public void testMock() {
        AutoSuggestDisabledQueriesDao autoSuggestDisabledQueriesDao = new MockAutoSuggestDisabledQueriesDao().getAutoSuggestDisabledQueriesDao();
        Assert.assertTrue(autoSuggestDisabledQueriesDao.getMap().size() > 0);
        autoSuggestDisabledQueriesDao.reloadCache();
        Assert.assertEquals(autoSuggestDisabledQueriesDao.getGenericMap().size(), autoSuggestDisabledQueriesDao.size());
        Assert.assertEquals(autoSuggestDisabledQueriesDao.get("jun"), "jun");
    }
}