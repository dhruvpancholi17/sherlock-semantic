package com.flipkart.sherlock.semantic.autosuggest.providers;

import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.flipkart.sherlock.semantic.common.util.FkConfigServiceWrapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Created by dhruv.pancholi on 30/05/17.
 */
public class WrapperProvider extends AbstractModule {

    private FkConfigServiceWrapper fkConfigServiceWrapper;

    public WrapperProvider(FkConfigServiceWrapper fkConfigServiceWrapper) {
        this.fkConfigServiceWrapper = fkConfigServiceWrapper;
    }

    @Override
    protected void configure() {

    }

    @Provides
    @Singleton
    JsonSeDe jsonSeDeProvider() {
        return JsonSeDe.getInstance();
    }

    @Provides
    @Singleton
    FkConfigServiceWrapper getFkConfigServiceWrapper() {
        return fkConfigServiceWrapper;
    }
}
