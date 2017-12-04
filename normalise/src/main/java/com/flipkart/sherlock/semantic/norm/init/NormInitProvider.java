package com.flipkart.sherlock.semantic.norm.init;

import com.flipkart.sherlock.semantic.norm.core.INormalise;
import com.flipkart.sherlock.semantic.norm.core.IndividualTokenNormalisationService;
import com.flipkart.sherlock.semantic.norm.dao.NormalisationDao;
import com.flipkart.sherlock.semantic.norm.util.NormConstants;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import org.skife.jdbi.v2.DBI;

import java.util.concurrent.ExecutorService;

/**
 * Created by anurag.laddha on 03/12/17.
 */
public class NormInitProvider extends AbstractModule {

    private int brandCacheExpirySec;
    private int singulariseResourceCacheExpirySec;
    private int maxBrandEvaluateTokenLength;
    private ExecutorService inMemCacheReloadES;

    public NormInitProvider(int brandCacheExpirySec, int singulariseResourceCacheExpirySec, int maxBrandEvaluateTokenLength, ExecutorService inMemCacheReloadES) {
        this.brandCacheExpirySec = brandCacheExpirySec;
        this.singulariseResourceCacheExpirySec = singulariseResourceCacheExpirySec;
        this.inMemCacheReloadES = inMemCacheReloadES;
        this.maxBrandEvaluateTokenLength = maxBrandEvaluateTokenLength;
    }

    @Override
    protected void configure(){
        /**
         * Instance bindings
         */
        bind(Integer.class)
            .annotatedWith(Names.named(NormConstants.GUICE_CACHE_EXPIRY_BRAND))
            .toInstance(this.brandCacheExpirySec);

        bind(Integer.class)
            .annotatedWith(Names.named(NormConstants.GUICE_CACHE_EXPIRY_SINGULARISE_RESOURCES))
            .toInstance(this.singulariseResourceCacheExpirySec);

        bind(ExecutorService.class)
            .annotatedWith(Names.named(NormConstants.GUICE_EXECUTOR_SERVICE_CACHE_RELOAD))
            .toInstance(this.inMemCacheReloadES);

        bind(Integer.class)
            .annotatedWith(Names.named(NormConstants.GUICE_LENGTH_BRAND_CONSIDERATION))
            .toInstance(this.maxBrandEvaluateTokenLength);

        /**
         * Binder for normalisation type to concrete implementation
         */
        MapBinder<INormalise.Type, INormalise> typeToNormaliseImplMap =
            MapBinder.newMapBinder(binder(), INormalise.Type.class, INormalise.class);

        typeToNormaliseImplMap.addBinding(INormalise.Type.IndividualToken)
            .to(IndividualTokenNormalisationService.class).in(Singleton.class);
    }

    @Inject
    @Singleton
    @Provides
    public NormalisationDao getNormalisationDao(DBI dbi){
        return dbi.onDemand(NormalisationDao.class);
    }
}
