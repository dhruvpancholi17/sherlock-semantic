package com.flipkart.sherlock.semantic.autosuggest.flow;

import com.fasterxml.jackson.core.type.TypeReference;
import com.flipkart.sherlock.semantic.autosuggest.models.*;
import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dhruv.pancholi on 07/12/17.
 */
@Singleton
public class L2Handler {

    @Inject
    private JsonSeDe jsonSeDe;

    @Inject
    private StoreHandler storeHandler;

    public AutoSuggestResponse getReRankedResponse(Params params, AutoSuggestResponse autoSuggestResponse, UserInsightResponse userInsightResponse) {

        double alpha = 0.3;
        double beta = 1 - alpha;

        List<AutoSuggestDoc> autoSuggestDocs = autoSuggestResponse.getAutoSuggestDocs();
        if (userInsightResponse == null || userInsightResponse.getStoreWeight() == null) return autoSuggestResponse;

        Map<String, Integer> storeWeight = userInsightResponse.getStoreWeight();

        if(params.getUie() != null && !params.getUie().isEmpty()) {
            storeWeight = JsonSeDe.getInstance().readValue(params.getUie(), new TypeReference<Map<String, Integer>>() {});
        }

        if(params.getAlpha() != 0.0) {
            alpha = params.getAlpha();
            beta = 1 - alpha;
        }

        List<WeightedAutoSuggestDoc> weightedAutoSuggestDocs = new ArrayList<>();

        for (AutoSuggestDoc autoSuggestDoc : autoSuggestDocs) {

            float solrScore = autoSuggestDoc.getSolrScore();
            double solrWeight = (alpha * solrScore);

            Map<String, Double> storeClassifier = autoSuggestDoc.getStoreClassifier();

            double uieWeight = 0.0;
            for (Map.Entry<String, Integer> storeFrequency : storeWeight.entrySet()) {
                String store = storeFrequency.getKey();
                Integer frequency = storeFrequency.getValue();
                if (storeClassifier.containsKey(store)) {
                    uieWeight += (beta * frequency * storeClassifier.get(store));
                }
            }
            weightedAutoSuggestDocs.add(new WeightedAutoSuggestDoc(autoSuggestDoc, solrWeight, uieWeight, uieWeight + solrWeight));
        }

        weightedAutoSuggestDocs.sort((o1, o2) -> Double.compare(o2.getCombinedWeight(), o1.getCombinedWeight()));

        List<QuerySuggestion> querySuggestions = new ArrayList<>();

        int numberOfDocsWithStores = storeHandler.getNumberOfDocsWithStores(autoSuggestDocs);
        int processCount = 0;

        for (WeightedAutoSuggestDoc weightedAutoSuggestDoc : weightedAutoSuggestDocs) {
            List<Store> stores = (processCount < numberOfDocsWithStores) ? storeHandler.getStoresFromProductStore(
                    weightedAutoSuggestDoc.getAutoSuggestDoc().getProductStores(),
                    params.getMaxNumberOfStorePerQuery(),
                    params.getStore(),
                    params.getMarketPlaceIds()) : new ArrayList<>();
            querySuggestions.add(new QuerySuggestion(
                    weightedAutoSuggestDoc.getAutoSuggestDoc().getCorrectedQuery(),
                    stores,
                    params.isDebug() ? weightedAutoSuggestDoc.getSolrWeight() : null,
                    params.isDebug() ? weightedAutoSuggestDoc.getUieWeight() : null,
                    params.isDebug() ? weightedAutoSuggestDoc.getCombinedWeight() : null));
            processCount++;
        }

        return new AutoSuggestResponse(
                autoSuggestResponse.getPayloadId(),
                querySuggestions,
                autoSuggestResponse.getProductSuggestions(),
                autoSuggestResponse.getParams(),
                autoSuggestResponse.getQuerySolrQuery(),
                autoSuggestResponse.getProductSolrQuery(),
                params.isDebug() ? autoSuggestResponse.getAutoSuggestDocs() : null);
    }
}
