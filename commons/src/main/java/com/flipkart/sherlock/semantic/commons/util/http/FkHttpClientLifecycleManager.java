package com.flipkart.sherlock.semantic.commons.util.http;

import io.dropwizard.lifecycle.Managed;

/**
 * Created by anurag.laddha on 07/12/17.
 */

/**
 * Dropwizard lifecycle manager for Http client
 */
public class FkHttpClientLifecycleManager implements Managed {

    private FkHttpClient httpClient;

    public FkHttpClientLifecycleManager(FkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() throws Exception {
        this.httpClient.close();
    }
}
