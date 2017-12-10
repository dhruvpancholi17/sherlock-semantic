package com.flipkart.sherlock.semantic.commons.config;

import com.flipkart.kloud.config.ConfigClient;
import com.flipkart.kloud.config.DynamicBucket;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Wrapper for fk config service client
 * Reads environment value from /etc/default/fk-env on the machine
 * Creates bucket name as: <bucket name>-<environment>
 */
@Slf4j
public class FkConfigServiceWrapper {

    private static final String FK_ENV_FILE = "/etc/default/fk-env";

    private DynamicBucket bucket;


    /**
     * Initialises config service wrapper
     *
     * @param bucketName:        name of bucket to fetch configurations from
     * @param appendEnvironment: if true, reads environment from /etc/default/fk-env and appends to bucket name using '-' as delimiter
     */
    public FkConfigServiceWrapper(String bucketName, boolean appendEnvironment) {
        try {
            String completeBucketName = appendEnvironment ? bucketName + "-" + getHostEnvironment() : bucketName;
            log.info("Loading configuration for bucket: {}", completeBucketName);
            ConfigClient configuration = new ConfigClient();
            this.bucket = configuration.getDynamicBucket(completeBucketName);
        } catch (Exception ex) {
            log.error("Error in fetching configs", ex);
            throw new RuntimeException("Error in fetching configs", ex);
        }
    }


    public Integer getInt(String path) {
        return executeCallable(() -> this.bucket.getInt(path), path, null);
    }

    public Integer getInt(String path, int default_) {
        return executeCallable(() -> this.bucket.getInt(path), path, default_);
    }


    public String getString(String path) {
        return executeCallable(() -> this.bucket.getString(path), path, null);
    }


    public String getString(String path, String default_) {
        return executeCallable(() -> this.bucket.getString(path), path, default_);
    }


    public Boolean getBoolean(String path) {
        return executeCallable(() -> this.bucket.getBoolean(path), path, null);
    }


    public Boolean getBoolean(String path, Boolean default_) {
        return executeCallable(() -> this.bucket.getBoolean(path), path, default_);
    }


    public Double getDouble(String path) {
        return executeCallable(() -> this.bucket.getDouble(path), path, null);
    }


    public Double getDouble(String path, Double default_) {
        return executeCallable(() -> this.bucket.getDouble(path), path, default_);
    }

    private String getHostEnvironment() throws IOException {
        List<String> environment = getContentFromFile(FK_ENV_FILE);
        return environment != null && environment.size() > 0 ? environment.get(0) : "local";
    }


    @VisibleForTesting
    List<String> getContentFromFile(String fileName) throws IOException{
        return FileUtils.readLines(new File(fileName));
    }


    /**
     * Helper function for exception handling, logging and assigning default value in case of error
     * This prevents each method from having to write same exception handling code
     * @return
     */
    private static <V> V executeCallable(Callable<V> callable, String path, V default_) {
        V value = null;
        try {
            value = callable.call();
        } catch (Exception ex) {
            log.error("Error in fetching property {}", path, ex);
        }
        return value != null ? value : default_;
    }
}

