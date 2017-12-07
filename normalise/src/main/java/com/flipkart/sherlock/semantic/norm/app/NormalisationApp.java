package com.flipkart.sherlock.semantic.norm.app;

import com.flipkart.sherlock.semantic.commons.dao.mysql.entity.MysqlConfig;
import com.flipkart.sherlock.semantic.commons.dao.mysql.entity.MysqlConnectionPoolConfig;
import com.flipkart.sherlock.semantic.commons.init.CommonInitProvider;
import com.flipkart.sherlock.semantic.commons.init.MysqlDBIProvider;
import com.flipkart.sherlock.semantic.commons.util.http.FkHttpClient;
import com.flipkart.sherlock.semantic.commons.util.http.FkHttpClientConfig;
import com.flipkart.sherlock.semantic.commons.util.http.FkHttpClientLifecycleManager;
import com.flipkart.sherlock.semantic.norm.init.NormInitProvider;
import com.flipkart.sherlock.semantic.norm.resources.NormResource;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.concurrent.ExecutorService;

/**
 * Created by anurag.laddha on 28/11/17.
 */
public class NormalisationApp extends Application<NormalisationYmlConfig> {

    @Override
    public void initialize(Bootstrap<NormalisationYmlConfig> bootstrap) {
    }

    @Override
    public void run(NormalisationYmlConfig normalisationYmlConfig, Environment environment) throws Exception {
        //TODO: config client integration

        ExecutorService cacheReloadExecutorService = environment.lifecycle().executorService("norm-cache-reload")
            .minThreads(1).maxThreads(3).build();

        int brandCacheExpirySec = 60 * 60;
        int singulariseResourceCacheExpirySec = 60 * 60 * 24;
        int maxBrandEvaluateTokenLength = 4;

        MysqlConfig mysqlConfig = new MysqlConfig("127.0.0.1", 3306, "root", "", "sherlock");
        MysqlConnectionPoolConfig mysqlConnectionPoolConfig = new MysqlConnectionPoolConfig
            .MysqlConnectionPoolConfigBuilder(1,10).build();

        Injector injector = Guice.createInjector(
            new CommonInitProvider(environment.metrics(), getHttpClientConfig()),
            new MysqlDBIProvider(mysqlConfig, mysqlConnectionPoolConfig),
            new NormInitProvider(brandCacheExpirySec, singulariseResourceCacheExpirySec, maxBrandEvaluateTokenLength, cacheReloadExecutorService));

        /**
         * Initialise resources
         */
        environment.jersey().register(injector.getInstance(NormResource.class));

        /**
         * Object lifecycle
         */
        environment.lifecycle().manage(new FkHttpClientLifecycleManager(injector.getInstance(FkHttpClient.class)));
    }


    private FkHttpClientConfig getHttpClientConfig(){
        //TODO integrate with config service
        return new FkHttpClientConfig.FkHttpClientConfigBuilder()
            .setConnectTimeoutMs(1000)
            .setConnectionRequestTimeoutMs(1000)
            .setSocketTimeoutMs(2000)
            .setMaxConnections(50)
            .setMaxConnectionsPerRoute(5)
            .build();
    }

    public static void main(String[] args) throws Exception {
        new NormalisationApp().run(args);
    }
}
