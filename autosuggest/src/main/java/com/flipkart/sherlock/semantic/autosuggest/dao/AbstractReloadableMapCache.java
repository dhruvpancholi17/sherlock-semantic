package com.flipkart.sherlock.semantic.autosuggest.dao;

import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.flipkart.sherlock.semantic.common.dao.mysql.CompleteTableDao;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by dhruv.pancholi on 31/05/17.
 */
@Slf4j
public abstract class AbstractReloadableMapCache<ValueType> {

    CompleteTableDao completeTableDao;

    protected JsonSeDe jsonSeDe;

    private LoadingCache<String, Map<String, ValueType>> cache;

    AbstractReloadableMapCache(CompleteTableDao completeTableDao, JsonSeDe jsonSeDe, int refreshTime, TimeUnit unit) {
        this.completeTableDao = completeTableDao;
        this.jsonSeDe = jsonSeDe;
        cache = CacheBuilder.newBuilder()
                .maximumSize(1)
                .refreshAfterWrite(refreshTime, unit).build(new CacheLoader<String, Map<String, ValueType>>() {
                    @Override
                    public Map<String, ValueType> load(String s) throws Exception {
                        return getFromSource();
                    }
                });
    }

    public ValueType get(String key) {
        return getCached().get(key);
    }

    public int size() {
        return getCached().size();
    }

    public Map<String, ValueType> getMap() {
        return getCached();
    }

    Map<String, ValueType> getCached() {
        try {
            return cache.get("key");
        } catch (ExecutionException e) {
            log.error("{}", e);
        }
        return null;
    }

    protected abstract Map<String, ValueType> getFromSource();

    Map<String, Object> getGenericMap() {
        Map<String, Object> genericMap = new HashMap<>();
        Map<String, ValueType> map = getCached();
        Set<Entry<String, ValueType>> entrySet = map.entrySet();
        for (Entry<String, ValueType> stringValueTypeEntry : entrySet) {
            genericMap.put(stringValueTypeEntry.getKey(), stringValueTypeEntry.getValue());
        }
        return genericMap;
    }

    void reloadCache() {
        for (String key : cache.asMap().keySet()) {
            cache.refresh(key);
        }
    }
}
