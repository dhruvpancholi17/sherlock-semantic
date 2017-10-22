package com.flipkart.sherlock.semantic.autosuggest.outer;

import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.flipkart.sherlock.semantic.autosuggest.views.AutoSuggestView;
import com.flipkart.sherlock.semantic.autosuggest.views.HealthCheckView;
import com.flipkart.sherlock.semantic.common.dao.mysql.CompleteTableDao;
import com.flipkart.sherlock.semantic.common.util.FkConfigServiceWrapper;
import com.flipkart.sherlock.semantic.core.search.SolrSearchServer;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;

import static com.flipkart.sherlock.semantic.app.AutoSuggestApp.getTracedItems;
import static com.flipkart.sherlock.semantic.common.metrics.MetricsManager.init;
import static org.junit.Assert.assertNotNull;

/**
 * Created by dhruv.pancholi on 18/10/17.
 */
public class MockAutoSuggestAppInjector {


    private Injector autoSuggestAppInjector;

    @Before
    public void setUp() throws Exception {
        autoSuggestAppInjector = getAutoSuggestAppInjector();
    }

    @Test
    public void testNotNull() throws Exception {
        assertNotNull(autoSuggestAppInjector);
        assertNotNull(autoSuggestAppInjector.getInstance(AutoSuggestView.class));
        assertNotNull(autoSuggestAppInjector.getInstance(HealthCheckView.class));
        assertNotNull(autoSuggestAppInjector.getInstance(SolrSearchServer.class));
    }

    public Injector getAutoSuggestAppInjector() throws Exception {
        init(getTracedItems());
        CompleteTableDao completeTableDao = new MockCompleteTableDao().getCompleteTableDao();
        FkConfigServiceWrapper fkConfigServiceWrapper = new MockFkConfigServiceWrapper().getFkConfigServiceWrapper();
        SolrSearchServer solrSearchServer = new MockSolrSearchServer().getSolrSearchServer();
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(JsonSeDe.class).toInstance(JsonSeDe.getInstance());
                bind(FkConfigServiceWrapper.class).toInstance(fkConfigServiceWrapper);
                bind(CompleteTableDao.class).toInstance(completeTableDao);
                bind(SolrSearchServer.class).toInstance(solrSearchServer);
            }
        });
    }
}
