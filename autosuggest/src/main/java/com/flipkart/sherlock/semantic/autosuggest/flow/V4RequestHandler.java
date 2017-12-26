package com.flipkart.sherlock.semantic.autosuggest.flow;

import com.flipkart.sherlock.semantic.autosuggest.dao.AutoSuggestColdStartDao;
import com.flipkart.sherlock.semantic.autosuggest.helpers.MarketAnalyzer;
import com.flipkart.sherlock.semantic.autosuggest.models.*;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.*;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.cards.V4FlashProduct;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.cards.V4FlashQuery;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.cards.V4FlashQueryStore;
import com.flipkart.sherlock.semantic.commons.config.FkConfigServiceWrapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;


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


    public V4FlashAutoSuggestResponse getV4Response(Params params) {
        String payloadId = UUID.randomUUID().toString();

        if (params.getQuery() == null || params.getQuery().isEmpty()) {
            return new V4FlashAutoSuggestResponse(payloadId,
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

        List<V4FlashSuggestion> v4FlashSuggestions = getV4Suggestion(queryResponse, productResponse);
        return new V4FlashAutoSuggestResponse(
                payloadId,
                fkConfigServiceWrapper.getInt(AUTOSUGGEST_COLD_START_VERSION),
                v4FlashSuggestions,
                params.isDebug() ? params : null,
                params.isDebug() ? queryResponse.getAutoSuggestSolrResponse().getSolrQuery() : null,
                params.isDebug() ? productResponse.getAutoSuggestSolrResponse().getSolrQuery() : null,
                params.isDebug() ? productResponse.getAutoSuggestSolrResponse().getAutoSuggestDocs() : null);
    }

    private List<V4FlashSuggestion> getV4Suggestion(QueryResponse queryResponse, ProductResponse productResponse) {
        List<V4FlashSuggestion> suggestions = new ArrayList<>();

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

    private V4FlashSuggestion getV4QuerySuggestion(String query, Store store) {
        V4FlashSuggestion v4FlashSuggestion = null;
        if (store == null) {
            V4FlashQuery v4Query = new V4FlashQuery();
            v4Query.setType(V4FlashSuggestionType.QUERY);
            v4Query.setClickUrl(getQueryStoreUrl(query, null));
            v4Query.setContentType(V4FlashContentType.RECENT);
            v4Query.setQuery(query);
            v4FlashSuggestion = v4Query;
        } else {
            V4FlashQueryStore v4QueryStore = new V4FlashQueryStore();
            v4QueryStore.setType(V4FlashSuggestionType.QUERY_STORE);
            v4QueryStore.setClickUrl(getQueryStoreUrl(query, store));
            v4QueryStore.setContentType(V4FlashContentType.RECENT);
            v4QueryStore.setQuery(query);
            v4QueryStore.setStore(store.getTitle());
            v4QueryStore.setMarketPlaceId(MarketAnalyzer.FLIP_MART.equals(store.getMarketPlaceId()) ?
                    V4FlashMarketPlace.GROCERY :
                    V4FlashMarketPlace.FLIPKART);
            v4FlashSuggestion = v4QueryStore;
        }
        return v4FlashSuggestion;
    }

    private V4FlashSuggestion getV4ProductSuggestion(String id) {
        V4FlashProduct v4Product = new V4FlashProduct();
        v4Product.setType(V4FlashSuggestionType.PRODUCT);
        v4Product.setClickUrl("clickUrl");
        v4Product.setContentType(V4FlashContentType.RECENT);
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
