package com.flipkart.sherlock.semantic.common.solr;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.flipkart.sherlock.semantic.common.config.Constants.FK_SOLR_SERVER_EXPIRY_TIME;
import static com.flipkart.sherlock.semantic.common.config.Constants.MAX_FK_SOLR_SERVERS;

/**
 * Created by anurag.laddha on 26/04/17.
 */

@Slf4j
@Singleton
public class SolrServerProvider {

    private LoadingCache<Core, FKHttpSolServer> solrCoreToServer;

    public SolrServerProvider() {
        this.solrCoreToServer = CacheBuilder.newBuilder()
                .expireAfterWrite(FK_SOLR_SERVER_EXPIRY_TIME, TimeUnit.MINUTES)
                .maximumSize(MAX_FK_SOLR_SERVERS)
                .build(new CacheLoader<Core, FKHttpSolServer>() {
                    @Override
                    public FKHttpSolServer load(Core solrCore) throws Exception {
                        log.info("Creating FKHttpSolServer for url: {}", solrCore.getSolrUrl());
                        return new FKHttpSolServer(solrCore.getSolrUrl());
                    }
                });
    }

    public SolrServer getSolrServer(Core solrCore) {
        if (StringUtils.isBlank(solrCore.getCore())) return null;
        if (StringUtils.isBlank(solrCore.getHostname())) return null;
        if (solrCore.getPort() <= 0) return null;
        try {
            FKHttpSolServer fkHttpSolServer = solrCoreToServer.get(solrCore);
            if (fkHttpSolServer == null) {
                solrCoreToServer.refresh(solrCore);
                return solrCoreToServer.get(solrCore);
            }
            return fkHttpSolServer;
        } catch (ExecutionException e) {
            log.error("Unable to retrieve FKHttpSolServer for {} from Guava Cache: {}", solrCore.getSolrUrl(), e);
            return null;
        }
    }
}
