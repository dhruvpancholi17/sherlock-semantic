package com.flipkart.sherlock.semantic.commons.cache;

/**
 * Created by anurag.laddha on 20/12/17.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * In memory cache configuration
 */
@Getter
@AllArgsConstructor
@ToString
public  class InMemoryCacheConfig{
    /**
     * Max number of keys to be kept in memory
     */
    private int maxKeyCount;

    /**
     * Number of seconds
     */
    private int expireAfterWriteSec;
}
