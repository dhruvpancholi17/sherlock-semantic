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
import java.util.Map;
import java.util.Optional;

/**
 * Created by anurag.laddha on 21/12/17.
 */

/**
 * Integration test for hystrix cache wrapper
 * File name must end in IT
 */
public class HystrixCacheWrapperTestIT {

    @Test
    public void testWithoutLocalCache(){
        testHystrixWrapperForCache(false);
    }

    @Test
    public void testWithLocalCache(){
        testHystrixWrapperForCache(true);
    }


    private void testHystrixWrapperForCache(boolean useInMemCache){

        //Cache setup
        CouchbaseConfig couchbaseConfig = new CouchbaseConfig.CouchbaseConfigBuilder(Sets.newHashSet("http://127.0.0.1:8091/pools"),
            "default", "", 5).build();
        FkCouchbaseClient<String, List<String>> couchbaseClient = new FkCouchbaseClient<>(couchbaseConfig);
        HystrixExecutor hystrixExecutor = CommonITContext.getInstance(HystrixExecutor.class);

        HystrixCacheWrapper<String, List<String>> hystrixCacheWrapper = new HystrixCacheWrapper<>(couchbaseClient, hystrixExecutor,
            "searchEngine.search",
            useInMemCache ? Optional.of(new InMemoryCacheConfig(10, 60)) : Optional.empty());

        //Data setup
        String key = "key1";
        List<String> strList1 = Lists.newArrayList("s1", "s2", "s3");
        String key2 = "key2";
        List<String> strList2 = Lists.newArrayList("s1", "s2");

        //Cache is empty
        Assert.assertNull(hystrixCacheWrapper.get(key));

        //Put some value and test getting same value back
        hystrixCacheWrapper.put(key, strList1);
        hystrixCacheWrapper.put(key2, strList2);
        Assert.assertNotNull(hystrixCacheWrapper.get(key));
        Assert.assertEquals(strList1, hystrixCacheWrapper.get(key));

        //Get bulk
        Map<String, List<String>> bulkKeyValMap = hystrixCacheWrapper.getBulk(Sets.newHashSet(key, key2));
        Assert.assertNotNull(bulkKeyValMap);
        Assert.assertTrue(bulkKeyValMap.size() > 0);
        Assert.assertEquals(strList1, bulkKeyValMap.get(key));
        Assert.assertEquals(strList2, bulkKeyValMap.get(key2));

        //Remove and test key is not present
        hystrixCacheWrapper.remove(key);
        Assert.assertNull(hystrixCacheWrapper.get(key));
        Assert.assertNotNull(hystrixCacheWrapper.get(key2));

        hystrixCacheWrapper.shutdown();
    }
}