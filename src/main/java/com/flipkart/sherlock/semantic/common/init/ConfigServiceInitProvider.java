package com.flipkart.sherlock.semantic.common.init;

import com.flipkart.sherlock.semantic.common.util.FkConfigServiceWrapper;
import com.google.inject.AbstractModule;

/**
 * Created by anurag.laddha on 27/07/17.
 */

/**
 * This is kept separate because diff apps will have configs based on diff buckets
 */
public class ConfigServiceInitProvider extends AbstractModule {

    private FkConfigServiceWrapper fkConfigServiceWrapper;

    public ConfigServiceInitProvider(FkConfigServiceWrapper fkConfigServiceWrapper) {
        this.fkConfigServiceWrapper = fkConfigServiceWrapper;
    }

    @Override
    protected void configure() {
        bind(FkConfigServiceWrapper.class).toInstance(this.fkConfigServiceWrapper);
    }
}
