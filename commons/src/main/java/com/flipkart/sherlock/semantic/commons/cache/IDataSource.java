package com.flipkart.sherlock.semantic.commons.cache;

/**
 * Created by anurag.laddha on 20/12/17.
 */

/**
 * Read only data source
 */
public interface IDataSource<K,V> {
    /**
     * Resource setup
     */
    void prepare();

    /**
     * Get value for given key
     */
    V get(K key);

    /**
     * Clean up resources
     */
    void cleanup();
}
