package com.flipkart.sherlock.semantic.commons.hystrix;

import com.netflix.hystrix.HystrixCommand;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by anurag.laddha on 27/07/17.
 */

/**
 * Helper to execute hystrix commands
 * Common activities (eg logging, metrics, etc) can be done here
 */

@Slf4j
public class HystrixCommandHelper {

    /**
     * Execute command synchronously
     *
     * @param command:     command to execute
     * @param group:       group name
     * @param commandName: command name
     */
    public static <R> R executeSync(HystrixCommand<R> command, String group, String commandName) {
        if (command != null) {
            try {
                return command.execute();
            } catch (Exception ex) {
                log.error("Error in executing hystrix command: {} of group: {}", commandName, group, ex);
            }
        }
        return null;
    }

    /**
     * Execute command synchronously with max timeout
     *
     * @param command:     command to execute
     * @param group:       group name
     * @param commandName: command name
     * @param timeoutMs:   timeout in ms
     */
    public static <R> R executeWithTimeout(HystrixCommand<R> command, String group, String commandName, long timeoutMs) {
        R value = null;
        if (command != null) {
            try {
                Future<R> future = command.queue();
                value = future.get(timeoutMs, TimeUnit.MILLISECONDS);
            } catch (Exception ex) {
                log.error("Error in executing hystrix command: {} of group: {}, with timeoutMs", commandName, group, timeoutMs, ex);
            }
        }
        return value;
    }
}
