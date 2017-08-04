package com.flipkart.sherlock.semantic.autosuggest.dao;

import com.flipkart.kloud.config.Bucket;
import com.flipkart.kloud.config.ConfigClient;
import com.flipkart.kloud.config.error.ConfigServiceException;
import com.flipkart.sherlock.semantic.autosuggest.models.SolrConfig;
import com.flipkart.sherlock.semantic.autosuggest.utils.IOUtils;
import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.flipkart.sherlock.semantic.common.dao.mysql.CompleteTableDao;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Created by dhruv.pancholi on 31/05/17.
 */
@Slf4j
@Singleton
public class ConfigsDao extends AbstractReloadableCache<Map<String, SolrConfig>> {

    @Inject
    public ConfigsDao(CompleteTableDao completeTableDao, JsonSeDe jsonSeDe) {
        super(completeTableDao, jsonSeDe, 1, TimeUnit.DAYS);
    }

    @Override
    protected Map<String, SolrConfig> getFromSource() {
        ConfigClient configClient = new ConfigClient();
        Bucket bucket = null;
        Map<String, SolrConfig> configMap = new HashMap<>();
        try {
            String clusterName = getClusterName();
            log.info("Getting configuration for the cluster name: {}", clusterName);
            bucket = configClient.getBucket(clusterName, -1);
            log.info("Config obtained from config service: {}", jsonSeDe.writeValueAsString(bucket));
            Map<String, Object> keys = bucket.getKeys();
            for (String key : keys.keySet()) {
                String configString = bucket.getString(key);
                if (configString == null || configString.isEmpty()) continue;
                SolrConfig solrConfig = jsonSeDe.readValue(configString, SolrConfig.class);
                if (solrConfig == null) continue;
                configMap.put(key, solrConfig);
            }
        } catch (ConfigServiceException | IOException e) {
            configMap.put("default", new SolrConfig());
            log.error("Unable to get config from config service", e);
        }
        if (!configMap.containsKey("default")) configMap.put("default", new SolrConfig());
        return configMap;
    }

    private static String getClusterName() {
        return IOUtils.open("/etc/default/soa-cluster-name").readLines().get(0);
    }

    public static void main(String[] args) throws IOException, ConfigServiceException {
        String clusterName = getClusterName();
        System.out.println(clusterName);
    }
}
