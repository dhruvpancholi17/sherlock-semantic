package com.flipkart.sherlock.semantic.norm.core;

import com.flipkart.sherlock.semantic.commons.Constants;
import com.flipkart.sherlock.semantic.norm.dao.NormalisationDao;
import com.flipkart.sherlock.semantic.norm.entity.PluralSingularInfo;
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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by anurag.laddha on 03/12/17.
 */


/**
 * Fetches and catches plural to singular resource and provides singularised form of given text/token
 */
@Slf4j
@Singleton
public class SingulariseResourcesDataSource {

    private LoadingCache<String, Map<String, String>> singularInfoCache;

    @Inject
    public SingulariseResourcesDataSource(NormalisationDao normalisationDao,
                                          @Named(NormConstants.GUICE_EXECUTOR_SERVICE_CACHE_RELOAD) ExecutorService executorService,
                                          @Named(NormConstants.GUICE_CACHE_EXPIRY_SINGULARISE_RESOURCES) int cacheExpireSec){
        SingularResourceDataLoader singularResourceLoader = new SingularResourceDataLoader(normalisationDao, executorService);
        this.singularInfoCache = CacheBuilder.newBuilder()
            .maximumSize(10)
            .refreshAfterWrite(cacheExpireSec, TimeUnit.SECONDS)
            .build(singularResourceLoader);
    }

    /**
     * Provides singularised form of given plural token(s)
     * @param plural: Plural token to singularise
     */
    public String singularise(String plural){
        if (StringUtils.isNotBlank(plural)){
            try {
                String singular = this.singularInfoCache.get(Constants.DUMMY_KEY).get(plural.toLowerCase().trim());
                //return plural itself if singular not found for it
                return singular != null ? singular : plural;
            } catch (ExecutionException e) {
                log.error("Error while fetching singular form of plural", e);
            }
        }
        return plural;
    }


    /**
     * Loader to fetch singular resources (plural singular tuple) from data source
     */
    static class SingularResourceDataLoader extends CacheLoader<String, Map<String, String>> {

        private NormalisationDao normalisationDao;
        private ExecutorService executorService;

        public SingularResourceDataLoader(NormalisationDao normalisationDao, ExecutorService executorService) {
            this.normalisationDao = normalisationDao;
            this.executorService = executorService;
        }

        @Override
        public Map<String, String> load(String s) throws Exception {
            log.info("Loading singularise resources");
            Map<String, String> pluralToSingularMap = getAllPluralToSingularEntries();
            log.info("Finished loading singularise resources. Number of entries: {}", pluralToSingularMap != null ? pluralToSingularMap.size() : 0);
            return pluralToSingularMap;
        }

        @Override
        public ListenableFuture<Map<String, String>> reload(String key, Map<String, String> oldValue) throws Exception {
            log.info("Queued loading brand info async");
            ListenableFutureTask<Map<String, String>> task = ListenableFutureTask.create(() ->{
                log.info("Started reloading singular resources async");
                Map<String, String> pluralToSingularMap = getAllPluralToSingularEntries();
                log.info("Finished reloading singular resources async. Number of elements : {}",pluralToSingularMap != null ? pluralToSingularMap.size() : 0);
                return pluralToSingularMap;
            });
            this.executorService.submit(task);
            return task;
        }

        /**
         * Gets singularisation resources from data source
         * Converts them to lower case, makes them available only if singular is not same as plural and both are not null
         */
        private Map<String, String> getAllPluralToSingularEntries(){
            List<PluralSingularInfo> allPluralToSingularEntries = this.normalisationDao.getAllPluralSingularEntries();
            return allPluralToSingularEntries.stream()
                .filter(elem ->
                    StringUtils.isNotBlank(elem.getPlural()) && StringUtils.isNotBlank(elem.getSingular())
                        && !elem.getPlural().trim().equalsIgnoreCase(elem.getSingular().trim()))
                .collect(Collectors.toMap(a -> a.getPlural().toLowerCase().trim(), a -> a.getSingular().toLowerCase().trim()));
        }
    }

}
