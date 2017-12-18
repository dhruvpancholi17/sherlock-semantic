package com.flipkart.sherlock.semantic.common.util;

import com.flipkart.sherlock.semantic.autosuggest.outer.MockFkConfigServiceWrapper;
import com.flipkart.sherlock.semantic.commons.config.FkConfigServiceWrapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by dhruv.pancholi on 17/10/17.
 */
public class FkConfigServiceWrapperTest {

    private FkConfigServiceWrapper fkConfigServiceWrapper;

    @Before
    public void setUp() {
        fkConfigServiceWrapper = new MockFkConfigServiceWrapper().getFkConfigServiceWrapper();
    }

    @Ignore("DS-1435 - fix failing test")
    @Test
    public void testString() {
        assertEquals(fkConfigServiceWrapper.getString("sampleString", "string"), "string");
        assertEquals(fkConfigServiceWrapper.getString("hystrix.searchEngine.search"), "{\"groupKey\":\"search\",\"commandKey\":\"search\",\"threadPoolKey\":\"pool\",\"executionTimeoutMs\":1000,\"poolCoreSize\":50,\"poolMaxSize\":1000,\"poolMaxQueueSize\":1000}");
        assertEquals(fkConfigServiceWrapper.getString("hystrix.searchEngine.spell"), "{\"groupKey\":\"search\",\"commandKey\":\"spell\",\"threadPoolKey\":\"pool\",\"executionTimeoutMs\":1000,\"poolCoreSize\":50,\"poolMaxSize\":1000,\"poolMaxQueueSize\":1000}");
        assertEquals(fkConfigServiceWrapper.getString("mysql.dbName"), "sherlock");
        assertEquals(fkConfigServiceWrapper.getString("mysql.host"), "sherlock-app-slave-db.nm.flipkart.com");
        assertEquals(fkConfigServiceWrapper.getString("mysql.password"), "fB342jPm");
        assertEquals(fkConfigServiceWrapper.getString("mysql.user"), "sherlock_ro");
        assertEquals(fkConfigServiceWrapper.getString("solr.default"), "{\"solrHost\":\"10.47.2.32\",\"solrPort\":25280,\"solrCore\":\"autosuggest\",\"queryField\":\"textSpell\",\"prefixField\":\"prefixSpell_edgytext\",\"phraseField\":\"textSpell_edgytext_phrase\",\"phraseBoost\":1,\"boostFunction\":\"min(div(log(field(impressions_sint)),log(10)),10.0)^1\",\"sortFunctionString\":\"score desc,ranking-score_sfloat desc,p-hits_sfloat desc,ctr_sfloat desc\",\"rows\":10,\"ctrThreshold\":0.05,\"ctrField\":\"ctr_float\",\"fqsString\":\"\",\"maxNumberOfStorePerQuery\":3,\"solrSpellCorrection\":true,\"wilsonCtrThreshold\":0.1,\"numTokens\":5,\"impressionsThreshold\":100,\"stateHitsThreshold\":0.0}");
        assertEquals(fkConfigServiceWrapper.getString("solr.generative_boosting_score"), "{\"solrHost\":\"10.32.197.131\",\"solrPort\":25280,\"solrCore\":\"autosuggest_ab\",\"queryField\":\"textSpell\",\"prefixField\":\"prefixSpell_edgytext\",\"phraseField\":\"textSpell_edgytext_phrase\",\"phraseBoost\":1,\"boostFunction\":\"boosting_score_sfloat^1\",\"sortFunctionString\":\"score desc,ranking-score_sfloat desc,p-hits_sfloat desc,ctr_sfloat desc\",\"rows\":10,\"ctrThreshold\":0.05,\"ctrField\":\"ctr_float\",\"fqsString\":\"\",\"maxNumberOfStorePerQuery\":3,\"solrSpellCorrection\":true,\"wilsonCtrThreshold\":0.1,\"numTokens\":5,\"impressionsThreshold\":100,\"stateHitsThreshold\":0.0}");
        assertEquals(fkConfigServiceWrapper.getString("solr.generative_impressions"), "{\"solrHost\":\"10.32.197.131\",\"solrPort\":25280,\"solrCore\":\"autosuggest_ab\",\"queryField\":\"textSpell\",\"prefixField\":\"prefixSpell_edgytext\",\"phraseField\":\"textSpell_edgytext_phrase\",\"phraseBoost\":1,\"boostFunction\":\"min(div(log(field(impressions_sint)),log(10)),10.0)^1\",\"sortFunctionString\":\"score desc,ranking-score_sfloat desc,p-hits_sfloat desc,ctr_sfloat desc\",\"rows\":10,\"ctrThreshold\":0.05,\"ctrField\":\"ctr_float\",\"fqsString\":\"\",\"maxNumberOfStorePerQuery\":3,\"solrSpellCorrection\":true,\"wilsonCtrThreshold\":0.1,\"numTokens\":5,\"impressionsThreshold\":100,\"stateHitsThreshold\":0.0}");
    }

    @Test
    public void testBoolean() {
        assertEquals(fkConfigServiceWrapper.getBoolean("sampleBoolean", true), Boolean.TRUE);
        assertTrue(fkConfigServiceWrapper.getBoolean("dummyTrue"));
        assertFalse(fkConfigServiceWrapper.getBoolean("dummyFalse"));
    }

    @Test
    public void testInt() {
        assertEquals(fkConfigServiceWrapper.getInt("sampleInt", 1), Integer.valueOf(1));
        assertEquals(fkConfigServiceWrapper.getInt("mysql.pool.acquireIncrement"), Integer.valueOf(1));
        assertEquals(fkConfigServiceWrapper.getInt("mysql.pool.initialPoolSize"), Integer.valueOf(1));
        assertEquals(fkConfigServiceWrapper.getInt("mysql.pool.maxIdleTimeSec"), Integer.valueOf(3600));
        assertEquals(fkConfigServiceWrapper.getInt("mysql.pool.maxPoolSize"), Integer.valueOf(5));
        assertEquals(fkConfigServiceWrapper.getInt("mysql.pool.minPoolSize"), Integer.valueOf(1));
        assertEquals(fkConfigServiceWrapper.getInt("mysql.port"), Integer.valueOf(3306));
        assertEquals(fkConfigServiceWrapper.getInt("webserver.idleTimeoutMs"), Integer.valueOf(60000));
        assertEquals(fkConfigServiceWrapper.getInt("webserver.maxQueuedRequests"), Integer.valueOf(5000));
        assertEquals(fkConfigServiceWrapper.getInt("webserver.maxThreads"), Integer.valueOf(1500));
        assertEquals(fkConfigServiceWrapper.getInt("webserver.minThreads"), Integer.valueOf(100));
    }

    @Test
    public void testDouble() {
        assertEquals(fkConfigServiceWrapper.getDouble("dummyDouble"), 2.71828, 0.1);
        assertEquals(fkConfigServiceWrapper.getDouble("dummyDouble", 2.71828), 2.71828, 0.1);
    }
}
