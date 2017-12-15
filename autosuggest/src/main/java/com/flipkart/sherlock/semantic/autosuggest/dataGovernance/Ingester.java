package com.flipkart.sherlock.semantic.autosuggest.dataGovernance;


import com.flipkart.seraph.fkint.cp.discover_search.*;
import com.flipkart.seraph.fkint.cp.discover_search.AutoSuggestResponse;
import com.flipkart.seraph.schema.BaseSchema;
import com.flipkart.sherlock.semantic.autosuggest.models.*;
import com.flipkart.sherlock.semantic.common.util.JmxMetricRegistry;
import entity.IngestionMode;
import entity.IngestionObj;
import entity.MessageType;
import exception.TransformException;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.server.ContainerRequest;
import service.Publisher;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;
import service.Transformer;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.*;

import static com.flipkart.sherlock.semantic.autosuggest.dataGovernance.Constants.*;

@Slf4j
public class Ingester implements Transformer {


    @Override
    public BaseSchema transform(Object rawObject) throws TransformException {
        AutoSuggestResponseData autoSuggestData = (AutoSuggestResponseData) rawObject;
        Params params = autoSuggestData.getParams();
        MultivaluedMap<String, String> header = autoSuggestData.getHeader();
        MultivaluedMap<String, String> queryParam = autoSuggestData.getQueryParam();
        MultivaluedMap<String, String> pathParam = autoSuggestData.getPathParam();


        String payloadId = autoSuggestData.getPayLoadId();
        QueryResponse queryResponse = autoSuggestData.getQueryResponse();
        ProductResponse productResponse = autoSuggestData.getProductResponse();


        AutoSuggest autoSuggest = new AutoSuggest();
        AutoSuggestRequest autoSuggestRequest = new AutoSuggestRequest();
        AutoSuggestResponse autoSuggestResponse = new AutoSuggestResponse();

        loadAutoSuggestRequest(autoSuggestRequest, params, header, queryParam, pathParam);
        loadAutoSuggestResponse(payloadId, autoSuggestResponse, params, queryResponse, productResponse);
        autoSuggest.setAutoSuggestRequest(autoSuggestRequest);
        autoSuggest.setAutoSuggestResponse(autoSuggestResponse);
        return autoSuggest;

    }

    private void loadAutoSuggestRequest(AutoSuggestRequest autoSuggestRequest, Params params, MultivaluedMap<String, String> header, MultivaluedMap<String, String> queryParam, MultivaluedMap<String, String> pathParam) {
        if (header != null) {
            if(header.containsKey(xDeviceId))  autoSuggestRequest.setXDeviceId(header.getFirst(xDeviceId));
            if(header.containsKey(xABIds)) autoSuggestRequest.setXABIdsList(header.get(xABIds));
            if(header.containsKey(xRequestId)) autoSuggestRequest.setXRequestId(header.getFirst(xRequestId));
            if(header.containsKey(xSearchSessionId)) autoSuggestRequest.setXSearchSessionId(header.getFirst(xSearchSessionId));
            if(header.containsKey(xSearchQueryId)) autoSuggestRequest.setXSearchQueryId(header.getFirst(xSearchQueryId));
            if(header.containsKey(xClientDeviceChannel)) autoSuggestRequest.setXClientDeviceChannel(header.getFirst(xClientDeviceChannel));
        }
        autoSuggestRequest.setMarketPlaceId(params.getMarketPlaceIds().toString());
        setParamsList(autoSuggestRequest, queryParam, pathParam);
    }

    private void setParamsList(AutoSuggestRequest autoSuggestRequest, MultivaluedMap<String, String> queryParams, MultivaluedMap<String, String> pathParams) {
        ArrayList<RequestParamsMap> paramsMaps = new ArrayList<>();
        String key,val;
        if (pathParams != null) {
            for (Map.Entry pathParam : pathParams.entrySet()) {
                key = pathParam.getKey().toString();
                val = pathParam.getValue().toString();
                RequestParamsMap requestParamsMap = new RequestParamsMap();
                requestParamsMap.setKey(key);
                requestParamsMap.setValueList(Collections.singletonList(val));
                paramsMaps.add(requestParamsMap);
                if(key.equalsIgnoreCase(store)) {
                    autoSuggestRequest.setStorePath(val);
                }
            }
        }

        if (queryParams != null) {
            for (Map.Entry queryParam : queryParams.entrySet()) {
                key = queryParam.getKey().toString();
                val = queryParam.getValue().toString();
                RequestParamsMap requestParamsMap = new RequestParamsMap();
                requestParamsMap.setKey(queryParam.getKey().toString());
                requestParamsMap.setValueList(Collections.singletonList(val));
                paramsMaps.add(requestParamsMap);
                if (key.equalsIgnoreCase(originalPrefix)) {
                    autoSuggestRequest.setQueryPrefix(val);
                }
            }
        }

        autoSuggestRequest.setRequestParamsList(paramsMaps);
    }

