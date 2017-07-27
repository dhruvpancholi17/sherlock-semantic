package com.flipkart.sherlock.semantic.common.util;

/**
 * Created by anurag.laddha on 16/04/17.
 */

import com.flipkart.sherlock.semantic.common.dao.mysql.entity.MysqlConfig;
import com.flipkart.sherlock.semantic.common.dao.mysql.entity.MysqlConnectionPoolConfig;
import com.flipkart.sherlock.semantic.common.init.MiscInitProvider;
import com.flipkart.sherlock.semantic.common.init.MysqlDaoProvider;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

import java.util.InvalidPropertiesFormatException;
import java.util.concurrent.TimeUnit;

import static com.flipkart.sherlock.semantic.app.AppConstants.AUTOSUGGEST_BUCKET;

/**
 * Use this context to get object instances for testing
 * This must be used for integration tests ONLY
 */

public class TestContext {
    private static Injector injector;

    private static void init() throws InvalidPropertiesFormatException {
        if (injector == null) {
            synchronized (TestContext.class) {
                if (injector == null) {
                    FkConfigServiceWrapper fkConfigServiceWrapper = new FkConfigServiceWrapper(AUTOSUGGEST_BUCKET, true);
                    MysqlConfig mysqlConfig = MysqlConfig.getConfig(fkConfigServiceWrapper);
                    MysqlConnectionPoolConfig connectionPoolConfig = new MysqlConnectionPoolConfig.MysqlConnectionPoolConfigBuilder(1,10)
                        .setInitialPoolSize(1)
                        .setAcquireIncrement(2)
                        .setMaxIdleTimeSec((int) TimeUnit.MINUTES.toSeconds(30)).build();

                    injector = Guice.createInjector(new MysqlDaoProvider(mysqlConfig, connectionPoolConfig),
                        new MiscInitProvider(10, 5));
                }
            }
        }
    }


    public static <T> T getInstance(Class<T> klass){
        try {
            init();
        } catch (InvalidPropertiesFormatException e) {
            e.printStackTrace();
        }
        return injector.getInstance(klass);
    }

    public static <T> T getInstance(Class<T> klass, String namedAnnotation){
        try {
            init();
        } catch (InvalidPropertiesFormatException e) {
            e.printStackTrace();
        }
        return injector.getInstance(Key.get(klass, Names.named(namedAnnotation)));
    }
}
