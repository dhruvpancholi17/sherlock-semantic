package com.flipkart.sherlock.semantic.common.init;

import com.flipkart.sherlock.semantic.common.util.FkConfigServiceWrapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Created by anurag.laddha on 24/07/17.
 */
public class ConfigServiceProvider extends AbstractModule {

    private String bucketName;
    private boolean appendEnvironment;

    public ConfigServiceProvider(String bucketName, boolean appendEnvironment) {
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