    // Load data for auto-suggest Version 3

    public void publishData(QueryResponse queryResponse,Params params, ProductResponse productResponse, HttpHeaders headers, UriInfo uriInfo) {
        Timer timer = JmxMetricRegistry.INSTANCE.getInstance().timer("DG-publish-timer");
        Context timerContext = timer.time();
        try {
            MultivaluedMap<String, String> header = ((ContainerRequest) headers).getHeaders();
            MultivaluedMap<String, String> pathParam = uriInfo.getPathParameters();
            MultivaluedMap<String, String> queryParam = uriInfo.getQueryParameters();
            final String requestId = (header.containsKey(xRequestId)) ? header.getFirst(xRequestId) : defaultId;
            final Boolean isPerfTest = (header.containsKey(xPerfTest)) ? (Boolean.valueOf(header.getFirst(xPerfTest))) : false;
            AutoSuggestResponseData autoSuggestData = new AutoSuggestResponseData(requestId, params, queryResponse, productResponse, header, queryParam, pathParam);
            IngestionObj ingestionObj = new IngestionObj("semantic_autosuggest", autoSuggestData, isPerfTest, MessageType.EVENT, IngestionMode.SPECTER, this, requestId);
            Publisher.INSTANCE.publish(ingestionObj);
        } catch (Exception e) {
            Meter meter = JmxMetricRegistry.INSTANCE.getInstance().meter("DG-publish-exception");
            meter.mark();
            log.error("AutoSuggest DG Exception" , e);
        } finally {
            timerContext.stop();
        }
    }

    private void loadAutoSuggestResponse (String payloadId, AutoSuggestResponse autoSuggestResponse, Params params, QueryResponse queryResponse , ProductResponse productResponse) {
        List<QuerySuggestion> querySuggestions = queryResponse.getQuerySuggestions();
        List<ProductSuggestion> productSuggestions = productResponse.getProductSuggestions();
        List<AutoSuggestDoc> autoSuggestDocs = productResponse.getAutoSuggestSolrResponse().getAutoSuggestDocs();
        Boolean isSolrSpellUsed = queryResponse.isSolrSpellCorrectionUsed();
        String solrSpellCorrectedQuery = (isSolrSpellUsed) ? queryResponse.getSolrSpellCorrectionOutput() : "";

        autoSuggestResponse.setSolrSpellcheckSuggestionUsed(solrSpellCorrectedQuery);
        autoSuggestResponse.setIsSolrSpellCheckUsed(isSolrSpellUsed);

        autoSuggestResponse.setPayloadId(payloadId);
        autoSuggestResponse.setColdStartVersion(0);
        autoSuggestResponse.setCleanQuery(params.getQuery());

        ArrayList<AutoSuggestSuggestion> autoSuggestSuggestions = new ArrayList<>();

        setQuerySuggestion(querySuggestions, autoSuggestSuggestions, autoSuggestDocs);

        setProductSuggestion (productSuggestions, autoSuggestSuggestions);

        autoSuggestResponse.setSuggestionsList(autoSuggestSuggestions);
    }

