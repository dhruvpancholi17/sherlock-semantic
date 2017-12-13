package com.flipkart.sherlock.semantic.commons.hystrix;

import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by anurag.laddha on 27/07/17.
 */

public class HystrixCommandConfigTest {

    @Test
    public void testSerde() throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        HystrixCommandConfig config = new HystrixCommandConfig("group", "command", "pool", 10, 20, 30, 40, true);
        System.out.println(objectMapper.writeValueAsString(config));

        String json = "{\"groupKey\":\"group\",\"commandKey\":\"command\",\"threadPoolKey\":\"pool\",\"executionTimeoutMs\":10,\"poolCoreSize\":20,\"poolMaxSize\":30,\"poolMaxQueueSize\":40}";
        HystrixCommandConfig deserialisedConfig = objectMapper.readValue(json, HystrixCommandConfig.class);

        Assert.assertEquals(config.getGroupKey(), deserialisedConfig.getGroupKey());
        Assert.assertEquals(config.getCommandKey(), deserialisedConfig.getCommandKey());
        Assert.assertEquals(config.getThreadPoolKey(), deserialisedConfig.getThreadPoolKey());
        Assert.assertFalse(deserialisedConfig.isAllowMaximumSizeToDivergeFromCoreSize());

        System.out.println(TimeUnit.MINUTES.toMillis(5));
    }
}