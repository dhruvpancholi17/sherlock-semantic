package com.flipkart.sherlock.semantic.common.hystrix;

/**
 * Created by anurag.laddha on 27/07/17.
 */

public interface IHystrixConfigFetcher {
    /**
     * Get hystrix configuration for given command group and command name
     */
    HystrixCommandConfig getConfig(String commandGroup, String command);
}
