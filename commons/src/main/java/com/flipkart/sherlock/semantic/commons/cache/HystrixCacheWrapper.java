package com.flipkart.sherlock.semantic.commons.cache;

import com.flipkart.sherlock.semantic.commons.hystrix.HystrixExecutor;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.concurrent.ThreadSafe;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by anurag.laddha on 20/12/17.
 */

/**
 * Hystrix wrapper for any ICache implementation
 * Executes cache commands through hystrix and fetches hystrix configurations from config service
 */
@Slf4j
@ThreadSafe
public class HystrixCacheWrapper<K,V> implements ICache<K,V> {
    private ICache<K,V> cache;
    private Cache<K,V> inMemCache = null;
    private HystrixExecutor hystrixExecutor;
    private String hystrixConfigKey;

    /**
     *
     * @param cache: remote cache
     * @param hystrixExecutor
     * @param hystrixConfigKey: name of config key for hystrix parameters for calling remote cache
     * @param inMemoryCacheConfigOpt: Optional configuration for in memory cache. If optional value is present, in memory cache will be used
     */
    public HystrixCacheWrapper(ICache<K, V> cache, HystrixExecutor hystrixExecutor, String hystrixConfigKey,
                               Optional<InMemoryCacheConfig> inMemoryCacheConfigOpt) {
        this.cache = cache;
        this.hystrixExecutor = hystrixExecutor;
        this.hystrixConfigKey = hystrixConfigKey;

        if (inMemoryCacheConfigOpt.isPresent()){
            InMemoryCacheConfig inMemoryCacheConfig = inMemoryCacheConfigOpt.get();
            inMemCache = CacheBuilder.newBuilder()
                .maximumSize(inMemoryCacheConfig.getMaxKeyCount())
                .expireAfterWrite(inMemoryCacheConfig.getExpireAfterWriteSec(), TimeUnit.SECONDS)
                .build();
        }
    }

    @Override
    public V get(K key) {
        V value = null;
        if (this.inMemCache != null){
            value = this.inMemCache.getIfPresent(key);
        }

        if (value == null) {
            log.debug("Fetching from remote cache");
            value = this.hystrixExecutor.executeSync(this.hystrixConfigKey, () -> this.cache.get(key));
            if (value != null && inMemCache != null){
                this.inMemCache.put(key, value);
            }
        }

        return value;
    }

    @Override
    public void put(K key, V value) {
        if (key != null && value != null){
            if (this.inMemCache != null){
                this.inMemCache.put(key, value);
            }
            this.hystrixExecutor.executeSync(this.hystrixConfigKey, () -> {
                this.cache.put(key, value);
                return null;
            });
        }
    }

    @Override
    public void put(K key, V value, int timeToLiveSec) {
        if (key != null && value != null){
            if (this.inMemCache != null){
                this.inMemCache.put(key, value);
            }

            this.hystrixExecutor.executeSync(this.hystrixConfigKey, () -> {
                this.cache.put(key, value, timeToLiveSec);
                return null;
            });
        }
    }

    @Override
    public Map<K, V> getBulk(Iterable<K> keys) {
        if (keys !=  null){
            Map<K,V> allKeyValueMap = new HashMap<>();
            Set<K> missingKeys = Sets.newHashSet(keys);
            if (this.inMemCache != null){
                Map<K,V> inMemValues = this.inMemCache.getAllPresent(keys);
                if (inMemValues != null && inMemValues.size() > 0){
                    missingKeys.removeAll(inMemValues.keySet());
                    allKeyValueMap.putAll(inMemValues);
                }
            }

            if (missingKeys.size() > 0){
                log.debug("Fetching from remote cache, keys: {}", missingKeys);
                allKeyValueMap.putAll(this.hystrixExecutor.executeSync(this.hystrixConfigKey,
                    () -> this.cache.getBulk(missingKeys)));
            }

            return allKeyValueMap;
        }

        return null;
    }

    @Override
    public boolean remove(K key) {
        if (key != null){
            if (this.inMemCache != null){
                this.inMemCache.invalidate(key);
            }
            return this.hystrixExecutor.executeSync(this.hystrixConfigKey, () -> this.cache.remove(key));
        }
        return false;
    }

    @Override
    public void shutdown() {
        if (this.inMemCache != null){
            this.inMemCache.invalidateAll();
        }
        this.cache.shutdown();
    }
}
