package com.flipkart.sherlock.semantic.common.init;

import com.flipkart.sherlock.semantic.common.dao.mysql.CompleteTableDao;
import com.flipkart.sherlock.semantic.common.dao.mysql.ConfigsDao;
import com.flipkart.sherlock.semantic.common.dao.mysql.entity.MysqlConfig;
import com.flipkart.sherlock.semantic.common.dao.mysql.entity.MysqlConnectionPoolConfig;
import com.flipkart.sherlock.semantic.dao.mysql.AugmentationDao;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.skife.jdbi.v2.DBI;

/**
 * Created by anurag.laddha on 03/04/17.
 */
public class MysqlDaoProvider extends AbstractModule {

    private final MysqlConfig mysqlConfig;
    private final MysqlConnectionPoolConfig mysqlConnectionPoolConfig;

    public MysqlDaoProvider(MysqlConfig mysqlConfig, MysqlConnectionPoolConfig mysqlConnectionPoolConfig) {
        this.mysqlConfig = mysqlConfig;
        this.mysqlConnectionPoolConfig = mysqlConnectionPoolConfig;
    }


    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    public DBI DBIProvider() throws Exception {
        String dburl = "jdbc:mysql://" + mysqlConfig.getHost() + ":" + mysqlConfig.getPort()
                + "/" + mysqlConfig.getDbName() + "?rewriteBatchedStatements=true";

        //c3p0 Connection pool configuration
        ComboPooledDataSource pooledDataSource = new ComboPooledDataSource();
        pooledDataSource.setDriverClass("com.mysql.jdbc.Driver");
        pooledDataSource.setJdbcUrl(dburl);
        pooledDataSource.setUser(mysqlConfig.getUser());
        pooledDataSource.setPassword(mysqlConfig.getPassword());
        pooledDataSource.setMinPoolSize(mysqlConnectionPoolConfig.getMinPoolSize());
        pooledDataSource.setMaxPoolSize(mysqlConnectionPoolConfig.getMaxPoolSize());
        pooledDataSource.setInitialPoolSize(mysqlConnectionPoolConfig.getInitialPoolSize());
        pooledDataSource.setAcquireIncrement(mysqlConnectionPoolConfig.getAcquireIncrement());
        pooledDataSource.setMaxIdleTime(mysqlConnectionPoolConfig.getMaxIdleTimeSec());

        return new DBI(pooledDataSource);
    }

    @Inject
    @Singleton
    @Provides
    public ConfigsDao getConfigsDao(DBI dbi) {
        return dbi.onDemand(ConfigsDao.class);
    }
    // TODO: Remove this piece from here

    @Inject
    @Singleton
    @Provides
    public AugmentationDao getAugmentationDao(DBI dbi) {
        return dbi.onDemand(AugmentationDao.class);
    }
    // TODO: Remove this piece from here

    @Inject
    @Singleton
    @Provides
    public CompleteTableDao getCompleteTableDao(DBI dbi) {
        return dbi.onDemand(CompleteTableDao.class);
    }
}
