package com.flipkart.sherlock.semantic.autosuggest.flow;

import com.flipkart.sherlock.semantic.app.AppConstants;
import com.flipkart.sherlock.semantic.autosuggest.helpers.AutoSuggestQueryAnalyzer;
import com.flipkart.sherlock.semantic.autosuggest.helpers.MarketAnalyzer;
import com.flipkart.sherlock.semantic.autosuggest.models.Params;
import com.flipkart.sherlock.semantic.autosuggest.models.SolrConfig;
import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.flipkart.sherlock.semantic.common.util.FkConfigServiceWrapper;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.UriInfo;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dhruv.pancholi on 02/06/17.
 */
@Slf4j
public class ParamsHandler {

    @Inject
    private AutoSuggestQueryAnalyzer autoSuggestQueryAnalyzer;

    @Inject
    private MarketAnalyzer marketAnalyzer;

    @Inject
    private FkConfigServiceWrapper fkConfigServiceWrapper;

    @Inject
    private JsonSeDe jsonSeDe;

    public Params getParams(String store, UriInfo uriInfo) {

        String bucket = uriInfo.getQueryParameters().getFirst("bucket");
        bucket = (bucket == null) ? "default" : bucket;

        String solrConfigString = fkConfigServiceWrapper.getString(AppConstants.SOLR_CONFIG_PREFIX + bucket);

        if (solrConfigString == null || solrConfigString.isEmpty()) {
            log.error("Property {} not found from the config service", AppConstants.SOLR_CONFIG_PREFIX + bucket);
        }

        SolrConfig solrConfig = jsonSeDe.readValue(solrConfigString, SolrConfig.class);

        Params params = new Params();

        store = StoreHandler.removeAllStores(store);
        params.setStore(store);
        params.setStoreNodes(Arrays.asList(store.split("/")));
        params.setLeafNode(params.getStoreNodes().get(params.getStoreNodes().size() - 1));

        updateDebug(params, solrConfig, uriInfo);

        String query = uriInfo.getQueryParameters().getFirst("q");
        query = (query == null) ? "" : query;
        params.setOriginalQuery(query);
        params.setQuery(AutoSuggestQueryAnalyzer.getCleanQuery(query));

        updateTypes(params, uriInfo);
        updateMarketPlaceIds(params, store, solrConfig, uriInfo, marketAnalyzer);

        params.setQueryDisabled(!params.getCompletionTypes().contains("query")
                || autoSuggestQueryAnalyzer.isDisabled(query));
        params.setProductDisabled(!params.getCompletionTypes().contains("product"));


        String abParamsString = uriInfo.getQueryParameters().getFirst("ab-params");
        List<String> abParams = (abParamsString != null && !abParamsString.isEmpty()) ?
                Arrays.asList(abParamsString.split(",")) : null;

        updateRows(params, solrConfig, uriInfo);

        params.setSolrHost(solrConfig.getSolrHost());
        params.setSolrPort(solrConfig.getSolrPort());
        params.setSolrCore(solrConfig.getSolrCore());

        updateQueryField(params, solrConfig, uriInfo);
        updatePrefixField(params, solrConfig, uriInfo);
        updatePhraseField(params, solrConfig, uriInfo);
        updatePhraseBoost(params, solrConfig, uriInfo);
        updateBoostFunction(params, solrConfig, uriInfo);
        updateSortFunction(params, solrConfig, uriInfo);

        updateMaxNumberOfStorePerQuery(params, solrConfig, uriInfo);
        updateSolrSpellCorrection(params, solrConfig, uriInfo);

        updateCtrField(params, solrConfig, uriInfo);
        updateCtrThreshold(params, solrConfig, uriInfo);
        updateWilsonCtrThreshold(params, solrConfig, uriInfo);
        updateImpressionsThreshold(params, solrConfig, uriInfo);
        updateStateHitsThreshold(params, solrConfig, uriInfo);
        updateNumTokens(params, solrConfig, uriInfo);

        return params;
    }

    private void updatePhraseField(Params params, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("phraseField");
        if (s == null || s.isEmpty()) {
            params.setPhraseField(solrConfig.getPhraseField());
        } else {
            params.setPhraseField(s);
        }
    }

    private void updateCtrField(Params params, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("ctrField");
        if (s == null || s.isEmpty()) {
            params.setCtrField(solrConfig.getCtrField());
        } else {
            params.setCtrField(s);
        }
    }

