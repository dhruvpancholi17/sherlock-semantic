package com.flipkart.sherlock.semantic.commons.util.http;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * Created by anurag.laddha on 05/12/17.
 */

/**
 * Configs for Http client
 */
@Getter
public class FkHttpClientConfig {

    /**
     * Timeout in milliseconds for waiting for data or, put differently, a maximum period inactivity between two consecutive data packets).
     */
    private int socketTimeoutMs;

    /**
     *  timeout in milliseconds until a connection is established. A timeout value of zero is interpreted as an infinite timeout.
     */
    private int connectTimeoutMs;

    /**
     * timeout in milliseconds used when requesting a connection from the connection manager. A timeout value of zero is interpreted as an infinite timeout.
     */
    private int connectionRequestTimeoutMs;

    /**
     * defines maximum life span of persistent connections. No persistent connection will be re-used past its TTL value.
     */
    private int timeToLiveMs;

    /**
     * The maximum number of concurrent open connections.
     */
    private int maxConnections;

    /**
     * The maximum number of concurrent open connections per route.
     */
    private int maxConnectionsPerRoute;

    private FkHttpClientConfig(int socketTimeoutMs, int connectTimeoutMs, int connectionRequestTimeoutMs, int timeToLiveMs, int maxConnections, int maxConnectionsPerRoute) {
        this.socketTimeoutMs = socketTimeoutMs;
        this.connectTimeoutMs = connectTimeoutMs;
        this.connectionRequestTimeoutMs = connectionRequestTimeoutMs;
        this.timeToLiveMs = timeToLiveMs;
        this.maxConnections = maxConnections;
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
    }

    public static class FkHttpClientConfigBuilder {
        private int socketTimeoutMs = 500;
        private int connectTimeoutMs = 500;
        private int connectionRequestTimeoutMs = 500;
        private int timeToLiveMs = (int) TimeUnit.MINUTES.toMillis(10);
        private int maxConnections = 1024;
        private int maxConnectionsPerRoute = 1024;

        public FkHttpClientConfigBuilder setSocketTimeoutMs(int socketTimeoutMs) {
            this.socketTimeoutMs = socketTimeoutMs;
            return this;
        }

        public FkHttpClientConfigBuilder setConnectTimeoutMs(int connectTimeoutMs) {
            this.connectTimeoutMs = connectTimeoutMs;
            return this;
        }

        public FkHttpClientConfigBuilder setConnectionRequestTimeoutMs(int connectionRequestTimeoutMs) {
            this.connectionRequestTimeoutMs = connectionRequestTimeoutMs;
            return this;
        }

        public FkHttpClientConfigBuilder setTimeToLiveMs(int timeToLiveMs) {
            this.timeToLiveMs = timeToLiveMs;
            return this;
        }

        public FkHttpClientConfigBuilder setMaxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
            return this;
        }

        public FkHttpClientConfigBuilder setMaxConnectionsPerRoute(int maxConnectionsPerRoute) {
            this.maxConnectionsPerRoute = maxConnectionsPerRoute;
            return this;
        }

        public FkHttpClientConfig build() {
            return new FkHttpClientConfig(socketTimeoutMs, connectTimeoutMs, connectionRequestTimeoutMs, timeToLiveMs, maxConnections, maxConnectionsPerRoute);
        }
    }
}
