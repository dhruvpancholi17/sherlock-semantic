package com.flipkart.sherlock.semantic.commons.hystrix;

import com.flipkart.sherlock.semantic.commons.CommonITContext;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by anurag.laddha on 11/12/17.
 */

/**
 * Integration test for fetching hystrix configs from fk config service
 */
public class CachedConfigServiceHystrixConfigFetcherTestIT {

    @Test
    public void testFetchHystrixConfig(){
        IHystrixConfigFetcher hystrixConfigFetcher = CommonITContext.getInstance(IHystrixConfigFetcher.class);
        HystrixCommandConfig hystrixCommandConfig = hystrixConfigFetcher.getConfig("searchEngine", "search");
        Assert.assertNotNull(hystrixCommandConfig);
        System.out.println(hystrixCommandConfig);
    }
}