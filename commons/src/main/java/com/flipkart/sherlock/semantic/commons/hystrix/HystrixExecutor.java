package com.flipkart.sherlock.semantic.commons.hystrix;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by anurag.laddha on 19/12/17.
 */

/**
 * Helps executing hystrix commands.
 * Fetches hystrix configs from config fetcher based on group and command name and executes the supplier as a hystrix command
 */
@ThreadSafe
@Slf4j
public class HystrixExecutor {

    private IHystrixConfigFetcher hystrixConfigFetcher;

    @Inject
    public HystrixExecutor(IHystrixConfigFetcher hystrixConfigFetcher) {
        this.hystrixConfigFetcher = hystrixConfigFetcher;
    }

    /**
     * Execute given supplier synchronously using hystrix
     * Configs will be based on hystrix group and command name
     * @param hystrixConfigName: name of config for hystrix command
     * @param supplier: piece of code to execute using hystrix
     * @param <T>
     * @return: value provided by supplier
     */
    public <T> T executeSync(String hystrixConfigName, Callable<T> supplier){
        if (hystrixConfigName != null) {
            try {
                HystrixCommandWrapper<T> commandWrapper = getHystrixCommand(hystrixConfigName, supplier);
                return commandWrapper.execute();
            } catch (Exception ex) {
                log.error("Error in executing hystrix command with configName: {}", hystrixConfigName, ex);
            }
        }
        return null;
    }


    /**
     *  Execute given supplier async using hystrix, wait atmost till timeout specified
     *  Configs will be based on hystrix group and command name
     *
     * @param hystrixConfigName: name of config for hystrix command
     * @param timeoutMs: max time to wait for command execution to complete
     * @param supplier: piece of code to execute using hystrix
     * @param <T>
     * @return
     */
    public <T> T executeWithTimeout(String hystrixConfigName, long timeoutMs, Callable<T> supplier) {
        T value = null;
        if (hystrixConfigName != null) {
            try {
                HystrixCommandWrapper<T> commandWrapper = getHystrixCommand(hystrixConfigName, supplier);
                Future<T> future = commandWrapper.queue();
                value = future.get(timeoutMs, TimeUnit.MILLISECONDS);
            } catch (Exception ex) {
                log.error("Error in executing hystrix command with config name: {} with timeoutMs", hystrixConfigName, timeoutMs, ex);
            }
        }
        return value;
    }

    /**
     * Fetches hystrix command config based on group and command name. Creates hystrix command to be executed
     */
    private <T> HystrixCommandWrapper<T> getHystrixCommand(String hystrixConfigName, Callable<T> supplier) throws Exception {
        HystrixCommandConfig commandConfig = this.hystrixConfigFetcher.getConfig(hystrixConfigName);
        if (commandConfig == null) throw new Exception("Hystrix command config not found for key: " + hystrixConfigName);
        return new HystrixCommandWrapper<>(commandConfig, supplier);
    }
}
