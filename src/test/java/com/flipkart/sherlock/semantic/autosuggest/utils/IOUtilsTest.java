package com.flipkart.sherlock.semantic.autosuggest.utils;

import com.flipkart.sherlock.semantic.autosuggest.models.SolrConfig;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dhruv.pancholi on 14/10/17.
 */
public class IOUtilsTest {

    private static final String SOLR_CONFIG_RESOURCE = "solrconfig.json";

    @Test
    public void open() throws Exception {
        Assert.assertNotNull(IOUtils.open("/etc/hosts"));
    }

    @Test
    public void openFromResource() throws Exception {
        Assert.assertNotNull(IOUtils.openFromResource(SOLR_CONFIG_RESOURCE));
    }

    @Test
    public void readLines() throws Exception {
        Assert.assertNotNull(IOUtils.openFromResource(SOLR_CONFIG_RESOURCE).readLines());
    }

    @Test
    public void readObject() throws Exception {
        Assert.assertNotNull(IOUtils.openFromResource(SOLR_CONFIG_RESOURCE).readObject(SolrConfig.class));
    }

    @Test
    public void readAll() throws Exception {
        Assert.assertNotNull(IOUtils.openFromResource(SOLR_CONFIG_RESOURCE).readAll());
    }

    @Test
    public void testGetFromResource() {
        Assert.assertNotNull(IOUtils.openFromResource(SOLR_CONFIG_RESOURCE).readObject(SolrConfig.class));
    }
}