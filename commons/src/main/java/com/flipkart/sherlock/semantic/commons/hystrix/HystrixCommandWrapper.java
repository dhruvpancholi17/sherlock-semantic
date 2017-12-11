package com.flipkart.sherlock.semantic.commons.hystrix;

/**
 * Created by anurag.laddha on 27/07/17.
 */

import com.netflix.hystrix.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * Wrapper for executing commands through hystrix
 * @param <R>
 */
@Slf4j
public class HystrixCommandWrapper<R> extends HystrixCommand<R> {

    private HystrixCommandConfig commandConfig;
    private Callable<R> command;

    /**
     * Setting up hystrix command to execute
     * @param commandConfig: configuration for command
     * @param command: command to execute
     */
    public HystrixCommandWrapper(HystrixCommandConfig commandConfig, Callable<R> command) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(commandConfig.getGroupKey()))
            .andCommandKey(HystrixCommandKey.Factory.asKey(commandConfig.getCommandKey()))
            .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(commandConfig.getThreadPoolKey()))
            .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionTimeoutInMilliseconds(commandConfig.getExecutionTimeoutMs()))
            .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                .withCoreSize(commandConfig.getPoolCoreSize())
                .withMaximumSize(commandConfig.getPoolMaxSize())
                .withAllowMaximumSizeToDivergeFromCoreSize(true)
                .withMaxQueueSize(commandConfig.getPoolMaxQueueSize())));
        this.command = command;
        this.commandConfig = commandConfig;
    }

    @Override
    protected R run() throws Exception {
        try {
            return this.command.call();
        }catch(Exception ex){
            log.error("Error in executing hystrix command from group: {}, command key: {}", this.commandConfig.getGroupKey(),
                this.commandConfig.getCommandKey());
        }
        return null;
    }
}
