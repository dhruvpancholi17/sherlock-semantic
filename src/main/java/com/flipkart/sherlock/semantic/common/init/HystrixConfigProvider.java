package com.flipkart.sherlock.semantic.common.init;

import com.flipkart.sherlock.semantic.common.config.Constants;
import com.flipkart.sherlock.semantic.common.hystrix.ConfigServiceHystrixConfigFetcher;
import com.flipkart.sherlock.semantic.common.hystrix.IHystrixConfigFetcher;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

/**
 * Created by anurag.laddha on 27/07/17.
 */

public class HystrixConfigProvider extends AbstractModule {

    int hystrixConfigCacheRefreshTimeSec;

    public HystrixConfigProvider(int hystrixConfigCacheRefreshTimeSec) {
        this.hystrixConfigCacheRefreshTimeSec = hystrixConfigCacheRefreshTimeSec;
    }

    @Override
    protected void configure() {
        bind(Integer.class)
            .annotatedWith(Names.named(Constants.HYSTRIX_CONFIG_CACHE_EXPIRY))
            .toInstance(this.hystrixConfigCacheRefreshTimeSec);

        bind(IHystrixConfigFetcher.class).to(ConfigServiceHystrixConfigFetcher.class).in(Singleton.class);
    }
}
