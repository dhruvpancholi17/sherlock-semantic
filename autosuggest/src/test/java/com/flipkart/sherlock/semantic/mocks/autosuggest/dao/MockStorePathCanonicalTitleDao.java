package com.flipkart.sherlock.semantic.mocks.autosuggest.dao;

import com.flipkart.sherlock.semantic.autosuggest.dao.StorePathCanonicalTitleDao;
import com.flipkart.sherlock.semantic.autosuggest.utils.IOUtils;
import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.flipkart.sherlock.semantic.common.dao.mysql.CompleteTableDao;
import com.flipkart.sherlock.semantic.common.dao.mysql.CompleteTableDao.StorePathMetaData;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhruv.pancholi on 16/10/17.
 */
public class MockStorePathCanonicalTitleDao {

    @Mock
    private CompleteTableDao completeTableDao;

    public StorePathCanonicalTitleDao getStorePathCanonicalTitleDao() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(completeTableDao.getStorePathCanonicalTitles()).thenReturn(getStorePathCanonicalTitles());
        return new StorePathCanonicalTitleDao(completeTableDao, JsonSeDe.getInstance());
    }

    private List<StorePathMetaData> getStorePathCanonicalTitles() {
        List<StorePathMetaData> storePathMetaDatas = new ArrayList<>();
        List<String> lines = IOUtils.openFromResource("store_path.txt").readLines();
        for (String line : lines) {
            line = line.trim();
            String[] split = line.split("\t");
            if (line.isEmpty()) continue;
            storePathMetaDatas.add(new StorePathMetaData(split[0], split[1]));
        }
        return storePathMetaDatas;
    }
}
