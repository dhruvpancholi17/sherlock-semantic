package com.flipkart.sherlock.semantic.autosuggest.flow;

import com.flipkart.sherlock.semantic.app.AppConstants;
import com.flipkart.sherlock.semantic.autosuggest.helpers.AutoSuggestQueryAnalyzer;
import com.flipkart.sherlock.semantic.autosuggest.helpers.MarketAnalyzer;
import com.flipkart.sherlock.semantic.autosuggest.models.Params;
import com.flipkart.sherlock.semantic.autosuggest.models.Params.ParamsBuilder;
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

        String bucket = uriInfo.getQueryParameters().getFirst("as-bucket");
        bucket = (bucket == null) ? "default" : bucket;

        String solrConfigString = fkConfigServiceWrapper.getString(AppConstants.SOLR_CONFIG_PREFIX + bucket);

        if (solrConfigString == null || solrConfigString.isEmpty()) {
            log.error("Property {} not found from the config service", AppConstants.SOLR_CONFIG_PREFIX + bucket);
        }

        SolrConfig solrConfig = jsonSeDe.readValue(solrConfigString, SolrConfig.class);

        ParamsBuilder paramsBuilder = Params.builder();

        store = StoreHandler.removeAllStores(store);
        paramsBuilder.store(store);
        List<String> storeNodes = Arrays.asList(store.split("/"));
        paramsBuilder.storeNodes(storeNodes);
        paramsBuilder.leafNode(storeNodes.get(storeNodes.size() - 1));

        String uieString = uriInfo.getQueryParameters().getFirst("uie");
        if (uieString != null && !uieString.isEmpty()) {
            paramsBuilder.uie(uieString);
        } else {
            paramsBuilder.uie("");
        }

        String alphaString = uriInfo.getQueryParameters().getFirst("alpha");
        if (alphaString != null && !alphaString.isEmpty()) {
            paramsBuilder.alpha(Double.valueOf(alphaString));
        } else {
            paramsBuilder.alpha(0.0);
        }

        String isL2EnableString = uriInfo.getQueryParameters().getFirst("isL2Enable");
        if (isL2EnableString != null && !isL2EnableString.isEmpty()) {
            paramsBuilder.isL2Enable(Boolean.valueOf(isL2EnableString));
        } else {
            paramsBuilder.isL2Enable(true);
        }


        String diversifyString = uriInfo.getQueryParameters().getFirst("diversify");
        if (diversifyString != null && !diversifyString.isEmpty()) {
            paramsBuilder.diversify(Boolean.valueOf(diversifyString));
        } else {
            paramsBuilder.diversify(true);
        }

        updateDebug(paramsBuilder, solrConfig, uriInfo);

        String query = uriInfo.getQueryParameters().getFirst("q");
        query = (query == null) ? "" : query;
        paramsBuilder.originalQuery(query);
        paramsBuilder.query(AutoSuggestQueryAnalyzer.getCleanQuery(query));

        List<String> completionTypes = getCompletionTypes(uriInfo);
        paramsBuilder.completionTypes(completionTypes);

        paramsBuilder.queryDisabled(!completionTypes.contains("query")
                || autoSuggestQueryAnalyzer.isDisabled(query));
        paramsBuilder.productDisabled(!completionTypes.contains("product"));

        updateMarketPlaceIds(paramsBuilder, store, solrConfig, uriInfo, marketAnalyzer);

        updateRows(paramsBuilder, solrConfig, uriInfo);

        paramsBuilder.solrHost(solrConfig.getSolrHost());
        paramsBuilder.solrPort(solrConfig.getSolrPort());
        paramsBuilder.solrCore(solrConfig.getSolrCore());

        updateQueryField(paramsBuilder, solrConfig, uriInfo);
        updatePrefixField(paramsBuilder, solrConfig, uriInfo);
        updatePhraseField(paramsBuilder, solrConfig, uriInfo);
        updatePhraseBoost(paramsBuilder, solrConfig, uriInfo);
        updateBoostFunction(paramsBuilder, solrConfig, uriInfo);
        updateSortFunction(paramsBuilder, solrConfig, uriInfo);

        updateMaxNumberOfStorePerQuery(paramsBuilder, solrConfig, uriInfo);
        updateSolrSpellCorrection(paramsBuilder, solrConfig, uriInfo);

        updateCtrField(paramsBuilder, solrConfig, uriInfo);
        updateCtrThreshold(paramsBuilder, solrConfig, uriInfo);
        updateWilsonCtrThreshold(paramsBuilder, solrConfig, uriInfo);
        updateImpressionsThreshold(paramsBuilder, solrConfig, uriInfo);
        updateStateHitsThreshold(paramsBuilder, solrConfig, uriInfo);
        updateNumTokens(paramsBuilder, solrConfig, uriInfo);
        updatePrefixCorrectionFields(paramsBuilder, solrConfig, uriInfo);

        return paramsBuilder.build();
    }

    private void updatePrefixCorrectionFields(ParamsBuilder paramsBuilder, SolrConfig solrConfig, UriInfo uriInfo) {
        String pristinePrefixField = uriInfo.getQueryParameters().getFirst("pristinePrefixField");
        if (pristinePrefixField == null || pristinePrefixField.isEmpty()) {
            paramsBuilder.pristinePrefixField(solrConfig.getPristinePrefixField());
        } else {
            paramsBuilder.pristinePrefixField(pristinePrefixField);
        }

        String minCharsForIncorrectPrefix = uriInfo.getQueryParameters().getFirst("minCharsForIncorrectPrefix");
        if (minCharsForIncorrectPrefix == null || minCharsForIncorrectPrefix.isEmpty()) {
            paramsBuilder.minCharsForIncorrectPrefix(solrConfig.getMinCharsForIncorrectPrefix());
        } else {
            paramsBuilder.minCharsForIncorrectPrefix(Integer.parseInt(minCharsForIncorrectPrefix));
        }
    }

    private void updatePhraseField(ParamsBuilder paramsBuilder, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("phraseField");
        if (s == null || s.isEmpty()) {
            paramsBuilder.phraseField(solrConfig.getPhraseField());
        } else {
            paramsBuilder.phraseField(s);
        }
    }

    private void updateCtrField(ParamsBuilder paramsBuilder, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("ctrField");
        if (s == null || s.isEmpty()) {
            paramsBuilder.ctrField(solrConfig.getCtrField());
        } else {
            paramsBuilder.ctrField(s);
        }
    }

    private void updateRows(ParamsBuilder paramsBuilder, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("rows");
        if (s == null || s.isEmpty()) {
            paramsBuilder.rows(solrConfig.getRows());
        } else {
            paramsBuilder.rows(Integer.parseInt(s));
        }
    }

    private void updateSortFunction(ParamsBuilder paramsBuilder, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("sortFunction");
        if (s == null || s.isEmpty()) {
            paramsBuilder.sortFunctions(Arrays.asList(solrConfig.getSortFunctionString().split(",")));
        } else {
            paramsBuilder.sortFunctions(Arrays.asList(s.split(",")));
        }
    }

    private void updateBoostFunction(ParamsBuilder paramsBuilder, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("boostFunction");
        if (s == null || s.isEmpty()) {
            paramsBuilder.boostFunction(solrConfig.getBoostFunction());
        } else {
            paramsBuilder.boostFunction(s);
        }
    }

    private void updatePhraseBoost(ParamsBuilder paramsBuilder, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("phraseBoost");
        if (s == null || s.isEmpty()) {
            paramsBuilder.phraseBoost(solrConfig.getPhraseBoost());
        } else {
            paramsBuilder.phraseBoost(Integer.parseInt(s));
        }
    }

    private void updatePrefixField(ParamsBuilder paramsBuilder, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("prefixField");
        if (s == null || s.isEmpty()) {
            paramsBuilder.prefixField(solrConfig.getPrefixField());
        } else {
            paramsBuilder.prefixField(s);
        }
    }

    private void updateQueryField(ParamsBuilder paramsBuilder, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("queryField");
        if (s == null || s.isEmpty()) {
            paramsBuilder.queryField(solrConfig.getQueryField());
        } else {
            paramsBuilder.queryField(s);
        }
    }

    private void updateMaxNumberOfStorePerQuery(ParamsBuilder paramsBuilder, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("maxNumberOfStorePerQuery");
        if (s == null || s.isEmpty()) {
            paramsBuilder.maxNumberOfStorePerQuery(solrConfig.getMaxNumberOfStorePerQuery());
        } else {
            paramsBuilder.maxNumberOfStorePerQuery(Integer.parseInt(s));
        }
    }

    private void updateSolrSpellCorrection(ParamsBuilder paramsBuilder, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("solrSpellCorrection");
        if (s == null || s.isEmpty()) {
            paramsBuilder.solrSpellCorrection(solrConfig.isSolrSpellCorrection());
        } else {
            paramsBuilder.solrSpellCorrection(Boolean.parseBoolean(s));
        }
    }

    private List<String> getCompletionTypes(UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("types");
        if (s == null || s.isEmpty()) {
            return Params.DEFAULT_COMPLETION_TYPES;
        } else {
            return Arrays.asList(s.split(","));
        }
    }

    private static void updateMarketPlaceIds(ParamsBuilder paramsBuilder, String store, SolrConfig solrConfig, UriInfo uriInfo, MarketAnalyzer marketAnalyzer) {
        List<String> marketPlaceIds = marketAnalyzer.getMarketPlaceIds(store,
                uriInfo.getQueryParameters().getFirst("groceryContext"),
                uriInfo.getQueryParameters().getFirst("marketplaceId"));
        paramsBuilder.marketPlaceIds(marketPlaceIds);
    }

    private void updateDebug(ParamsBuilder paramsBuilder, SolrConfig solrConfig, UriInfo uriInfo) {
        paramsBuilder.debug(uriInfo.getQueryParameters().getFirst("debug") != null);
    }

    private void updateNumTokens(ParamsBuilder paramsBuilder, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("numTokens");
        if (s == null || s.isEmpty()) {
            paramsBuilder.numTokens(solrConfig.getNumTokens());
        } else {
            paramsBuilder.numTokens(Integer.parseInt(s));
        }
    }

    private void updateImpressionsThreshold(ParamsBuilder paramsBuilder, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("impressionsThreshold");
        if (s == null || s.isEmpty()) {
            paramsBuilder.impressionsThreshold(solrConfig.getImpressionsThreshold());
        } else {
            paramsBuilder.impressionsThreshold(Integer.parseInt(s));
        }
    }

    private void updateStateHitsThreshold(ParamsBuilder paramsBuilder, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("stateHitsThreshold");
        if (s == null || s.isEmpty()) {
            paramsBuilder.stateHitsThreshold(solrConfig.getStateHitsThreshold());
        } else {
            paramsBuilder.stateHitsThreshold(Double.parseDouble(s));
        }
    }

    private void updateWilsonCtrThreshold(ParamsBuilder paramsBuilder, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("wilsonCtrThreshold");
        if (s == null || s.isEmpty()) {
            paramsBuilder.wilsonCtrThreshold(solrConfig.getWilsonCtrThreshold());
        } else {
            paramsBuilder.wilsonCtrThreshold(Double.parseDouble(s));
        }
    }

    private void updateCtrThreshold(ParamsBuilder paramsBuilder, SolrConfig solrConfig, UriInfo uriInfo) {
        String s = uriInfo.getQueryParameters().getFirst("ctrThreshold");
        if (s == null || s.isEmpty()) {
            paramsBuilder.ctrThreshold(solrConfig.getCtrThreshold());
        } else {
            paramsBuilder.ctrThreshold(Double.parseDouble(s));
        }
    }
}
