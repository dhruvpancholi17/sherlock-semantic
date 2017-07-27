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
import java.util.ArrayList;
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
        Params params = new Params();

        store = StoreHandler.removeAllStores(store);
        params.setStore(store);
        params.setStoreNodes(Arrays.asList(store.split("/")));
        params.setLeafNode(params.getStoreNodes().get(params.getStoreNodes().size() - 1));

        params.setDebug(uriInfo.getQueryParameters().getFirst("debug") != null);

        String query = uriInfo.getQueryParameters().getFirst("q");
        query = (query == null) ? "" : query;
        params.setOriginalQuery(query);
        params.setQuery(AutoSuggestQueryAnalyzer.getCleanQuery(query));

        String types = uriInfo.getQueryParameters().getFirst("types");
        if (types != null) params.setCompletionTypes(Arrays.asList(types.split(",")));

        params.setMarketPlaceIds(marketAnalyzer.getMarketPlaceIds(store, uriInfo.getQueryParameters().getFirst("groceryContext"), uriInfo.getQueryParameters().getFirst("marketplaceId")));
        params.setQueryDisabled(!params.getCompletionTypes().contains("query") || autoSuggestQueryAnalyzer.isDisabled(query));
        params.setProductDisabled(!params.getCompletionTypes().contains("product") || MarketAnalyzer.removeProducts(query, params.getMarketPlaceIds()));


        String abParamsString = uriInfo.getQueryParameters().getFirst("ab-params");
        List<String> abParams = (abParamsString != null && !abParamsString.isEmpty()) ? Arrays.asList(abParamsString.split(",")) : null;

        String bucket = uriInfo.getQueryParameters().getFirst("bucket");
        bucket = (bucket == null) ? "default" : bucket;

        String solrConfigString = fkConfigServiceWrapper.getString(AppConstants.SOLR_CONFIG_PREFIX + bucket);

        if (solrConfigString == null || solrConfigString.isEmpty()) {
            log.error("Property {} not found from the config service", AppConstants.SOLR_CONFIG_PREFIX + bucket);
        }

        SolrConfig solrConfig = jsonSeDe.readValue(solrConfigString, SolrConfig.class);


//        String solrHost = uriInfo.getQueryParameters().getFirst("solrHost");
//        params.setSolrHost(solrHost == null ? solrConfig.getSolrHost() : solrHost);
        params.setSolrHost(solrConfig.getSolrHost());

//        String solrPort = uriInfo.getQueryParameters().getFirst("solrPort");
//        params.setSolrPort(solrPort == null ? solrConfig.getSolrPort() : Integer.parseInt(solrPort));
        params.setSolrPort(solrConfig.getSolrPort());

//        String solrCore = uriInfo.getQueryParameters().getFirst("solrCore");
//        params.setSolrCore(solrCore == null ? solrConfig.getSolrCore() : solrCore);
        params.setSolrCore(solrConfig.getSolrCore());

        String queryField = uriInfo.getQueryParameters().getFirst("queryField");
        params.setQueryField(queryField == null ? solrConfig.getQueryField() : queryField);

        String prefixEdgyField = uriInfo.getQueryParameters().getFirst("prefixField");
        params.setPrefixField(prefixEdgyField == null ? solrConfig.getPrefixField() : prefixEdgyField);

        String phraseField = uriInfo.getQueryParameters().getFirst("phraseField");
        params.setPhraseField(phraseField == null ? solrConfig.getPhraseField() : phraseField);

        String phraseBoost = uriInfo.getQueryParameters().getFirst("phraseBoost");
        params.setPhraseBoost(phraseBoost == null ? solrConfig.getPhraseBoost() : Integer.parseInt(phraseBoost));

        String boostFunction = uriInfo.getQueryParameters().getFirst("boostFunction");
        params.setBoostFunction(boostFunction == null ? solrConfig.getBoostFunction() : boostFunction);

        String sortFunctionString = uriInfo.getQueryParameters().getFirst("sortFunction");
        params.setSortFunctions(sortFunctionString == null ? Arrays.asList(solrConfig.getSortFunctionString().split(",")) : Arrays.asList(sortFunctionString.split(",")));

        String rows = uriInfo.getQueryParameters().getFirst("rows");
        params.setRows(rows == null ? solrConfig.getRows() : Integer.parseInt(rows));

        String ctrThreshold = uriInfo.getQueryParameters().getFirst("ctrThreshold");
        params.setCtrThreshold(ctrThreshold == null ? solrConfig.getCtrThreshold() : Double.parseDouble(ctrThreshold));

        String ctrField = uriInfo.getQueryParameters().getFirst("ctrField");
        params.setCtrField(ctrField == null ? solrConfig.getCtrField() : ctrField);

        String fqs = uriInfo.getQueryParameters().getFirst("fqs");
        params.setFqs(fqs == null ? (solrConfig.getFqsString() == null || solrConfig.getFqsString().isEmpty() ? new ArrayList<>() : Arrays.asList(solrConfig.getFqsString().split(","))) : Arrays.asList(fqs.split(",")));

        String maxNumberOfStorePerQuery = uriInfo.getQueryParameters().getFirst("maxNumberOfStorePerQuery");
        params.setMaxNumberOfStorePerQuery(maxNumberOfStorePerQuery == null ? solrConfig.getMaxNumberOfStorePerQuery() : Integer.parseInt(maxNumberOfStorePerQuery));

        String solrSpellCorrection = uriInfo.getQueryParameters().getFirst("solrSpellCorrection");
        params.setSolrSpellCorrection(solrSpellCorrection == null ? solrConfig.isSolrSpellCorrection() : Boolean.parseBoolean(solrSpellCorrection));

        return params;
    }
}
