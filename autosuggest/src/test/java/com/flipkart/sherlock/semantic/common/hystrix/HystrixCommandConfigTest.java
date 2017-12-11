package com.flipkart.sherlock.semantic.common.hystrix;

import com.flipkart.sherlock.semantic.TestUtils;
import junit.framework.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by anurag.laddha on 27/07/17.
 */

public class HystrixCommandConfigTest {

    @Test
    public void testSerde() throws Exception{
        HystrixCommandConfig config = new HystrixCommandConfig("group", "command", "pool", 10, 20, 30, 40, true);
        System.out.println(TestUtils.getObjectMapper().writeValueAsString(config));

        String json = "{\"groupKey\":\"group\",\"commandKey\":\"command\",\"threadPoolKey\":\"pool\",\"executionTimeoutMs\":10,\"poolCoreSize\":20,\"poolMaxSize\":30,\"poolMaxQueueSize\":40}";
        HystrixCommandConfig deserialisedConfig = TestUtils.getObjectMapper().readValue(json, HystrixCommandConfig.class);

        Assert.assertEquals(config.getGroupKey(), deserialisedConfig.getGroupKey());
        Assert.assertEquals(config.getCommandKey(), deserialisedConfig.getCommandKey());
        Assert.assertEquals(config.getThreadPoolKey(), deserialisedConfig.getThreadPoolKey());
        Assert.assertFalse(deserialisedConfig.isAllowMaximumSizeToDivergeFromCoreSize());

        System.out.println(TimeUnit.MINUTES.toMillis(5));
    }
}