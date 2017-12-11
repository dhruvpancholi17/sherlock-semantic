package com.flipkart.sherlock.semantic.commons.hystrix;

import lombok.*;

/**
 * Created by anurag.laddha on 27/07/17.
 */

/**
 * Hystrix command configuration
 */
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class HystrixCommandConfig {

    /**
     * Hystrix command group name
     */
    private String groupKey;

    /**
     * Hystrix command name
     */
    private String commandKey;

    /**
     * Threadpool name
     */
    private String threadPoolKey;

    /**
     * Timeout after which caller will observe a timeout and walk away from the command execution
     * This includes time spent in queue
     */
    private int executionTimeoutMs;

    /**
     * Core thread pool sise
     */
    private int poolCoreSize;

    /**
     * max thread pool size
     */
    private int poolMaxSize;

    /**
     * maximum queue size of the BlockingQueue implementation.
     */
    private int poolMaxQueueSize;
}

