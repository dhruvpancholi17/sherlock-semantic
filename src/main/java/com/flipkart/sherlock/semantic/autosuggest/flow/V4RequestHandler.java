package com.flipkart.sherlock.semantic.autosuggest.flow;

import com.flipkart.sherlock.semantic.autosuggest.dao.AutoSuggestColdStartDao;
import com.flipkart.sherlock.semantic.autosuggest.helpers.MarketAnalyzer;
import com.flipkart.sherlock.semantic.autosuggest.models.*;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.*;
import com.flipkart.sherlock.semantic.common.util.FkConfigServiceWrapper;
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


    public V4AutoSuggestResponse getV4Response(String store, UriInfo uriInfo) {
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
                suggestions.add(getV4QuerySuggestionRow(query, null));
            } else {
                for (Store store : stores) {
                    suggestions.add(getV4QuerySuggestionRow(query, store));
                }
            }
        }

        suggestions = (suggestions.size() > 10) ? suggestions.subList(0, 10) : suggestions;

        List<ProductSuggestion> productSuggestions = productResponse.getProductSuggestions();
        for (ProductSuggestion productSuggestion : productSuggestions) {
            suggestions.add(getV4ProductSuggestionRow(productSuggestion.getId()));
        }

        return suggestions;
    }

    private V4SuggestionRow getV4QuerySuggestionRow(String query, Store store) {
        V4Suggestion v4Suggestion = V4Suggestion.builder()
                .titleText(query)
                .contentType(getV4QueryType(query, store))
                .store(getV4Store(store))
                .clickUrl(getQueryStoreUrl(query, store))
                .build();
        return new V4SuggestionRow((store != null) ? V4SuggestionType.QUERY_STORE : V4SuggestionType.QUERY, v4Suggestion);
    }

    private V4SuggestionRow getV4ProductSuggestionRow(String id) {
        V4Suggestion v4Suggestion = V4Suggestion.builder()
                .pid(id)
                .contentType(V4ContentType.RECENT)
                .titleText("Quick Heal TOTAL SECURITY 2USER 1YEAR")
                .imageUrl("https://rukminim1.flixcart.com/image/832/832/j2516kw0/security-software/e/4/n/total-security-2-pc-1-year-tr2-original-imaetezd8ydffquf.jpeg?q=70")
                .clickUrl("/quick-heal-total-security-2user-1year/p/itmet7fvcdhzuhm7?pid=SECEP6PSD9H3RFFB")
                .build();
        return new V4SuggestionRow(V4SuggestionType.PRODUCT, v4Suggestion);
    }

    private V4ContentType getV4QueryType(String query, Store store) {
        return V4ContentType.RECENT;
    }

    private V4Store getV4Store(Store store) {
        if (store == null) return null;
        String marketPlaceId = store.getMarketPlaceId();
        V4MarketPlace v4MarketPlace = (MarketAnalyzer.FLIP_MART.equals(marketPlaceId)) ?
                V4MarketPlace.GROCERY :
                V4MarketPlace.FLIPKART;
        return new V4Store(store.getTitle(), v4MarketPlace, store.getStore());
    }

    private String getQueryStoreUrl(String query, Store store) {
        return "/search?q=" + query +
                ((store != null) ? ("&sid=" + store.getStore()) : "") +
                "&as=on&as-show=on";
    }
}
