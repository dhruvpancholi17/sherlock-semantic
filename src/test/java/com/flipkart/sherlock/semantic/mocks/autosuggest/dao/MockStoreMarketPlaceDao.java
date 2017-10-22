package com.flipkart.sherlock.semantic.mocks.autosuggest.dao;

import com.flipkart.sherlock.semantic.autosuggest.dao.StoreMarketPlaceDao;
import com.flipkart.sherlock.semantic.autosuggest.utils.IOUtils;
import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.flipkart.sherlock.semantic.common.dao.mysql.CompleteTableDao;
import com.flipkart.sherlock.semantic.common.dao.mysql.CompleteTableDao.Stores;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhruv.pancholi on 16/10/17.
 */
public class MockStoreMarketPlaceDao {

    @Mock
    private CompleteTableDao completeTableDao;

    public StoreMarketPlaceDao getStoreMarketPlaceDao() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(completeTableDao.getStores()).thenReturn(getStores());
        return new StoreMarketPlaceDao(completeTableDao, JsonSeDe.getInstance());
    }

    private List<Stores> getStores() {
        List<Stores> stores = new ArrayList<>();
        List<String> lines = IOUtils.openFromResource("stores.txt").readLines();
        for (String line : lines) {
            line = line.trim();
            String[] split = line.split("\t");
            if (line.isEmpty()) continue;
            stores.add(new Stores(split[0], split[1], split[2], split[3], split[4], split[5], split[6], Integer.parseInt(split[7]), split[8]));
        }
        return stores;
    }
}
