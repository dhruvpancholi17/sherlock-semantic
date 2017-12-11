package com.flipkart.sherlock.semantic.commons.init;

import com.codahale.metrics.MetricRegistry;
import com.flipkart.sherlock.semantic.commons.Constants;
import com.flipkart.sherlock.semantic.commons.hystrix.CachedConfigServiceHystrixConfigFetcher;
import com.flipkart.sherlock.semantic.commons.hystrix.IHystrixConfigFetcher;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.netflix.hystrix.contrib.codahalemetricspublisher.HystrixCodaHaleMetricsPublisher;
import com.netflix.hystrix.strategy.HystrixPlugins;

/**
 * Created by anurag.laddha on 11/12/17.
 */
public class HystrixInitProvider extends AbstractModule {

    private int hystrixConfigCacheRefreshTimeSec;
    private MetricRegistry metricRegistry;

    public HystrixInitProvider(int hystrixConfigCacheRefreshTimeSec, MetricRegistry metricRegistry) {
        this.hystrixConfigCacheRefreshTimeSec = hystrixConfigCacheRefreshTimeSec;
        this.metricRegistry = metricRegistry;
    }

    @Override
    protected void configure() {
        bind(Integer.class)
            .annotatedWith(Names.named(Constants.GUICE_CACHE_EXPIRY_HYSTRIX_CONFIG))
            .toInstance(this.hystrixConfigCacheRefreshTimeSec);

        bind(IHystrixConfigFetcher.class).to(CachedConfigServiceHystrixConfigFetcher.class).in(Singleton.class);

        //Exposes hystrix metrics using given metric registry.
        HystrixPlugins.getInstance().registerMetricsPublisher(new HystrixCodaHaleMetricsPublisher(
            metricRegistry));
    }
}
