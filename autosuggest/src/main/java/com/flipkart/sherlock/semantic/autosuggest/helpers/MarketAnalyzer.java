package com.flipkart.sherlock.semantic.autosuggest.helpers;

import com.flipkart.sherlock.semantic.autosuggest.dao.StoreMarketPlaceDao;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Arrays;
import java.util.List;

/**
 * Created by dhruv.pancholi on 27/04/17.
 */
@Singleton
public class MarketAnalyzer {

    /**
     * Enum values, based on which the whole grocery filtering is based on
     */
    public static final String FLIP_KART = "flipkart";
    public static final String FLIP_MART = "flipmart";

    public static final List<String> DEFAULT_MARKET_PLACE_IDS = Arrays.asList(FLIP_KART);

    @Inject
    private StoreMarketPlaceDao storeMarketPlaceDao;

    /**
     * For a given path/store, identify if it belongs to flipkart/flipmart/flipfresh
     *
     * @param store
     * @return
     */
    public String getMarketPlaceId(String store) {
        if (store == null || store.isEmpty()) return FLIP_KART;
        String[] nodes = store.split("/");
        return storeMarketPlaceDao.getMarketPlaceId(nodes[0]);
    }

    /**
     * Parse the params that comes from hudson and generate the list of market places
     *
     * @param groceryContext
     * @param marketPlaceId
     * @return
     */
    public List<String> getMarketPlaceIds(String contextStore, String groceryContext, String marketPlaceId) {
        if (groceryContext == null) groceryContext = "false";
        if (groceryContext.toLowerCase().equals("true")) return Arrays.asList(FLIP_MART);

        marketPlaceId = (marketPlaceId == null) ? FLIP_KART : marketPlaceId.toLowerCase();
        if (marketPlaceId.equals("grocery") || FLIP_MART.equals(marketPlaceId))
            return Arrays.asList(FLIP_KART, FLIP_MART);

        return Arrays.asList(FLIP_KART);
    }
}
