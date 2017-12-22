package com.flipkart.sherlock.semantic.commons.cache;

import com.flipkart.sherlock.semantic.commons.hystrix.HystrixExecutor;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by anurag.laddha on 20/12/17.
 */

/**
 * Cache (ideally remote) backed by data source.
 * Can optionally use in memory cache.
 *
 * Access pattern: In memory -> cache -> data source. Data is accessed in following order, go to next source only if not found at current layer and populate previous layers if data is found at some layer.
 * There should be no PUT function in this class. Data shall be fetched from data source.
 */

@Slf4j
@ThreadSafe
public class CacheWithDataSource<K,V> {

    private Cache<K,V> inMemCache = null;
    private ICache<K,V> remoteCache;
    private IDataSource<K,V> dataSource;
    private ExecutorService executorService;
    private HystrixExecutor hystrixExecutor;
    private String dataSourceHystrixConfigKey;
    private String remoteCacheHystrixConfigKey;
    private final Object syncObj = new Object();

    /**
     * @param remoteCache: remote cache
     * @param dataSource: actual data source
     * @param hystrixExecutor
     * @param remoteCacheHystrixConfigKey: name of config key for hystrix parameters for calling remote cache
     * @param dataSourceHystrixConfigKey: name of config key for hystrix parameters for calling data source
     * @param inMemoryCacheConfigOpt: Optional configuration for in memory cache. If optional value is present, in memory cache will be used
     */
    public CacheWithDataSource(ICache<K, V> remoteCache, IDataSource<K, V> dataSource, ExecutorService executorService,
                               HystrixExecutor hystrixExecutor, String remoteCacheHystrixConfigKey, String dataSourceHystrixConfigKey,
                               Optional<InMemoryCacheConfig> inMemoryCacheConfigOpt) {

        this.remoteCache = remoteCache;
        this.remoteCacheHystrixConfigKey = remoteCacheHystrixConfigKey;
        this.executorService = executorService;
        this.hystrixExecutor = hystrixExecutor;
        this.dataSource = dataSource;
        this.dataSourceHystrixConfigKey = dataSourceHystrixConfigKey;

        if (inMemoryCacheConfigOpt.isPresent()){
            InMemoryCacheConfig inMemoryCacheConfig = inMemoryCacheConfigOpt.get();
            inMemCache = CacheBuilder.newBuilder()
                .maximumSize(inMemoryCacheConfig.getMaxKeyCount())
                .expireAfterWrite(inMemoryCacheConfig.getExpireAfterWriteSec(), TimeUnit.SECONDS)
                .build();
        }
    }

    /**
     * Get value for given key.
     * First looks in inmemory cache, if available, then in remote cache, then in data source
     */
    public V get(K key){
        V value = null;
        if (key != null){

            /**
             * Get value from in memory cache, if enabled
             */
            if (inMemCache != null){
                log.debug("Evaluating inmemory cache for key: {}", key);
                value = inMemCache.getIfPresent(key);
            }

            /**
             * Get value from remote cache. Use hystrix if enabled, else directly call cache
             * If value is returned, populate in memory cache
             */

            if (value == null) {
                log.debug("Evaluating remote cache for key: {}", key);
                value = this.hystrixExecutor.executeSync(this.remoteCacheHystrixConfigKey, () -> this.remoteCache.get(key));
                if (value != null && inMemCache != null){
                    this.inMemCache.put(key, value);
                }
            }

            /**
             * Get value from data source
             * If value is returned, populate in memory cache and remote cache
             */
            if (value == null){
                log.debug("Evaluating data source for key: {}", key);
                value = this.hystrixExecutor.executeSync(this.dataSourceHystrixConfigKey, () -> this.dataSource.get(key));

                if (value != null){
                    V copyValue = value; //effectively final value for usage in functional interface

                    if (this.inMemCache != null) {
                        this.inMemCache.put(key, value);
                    }

                    this.hystrixExecutor.executeSync(this.remoteCacheHystrixConfigKey, () -> {  //populate this async?
                        this.remoteCache.put(key, copyValue);
                        return null;
                    });
                }
            }
        }
        return value;
    }


    /**
     * Asynchronously populate local (if available) and remote cache with data from data source
     * @param key
     */
    public void populateFromSourceAsync(K key){
        if (key != null){   //use hystrix? make this behavior part of get() with some param?
            CompletableFuture.supplyAsync(() -> {
                        log.debug("Fetching data from source for key: {}", key);
                        return this.dataSource.get(key);
                    }, this.executorService)
                .thenAccept((data) -> {
                    log.debug("Populating remote and local cache for key: {}", key);
                    this.remoteCache.put(key, data);
                    if (this.inMemCache != null){
                        this.inMemCache.put(key, data);
                    }
                })
                .exceptionally((ex) -> {
                    log.error("Error while populating data async for key: {}", key, ex);
                    return null;
                });
        }
    }


    /**
     * Shutdown cache
     */
    public void shutdown() {
        synchronized (this.syncObj) {
            if (this.inMemCache != null) {
                this.inMemCache.invalidateAll();
            }
            this.remoteCache.shutdown();
            this.dataSource.cleanup();
        }
    }

    /**
     * In memory cache configuration
     */
    @Getter
    @AllArgsConstructor
    @ToString
    public static class InMemoryCacheConfig{
        /**
         * Max number of keys to be kept in memory
         */
        private int maxKeyCount;

        /**
         * Number of seconds
         */
        private int expireAfterWriteSec;
    }
}
