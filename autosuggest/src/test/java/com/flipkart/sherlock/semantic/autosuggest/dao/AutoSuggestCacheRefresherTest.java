package com.flipkart.sherlock.semantic.autosuggest.dao;

import com.flipkart.sherlock.semantic.mocks.autosuggest.dao.MockAutoSuggestDisabledQueriesDao;
import com.flipkart.sherlock.semantic.mocks.autosuggest.dao.MockRedirectionStoreDao;
import com.flipkart.sherlock.semantic.mocks.autosuggest.dao.MockStorePathCanonicalTitleDao;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by dhruv.pancholi on 16/10/17.
 */
public class AutoSuggestCacheRefresherTest {

    private AutoSuggestCacheRefresher autoSuggestCacheRefresher;

    @Before
    public void setUp() {
        AutoSuggestDisabledQueriesDao autoSuggestDisabledQueriesDao = new MockAutoSuggestDisabledQueriesDao()
                .getAutoSuggestDisabledQueriesDao();
        RedirectionStoreDao redirectionStoreDao = new MockRedirectionStoreDao()
                .getRedirectionStoreDao();
        StorePathCanonicalTitleDao storePathCanonicalTitleDao = new MockStorePathCanonicalTitleDao()
                .getStorePathCanonicalTitleDao();
        autoSuggestCacheRefresher = new AutoSuggestCacheRefresher(autoSuggestDisabledQueriesDao,
                redirectionStoreDao, storePathCanonicalTitleDao);
    }

    //TODO
    @Test
    public void refreshCache() throws Exception {
        autoSuggestCacheRefresher.refreshCache(null);
    }

    //TODO
    @Test
    public void cacheGet() throws Exception {
        autoSuggestCacheRefresher.cacheGet("AutoSuggestDisabledQueriesDao", "");
        autoSuggestCacheRefresher.cacheGet("RedirectionStoreDao", "");
        autoSuggestCacheRefresher.cacheGet("StorePathCanonicalTitleDao", "");
    }

}