    private void updateRows(Params params, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("rows");
        if (s == null || s.isEmpty()) {
            params.setRows(solrConfig.getRows());
        } else {
            params.setRows(Integer.parseInt(s));
        }
    }

    private void updateSortFunction(Params params, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("sortFunction");
        if (s == null || s.isEmpty()) {
            params.setSortFunctions(Arrays.asList(solrConfig.getSortFunctionString().split(",")));
        } else {
            params.setSortFunctions(Arrays.asList(s.split(",")));
        }
    }

    private void updateBoostFunction(Params params, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("boostFunction");
        if (s == null || s.isEmpty()) {
            params.setBoostFunction(solrConfig.getBoostFunction());
        } else {
            params.setBoostFunction(s);
        }
    }

    private void updatePhraseBoost(Params params, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("phraseBoost");
        if (s == null || s.isEmpty()) {
            params.setPhraseBoost(solrConfig.getPhraseBoost());
        } else {
            params.setPhraseBoost(Integer.parseInt(s));
        }
    }

    private void updatePrefixField(Params params, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("prefixField");
        if (s == null || s.isEmpty()) {
            params.setPrefixField(solrConfig.getPrefixField());
        } else {
            params.setPrefixField(s);
        }
    }

    private void updateQueryField(Params params, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("queryField");
        if (s == null || s.isEmpty()) {
            params.setQueryField(solrConfig.getQueryField());
        } else {
            params.setQueryField(s);
        }
    }

    private void updateMaxNumberOfStorePerQuery(Params params, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("maxNumberOfStorePerQuery");
        if (s == null || s.isEmpty()) {
            params.setMaxNumberOfStorePerQuery(solrConfig.getMaxNumberOfStorePerQuery());
        } else {
            params.setMaxNumberOfStorePerQuery(Integer.parseInt(s));
        }
    }

    private void updateSolrSpellCorrection(Params params, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("solrSpellCorrection");
        if (s == null || s.isEmpty()) {
            params.setSolrSpellCorrection(solrConfig.isSolrSpellCorrection());
        } else {
            params.setSolrSpellCorrection(Boolean.parseBoolean(s));
        }
    }

    private void updateTypes(Params params, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("types");
        if (s == null || s.isEmpty()) {
            params.setCompletionTypes(Params.DEFAULT_COMPLETION_TYPES);
        } else {
            params.setCompletionTypes(Arrays.asList(s.split(",")));
        }

    }

    private static void updateMarketPlaceIds(Params params, String store, SolrConfig solrConfig, UriInfo uriInfo, MarketAnalyzer marketAnalyzer) {
        List<String> marketPlaceIds = marketAnalyzer.getMarketPlaceIds(store,
                uriInfo.getQueryParameters().getFirst("groceryContext"),
                uriInfo.getQueryParameters().getFirst("marketplaceId"));
        params.setMarketPlaceIds(marketPlaceIds);
    }

    private void updateDebug(Params params, SolrConfig solrConfig, UriInfo uriInfo) {
        params.setDebug(uriInfo.getQueryParameters().getFirst("debug") != null);
    }

    private void updateNumTokens(Params params, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("numTokens");
        if (s == null || s.isEmpty()) {
            params.setNumTokens(solrConfig.getNumTokens());
        } else {
            params.setNumTokens(Integer.parseInt(s));
        }
    }

    private void updateImpressionsThreshold(Params params, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("impressionsThreshold");
        if (s == null || s.isEmpty()) {
            params.setImpressionsThreshold(solrConfig.getImpressionsThreshold());
        } else {
            params.setImpressionsThreshold(Integer.parseInt(s));
        }
    }

    private void updateStateHitsThreshold(Params params, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("stateHitsThreshold");
        if (s == null || s.isEmpty()) {
            params.setStateHitsThreshold(solrConfig.getStateHitsThreshold());
        } else {
            params.setStateHitsThreshold(Double.parseDouble(s));
        }
    }

    private void updateWilsonCtrThreshold(Params params, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("wilsonCtrThreshold");
        if (s == null || s.isEmpty()) {
            params.setWilsonCtrThreshold(solrConfig.getWilsonCtrThreshold());
        } else {
            params.setWilsonCtrThreshold(Double.parseDouble(s));
        }
    }

    private void updateCtrThreshold(Params params, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("ctrThreshold");
        if (s == null || !s.isEmpty()) {
            params.setCtrThreshold(solrConfig.getCtrThreshold());
        } else {
            params.setCtrThreshold(Double.parseDouble(s));
        }
    }
}
