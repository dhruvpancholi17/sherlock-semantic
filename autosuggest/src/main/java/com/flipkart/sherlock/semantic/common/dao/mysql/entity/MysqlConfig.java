package com.flipkart.sherlock.semantic.common.dao.mysql.entity;

import com.flipkart.sherlock.semantic.commons.config.FkConfigServiceWrapper;
import lombok.*;

import java.util.InvalidPropertiesFormatException;

/**
 * Created by anurag.laddha on 03/04/17.
 */
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MysqlConfig {

    private static final String DB_NAME = "mysql.dbName";
    private static final String HOST = "mysql.host";
    private static final String PASSWORD = "mysql.password";
    private static final String ACQUIRE_INCREMENT = "mysql.pool.acquireIncrement";
    private static final String INITIAL_POOL_SIZE = "mysql.pool.initialPoolSize";
    private static final String MAX_IDLE_TIME_SEC = "mysql.pool.maxIdleTimeSec";
    private static final String MAX_POOL_SIZE = "mysql.pool.maxPoolSize";
    private static final String MIN_POOL_SIZE = "mysql.pool.minPoolSize";
    private static final String PORT = "mysql.port";
    private static final String USER = "mysql.user";

    private String dbName;
    private String host;
    private String password;
    private Integer acquireIncrement;
    private Integer initialPoolSize;
    private Integer maxIdleTimeSec;
    private Integer maxPoolSize;
    private Integer minPoolSize;
    private Integer port;
    private String user;

    public static MysqlConfig getConfig(FkConfigServiceWrapper fkConfigServiceWrapper) throws InvalidPropertiesFormatException {

        MysqlConfig mysqlConfig = MysqlConfig.builder()
                .dbName(fkConfigServiceWrapper.getString(DB_NAME))
                .host(fkConfigServiceWrapper.getString(HOST))
                .host(fkConfigServiceWrapper.getString(HOST))
                .password(fkConfigServiceWrapper.getString(PASSWORD))
                .acquireIncrement(fkConfigServiceWrapper.getInt(ACQUIRE_INCREMENT))
                .initialPoolSize(fkConfigServiceWrapper.getInt(INITIAL_POOL_SIZE))
                .maxIdleTimeSec(fkConfigServiceWrapper.getInt(MAX_IDLE_TIME_SEC))
                .maxPoolSize(fkConfigServiceWrapper.getInt(MAX_POOL_SIZE))
                .minPoolSize(fkConfigServiceWrapper.getInt(MIN_POOL_SIZE))
                .port(fkConfigServiceWrapper.getInt(PORT))
                .user(fkConfigServiceWrapper.getString(USER))
                .build();

        validateMysqlConfig(mysqlConfig);

        return mysqlConfig;
    }

    private static void validateMysqlConfig(MysqlConfig mysqlConfig) throws InvalidPropertiesFormatException {
        if (mysqlConfig.getDbName() == null)
            throw new InvalidPropertiesFormatException("Property dbName from config cannot be null");
        if (mysqlConfig.getHost() == null)
            throw new InvalidPropertiesFormatException("Property host from config cannot be null");
        if (mysqlConfig.getPassword() == null)
            throw new InvalidPropertiesFormatException("Property password from config cannot be null");
        if (mysqlConfig.getAcquireIncrement() == null)
            throw new InvalidPropertiesFormatException("Property acquireIncrement from config cannot be null");
        if (mysqlConfig.getInitialPoolSize() == null)
            throw new InvalidPropertiesFormatException("Property initialPoolSize from config cannot be null");
        if (mysqlConfig.getMaxIdleTimeSec() == null)
            throw new InvalidPropertiesFormatException("Property maxIdleTimeSec from config cannot be null");
        if (mysqlConfig.getMaxPoolSize() == null)
            throw new InvalidPropertiesFormatException("Property maxPoolSize from config cannot be null");
        if (mysqlConfig.getMinPoolSize() == null)
            throw new InvalidPropertiesFormatException("Property minPoolSize from config cannot be null");
        if (mysqlConfig.getPort() == null)
            throw new InvalidPropertiesFormatException("Property port from config cannot be null");
        if (mysqlConfig.getUser() == null)
            throw new InvalidPropertiesFormatException("Property user from config cannot be null");
    }
}
