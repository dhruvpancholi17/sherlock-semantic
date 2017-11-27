package com.flipkart.sherlock.semantic.autosuggest.dao;

import com.flipkart.sherlock.semantic.mocks.autosuggest.dao.MockRedirectionStoreDao;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by dhruv.pancholi on 16/10/17.
 */
public class RedirectionStoreDaoTest {

    @Test
    public void testMock() {
        RedirectionStoreDao redirectionStoreDao = new MockRedirectionStoreDao().getRedirectionStoreDao();
        Assert.assertTrue(redirectionStoreDao.getMap().size() > 0);
        redirectionStoreDao.reloadCache();
        Assert.assertEquals(redirectionStoreDao.getGenericMap().size(), redirectionStoreDao.size());
        Assert.assertEquals(redirectionStoreDao.get("0pm/0o7/ejj"), "0pm/0o7");
        Assert.assertEquals(redirectionStoreDao.get("6bo/ord/713/fp0/f2n"), "0pm/fcn");
        Assert.assertEquals(redirectionStoreDao.get("6bo/ord/713/fp0/83e/fb6"), "0pm/fcn");
        Assert.assertEquals(redirectionStoreDao.get("tyy/4mr/qtx"), "0pm/fcn");
        Assert.assertEquals(redirectionStoreDao.get("tyy/4mr/fp0/83e/c4l"), "0pm/fcn");
        Assert.assertEquals(redirectionStoreDao.get("anx"), "wwe");
        Assert.assertEquals(redirectionStoreDao.get("anx/baj/kdm/rbu"), "wwe/xdf/fcg");
        Assert.assertEquals(redirectionStoreDao.get("anx/baj/109"), "wwe/v1j/vvt");
    }
}