package com.flipkart.sherlock.semantic.norm.core;

import com.flipkart.sherlock.semantic.commons.Constants;
import com.flipkart.sherlock.semantic.norm.dao.NormalisationDao;
import com.flipkart.sherlock.semantic.norm.entity.BrandInfo;
import com.flipkart.sherlock.semantic.norm.util.NormConstants;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by anurag.laddha on 28/11/17.
 */

/**
 * Fetches and catches brands data and evaluates if given text is a brand name
 */

@Slf4j
@Singleton
public class BrandDataSource {

    private LoadingCache<String, Set<BrandInfo>> brandInfoCache;

    @Inject
    public BrandDataSource(NormalisationDao normalisationDao,
                           @Named(NormConstants.GUICE_EXECUTOR_SERVICE_CACHE_RELOAD) ExecutorService executorService,
                           @Named(NormConstants.GUICE_CACHE_EXPIRY_BRAND) int cacheExpireSec){
        BrandDataLoader brandDataLoader = new BrandDataLoader(normalisationDao, executorService);
        this.brandInfoCache = CacheBuilder.newBuilder()
            .maximumSize(10)
            .refreshAfterWrite(cacheExpireSec, TimeUnit.SECONDS)
            .build(brandDataLoader);
    }

    /**
     * Evaluate if given text is a brand in given context
     */
    public boolean isBrand(BrandInfo brandInfo){
        if (brandInfo != null){
            try {
                return this.brandInfoCache.get(Constants.DUMMY_KEY).contains(brandInfo);
            } catch (ExecutionException e) {
                log.error("Error in fetching brand info from cache", e);
            }
        }
        return false;
    }

    /**
     * Loader to fetch brand info from data source
     */
    static class BrandDataLoader extends CacheLoader<String, Set<BrandInfo>> {

        private NormalisationDao normalisationDao;
        private ExecutorService executorService;

        public BrandDataLoader(NormalisationDao normalisationDao, ExecutorService executorService) {
            this.normalisationDao = normalisationDao;
            this.executorService = executorService;
        }

        @Override
        public Set<BrandInfo> load(String s) throws Exception {
            log.info("Loading brands data");
            Set<BrandInfo> allBrands = getAllBrands();
            log.info("Finished loading brands data. Number of entries: {}", allBrands != null ? allBrands.size() : 0);
            return allBrands;
        }

        @Override
        public ListenableFuture<Set<BrandInfo>> reload(String key, Set<BrandInfo> oldValue) throws Exception {
            log.info("Queued loading brand info async");
            ListenableFutureTask<Set<BrandInfo>> task = ListenableFutureTask.create(() ->{
                log.info("Started loading brand info async");
                Set<BrandInfo> allBrands = getAllBrands();
                log.info("Finished fetching brand info async. Number of elements : {}",allBrands != null ? allBrands.size() : 0);
                return allBrands;
            });
            this.executorService.submit(task);
            return task;
        }

        /**
         * Gets brand info from data source
         * Brand can be associated with more than 1 store (comma separated)
         * Creates (brand,store) combination + brand alone, lower cases brand and store name
         *
         * Eg
         *      Input: brand: b1 ;; store: s1,s2
         *      Output: (b1,s1), (b1,s2), (b1)
         */
        private Set<BrandInfo> getAllBrands(){
            List<BrandInfo> allBrands = this.normalisationDao.getAllBrands();
            Set<BrandInfo> processedBrands = new HashSet<>();
            List<String> stores;
            for (BrandInfo currBrand : allBrands) {
                String brandStr = currBrand.getName().toLowerCase().trim();
                if (StringUtils.isNotBlank(currBrand.getName())){
                    if (StringUtils.isNotBlank(currBrand.getStore())) {
                        stores = Arrays.stream(currBrand.getStore().split(Constants.DELIM_COMMA)).map(elem -> elem.toLowerCase().trim()).collect(Collectors.toList());
                        stores.forEach(elem -> processedBrands.add(new BrandInfo(brandStr, elem)));
                    }
                    //brand without store
                    processedBrands.add(new BrandInfo(brandStr, null));
                }
            }
            return processedBrands;
        }
    }

}
