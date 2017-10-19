package com.flipkart.sherlock.semantic.autosuggest.dao;

import com.flipkart.sherlock.semantic.mocks.autosuggest.dao.MockStoreMarketPlaceDao;
import org.junit.Test;

import static com.flipkart.sherlock.semantic.autosuggest.helpers.MarketAnalyzer.FLIP_KART;
import static com.flipkart.sherlock.semantic.autosuggest.helpers.MarketAnalyzer.FLIP_MART;
import static org.junit.Assert.assertEquals;

/**
 * Created by dhruv.pancholi on 16/10/17.
 */
public class StoreMarketPlaceDaoTest {
    @Test
    public void testMock() {
        StoreMarketPlaceDao storeMarketPlaceDao = new MockStoreMarketPlaceDao().getStoreMarketPlaceDao();
        assertEquals(FLIP_MART, storeMarketPlaceDao.getMarketPlaceId("73z"));
        assertEquals(FLIP_KART, storeMarketPlaceDao.getMarketPlaceId("tyy"));
        assertEquals(FLIP_KART, storeMarketPlaceDao.getMarketPlaceId("osp"));
    }
}