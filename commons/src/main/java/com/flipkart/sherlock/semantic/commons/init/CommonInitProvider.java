package com.flipkart.sherlock.semantic.commons.init;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.flipkart.sherlock.semantic.commons.util.http.FkHttpClient;
import com.flipkart.sherlock.semantic.commons.util.http.FkHttpClientConfig;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.util.concurrent.TimeUnit;

/**
 * Created by anurag.laddha on 07/12/17.
 */

/**
 * Initialises objects that can be required across different modules
 * If object is not common, please create a provider within that module
 */
public class CommonInitProvider extends AbstractModule {
    private MetricRegistry metricRegistry;
    private FkHttpClientConfig fkHttpClientConfig;

    public CommonInitProvider(MetricRegistry metricRegistry, FkHttpClientConfig fkHttpClientConfig) {
        this.metricRegistry = metricRegistry;
        this.fkHttpClientConfig = fkHttpClientConfig;
        setupJmxReporter(metricRegistry);
    }

    @Override
    protected void configure() {
    }

    private void setupJmxReporter(MetricRegistry metricRegistry){
        //Report every second
        JmxReporter jmxReporter = JmxReporter.forRegistry(metricRegistry)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .build();
        jmxReporter.start();
    }

    @Provides
    @Singleton
    ObjectMapper getObjectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return objectMapper;
    }

    @Inject
    @Provides
    @Singleton
    FkHttpClient getHttpClient(ObjectMapper objectMapper){
        return new FkHttpClient(fkHttpClientConfig, objectMapper, metricRegistry);
    }

    @Provides
    @Singleton
    MetricRegistry getMetricRegistry(){
        return this.metricRegistry;
    }
}
