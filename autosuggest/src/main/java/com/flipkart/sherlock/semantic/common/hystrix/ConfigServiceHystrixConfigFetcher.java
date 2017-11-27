package com.flipkart.sherlock.semantic.common.hystrix;

import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.flipkart.sherlock.semantic.common.config.Constants;
import com.flipkart.sherlock.semantic.common.util.FkConfigServiceWrapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by anurag.laddha on 27/07/17.
 */

/**
 * Fetches hystrix config from fk config service based on hystrix command group and command name
 */

@Slf4j
@Singleton
public class ConfigServiceHystrixConfigFetcher implements IHystrixConfigFetcher {

    private LoadingCache<String, HystrixCommandConfig> hystrixCommandToConfig;
    private static final String prefix = "hystrix";

    @Inject
    public ConfigServiceHystrixConfigFetcher(FkConfigServiceWrapper configServiceWrapper,
                                             JsonSeDe jsonSeDe,
                                             @Named(Constants.GUICE_LOCAL_CACHE_LOADING_EXECUTOR_SERVICE) ExecutorService executorService,
                                             @Named(Constants.HYSTRIX_CONFIG_CACHE_EXPIRY) int cacheExpiryTimeSec) {
        HystrixConfigLoader configLoader = new HystrixConfigLoader(jsonSeDe, executorService, configServiceWrapper);
        this.hystrixCommandToConfig = CacheBuilder.newBuilder().maximumSize(1000)
            .refreshAfterWrite(cacheExpiryTimeSec, TimeUnit.SECONDS)
            .build(configLoader);
    }


    @Override
    public HystrixCommandConfig getConfig(String commandGroup, String command) {
        HystrixCommandConfig config = null;
        String configServiceConfigKey = prefix + "." + commandGroup + "." + command;
        try {
            config = this.hystrixCommandToConfig.get(configServiceConfigKey);
        }
        catch(Exception ex){
            log.error("Error in fetching hystrix config from cache for key: {}", configServiceConfigKey, ex);
        }
        return config;
    }


    /**
     * Loader to fetch hystrxi configs
     */
    static class HystrixConfigLoader extends CacheLoader<String, HystrixCommandConfig> {

        private JsonSeDe jsonSeDe;
        private ExecutorService executorService;
        private FkConfigServiceWrapper configServiceWrapper;

        public HystrixConfigLoader(JsonSeDe jsonSeDe, ExecutorService executorService, FkConfigServiceWrapper configServiceWrapper) {
            this.jsonSeDe = jsonSeDe;
            this.executorService = executorService;
            this.configServiceWrapper = configServiceWrapper;
        }

        @Override
        public HystrixCommandConfig load(String key) throws Exception {
            log.info("Fetching hystrix config for key: {}", key);
            HystrixCommandConfig config = getHystrixConfig(key);
            log.info("Done Fetching hystrix config for key: {}", key);
            return config;
        }

        @Override
        public ListenableFuture<HystrixCommandConfig> reload(String key, HystrixCommandConfig oldValue) throws Exception {
            log.info("Queued Fetching hystrix config for key: {}", key);
            ListenableFutureTask<HystrixCommandConfig> task = ListenableFutureTask.create(() ->{
                log.info("Started loading hystrix config for key: {}", key);
                HystrixCommandConfig config = getHystrixConfig(key);
                log.info("Finished loading hystrix config for key: {}", key);
                return config;
            });
            this.executorService.submit(task);
            return task;
        }

        private HystrixCommandConfig getHystrixConfig(String key){
            HystrixCommandConfig config = null;
            try {
                String configJson = this.configServiceWrapper.getString(key);
                if (!StringUtils.isBlank(configJson)) {
                    config = jsonSeDe.readValue(configJson, HystrixCommandConfig.class);
                } else {
                    log.error("Hystrix config for command: {} not found", key);
                }
            }
            catch(Exception ex){
                log.error("Error in fetching configs for Hystrix command: {}", key, ex);
            }

            return config;
        }
    }
}
