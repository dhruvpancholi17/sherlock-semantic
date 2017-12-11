package com.flipkart.sherlock.semantic.autosuggest.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.flipkart.sherlock.semantic.autosuggest.helpers.MarketAnalyzer;
import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.flipkart.sherlock.semantic.common.dao.mysql.CompleteTableDao;
import com.flipkart.sherlock.semantic.common.dao.mysql.CompleteTableDao.Stores;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.flipkart.sherlock.semantic.autosuggest.helpers.MarketAnalyzer.FLIP_MART;

/**
 * Created by dhruv.pancholi on 30/05/17.
 */
@Slf4j
@Singleton
public class StoreMarketPlaceDao {

    private Map<String, String> storeMarketPlaceMap;

    private static TypeReference<Map<String, Object>> metadataTypereference = new TypeReference<Map<String, Object>>() {
    };

    @Inject
    public StoreMarketPlaceDao(CompleteTableDao completeTableDao, JsonSeDe jsonSeDe) {

        storeMarketPlaceMap = new HashMap<>();

        List<Stores> stores = completeTableDao.getStores();
        for (Stores store : stores) {
            String storeId = store.getId();
            if (storeId == null || storeId.isEmpty()) continue;
            if (storeId.startsWith("/")) storeId = storeId.substring(1);

            String metadata = store.getMetadata();
            if (metadata == null || metadata.isEmpty()) continue;

            Map<String, Object> metaMap = null;
            try {
                metaMap = jsonSeDe.readValue(metadata, metadataTypereference);
            } catch (Exception e) {
                log.error("Cannot de-serialize metadata {} for store: {}", metadata, store.getId(), e);
            }


            if (metaMap == null || !metaMap.containsKey("marketplaceId") || metaMap.get("marketplaceId") == null) {
                continue;
            }

            String marketplaceId = String.valueOf(metaMap.get("marketplaceId"));
            marketplaceId = marketplaceId.toLowerCase();
            if ("grocery".equals(marketplaceId)) marketplaceId = FLIP_MART;
            storeMarketPlaceMap.put(storeId, marketplaceId);
        }
    }

    public String getMarketPlaceId(String store) {
        if (!storeMarketPlaceMap.containsKey(store)) return MarketAnalyzer.FLIP_KART;
        return storeMarketPlaceMap.get(store);
    }
}
