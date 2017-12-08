package com.flipkart.sherlock.semantic.autosuggest.flow;

import com.fasterxml.jackson.core.type.TypeReference;
import com.flipkart.sherlock.semantic.autosuggest.models.*;
import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.*;

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

        if (params.getUie() != null && !params.getUie().isEmpty()) {
            storeWeight = JsonSeDe.getInstance().readValue(params.getUie(), new TypeReference<Map<String, Integer>>() {
            });
        }

        if (params.getAlpha() != 0.0) {
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
            weightedAutoSuggestDocs.add(new WeightedAutoSuggestDoc(autoSuggestDoc, solrWeight, uieWeight, uieWeight + solrWeight, null));
        }

        weightedAutoSuggestDocs.sort((o1, o2) -> Double.compare(o2.getCombinedWeight(), o1.getCombinedWeight()));

        if (params.isDiversify()) {
            weightedAutoSuggestDocs = getDiversifiedRanking(weightedAutoSuggestDocs);
        }

        List<QuerySuggestion> querySuggestions = new ArrayList<>();

        int numberOfDocsWithStores = storeHandler.getNumberOfDocsWithStores(autoSuggestDocs);
        int processCount = 0;

        for (WeightedAutoSuggestDoc weightedAutoSuggestDoc : weightedAutoSuggestDocs) {
            List<Store> stores = (processCount < numberOfDocsWithStores) ? storeHandler.getStoresFromProductStore(
                    weightedAutoSuggestDoc.getAutoSuggestDoc().getProductStores(),
                    params.getMaxNumberOfStorePerQuery(),
                    params.getStore(),
                    params.getMarketPlaceIds(),
                    weightedAutoSuggestDoc.getAutoSuggestDoc().getStoreClassifier()) : new ArrayList<>();
            querySuggestions.add(new QuerySuggestion(
                    weightedAutoSuggestDoc.getAutoSuggestDoc().getCorrectedQuery(),
                    stores,
                    params.isDebug() ? weightedAutoSuggestDoc.getSolrWeight() : null,
                    params.isDebug() ? weightedAutoSuggestDoc.getUieWeight() : null,
                    params.isDebug() ? weightedAutoSuggestDoc.getCombinedWeight() : null,
                    params.isDebug() ? weightedAutoSuggestDoc.getTopStoreBucket() : null));
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

    private List<WeightedAutoSuggestDoc> getDiversifiedRanking(List<WeightedAutoSuggestDoc> weightedAutoSuggestDocs) {
        Map<String, List<WeightedAutoSuggestDoc>> topStoreMap = new LinkedHashMap<>();
        for (WeightedAutoSuggestDoc weightedAutoSuggestDoc : weightedAutoSuggestDocs) {
            AutoSuggestDoc autoSuggestDoc = weightedAutoSuggestDoc.getAutoSuggestDoc();
            String topStore = getTopStore(autoSuggestDoc.getStoreClassifier());
            if (!topStoreMap.containsKey(topStore)) topStoreMap.put(topStore, new LinkedList<>());
            topStoreMap.get(topStore).add(weightedAutoSuggestDoc);
        }
        List<WeightedAutoSuggestDoc> finalWeightedAutoSuggestDoc = new ArrayList<>();
        int emptyKeys = 0;
        while (true) {
            if (emptyKeys == topStoreMap.size()) break;
            for (Map.Entry<String, List<WeightedAutoSuggestDoc>> topStoreEntry : topStoreMap.entrySet()) {
                if (topStoreEntry.getValue().isEmpty()) continue;
                for (int i = 0; i < 2; i++) {
                    if (topStoreEntry.getValue().size() > 0) {
                        WeightedAutoSuggestDoc remove = topStoreEntry.getValue().remove(0);
                        finalWeightedAutoSuggestDoc.add(new WeightedAutoSuggestDoc(
                                remove.getAutoSuggestDoc(),
                                remove.getSolrWeight(),
                                remove.getUieWeight(),
                                remove.getCombinedWeight(),
                                topStoreEntry.getKey()));
                        if (topStoreEntry.getValue().isEmpty()) {
                            emptyKeys++;
                            break;
                        }
                    }
                }
            }
        }
        return finalWeightedAutoSuggestDoc;
    }

    private String getTopStore(Map<String, Double> storeWeightMap) {
        if (storeWeightMap == null || storeWeightMap.isEmpty()) return "search.flipkart.com";
        double maxWeight = 0;
        String store = "search.flipkart.com";
        for (Map.Entry<String, Double> storeWeight : storeWeightMap.entrySet()) {
            if (storeWeight.getValue() > maxWeight) {
                maxWeight = storeWeight.getValue();
                store = storeWeight.getKey();
            }
        }
        return store;
    }
}
