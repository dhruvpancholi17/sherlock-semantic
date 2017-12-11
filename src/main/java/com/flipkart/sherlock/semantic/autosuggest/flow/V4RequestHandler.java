package com.flipkart.sherlock.semantic.autosuggest.flow;

import com.flipkart.sherlock.semantic.autosuggest.dao.AutoSuggestColdStartDao;
import com.flipkart.sherlock.semantic.autosuggest.dataGovernance.Ingester;
import com.flipkart.sherlock.semantic.autosuggest.models.*;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.*;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.types.V4Product;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.types.V4Query;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.types.V4QueryStore;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.types.V4Rich;
import com.flipkart.sherlock.semantic.common.util.FkConfigServiceWrapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.flipkart.sherlock.semantic.app.AppConstants.AUTOSUGGEST_COLD_START_VERSION;

/**
 * Created by dhruv.pancholi on 01/06/17.
 */
@Slf4j
@Singleton
public class V4RequestHandler {

    @Inject
    private ParamsHandler paramsHandler;

    @Inject
    private QueryRequestHandler queryRequestHandler;

    @Inject
    private ProductRequestHandler productRequestHandler;

    @Inject
    private AutoSuggestColdStartDao autoSuggestColdStartDao;

    @Inject
    private FkConfigServiceWrapper fkConfigServiceWrapper;


    public V4AutoSuggestResponse getV4Response(String store, UriInfo uriInfo, HttpHeaders headers) {
        String payloadId = UUID.randomUUID().toString();

        Params params = paramsHandler.getParams(store, uriInfo);

        if (params.getQuery() == null || params.getQuery().isEmpty()) {
            return new V4AutoSuggestResponse(payloadId, fkConfigServiceWrapper.getInt(AUTOSUGGEST_COLD_START_VERSION),
                    autoSuggestColdStartDao.getColdStartRows(), null,
                    null, null, null);
        }

        QueryResponse queryResponse = queryRequestHandler
                .getQuerySuggestions(params.getQuery(), new QueryRequest(params, null));

        ProductResponse productResponse = productRequestHandler
                .getProductSuggestions(params.getQuery(),
                        new ProductRequest(params, queryResponse.getAutoSuggestSolrResponse()));

        List<V4SuggestionRow> v4Suggestions = getV4Suggestion(queryResponse, productResponse);
        new Ingester().publishDataV4(payloadId, params, v4Suggestions, queryResponse, productResponse, headers, uriInfo);
        return new V4AutoSuggestResponse(
                payloadId,
                0,
                v4Suggestions,
                params.isDebug() ? params : null,
                params.isDebug() ? queryResponse.getAutoSuggestSolrResponse().getSolrQuery() : null,
                params.isDebug() ? productResponse.getAutoSuggestSolrResponse().getSolrQuery() : null,
                params.isDebug() ? productResponse.getAutoSuggestSolrResponse().getAutoSuggestDocs() : null);
    }

    private List<V4SuggestionRow> getV4Suggestion(QueryResponse queryResponse, ProductResponse productResponse) {
        List<V4SuggestionRow> suggestions = new ArrayList<>();

        int count = 0;
        List<QuerySuggestion> querySuggestions = queryResponse.getQuerySuggestions();
        for (QuerySuggestion querySuggestion : querySuggestions) {
            String query = querySuggestion.getQuery();
            List<Store> stores = querySuggestion.getStores();
            if (stores == null || stores.size() == 0) {
//                suggestions.add(new V4SuggestionRow(V4SuggestionType.QUERY, getQuerySuggestion(query)));
            } else {
                for (Store store : stores) {
//                    suggestions.add(new V4SuggestionRow(V4SuggestionType.QUERY_STORE, getQueryStoreSuggestion(query, store)));
                }
            }
        }

        suggestions = (suggestions.size() > 10) ? suggestions.subList(0, 10) : suggestions;

        List<ProductSuggestion> productSuggestions = productResponse.getProductSuggestions();
        for (ProductSuggestion productSuggestion : productSuggestions) {
//            suggestions.add(new V4SuggestionRow(V4SuggestionType.PRODUCT, getProductSuggestion(productSuggestion.getId(), "", "")));
        }

        return suggestions;
    }

    private V4SuggestionRow getQuerySuggestion(String query) {
        return V4Query.builder()
                .clickUrl(getQueryUrl(query))
                .contentType(V4ContentType.RECENT)
                .query(query)
                .build();
    }

    private V4SuggestionRow getQueryStoreSuggestion(String query, Store store) {
        String clickUrl = getQueryUrl(query) + "&sid=" + store.getStore();
        return V4QueryStore.builder()
                .clickUrl(clickUrl)
                .contentType(V4ContentType.RECENT)
                .query(query)
                .store(store.getTitle())
                .marketplaceId(store.getMarketPlaceId())
                .build();
    }

    private String getQueryUrl(String query) {
        return String.format("/search?q=%s&as=on&as-show=on", query);
    }

    private V4SuggestionRow getProductSuggestion(String pid, String title, String imageUrl) {
        return V4Product.builder()
                .clickUrl(String.format("https://www.flipkart.com/a/p/a?pid=%s", pid))
                .contentType(V4ContentType.RECENT)
                .pid(pid)
                .title(title)
                .imageUrl(imageUrl)
                .build();
    }
}
