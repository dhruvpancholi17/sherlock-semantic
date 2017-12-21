package com.flipkart.sherlock.semantic.commons.cache;

import com.flipkart.sherlock.semantic.commons.CommonITContext;
import com.flipkart.sherlock.semantic.commons.cache.couchbase.CouchbaseConfig;
import com.flipkart.sherlock.semantic.commons.cache.couchbase.FkCouchbaseClient;
import com.flipkart.sherlock.semantic.commons.hystrix.HystrixExecutor;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

/**
 * Created by anurag.laddha on 21/12/17.
 */
public class CacheWithDataSourceTestIT {

    static class StringValueDataSource implements IDataSource<String, String>{
        @Override
        public void prepare() {

        }

        @Override
        public String get(String key) {
            return key + "data";
        }

        @Override
        public void cleanup() {
        }
    }

    static class ListOfStringDataSource implements IDataSource<String, List<String>>{
        @Override
        public void prepare() {
        }

        @Override
        public List<String> get(String key) {
            return Lists.newArrayList(key + "data1", key + "data2");
        }

        @Override
        public void cleanup() {
        }
    }

    @Test
    public void testForSimpleValuesWithoutLocalCache() throws Exception{
        testSimpleValueCache(false);
    }

    @Test
    public void testStringValueCacheWithLocalCache() throws Exception{
        testSimpleValueCache(true);
    }

    private void testSimpleValueCache(boolean useInMemoryCache) throws Exception{

        //Cache setup
        CouchbaseConfig couchbaseConfig = new CouchbaseConfig.CouchbaseConfigBuilder(Sets.newHashSet("http://127.0.0.1:8091/pools"),
            "default", "", 5).build();
        FkCouchbaseClient<String, String> couchbaseClient = new FkCouchbaseClient<>(couchbaseConfig);
        HystrixExecutor hystrixExecutor = CommonITContext.getInstance(HystrixExecutor.class);
        IDataSource<String, String> dataSource = new StringValueDataSource();

        CacheWithDataSource<String, String> cacheWithDataSource = new CacheWithDataSource<>(couchbaseClient, dataSource, hystrixExecutor,
            "searchEngine.search", "searchEngine.search",
            useInMemoryCache ? Optional.of(new CacheWithDataSource.InMemoryCacheConfig(10,100)) : Optional.empty());

        String key1 = "key1";
        String key2 = "key2";
        System.out.println("First get: " + cacheWithDataSource.get(key1));
        System.out.println("Second get: " + cacheWithDataSource.get(key1));
        Assert.assertNotNull(cacheWithDataSource.get(key1));

        System.out.println("First get: " + cacheWithDataSource.get(key2));
        System.out.println("Second get: " + cacheWithDataSource.get(key2));
        Assert.assertNotNull(cacheWithDataSource.get(key1));

        cacheWithDataSource.shutdown();
    }

    @Test
    public void testListOfStringValuesWithoutLocalCache() throws Exception{
        testListOfStringValueCache(false);
    }

    @Test
    public void testListOfStringValuesWithLocalCache() throws Exception{
        testListOfStringValueCache(true);
    }

    private void testListOfStringValueCache(boolean useInMemoryCache) throws Exception{
        //Cache setup
        CouchbaseConfig couchbaseConfig = new CouchbaseConfig.CouchbaseConfigBuilder(Sets.newHashSet("http://127.0.0.1:8091/pools"),
            "default", "", 5).build();
        FkCouchbaseClient<String, List<String>> couchbaseClient = new FkCouchbaseClient<>(couchbaseConfig);
        HystrixExecutor hystrixExecutor = CommonITContext.getInstance(HystrixExecutor.class);
        IDataSource<String, List<String>> dataSource = new ListOfStringDataSource();

        CacheWithDataSource<String, List<String>> cacheWithDataSource = new CacheWithDataSource<>(couchbaseClient, dataSource, hystrixExecutor,
            "searchEngine.search", "searchEngine.search",
            useInMemoryCache ? Optional.of(new CacheWithDataSource.InMemoryCacheConfig(10,100)) : Optional.empty());

        String key1 = "key1";
        String key2 = "key2";
        System.out.println("First get: " + cacheWithDataSource.get(key1));
        System.out.println("Second get: " + cacheWithDataSource.get(key1));
        Assert.assertNotNull(cacheWithDataSource.get(key1));

        System.out.println("First get: " + cacheWithDataSource.get(key2));
        System.out.println("Second get: " + cacheWithDataSource.get(key2));
        Assert.assertNotNull(cacheWithDataSource.get(key2));

        cacheWithDataSource.shutdown();
    }
}