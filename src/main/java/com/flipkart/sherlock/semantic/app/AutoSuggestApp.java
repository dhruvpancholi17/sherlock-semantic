package com.flipkart.sherlock.semantic.app;


import com.flipkart.sherlock.semantic.autosuggest.providers.WrapperProvider;
import com.flipkart.sherlock.semantic.autosuggest.views.AutoSuggestView;
import com.flipkart.sherlock.semantic.autosuggest.views.HealthCheckView;
import com.flipkart.sherlock.semantic.common.dao.mysql.entity.MysqlConfig;
import com.flipkart.sherlock.semantic.common.dao.mysql.entity.MysqlConnectionPoolConfig;
import com.flipkart.sherlock.semantic.common.init.MysqlDaoProvider;
import com.flipkart.sherlock.semantic.common.metrics.MetricsManager;
import com.flipkart.sherlock.semantic.common.metrics.MetricsManager.TracedItem;
import com.flipkart.sherlock.semantic.common.util.FkConfigServiceWrapper;
import com.flipkart.sherlock.semantic.common.util.JmxMetricRegistry;
import com.flipkart.sherlock.semantic.common.util.SherlockMetricsServletContextListener;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.Slf4jRequestLog;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import service.Publisher;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import static com.flipkart.sherlock.semantic.app.AppConstants.AUTOSUGGEST_BUCKET;
import static com.flipkart.sherlock.semantic.autosuggest.views.AutoSuggestView.COSMOS_AUTO_SUGGEST_COMPONENT;
import static com.flipkart.sherlock.semantic.autosuggest.views.AutoSuggestView.COSMOS_AUTO_SUGGEST_V4_COMPONENT;
import static com.flipkart.sherlock.semantic.common.metrics.MetricsManager.ActionType.*;
import static com.flipkart.sherlock.semantic.common.metrics.MetricsManager.Service.Autosuggest;

/**
 * Created by anurag.laddha on 02/04/17.
 */
@Slf4j
public class AutoSuggestApp {

    public static void main(String[] args) throws Exception {

        MetricsManager.init(getTracedItems());

        FkConfigServiceWrapper configServiceWrapper = new FkConfigServiceWrapper("sherlock-autosuggest", true);

        FkConfigServiceWrapper fkConfigServiceWrapper = new FkConfigServiceWrapper(AUTOSUGGEST_BUCKET, true);
        MysqlConfig mysqlConfig = MysqlConfig.getConfig(fkConfigServiceWrapper);
        log.info("Staring the host with the following MySQL config: {}", mysqlConfig.toString());
        MysqlConnectionPoolConfig connectionPoolConfig = new MysqlConnectionPoolConfig
                .MysqlConnectionPoolConfigBuilder(mysqlConfig.getMinPoolSize(), mysqlConfig.getMaxPoolSize())
                .setInitialPoolSize(mysqlConfig.getInitialPoolSize())
                .setAcquireIncrement(mysqlConfig.getAcquireIncrement())
                .setMaxIdleTimeSec(mysqlConfig.getMaxIdleTimeSec())
                .build();

        // By default, jetty task queue is unbounded. Reject requests once queue is full.
        QueuedThreadPool threadPool = getWebserverThreadPool(configServiceWrapper);
        threadPool.setName("JettyContainer");

        Injector injector = Guice.createInjector(
                new MysqlDaoProvider(mysqlConfig, connectionPoolConfig),
                new WrapperProvider(fkConfigServiceWrapper));

        // Create embedded jetty container
        Server server = new Server(threadPool);

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(9007);
        connector.setName("AutoSuggestApplication");
        server.addConnector(connector);

        ContextHandlerCollection contexts = new ContextHandlerCollection();

        AutoSuggestView autoSuggestView = injector.getInstance(AutoSuggestView.class);
        HealthCheckView healthCheckView = injector.getInstance(HealthCheckView.class);
        ResourceConfig resourceConfig = new ResourceConfig()
                .register(autoSuggestView)
                .register(healthCheckView);

        ServletContextHandler contextDefault = new ServletContextHandler();
        contextDefault.setContextPath("/");
        contextDefault.addEventListener(new SherlockMetricsServletContextListener());
        contextDefault.addServlet(new ServletHolder(new ServletContainer(resourceConfig)), "/*");
        contextDefault.addServlet(com.codahale.metrics.servlets.MetricsServlet.class, "/metrics");
//        contextDefault.addServlet(new ServletHolder(new HystrixMetricsStreamServlet()),"/hystrix.stream");
        contextDefault.setVirtualHosts(new String[]{"@AutoSuggestApplication"});
        contexts.addHandler(contextDefault);

        server.setHandler(contexts);

        server.setRequestLog(new Slf4jRequestLog());

        // publisher for data governance
        try {
            Publisher.INSTANCE.init();
            Publisher.INSTANCE.withMetricRegistry(JmxMetricRegistry.INSTANCE.getInstance());
        } catch (Exception e) {
            log.error("Unable to Initiallize DG library", e);
        }

        try {
            server.start();
            server.join();
        } finally {
            Publisher.INSTANCE.shutdown();
            server.stop();
        }
    }


    /**
     * Get all the metrics to be tracked with the help of cosmos
     */
    public static Set<TracedItem> getTracedItems() {
        Set<TracedItem> tracedItems = new HashSet<>();
        tracedItems.add(new TracedItem(ERROR, Autosuggest, COSMOS_AUTO_SUGGEST_COMPONENT));
        tracedItems.add(new TracedItem(RPS, Autosuggest, COSMOS_AUTO_SUGGEST_COMPONENT));
        tracedItems.add(new TracedItem(NULL, Autosuggest, COSMOS_AUTO_SUGGEST_COMPONENT));
        tracedItems.add(new TracedItem(SUCCESS, Autosuggest, COSMOS_AUTO_SUGGEST_COMPONENT));
        tracedItems.add(new TracedItem(LATENCY, Autosuggest, COSMOS_AUTO_SUGGEST_COMPONENT));

        tracedItems.add(new TracedItem(ERROR, Autosuggest, COSMOS_AUTO_SUGGEST_V4_COMPONENT));
        tracedItems.add(new TracedItem(RPS, Autosuggest, COSMOS_AUTO_SUGGEST_V4_COMPONENT));
        tracedItems.add(new TracedItem(NULL, Autosuggest, COSMOS_AUTO_SUGGEST_V4_COMPONENT));
        tracedItems.add(new TracedItem(SUCCESS, Autosuggest, COSMOS_AUTO_SUGGEST_V4_COMPONENT));
        tracedItems.add(new TracedItem(LATENCY, Autosuggest, COSMOS_AUTO_SUGGEST_V4_COMPONENT));
        return tracedItems;
    }

    /**
     * Get web server threadpool configs from config service
     */
    private static QueuedThreadPool getWebserverThreadPool(FkConfigServiceWrapper configServiceWrapper) {
        int minThreads = configServiceWrapper.getInt("webserver.minThreads");
        int maxThreads = configServiceWrapper.getInt("webserver.maxThreads");
        int idleTimeoutMs = configServiceWrapper.getInt("webserver.idleTimeoutMs");
        int maxQueuedRequests = configServiceWrapper.getInt("webserver.maxQueuedRequests");

        log.info("Web server thread pool config minThreads: {},  maxThreads: {}, idleTimeoutMs: {}, maxQueuedRequests: {}",
                minThreads, maxThreads, idleTimeoutMs, maxQueuedRequests);

        return new QueuedThreadPool(maxThreads, minThreads, idleTimeoutMs, new ArrayBlockingQueue<>(maxQueuedRequests));
    }
}
