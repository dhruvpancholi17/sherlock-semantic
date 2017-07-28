package com.flipkart.sherlock.semantic.app;


import com.flipkart.sherlock.semantic.autosuggest.dao.ConfigsDao;
import com.flipkart.sherlock.semantic.autosuggest.providers.JsonSeDeProvider;
import com.flipkart.sherlock.semantic.autosuggest.views.AutoSuggestView;
import com.flipkart.sherlock.semantic.autosuggest.views.HealthCheckView;
import com.flipkart.sherlock.semantic.common.dao.mysql.entity.MysqlConfig;
import com.flipkart.sherlock.semantic.common.dao.mysql.entity.MysqlConnectionPoolConfig;
import com.flipkart.sherlock.semantic.common.init.ConfigServiceInitProvider;
import com.flipkart.sherlock.semantic.common.init.HystrixConfigProvider;
import com.flipkart.sherlock.semantic.common.init.MiscInitProvider;
import com.flipkart.sherlock.semantic.common.init.MysqlDaoProvider;
import com.flipkart.sherlock.semantic.common.util.FkConfigServiceWrapper;
import com.flipkart.sherlock.semantic.common.util.SherlockMetricsServletContextListener;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by anurag.laddha on 02/04/17.
 */
@Slf4j
public class AutoSuggestApp {

    public static void main(String[] args) throws Exception {
        FkConfigServiceWrapper configServiceWrapper = new FkConfigServiceWrapper("sherlock-autosuggest", true);

        MysqlConfig mysqlConfig = ConfigsDao.getMysqlConfig();
        log.info("Staring the host with the following MySQL config: {}", mysqlConfig.toString());
        MysqlConnectionPoolConfig connectionPoolConfig = new MysqlConnectionPoolConfig.MysqlConnectionPoolConfigBuilder(1, 10)
            .setInitialPoolSize(1)
            .setAcquireIncrement(2)
            .setMaxIdleTimeSec((int) TimeUnit.MINUTES.toSeconds(30)).build();

        // By default, jetty task queue is unbounded. Reject requests once queue is full.
        QueuedThreadPool threadPool = getWebserverThreadPool(configServiceWrapper);
        threadPool.setName("JettyContainer");

        Injector injector = Guice.createInjector(
            new MysqlDaoProvider(mysqlConfig, connectionPoolConfig),
            new JsonSeDeProvider(),
            new MiscInitProvider((int) TimeUnit.MINUTES.toSeconds(30), 10),
            new ConfigServiceInitProvider(configServiceWrapper),
            new HystrixConfigProvider((int)TimeUnit.MINUTES.toSeconds(2)));

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
        contextDefault.setVirtualHosts(new String[]{"@AutoSuggestApplication"});
        contexts.addHandler(contextDefault);

        server.setHandler(contexts);

        try {
            server.start();
            server.join();
        } finally {
            server.stop();
        }
    }

    /**
     * Get web server threadpool configs from config service
     */
    private static QueuedThreadPool getWebserverThreadPool(FkConfigServiceWrapper configServiceWrapper){
        int minThreads = configServiceWrapper.getInt("webserver.minThreads");
        int maxThreads = configServiceWrapper.getInt("webserver.maxThreads");
        int idleTimeoutMs = configServiceWrapper.getInt("webserver.idleTimeoutMs");
        int maxQueuedRequests = configServiceWrapper.getInt("webserver.maxQueuedRequests");

        log.info("Web server thread pool config minThreads: {},  maxThreads: {}, idleTimeoutMs: {}, maxQueuedRequests: {}",
            minThreads, maxThreads, idleTimeoutMs, maxQueuedRequests);

        return new QueuedThreadPool(maxThreads, minThreads, idleTimeoutMs, new ArrayBlockingQueue<>(maxQueuedRequests));
    }
}
