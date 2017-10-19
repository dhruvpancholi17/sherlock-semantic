package com.flipkart.sherlock.semantic.autosuggest.dao;

import com.flipkart.sherlock.semantic.mocks.autosuggest.dao.MockStorePathCanonicalTitleDao;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by dhruv.pancholi on 16/10/17.
 */
public class StorePathCanonicalTitleDaoTest {
    @Test
    public void testMock() {
        StorePathCanonicalTitleDao storePathCanonicalTitleDao = new MockStorePathCanonicalTitleDao().getStorePathCanonicalTitleDao();
        assertEquals("Mobiles", storePathCanonicalTitleDao.get("tyy/4io"));
    }

}