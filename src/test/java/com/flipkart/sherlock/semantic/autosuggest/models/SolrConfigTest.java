package com.flipkart.sherlock.semantic.autosuggest.models;

import com.flipkart.sherlock.semantic.autosuggest.flow.ParamsHandler;
import com.flipkart.sherlock.semantic.autosuggest.helpers.AutoSuggestQueryAnalyzer;
import com.flipkart.sherlock.semantic.autosuggest.helpers.MarketAnalyzer;
import com.flipkart.sherlock.semantic.autosuggest.utils.IOUtils;
import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.flipkart.sherlock.semantic.common.util.FkConfigServiceWrapper;
import com.flipkart.sherlock.semantic.test.utils.ObjectUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.UriInfo;

/**
 * Created by dhruv.pancholi on 04/08/17.
 */
public class SolrConfigTest {

    private static final String ALL_SEARCH_STORE = "search.flipkart.com";

    @Mock
    private FkConfigServiceWrapper fkConfigServiceWrapper;

    @Mock
    private AutoSuggestQueryAnalyzer autoSuggestQueryAnalyzer;

    @Mock
    private UriInfo uriInfo;

    @Spy
    private JsonSeDe jsonSeDe = JsonSeDe.getInstance();

    @Spy
    private MarketAnalyzer marketAnalyzer = new MarketAnalyzer();

    @InjectMocks
    private ParamsHandler paramsHandler;

    private Params params;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        Mockito.when(fkConfigServiceWrapper.getString("solr.default")).thenReturn(IOUtils.open("src/test/resources/solrconfig.json").readAll());
        Mockito.when(autoSuggestQueryAnalyzer.isDisabled(Mockito.anyString())).thenReturn(false);

        MultivaluedHashMap<String, String> uriParams = new MultivaluedHashMap<>();
        uriParams.add("q", "Dhruv Pancholi");
        Mockito.when(uriInfo.getQueryParameters()).thenReturn(uriParams);

        params = paramsHandler.getParams(ALL_SEARCH_STORE, uriInfo);
    }

    @Test
    public void testFields() {
        SolrConfig solrConfig = new SolrConfig();
        Assert.assertFalse(ObjectUtils.containsSetters(solrConfig));
        Assert.assertTrue(ObjectUtils.areAllFieldsPrivate(solrConfig));
    }

    @Test
    public void testQuery() throws Exception {
        Assert.assertEquals(params.getQuery(), "dhruv pancholi");
        Assert.assertEquals(params.getOriginalQuery(), "Dhruv Pancholi");
    }

    @Test
    public void testMarketPlaceIds() {
        Assert.assertEquals(params.getMarketPlaceIds(), MarketAnalyzer.DEFAULT_MARKET_PLACE_IDS);
    }

    @Test
    public void testTypes() {
        Assert.assertEquals(params.getCompletionTypes(), Params.DEFAULT_COMPLETION_TYPES);
    }

    @Test
    public void testThresholds() {
        Assert.assertEquals(params.getCtrThreshold(), 0.05, 0.0);
        Assert.assertEquals(params.getWilsonCtrThreshold(), 1.0, 0.0);
        Assert.assertEquals(params.getNumTokens(), 5);
        Assert.assertEquals(params.getImpressionsThreshold(), 100);
        Assert.assertEquals(params.getStateHitsThreshold(), 1.0, 0.0);
        Assert.assertEquals(params.getMaxNumberOfStorePerQuery(), 3);
    }

    @Test
    public void testSolrBox() {
        Assert.assertEquals(params.getSolrHost(), "10.47.2.32");
        Assert.assertEquals(params.getSolrPort(), 25280);
        Assert.assertEquals(params.getSolrCore(), "autosuggest");
    }

    @Test
    public void testSolrFields() {
        Assert.assertEquals(params.getCtrField(), "ctr_float");
        Assert.assertEquals(params.getQueryField(), "text");
        Assert.assertEquals(params.getPrefixField(), "prefix_edgytext");
        Assert.assertEquals(params.getPhraseField(), "text_edgytext_phrase");
        Assert.assertEquals(params.getBoostFunction(), "min(div(log(field(impressions_sint)),log(10)),10.0)^1");
        Assert.assertEquals(params.getPhraseBoost(), 1);
    }

    @Test
    public void testEnableDisable() {
        Assert.assertEquals(params.isDebug(), false);
        Assert.assertEquals(params.isQueryDisabled(), false);
        Assert.assertEquals(params.isProductDisabled(), false);
        Assert.assertEquals(params.isSolrSpellCorrection(), true);
    }
}
