package com.flipkart.sherlock.semantic.commons.init;

import com.flipkart.sherlock.semantic.commons.dao.mysql.entity.MysqlConfig;
import com.flipkart.sherlock.semantic.commons.dao.mysql.entity.MysqlConnectionPoolConfig;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import org.skife.jdbi.v2.DBI;

/**
 * Created by anurag.laddha on 03/12/17.
 */

/**
 * JDBI provider for Mysql
 * For module specific DAO dependent on DBI, please add AbstractModule in those module itself, not in commons
 */

@Slf4j
public class MysqlDBIProvider extends AbstractModule {

    private final MysqlConfig mysqlConfig;
    private final MysqlConnectionPoolConfig mysqlConnectionPoolConfig;

    public MysqlDBIProvider(MysqlConfig mysqlConfig, MysqlConnectionPoolConfig mysqlConnectionPoolConfig) {
        this.mysqlConfig = mysqlConfig;
        this.mysqlConnectionPoolConfig = mysqlConnectionPoolConfig;
    }


    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    public DBI DBIProvider() throws Exception {
        String dburl = "jdbc:mysql://" + mysqlConfig.getDbHost() + ":" + mysqlConfig.getDbPort()
            + "/" + mysqlConfig.getDbName() + "?rewriteBatchedStatements=true";
        log.info("Mysql connection URL: {}", dburl);

        //c3p0 Connection pool configuration
        ComboPooledDataSource pooledDataSource = new ComboPooledDataSource();
        pooledDataSource.setDriverClass("com.mysql.jdbc.Driver");
        pooledDataSource.setJdbcUrl(dburl);
        pooledDataSource.setUser(mysqlConfig.getUserName());
        pooledDataSource.setPassword(mysqlConfig.getPassword());
        pooledDataSource.setMinPoolSize(mysqlConnectionPoolConfig.getMinPoolSize());
        pooledDataSource.setMaxPoolSize(mysqlConnectionPoolConfig.getMaxPoolSize());
        pooledDataSource.setInitialPoolSize(mysqlConnectionPoolConfig.getInitialPoolSize());
        pooledDataSource.setAcquireIncrement(mysqlConnectionPoolConfig.getAcquireIncrement());
        pooledDataSource.setMaxIdleTime(mysqlConnectionPoolConfig.getMaxIdleTimeSec());

        return new DBI(pooledDataSource);
    }
}
