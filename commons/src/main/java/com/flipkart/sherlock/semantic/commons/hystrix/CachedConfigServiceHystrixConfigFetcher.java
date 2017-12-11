package com.flipkart.sherlock.semantic.commons.hystrix;

/**
 * Created by anurag.laddha on 10/12/17.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.sherlock.semantic.commons.Constants;
import com.flipkart.sherlock.semantic.commons.config.FkConfigServiceWrapper;
import com.flipkart.sherlock.semantic.commons.util.FkStringUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * Fetches hystrix config from fk config service based on hystrix command group and command name
 */

@Slf4j
@Singleton
public class CachedConfigServiceHystrixConfigFetcher implements IHystrixConfigFetcher {

    private Cache<String, HystrixCommandConfig> hystrixCommandToConfig;
    private static final String prefix = "hystrix";
    private ObjectMapper objectMapper;
    private FkConfigServiceWrapper fkConfigServiceWrapper;

    @Inject
    public CachedConfigServiceHystrixConfigFetcher(FkConfigServiceWrapper configServiceWrapper,
                                                   ObjectMapper objectMapper,
                                                   @Named(Constants.GUICE_CACHE_EXPIRY_HYSTRIX_CONFIG) int cacheExpiryTimeSec) {

        this.hystrixCommandToConfig = CacheBuilder.newBuilder()
            .maximumSize(5000)
            .expireAfterWrite(cacheExpiryTimeSec, TimeUnit.SECONDS).build();
        this.fkConfigServiceWrapper = configServiceWrapper;
        this.objectMapper = objectMapper;
    }


    @Override
    public HystrixCommandConfig getConfig(String commandGroup, String command) {
        HystrixCommandConfig config = null;
        String configServiceConfigKey = FkStringUtils.joinerOnDot.join(prefix, commandGroup, command);
        try {
            config = this.hystrixCommandToConfig.getIfPresent(configServiceConfigKey);
            if (config == null){
                config = getHystrixConfig(configServiceConfigKey);
                if (config != null){
                    this.hystrixCommandToConfig.put(configServiceConfigKey, config);
                }
            }
        }
        catch(Exception ex){
            log.error("Error in fetching hystrix config from cache for key: {}", configServiceConfigKey, ex);
        }
        return config;
    }


    private HystrixCommandConfig getHystrixConfig(String key){
        HystrixCommandConfig config = null;
        try {
            String configJson = this.fkConfigServiceWrapper.getString(key);
            if (!StringUtils.isBlank(configJson)) {
                config = this.objectMapper.readValue(configJson, HystrixCommandConfig.class);
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
