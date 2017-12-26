package com.flipkart.sherlock.semantic.commons.cache.couchbase;

import lombok.Getter;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by anurag.laddha on 19/12/17.
 */

@Getter
public class CouchbaseConfig {
    /**
     * Set of host URIs.
     * Format: http://host1:port/pools
     */
    private Set<String> hostsUri;

    /**
     * Bucket name
     */
    private String bucket;

    /**
     * Bucket password
     * If none, set to empty string
     */
    private String password;

    /**
     * Expiry time in seconds
     */
    private int timeToLiveSec;

    /**
     * Max operation timeout in milliseconds
     */
    private int operationTimeoutMs;

    /**
     * Set true if key should be hashed (to ensure fixed length key)
     */
    private boolean hashKey;

    private CouchbaseConfig(Set<String> hostsUri, String bucket, String password, int timeToLiveSec, int operationTimeoutMs, boolean hashKey) {
        this.hostsUri = hostsUri;
        this.bucket = bucket;
        this.password = password;
        this.timeToLiveSec = timeToLiveSec;
        this.operationTimeoutMs = operationTimeoutMs;
        this.hashKey = hashKey;
    }

    /**
     * Builder
     */
    public static class CouchbaseConfigBuilder {
        private Set<String> hostsUri;
        private String bucket;
        private String password;
        private int timeToLiveSec;
        private int operationTimeoutMs = -1; //to allow couchbase to use its default
        private boolean hashKey = false; //No hashing by default

        public CouchbaseConfigBuilder(Set<String> hostsUri, String bucket, String password, int timeToLiveSec){
            this.hostsUri = hostsUri;
            this.bucket = bucket;
            this.password = password;
            this.timeToLiveSec = timeToLiveSec > TimeUnit.DAYS.toSeconds(30) ? (int) TimeUnit.DAYS.toSeconds(30) - 1 : timeToLiveSec; //max TTL is 30 days
        }

        public CouchbaseConfigBuilder setOperationTimeoutMs(int operationTimeoutMs) {
            this.operationTimeoutMs = operationTimeoutMs;
            return this;
        }

        public CouchbaseConfigBuilder setHashKey(boolean hashKey) {
            this.hashKey = hashKey;
            return this;
        }

        public CouchbaseConfig build() {
            return new CouchbaseConfig(hostsUri, bucket, password, timeToLiveSec, operationTimeoutMs, hashKey);
        }
    }
}
