package com.flipkart.sherlock.semantic.common.util;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;

import java.util.concurrent.TimeUnit;

/**
 * Created by anurag.laddha on 20/07/17.
 */
public enum JmxMetricRegistry {
    INSTANCE;
    private MetricRegistry metricRegistry;

    private JmxMetricRegistry(){
        metricRegistry = new MetricRegistry();

        //Report every second
        JmxReporter jmxReporter = JmxReporter.forRegistry(metricRegistry)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .build();
        jmxReporter.start();
    }

    public MetricRegistry getInstance(){
        return metricRegistry;
    }
}
