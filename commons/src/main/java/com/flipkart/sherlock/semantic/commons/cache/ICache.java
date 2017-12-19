package com.flipkart.sherlock.semantic.commons.cache;

import java.util.Map;

/**
 * Created by anurag.laddha on 19/12/17.
 */
public interface ICache<K,V> {
    /**
     * Get value for given key
     */
    V get(K key);

    /**
     * Set value in cache for given key
     */
    void put(K key, V value);

    /**
     * Set value in cache for given key, to be expired after given time to live
     */
    void put(K key, V value, int timeToLiveSec);

    /**
     * Get values from cache given set of keys
     */
    Map<K,V> getBulk(Iterable<K> keys);


    void shutdown();
}
