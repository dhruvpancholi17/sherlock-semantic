package com.flipkart.sherlock.semantic.autosuggest.flow;

import com.flipkart.sherlock.semantic.app.AppConstants;
import com.flipkart.sherlock.semantic.autosuggest.helpers.AutoSuggestQueryAnalyzer;
import com.flipkart.sherlock.semantic.autosuggest.helpers.MarketAnalyzer;
import com.flipkart.sherlock.semantic.autosuggest.models.Params;
import com.flipkart.sherlock.semantic.autosuggest.models.Params.ParamsBuilder;
import com.flipkart.sherlock.semantic.autosuggest.models.SolrConfig;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.request.V4FlashPrivateAutoSuggestRequest;
import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.flipkart.sherlock.semantic.commons.config.FkConfigServiceWrapper;
import com.flipkart.sherlock.semantic.v4.request.V4FlashAutoSuggestRequest;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * Created by dhruv.pancholi on 02/06/17.
 */
@Slf4j
public class V4ParamsHandler {

    @Inject
    private AutoSuggestQueryAnalyzer autoSuggestQueryAnalyzer;

    @Inject
    private MarketAnalyzer marketAnalyzer;

    @Inject
    private FkConfigServiceWrapper fkConfigServiceWrapper;

    @Inject
    private JsonSeDe jsonSeDe;

    public Params getParams(V4FlashPrivateAutoSuggestRequest v4FlashPrivateAutoSuggestRequest) {


        String bucket = v4FlashPrivateAutoSuggestRequest.getAsBucket();
        bucket = (bucket == null) ? "default" : bucket;

        String solrConfigString = fkConfigServiceWrapper.getString(AppConstants.SOLR_CONFIG_PREFIX + bucket);

        if (solrConfigString == null || solrConfigString.isEmpty()) {
            log.error("Property {} not found from the config service", AppConstants.SOLR_CONFIG_PREFIX + bucket);
        }

        SolrConfig solrConfig = jsonSeDe.readValue(solrConfigString, SolrConfig.class);

        ParamsBuilder paramsBuilder = Params.builder();

        String store = StoreHandler.removeAllStores(v4FlashPrivateAutoSuggestRequest.getContextStore());
        paramsBuilder.store(store);
        List<String> storeNodes = Arrays.asList(store.split("/"));
        paramsBuilder.storeNodes(storeNodes);
        paramsBuilder.leafNode(storeNodes.get(storeNodes.size() - 1));

        paramsBuilder.debug(v4FlashPrivateAutoSuggestRequest.isDebug());

        String query = v4FlashPrivateAutoSuggestRequest.getQuery();
        query = (query == null) ? "" : query;
        paramsBuilder.originalQuery(query);
        paramsBuilder.query(AutoSuggestQueryAnalyzer.getCleanQuery(query));

        List<String> completionTypes = v4FlashPrivateAutoSuggestRequest.getTypes();
        completionTypes = completionTypes == null ? Params.DEFAULT_V4_COMPLETION_TYPES : completionTypes;
        paramsBuilder.completionTypes(completionTypes);

        paramsBuilder.queryDisabled(false);
        paramsBuilder.productDisabled(true);

        updateMarketPlaceIds(paramsBuilder, store, solrConfig, v4FlashPrivateAutoSuggestRequest, marketAnalyzer);

        paramsBuilder.rows(v4FlashPrivateAutoSuggestRequest.getRows());

        paramsBuilder.solrHost(solrConfig.getSolrHost());
        paramsBuilder.solrPort(solrConfig.getSolrPort());
        paramsBuilder.solrCore(solrConfig.getSolrCore());

        paramsBuilder.queryField(solrConfig.getQueryField());
        paramsBuilder.prefixField(solrConfig.getPrefixField());
        paramsBuilder.phraseField(solrConfig.getPhraseField());
        paramsBuilder.phraseBoost(solrConfig.getPhraseBoost());
        paramsBuilder.boostFunction(solrConfig.getBoostFunction());
        paramsBuilder.sortFunctions(Arrays.asList(solrConfig.getSortFunctionString().split(",")));

        paramsBuilder.maxNumberOfStorePerQuery(solrConfig.getMaxNumberOfStorePerQuery());
        paramsBuilder.solrSpellCorrection(solrConfig.isSolrSpellCorrection());

        paramsBuilder.ctrField(solrConfig.getCtrField());
        paramsBuilder.ctrThreshold(solrConfig.getCtrThreshold());
        paramsBuilder.wilsonCtrThreshold(solrConfig.getWilsonCtrThreshold());
        paramsBuilder.impressionsThreshold(solrConfig.getImpressionsThreshold());
        paramsBuilder.stateHitsThreshold(solrConfig.getStateHitsThreshold());
        paramsBuilder.numTokens(solrConfig.getNumTokens());
        paramsBuilder.pristinePrefixField(solrConfig.getPrefixField());
        paramsBuilder.minCharsForIncorrectPrefix(solrConfig.getMinCharsForIncorrectPrefix());

        return paramsBuilder.build();
    }

    private static void updateMarketPlaceIds(ParamsBuilder paramsBuilder, String store, SolrConfig solrConfig, V4FlashPrivateAutoSuggestRequest v4FlashPrivateAutoSuggestRequest, MarketAnalyzer marketAnalyzer) {
        List<String> marketPlaceIds = marketAnalyzer.getMarketPlaceIds(store,
                String.valueOf(v4FlashPrivateAutoSuggestRequest.isGroceryContext()),
                v4FlashPrivateAutoSuggestRequest.getMarketPlaceId().name());
        paramsBuilder.marketPlaceIds(marketPlaceIds);
    }
}
