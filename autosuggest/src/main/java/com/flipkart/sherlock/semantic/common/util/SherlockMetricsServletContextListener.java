package com.flipkart.sherlock.semantic.common.util;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlets.MetricsServlet;

/**
 * Created by anurag.laddha on 20/07/17.
 */

public class SherlockMetricsServletContextListener extends MetricsServlet.ContextListener {

    @Override
    protected MetricRegistry getMetricRegistry() {
        return JmxMetricRegistry.INSTANCE.getInstance();
    }
}
