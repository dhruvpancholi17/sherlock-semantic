package com.flipkart.sherlock.semantic.mocks.autosuggest.dao;

import com.flipkart.sherlock.semantic.autosuggest.dao.RedirectionStoreDao;
import com.flipkart.sherlock.semantic.autosuggest.utils.IOUtils;
import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.flipkart.sherlock.semantic.common.dao.mysql.CompleteTableDao;
import com.flipkart.sherlock.semantic.common.dao.mysql.CompleteTableDao.StorePathRedirect;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhruv.pancholi on 16/10/17.
 */
public class MockRedirectionStoreDao {

    @Mock
    private CompleteTableDao completeTableDao;

    public RedirectionStoreDao getRedirectionStoreDao() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(completeTableDao.getStorePathRedirect()).thenReturn(getStorePathRedirect());
        return new RedirectionStoreDao(completeTableDao, JsonSeDe.getInstance());
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
}
