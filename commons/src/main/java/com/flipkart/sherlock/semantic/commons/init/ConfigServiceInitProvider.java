package com.flipkart.sherlock.semantic.commons.init;

import com.flipkart.sherlock.semantic.commons.config.FkConfigServiceWrapper;
import com.google.inject.AbstractModule;

/**
 * Created by anurag.laddha on 11/12/17.
 */

/**
 * DI provider for Fk config service wrapper
 * We are instantiating FkConfigServiceWrapper beforehand initialising DI framework
 * because there might be configurations required (eg end points) to properly initialise all objects through DI f/w.
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
