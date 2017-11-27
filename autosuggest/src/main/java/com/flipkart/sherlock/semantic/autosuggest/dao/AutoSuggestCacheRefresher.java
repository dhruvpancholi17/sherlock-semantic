package com.flipkart.sherlock.semantic.autosuggest.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.*;

/**
 * Created by dhruv.pancholi on 31/05/17.
 */
@Singleton
public class AutoSuggestCacheRefresher {

    private Map<String, AbstractReloadableMapCache> abstractReloadableMapCaches;

    @Inject
    public AutoSuggestCacheRefresher(AutoSuggestDisabledQueriesDao autoSuggestDisabledQueriesDao,
                                     RedirectionStoreDao redirectionStoreDao,
                                     StorePathCanonicalTitleDao storePathCanonicalTitleDao) {

        abstractReloadableMapCaches = new HashMap<>();
        abstractReloadableMapCaches.put(autoSuggestDisabledQueriesDao.getClass().getSimpleName(), autoSuggestDisabledQueriesDao);
        abstractReloadableMapCaches.put(redirectionStoreDao.getClass().getSimpleName(), redirectionStoreDao);
        abstractReloadableMapCaches.put(storePathCanonicalTitleDao.getClass().getSimpleName(), storePathCanonicalTitleDao);
    }

    public Map<String, Integer> refreshCache(String daos) {
        Set<String> daoList = abstractReloadableMapCaches.keySet();
        if (daos != null && !daos.isEmpty()) daoList = new HashSet<>(Arrays.asList(daos.split(",")));

        Map<String, Integer> countMap = new HashMap<>();

        for (String dao : daoList) {
            if (abstractReloadableMapCaches.containsKey(dao)) {
                AbstractReloadableMapCache abstractReloadableMapCache = abstractReloadableMapCaches.get(dao);
                abstractReloadableMapCache.reloadCache();
                countMap.put(dao, abstractReloadableMapCache.size());
            }
        }
        return countMap;
    }

    public Map<String, Object> cacheGet(String dao, String keys) {

        if (dao == null || dao.isEmpty()) throw new RuntimeException("Please provide a dao to access values for.");
        if (keys == null) throw new RuntimeException("Please add a parameter keys, leave empty to fetch all keys.");

        Set<String> keySet = new HashSet<>(Arrays.asList(keys.split(",")));
        keySet.remove("");

        AbstractReloadableMapCache abstractReloadableMapCache = abstractReloadableMapCaches.get(dao);
        if (abstractReloadableMapCache == null) throw new RuntimeException("Dao specified in request not found.");

        Map<String, Object> returnMap = new HashMap<>();

        Map daoGenericMap = abstractReloadableMapCache.getGenericMap();

        if (keySet.isEmpty()) keySet = daoGenericMap.keySet();
        for (String key : keySet) returnMap.put(key, daoGenericMap.get(key));

        return returnMap;
    }
}
