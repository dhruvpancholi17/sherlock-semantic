package com.flipkart.sherlock.semantic.mocks.autosuggest.helpers;

import com.flipkart.sherlock.semantic.autosuggest.dao.StoreMarketPlaceDao;
import com.flipkart.sherlock.semantic.autosuggest.helpers.MarketAnalyzer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static com.flipkart.sherlock.semantic.autosuggest.helpers.MarketAnalyzer.FLIP_KART;
import static com.flipkart.sherlock.semantic.autosuggest.helpers.MarketAnalyzer.FLIP_MART;

/**
 * Created by dhruv.pancholi on 16/10/17.
 */
public class MockMarketAnalyzer {

    @Mock
    private StoreMarketPlaceDao storeMarketPlaceDao;

    @InjectMocks
    private MarketAnalyzer marketAnalyzer;

    public MarketAnalyzer getMarketAnalyzer() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(storeMarketPlaceDao.getMarketPlaceId("73z")).thenReturn(FLIP_MART);
        Mockito.when(storeMarketPlaceDao.getMarketPlaceId("tyy")).thenReturn(FLIP_KART);
        return marketAnalyzer;
    }
}