    private int setAutoSuggestSuggestionsHavingStore (QuerySuggestion querySuggestion, TransformedCtrObj ctrObj, Double wilsonCtr, Double solrScore, ArrayList<AutoSuggestSuggestion> autoSuggestSuggestions, int suggestionIndex) {
        for (Store store : querySuggestion.getStores()) {
            AutoSuggestSuggestion autoSuggestSuggestion = new AutoSuggestSuggestion();

            autoSuggestSuggestion.setSuggestionType(query);

            SuggestionContent suggestionContent = new SuggestionContent();

            SuggestionStore suggestionStore = new SuggestionStore();
            suggestionStore.setTitle(store.getTitle());
            suggestionStore.setStorePath(store.getStore());
            suggestionStore.setMarketPlaceId(store.getMarketPlaceId());
            suggestionContent.setStore(suggestionStore);

            suggestionContent.setTitle(querySuggestion.getQuery());
            suggestionContent.setQueryWilsonCTR(wilsonCtr);
            suggestionContent.setSolrSuggestionScore(solrScore);
            suggestionContent.setQueryPHits(ctrObj.getPHits());
            suggestionContent.setQueryDecayedImpressions(ctrObj.getStateHits());
            suggestionContent.setQueryImpressions(Integer.toString(ctrObj.getImpressions()));

            suggestionContent.setPosition(suggestionIndex++);

            autoSuggestSuggestion.setSuggestionContent(suggestionContent);
            autoSuggestSuggestions.add(autoSuggestSuggestion);
        }
        return suggestionIndex;
    }

    private int setAutoSuggestSuggestionsHavingNoStore(QuerySuggestion querySuggestion, TransformedCtrObj ctrObj, Double wilsonCtr, Double solrScore, ArrayList<AutoSuggestSuggestion> autoSuggestSuggestions, int suggestionIndex) {
        AutoSuggestSuggestion autoSuggestSuggestion = new AutoSuggestSuggestion();
        autoSuggestSuggestion.setSuggestionType(query);
        SuggestionContent suggestionContent = new SuggestionContent();

        suggestionContent.setTitle(querySuggestion.getQuery());
        suggestionContent.setQueryWilsonCTR(wilsonCtr);
        suggestionContent.setSolrSuggestionScore(solrScore);
        suggestionContent.setQueryPHits(ctrObj.getPHits());
        suggestionContent.setQueryDecayedImpressions(ctrObj.getStateHits());
        suggestionContent.setQueryImpressions(Integer.toString(ctrObj.getImpressions()));
        suggestionContent.setPosition(suggestionIndex++);
        autoSuggestSuggestion.setSuggestionContent(suggestionContent);
        autoSuggestSuggestions.add(autoSuggestSuggestion);
        return suggestionIndex ;
    }

    private void setQuerySuggestion (List<QuerySuggestion> querySuggestions, ArrayList<AutoSuggestSuggestion> autoSuggestSuggestions, List<AutoSuggestDoc> autoSuggestDocs) {
        int autoSuggestDocIndex = 0;
        int suggestionIndex = 1;
        for (QuerySuggestion querySuggestion : querySuggestions) {
            AutoSuggestDoc autoSuggestDoc = autoSuggestDocs.get(autoSuggestDocIndex++);

            TransformedCtrObj ctrObj = autoSuggestDoc.getCtrObj();
            Double solrScore = autoSuggestDoc.getSolrScore().doubleValue();
            Double wilsonCtr = autoSuggestDoc.getWilsonCTR();

            if ((querySuggestion.getStores() != null) && (querySuggestion.getStores().size() > 0)) {
                suggestionIndex = setAutoSuggestSuggestionsHavingStore(querySuggestion, ctrObj, wilsonCtr, solrScore, autoSuggestSuggestions, suggestionIndex);
            } else {
                suggestionIndex = setAutoSuggestSuggestionsHavingNoStore(querySuggestion, ctrObj, wilsonCtr, solrScore, autoSuggestSuggestions, suggestionIndex);
            }
        }
    }

    private void setProductSuggestion(List<ProductSuggestion> productSuggestions, ArrayList<AutoSuggestSuggestion> autoSuggestSuggestions ) {
        for (ProductSuggestion productSuggestion : productSuggestions) {
            AutoSuggestSuggestion autoSuggestSuggestion = new AutoSuggestSuggestion();
            autoSuggestSuggestion.setSuggestionType("PRODUCT");

            SuggestionContent suggestionContent = new SuggestionContent();
            suggestionContent.setProductId(productSuggestion.getId());
            autoSuggestSuggestion.setSuggestionContent(suggestionContent);
            autoSuggestSuggestions.add(autoSuggestSuggestion);
        }
    }

}
