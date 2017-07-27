package com.flipkart.sherlock.semantic.common.util;

import com.flipkart.kloud.config.ConfigClient;
import com.flipkart.kloud.config.DynamicBucket;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.concurrent.Callable;

/**
 * Created by anurag.laddha on 24/07/17.
 */

/**
 * Wrapper for fk config service client
 * Reads environment value from /etc/default/fk-env on the machine
 * Creates bucket name as: <bucket name>-<environment>
 */
@Slf4j
public class FkConfigServiceWrapper {

    static class ConfigHelper {
        public static <V> V executeCallable (Callable<V> callable, String path, V default_) {
            V value = null;
            try{
                value = callable.call();
            }
            catch(Exception ex){
                log.error("Error in fetching property {}", path, ex);
            }
            return value != null ? value : default_;
        }
    }

    private static final String FK_ENV_FILE = "/etc/default/fk-env";
    private DynamicBucket bucket;

    /**
     * Initialises config service wrapper
     * @param bucketName: name of bucket to fetch configurations from
     * @param appendEnvironment: if true, reads environment from /etc/default/fk-env and appends to bucket name using '-' as delimiter
     */
    public FkConfigServiceWrapper(String bucketName, boolean appendEnvironment){

        ConfigClient configuration = new ConfigClient();
        try {
            String completeBucketName = appendEnvironment ? bucketName + "-" + getHostEnvironment() : bucketName;
            log.info("Loading configuration for bucket: {}",completeBucketName);
            this.bucket = configuration.getDynamicBucket(completeBucketName);
        }
        catch(Exception ex){
            log.error("Error in fetching configs", ex);
            throw new RuntimeException("Error in fetching exception", ex);
        }
    }


    public Integer getInt(String path){
        return ConfigHelper.executeCallable(() -> this.bucket.getInt(path), path, null);
    }

    public Integer getInt(String path, int default_){
        return ConfigHelper.executeCallable(() -> this.bucket.getInt(path), path, default_);
    }


    public String getString(String path) {
        return ConfigHelper.executeCallable(() -> this.bucket.getString(path), path, null);
    }

    public String getString(String path, String default_) {
        return ConfigHelper.executeCallable(() -> this.bucket.getString(path), path, default_);
    }


    public Boolean getBoolean(String path) {
        return ConfigHelper.executeCallable(() -> this.bucket.getBoolean(path), path, null);
    }


    public Boolean getBoolean(String path, Boolean default_) {
        return ConfigHelper.executeCallable(() -> this.bucket.getBoolean(path), path, default_);
    }

    public Double getDouble(String path) {
        return ConfigHelper.executeCallable(() -> this.bucket.getDouble(path), path, null);
    }

    public Double getDouble(String path, Double default_) {
        return ConfigHelper.executeCallable(() -> this.bucket.getDouble(path), path, default_);
    }

    private String getHostEnvironment() throws IOException {
        String env = getContents(FK_ENV_FILE);
        return env != null ? env : "local";
    }


    private String getContents(String fileName) throws IOException {
        String content = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            content = reader.readLine();
            reader.close();
        }
        catch(Exception ex){
            log.error("Error in reading file: {}", fileName, ex);
        }
        return content;
    }
}
