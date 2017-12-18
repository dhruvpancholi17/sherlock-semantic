package com.flipkart.sherlock.semantic.commons.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.sherlock.semantic.commons.CommonITContext;
import junit.framework.Assert;
import org.apache.solr.core.SolrConfig;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by anurag.laddha on 10/12/17.
 */

/**
 * Integration test for Fk config service wrapper
 */
public class FkConfigServiceWrapperTestIT {

    @Test
    public void testAutoSugegstConfig(){
        FkConfigServiceWrapper fkConfigServiceWrapper = new FkConfigServiceWrapper("sherlock-autosuggest", true);
        System.out.println(fkConfigServiceWrapper.getInt("mysql.pool.maxIdleTimeSec"));
        Assert.assertTrue(fkConfigServiceWrapper.getInt("mysql.pool.maxIdleTimeSec") > 0);
    }


    @Test
    public void testSolrConfig() throws Exception {
        ObjectMapper objectMapper = CommonITContext.getInstance(ObjectMapper.class);
        FkConfigServiceWrapper fkConfigServiceWrapper = CommonITContext.getInstance(FkConfigServiceWrapper.class);
        Assert.assertNotNull(fkConfigServiceWrapper.getString("solr.default"));
        SolrConfig solrConfig = objectMapper.readValue(fkConfigServiceWrapper.getString("solr.default"), SolrConfig.class);
        assertNotNull(solrConfig);
    }
}