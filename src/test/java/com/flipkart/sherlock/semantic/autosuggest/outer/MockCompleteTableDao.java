package com.flipkart.sherlock.semantic.autosuggest.outer;

import com.flipkart.sherlock.semantic.autosuggest.utils.IOUtils;
import com.flipkart.sherlock.semantic.common.dao.mysql.CompleteTableDao;
import com.flipkart.sherlock.semantic.common.dao.mysql.CompleteTableDao.AutoSuggestDisabled;
import com.flipkart.sherlock.semantic.common.dao.mysql.CompleteTableDao.StorePathRedirect;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhruv.pancholi on 18/10/17.
 */
public class MockCompleteTableDao {

    @Mock
    private CompleteTableDao completeTableDao;

    @Before
    public void setUp() {
        completeTableDao = getCompleteTableDao();
    }

    @Test
    public void testNotNull() {
        Assert.assertNotNull(completeTableDao);
    }

    public CompleteTableDao getCompleteTableDao() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(completeTableDao.getAutoSuggestDisabledQueries()).thenReturn(getAutoSuggestDisabledQueries());
        Mockito.when(completeTableDao.getStorePathRedirect()).thenReturn(getStorePathRedirect());
        Mockito.when(completeTableDao.getStores()).thenReturn(getStores());
        Mockito.when(completeTableDao.getStorePathCanonicalTitles()).thenReturn(getStorePathCanonicalTitles());
        return completeTableDao;
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

    private List<StorePathRedirect> getStorePathRedirect() {
        List<StorePathRedirect> storePathRedirects = new ArrayList<>();
        List<String> lines = IOUtils.openFromResource("store_path_redirect.txt").readLines();
        for (String line : lines) {
            line = line.trim();
            String[] split = line.split("\t");
            if (line.isEmpty()) continue;
            storePathRedirects.add(new StorePathRedirect(split[0], split[1]));
        }
        return storePathRedirects;
    }

    private List<CompleteTableDao.Stores> getStores() {
        List<CompleteTableDao.Stores> stores = new ArrayList<>();
        List<String> lines = IOUtils.openFromResource("stores.txt").readLines();
        for (String line : lines) {
            line = line.trim();
            String[] split = line.split("\t");
            if (line.isEmpty()) continue;
            stores.add(new CompleteTableDao.Stores(split[0], split[1], split[2], split[3], split[4], split[5], split[6], Integer.parseInt(split[7]), split[8]));
        }
        return stores;
    }

    private List<CompleteTableDao.StorePathMetaData> getStorePathCanonicalTitles() {
        List<CompleteTableDao.StorePathMetaData> storePathMetaDatas = new ArrayList<>();
        List<String> lines = IOUtils.openFromResource("store_path.txt").readLines();
        for (String line : lines) {
            line = line.trim();
            String[] split = line.split("\t");
            if (line.isEmpty()) continue;
            storePathMetaDatas.add(new CompleteTableDao.StorePathMetaData(split[0], split[1]));
        }
        return storePathMetaDatas;
    }
}
