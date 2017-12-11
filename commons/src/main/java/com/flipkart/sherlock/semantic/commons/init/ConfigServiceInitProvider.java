package com.flipkart.sherlock.semantic.commons.init;

import com.flipkart.sherlock.semantic.commons.config.FkConfigServiceWrapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Created by anurag.laddha on 11/12/17.
 */
public class ConfigServiceInitProvider extends AbstractModule {

    private String bucketName;
    private boolean appendEnvironment;

    public ConfigServiceInitProvider(String bucketName, boolean appendEnvironment) {
        this.bucketName = bucketName;
        this.appendEnvironment = appendEnvironment;
    }

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    FkConfigServiceWrapper getConfigService(){
        return new FkConfigServiceWrapper(bucketName, appendEnvironment);
    }
}
