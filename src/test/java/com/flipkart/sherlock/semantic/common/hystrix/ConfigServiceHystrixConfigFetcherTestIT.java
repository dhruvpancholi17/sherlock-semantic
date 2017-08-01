package com.flipkart.sherlock.semantic.common.hystrix;

import com.flipkart.sherlock.semantic.common.util.TestContext;
import junit.framework.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by anurag.laddha on 28/07/17.
 */
public class ConfigServiceHystrixConfigFetcherTestIT {

    @Test
    public void testConfigFetcher(){
        ConfigServiceHystrixConfigFetcher configServiceHystrixConfigFetcher = TestContext.getInstance(ConfigServiceHystrixConfigFetcher.class);
        HystrixCommandConfig hystrixCommandConfig = configServiceHystrixConfigFetcher.getConfig("searchEngine", "search");
        System.out.println(hystrixCommandConfig);
        Assert.assertNotNull(hystrixCommandConfig);
    }

}