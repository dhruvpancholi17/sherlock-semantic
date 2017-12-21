package com.flipkart.sherlock.semantic.commons.cache;

/**
 * Created by anurag.laddha on 20/12/17.
 */

/**
 * Read only ata source
 */
public interface IDataSource<K,V> {
    void prepare();
    V get(K key);
    void cleanup();
}
