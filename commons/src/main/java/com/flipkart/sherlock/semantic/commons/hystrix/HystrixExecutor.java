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
     * @param group: command group name
     * @param command: command name
     * @param supplier: piece of code to execute using hystrix
     * @param <T>
     * @return: value provided by supplier
     */
    public <T> T executeSync(String group, String command, Callable<T> supplier){
        try {
            HystrixCommandWrapper<T> commandWrapper = getHystrixCommand(group, command, supplier);
            return commandWrapper.execute();
        }
        catch(Exception ex){
            log.error("Error in executing hystrix command: {}, from group: {}", command, group);
        }
        return null;
    }


    /**
     *  Execute given supplier async using hystrix, wait atmost till timeout specified
     *  Configs will be based on hystrix group and command name
     *
     * @param group: command group name
     * @param command: command name
     * @param timeoutMs: max time to wait for command execution to complete
     * @param supplier: piece of code to execute using hystrix
     * @param <T>
     * @return
     */
    public <T> T executeWithTimeout(String group, String command, long timeoutMs, Callable<T> supplier) {
        T value = null;
        if (command != null) {
            try {
                HystrixCommandWrapper<T> commandWrapper = getHystrixCommand(group, command, supplier);
                Future<T> future = commandWrapper.queue();
                value = future.get(timeoutMs, TimeUnit.MILLISECONDS);
            } catch (Exception ex) {
                log.error("Error in executing hystrix command: {} of group: {}, with timeoutMs", command, group, timeoutMs, ex);
            }
        }
        return value;
    }

    /**
     * Fetches hystrix command config based on group and command name. Creates hystrix command to be executed
     */
    private <T> HystrixCommandWrapper<T> getHystrixCommand(String group, String command, Callable<T> supplier){
        HystrixCommandConfig commandConfig = this.hystrixConfigFetcher.getConfig(group, command);
        return new HystrixCommandWrapper<>(commandConfig, supplier);
    }
}
