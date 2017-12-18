package com.flipkart.sherlock.semantic.autosuggest.flow;

import com.flipkart.sherlock.semantic.autosuggest.dao.AutoSuggestColdStartDao;
import com.flipkart.sherlock.semantic.autosuggest.helpers.MarketAnalyzer;
import com.flipkart.sherlock.semantic.autosuggest.models.*;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.*;
import com.flipkart.sherlock.semantic.commons.config.FkConfigServiceWrapper;
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


    public V4AutoSuggestResponse getV4Response(String store, UriInfo uriInfo) {
        String payloadId = UUID.randomUUID().toString();

        Params params = paramsHandler.getParams(store, uriInfo);

        if (params.getQuery() == null || params.getQuery().isEmpty()) {
            return new V4AutoSuggestResponse(payloadId,
                    fkConfigServiceWrapper.getInt(AUTOSUGGEST_COLD_START_VERSION),
                    autoSuggestColdStartDao.getColdStartRows(),
                    null, null,
                    null, null);
        }

        QueryResponse queryResponse = queryRequestHandler
                .getQuerySuggestions(params.getQuery(), new QueryRequest(params, null));

        ProductResponse productResponse = productRequestHandler
                .getProductSuggestions(params.getQuery(),
                        new ProductRequest(params, queryResponse.getAutoSuggestSolrResponse()));

        List<V4Suggestion> v4Suggestions = getV4Suggestion(queryResponse, productResponse);
        return new V4AutoSuggestResponse(
                payloadId,
                fkConfigServiceWrapper.getInt(AUTOSUGGEST_COLD_START_VERSION),
                v4Suggestions,
                params.isDebug() ? params : null,
                params.isDebug() ? queryResponse.getAutoSuggestSolrResponse().getSolrQuery() : null,
                params.isDebug() ? productResponse.getAutoSuggestSolrResponse().getSolrQuery() : null,
                params.isDebug() ? productResponse.getAutoSuggestSolrResponse().getAutoSuggestDocs() : null);
    }

    private List<V4Suggestion> getV4Suggestion(QueryResponse queryResponse, ProductResponse productResponse) {
        List<V4Suggestion> suggestions = new ArrayList<>();

        int count = 0;
        List<QuerySuggestion> querySuggestions = queryResponse.getQuerySuggestions();
        for (QuerySuggestion querySuggestion : querySuggestions) {
            String query = querySuggestion.getQuery();
            List<Store> stores = querySuggestion.getStores();
            if (stores == null || stores.size() == 0) {
                suggestions.add(getV4QuerySuggestion(query, null));
            } else {
                for (Store store : stores) {
                    suggestions.add(getV4QuerySuggestion(query, store));
                }
            }
        }

        suggestions = (suggestions.size() > 10) ? suggestions.subList(0, 10) : suggestions;

//        List<ProductSuggestion> productSuggestions = productResponse.getProductSuggestions();
//        for (ProductSuggestion productSuggestion : productSuggestions) {
//            suggestions.add(getV4ProductSuggestion(productSuggestion.getId()));
//        }

        return suggestions;
    }

    private V4Suggestion getV4QuerySuggestion(String query, Store store) {
        V4Suggestion v4Suggestion = null;
        if (store == null) {
            V4Query v4Query = new V4Query();
            v4Query.setType(V4SuggestionType.QUERY);
            v4Query.setClickUrl(getQueryStoreUrl(query, null));
            v4Query.setContentType(V4ContentType.RECENT);
            v4Query.setQuery(query);
            v4Suggestion = v4Query;
        } else {
            V4QueryStore v4QueryStore = new V4QueryStore();
            v4QueryStore.setType(V4SuggestionType.QUERY_STORE);
            v4QueryStore.setClickUrl(getQueryStoreUrl(query, store));
            v4QueryStore.setContentType(V4ContentType.RECENT);
            v4QueryStore.setQuery(query);
            v4QueryStore.setStore(store.getTitle());
            v4QueryStore.setMarketPlaceId(MarketAnalyzer.FLIP_MART.equals(store.getMarketPlaceId()) ?
                    V4MarketPlace.GROCERY :
                    V4MarketPlace.FLIPKART);
            v4Suggestion = v4QueryStore;
        }
        return v4Suggestion;
    }

    private V4Suggestion getV4ProductSuggestion(String id) {
        V4Product v4Product = new V4Product();
        v4Product.setType(V4SuggestionType.PRODUCT);
        v4Product.setClickUrl("clickUrl");
        v4Product.setContentType(V4ContentType.RECENT);
        v4Product.setPid(id);
        v4Product.setTitle("title");
        v4Product.setImageUrl("imageUrl");
        return v4Product;
    }

    private String getQueryStoreUrl(String query, Store store) {
        String url = "/search?q=" + query +
                ((store != null) ? ("&sid=" + store.getStore()) : "") +
                "&as=on&as-show=on";
        return url.replaceAll(" ", "+");
    }
}
