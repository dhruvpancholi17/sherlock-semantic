package com.flipkart.sherlock.semantic.common.init;

import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Created by anurag.laddha on 15/12/17.
 */
public class JsonSerdeInitProvider extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    JsonSeDe getJsonSerde(){
        return JsonSeDe.getInstance();
    }
}
