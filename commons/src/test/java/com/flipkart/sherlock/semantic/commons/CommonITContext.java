package com.flipkart.sherlock.semantic.commons;

import com.codahale.metrics.MetricRegistry;
import com.flipkart.sherlock.semantic.commons.dao.mysql.entity.MysqlConfig;
import com.flipkart.sherlock.semantic.commons.dao.mysql.entity.MysqlConnectionPoolConfig;
import com.flipkart.sherlock.semantic.commons.init.CommonInitProvider;
import com.flipkart.sherlock.semantic.commons.init.ConfigServiceInitProvider;
import com.flipkart.sherlock.semantic.commons.init.HystrixInitProvider;
import com.flipkart.sherlock.semantic.commons.init.MysqlDBIInitProvider;
import com.flipkart.sherlock.semantic.commons.util.http.FkHttpClientConfig;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

import java.util.InvalidPropertiesFormatException;
import java.util.concurrent.TimeUnit;

/**
 * Created by anurag.laddha on 11/12/17.
 */
public class CommonITContext {

    private static Injector injector;

    private static void init() throws InvalidPropertiesFormatException {
        if (injector == null) {
            synchronized (CommonITContext.class) {
                if (injector == null) {

                    MetricRegistry metricRegistry = new MetricRegistry();
                    MysqlConfig mysqlConfig = new MysqlConfig("127.0.0.1", 3306, "root", "", "sherlock");
                    MysqlConnectionPoolConfig connectionPoolConfig = new MysqlConnectionPoolConfig.MysqlConnectionPoolConfigBuilder(1, 10)
                        .setInitialPoolSize(1)
                        .setAcquireIncrement(2)
                        .setMaxIdleTimeSec((int) TimeUnit.MINUTES.toSeconds(30)).build();

                    injector = Guice.createInjector(new MysqlDBIInitProvider(mysqlConfig, connectionPoolConfig),
                        new CommonInitProvider(metricRegistry, getHttpClientConfig()),
                        new ConfigServiceInitProvider("sherlock-autosuggest", true),
                        new HystrixInitProvider(60, metricRegistry));
                }
            }
        }
    }


    private static FkHttpClientConfig getHttpClientConfig(){
            return new FkHttpClientConfig.FkHttpClientConfigBuilder()
                .setConnectTimeoutMs(1000)
                .setConnectionRequestTimeoutMs(1000)
                .setSocketTimeoutMs(2000)
                .setMaxConnections(50)
                .setMaxConnectionsPerRoute(5)
                .build();
        }

    public static <T> T getInstance(Class<T> klass) {
        try {
            init();
        } catch (InvalidPropertiesFormatException e) {
            e.printStackTrace();
        }
        return injector.getInstance(klass);
    }

    public static <T> T getInstance(Class<T> klass, String namedAnnotation) {
        try {
            init();
        } catch (InvalidPropertiesFormatException e) {
            e.printStackTrace();
        }
        return injector.getInstance(Key.get(klass, Names.named(namedAnnotation)));
    }
}
