package com.flipkart.sherlock.semantic.autosuggest.dataGovernance;


import com.flipkart.seraph.fkint.cp.discover_search.*;
import com.flipkart.seraph.fkint.cp.discover_search.AutoSuggestResponse;
import com.flipkart.seraph.schema.BaseSchema;
import com.flipkart.sherlock.semantic.autosuggest.models.*;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.V4Store;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.V4Suggestion;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.V4SuggestionRow;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.V4SuggestionType;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.TimerContext;
import com.yammer.metrics.core.Timer;
import entity.IngestionMode;
import entity.IngestionObj;
import entity.MessageType;
import exception.TransformException;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.server.ContainerRequest;
import service.Publisher;
import service.Transformer;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.flipkart.sherlock.semantic.autosuggest.dataGovernance.Constants.*;

@Slf4j
public class Ingester implements Transformer {


    public class AutoSuggestDataV4 {
        Params params;
        List<V4SuggestionRow> v4SuggestionRows;
        ProductResponse productResponse;
        String payLoadId;
        QueryResponse queryResponse;
        MultivaluedMap<String, String> header;
        UriInfo uriInfo;
        Boolean versionFour = false;

        public AutoSuggestDataV4(String payLoadId, Params params, List<V4SuggestionRow> v4SuggestionRows, QueryResponse queryResponse, ProductResponse productResponse, MultivaluedMap<String, String> header, UriInfo uriInfo, Boolean vesrion) {
            this.params = params;
            this.v4SuggestionRows = v4SuggestionRows;
            this.productResponse = productResponse;
            this.queryResponse = queryResponse;
            this.header = header;
            this.uriInfo = uriInfo;
            this.payLoadId = payLoadId;
            this.versionFour = vesrion;
        }

    }

    @Override
    public BaseSchema transform(Object rawObject) throws TransformException {
        AutoSuggestDataV4 autoSuggestData = (AutoSuggestDataV4) rawObject;
        if(autoSuggestData.versionFour) {
            List<AutoSuggestDoc>autoSuggestDocs = autoSuggestData.queryResponse.getAutoSuggestSolrResponse().getAutoSuggestDocs();
            List<V4SuggestionRow> suggestionRows = autoSuggestData.v4SuggestionRows;
            Params params = autoSuggestData.params;
            MultivaluedMap<String, String> header = autoSuggestData.header;
            UriInfo uriInfo = autoSuggestData.uriInfo;
            String payloadId = autoSuggestData.payLoadId;

            AutoSuggest autoSuggest = new AutoSuggest();
            AutoSuggestRequest autoSuggestRequest = new AutoSuggestRequest();
            AutoSuggestResponse autoSuggestResponse = new AutoSuggestResponse();
            loadAutoSuggestRequest(autoSuggestRequest, params, header, uriInfo);
            loadAutoSuggestResponseV4(payloadId, autoSuggestResponse, params, suggestionRows, autoSuggestDocs);
            autoSuggest.setAutoSuggestRequest(autoSuggestRequest);
            autoSuggest.setAutoSuggestResponse(autoSuggestResponse);

            return autoSuggest;
        } else {
            Params params = autoSuggestData.params;
            MultivaluedMap<String, String> header = autoSuggestData.header;
            String payloadId = autoSuggestData.payLoadId;
            UriInfo uriInfo = autoSuggestData.uriInfo;
            QueryResponse queryResponse = autoSuggestData.queryResponse;
            ProductResponse productResponse = autoSuggestData.productResponse;

            AutoSuggest autoSuggest = new AutoSuggest();
            AutoSuggestRequest autoSuggestRequest = new AutoSuggestRequest();
            AutoSuggestResponse autoSuggestResponse = new AutoSuggestResponse();

            loadAutoSuggestRequest(autoSuggestRequest, params, header, uriInfo);
            loadAutoSuggestResponse(payloadId, autoSuggestResponse, params, queryResponse, productResponse);
            autoSuggest.setAutoSuggestRequest(autoSuggestRequest);
            autoSuggest.setAutoSuggestResponse(autoSuggestResponse);
            return autoSuggest;
        }
    }

    // Load data for auto-suggest Version 4

    public void publishDataV4(String payloadId, Params params, List<V4SuggestionRow> v4SuggestionRows, QueryResponse queryResponse, ProductResponse productResponse, HttpHeaders headers, UriInfo uriInfo) {
        Timer timer = Metrics.newTimer(Ingester.class, "AutoSuggest DG publish timer");
        TimerContext timerContext = timer.time();
        try {
            MultivaluedMap<String, String> header = ((ContainerRequest) headers).getHeaders();
            AutoSuggestDataV4 autoSuggestData = new AutoSuggestDataV4(payloadId, params, v4SuggestionRows, queryResponse, productResponse,header, uriInfo, true);
            String requestId = (header.containsKey("X-Request-ID")) ? header.getFirst("X-Request-ID") : "ID-NOT-FOUND";
            Boolean isPerfTest = (header.containsKey("X-Perf-Test")) ? (Boolean.valueOf(header.getFirst("X-Perf-Test"))) : false;
            IngestionObj ingestionObj = new IngestionObj("semantic_autosuggest", autoSuggestData, isPerfTest, MessageType.EVENT, IngestionMode.SPECTER, this, requestId);
            Publisher.INSTANCE.publish(ingestionObj);
        } catch (Exception e) {
            Meter meter = Metrics.newMeter(Ingester.class, "DG-publish-exception", "count", TimeUnit.SECONDS);
            meter.mark();
            log.error("AutoSuggest DG Exception" , e);
        } finally {
            timerContext.stop();
        }

    }

    private void loadAutoSuggestRequest(AutoSuggestRequest autoSuggestRequest, Params params, MultivaluedMap<String, String> header, UriInfo uriInfo) {
        if(header.containsKey(xDeviceId))  autoSuggestRequest.setXDeviceId(header.getFirst(xDeviceId));
        if(header.containsKey(xABIds)) autoSuggestRequest.setXABIdsList(header.get(xABIds));
        if(header.containsKey(xRequestId)) autoSuggestRequest.setXRequestId(header.getFirst(xRequestId));
        if(header.containsKey(xSearchSessionId)) autoSuggestRequest.setXSearchSessionId(header.getFirst(xSearchSessionId));
        if(header.containsKey(xSearchQueryId)) autoSuggestRequest.setXSearchQueryId(header.getFirst(xSearchQueryId));
        if(header.containsKey(xClientDeviceChannel)) autoSuggestRequest.setXClientDeviceChannel(header.getFirst(xClientDeviceChannel));

        autoSuggestRequest.setMarketPlaceId(params.getMarketPlaceIds().toString());
        setParamsList(autoSuggestRequest, uriInfo);
    }

    private void setParamsList(AutoSuggestRequest autoSuggestRequest, UriInfo uriInfo) {
        ArrayList<RequestParamsMap> paramsMaps = new ArrayList<>();
        for (Map.Entry pathParam : uriInfo.getPathParameters().entrySet()) {
            String key = pathParam.getKey().toString();
            String val = pathParam.getValue().toString();
            RequestParamsMap requestParamsMap = new RequestParamsMap();
            requestParamsMap.setKey(key);
            requestParamsMap.setValueList(Collections.singletonList(val));
            paramsMaps.add(requestParamsMap);
            if(key.equalsIgnoreCase(store)) {
                autoSuggestRequest.setStorePath(val);
            }
        }

        for (Map.Entry queryParam : uriInfo.getQueryParameters().entrySet()) {
            String key = queryParam.getKey().toString();
            String val = queryParam.getValue().toString();
            RequestParamsMap requestParamsMap = new RequestParamsMap();
            requestParamsMap.setKey(queryParam.getKey().toString());
            requestParamsMap.setValueList(Collections.singletonList(queryParam.getValue().toString()));
            paramsMaps.add(requestParamsMap);
            if (key.equalsIgnoreCase(originalPrefix)) {
                autoSuggestRequest.setQueryPrefix(val);
            }
        }

        autoSuggestRequest.setRequestParamsList(paramsMaps);
    }

    private void loadAutoSuggestResponseV4(String payloadId, AutoSuggestResponse autoSuggestResponse, Params params, List<V4SuggestionRow> suggestionRows, List<AutoSuggestDoc>autoSuggestDocs) {
        autoSuggestResponse.setPayloadId(payloadId);
        autoSuggestResponse.setColdStartVersion(0);
        autoSuggestResponse.setCleanQuery(params.getQuery());

        ArrayList<AutoSuggestSuggestion> autoSuggestSuggestions = new ArrayList<>();
        int autoSuggestDocIndex = 0;
        String suggestedQuery = "";
        String autoSuggestDocQuery = "";
        for (V4SuggestionRow v4SuggestionRow : suggestionRows) {
            AutoSuggestSuggestion autoSuggestSuggestion = new AutoSuggestSuggestion();

            V4SuggestionType type = v4SuggestionRow.getType();

            AutoSuggestDoc autoSuggestDoc = autoSuggestDocs.get(autoSuggestDocIndex);
            suggestedQuery = v4SuggestionRow.getContent().getTitle();
            autoSuggestDocQuery = autoSuggestDoc.getCorrectedQuery();
            if (suggestedQuery.equalsIgnoreCase(autoSuggestDocQuery)) {
                autoSuggestDocIndex++;
            } else {
                autoSuggestDocIndex--;
                autoSuggestDoc = autoSuggestDocs.get(autoSuggestDocIndex);
                autoSuggestDocIndex++;
            }

            autoSuggestSuggestion.setSuggestionType(type.toString());

            SuggestionContent suggestionContent = new SuggestionContent();

            setV4SuggestionContent(v4SuggestionRow, suggestionContent);

            // since only query type suggestions are in consonance with that of corresponding auto-suggest doc, for product type suggestion these fields are not valid
            if(type.toString().equalsIgnoreCase(query)) {
                setV4AutoSuggestDocs(autoSuggestDoc, suggestionContent);
            }

            // set the autoSuggestSuggestion class
            autoSuggestSuggestion.setSuggestionContent(suggestionContent);
            // update the list by adding autoSuggestSuggestion entry
            autoSuggestSuggestions.add(autoSuggestSuggestion);
        }
        autoSuggestResponse.setSuggestionsList(autoSuggestSuggestions);
    }

    private void setV4SuggestionContent(V4SuggestionRow suggestionRow, SuggestionContent suggestionContent) {
        if (suggestionRow.getContent() != null) {
            V4Suggestion content = suggestionRow.getContent();
            suggestionContent.setClickUrl(content.getClickUrl());
            suggestionContent.setContentType(content.getContentType().toString());
            suggestionContent.setImageUrl(content.getImageUrl());
            suggestionContent.setListingId(content.getLid());
            suggestionContent.setProductId(content.getPid());
            suggestionContent.setTitle(content.getTitle());

            SuggestionStore store = new SuggestionStore();
            if (content.getStore() != null) {
                V4Store v4Store = content.getStore();
                store.setMarketPlaceId(v4Store.getMarketPlaceId().toString());
                store.setStorePath(v4Store.getStorePath());
                store.setTitle(v4Store.getTitle());
            }
            suggestionContent.setStore(store);
        }
    }

    private void setV4AutoSuggestDocs(AutoSuggestDoc autoSuggestDoc, SuggestionContent suggestionContent) {
        if (autoSuggestDoc.getCtrObj() != null) {
            TransformedCtrObj ctrObj = autoSuggestDoc.getCtrObj();
            suggestionContent.setQueryCTR(ctrObj.getCtr());
            suggestionContent.setQueryDecayedImpressions(ctrObj.getStateHits());
            suggestionContent.setQueryImpressions(Integer.toString(ctrObj.getImpressions()));
            suggestionContent.setQueryPHits(ctrObj.getPHits());
        }
        suggestionContent.setSolrSuggestionScore(autoSuggestDoc.getSolrScore().doubleValue());
        suggestionContent.setQueryWilsonCTR(autoSuggestDoc.getWilsonCTR());
    }

    // Load data for auto-suggest Version 3

    public void publishData(String payloadId, QueryResponse queryResponse,Params params, ProductResponse productResponse, HttpHeaders headers, UriInfo uriInfo) {
        Timer timer = Metrics.newTimer(Ingester.class, "AutoSuggest DG publish timer");
        TimerContext timerContext = timer.time();
        try {
            MultivaluedMap<String, String> header = ((ContainerRequest) headers).getHeaders();
            String requestId = (header.containsKey(xRequestId)) ? header.getFirst(xRequestId) : defaultId;
            Boolean isPerfTest = (header.containsKey(xPerfTest)) ? (Boolean.valueOf(header.getFirst(xPerfTest))) : false;
            AutoSuggestDataV4 autoSuggestData = new AutoSuggestDataV4(payloadId, params, null , queryResponse, productResponse, header, uriInfo, false);
            IngestionObj ingestionObj = new IngestionObj("semantic_autosuggest", autoSuggestData, isPerfTest, MessageType.EVENT, IngestionMode.SPECTER, this, requestId);
            Publisher.INSTANCE.publish(ingestionObj);
        } catch (Exception e) {
            Meter meter = Metrics.newMeter(Ingester.class, "DG-publish-exception", "count", TimeUnit.SECONDS);
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

        autoSuggestResponse.setPayloadId(payloadId);
        autoSuggestResponse.setColdStartVersion(0);
        autoSuggestResponse.setCleanQuery(params.getQuery());

        ArrayList<AutoSuggestSuggestion> autoSuggestSuggestions = new ArrayList<>();

        setQuerySuggestion(querySuggestions, autoSuggestSuggestions, autoSuggestDocs);

        setProductSuggestion (productSuggestions, autoSuggestSuggestions);

        autoSuggestResponse.setSuggestionsList(autoSuggestSuggestions);
    }

    private void setAutoSuggestSuggestionsHavingStore (QuerySuggestion querySuggestion, TransformedCtrObj ctrObj, Double wilsonCtr, Double solrScore, ArrayList<AutoSuggestSuggestion> autoSuggestSuggestions) {
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

            autoSuggestSuggestion.setSuggestionContent(suggestionContent);
            autoSuggestSuggestions.add(autoSuggestSuggestion);
        }
    }

    private void setAutoSuggestSuggestionsHavingNoStore(QuerySuggestion querySuggestion, TransformedCtrObj ctrObj, Double wilsonCtr, Double solrScore, ArrayList<AutoSuggestSuggestion> autoSuggestSuggestions) {
        AutoSuggestSuggestion autoSuggestSuggestion = new AutoSuggestSuggestion();
        autoSuggestSuggestion.setSuggestionType(query);
        SuggestionContent suggestionContent = new SuggestionContent();

        suggestionContent.setTitle(querySuggestion.getQuery());
        suggestionContent.setQueryWilsonCTR(wilsonCtr);
        suggestionContent.setSolrSuggestionScore(solrScore);
        suggestionContent.setQueryPHits(ctrObj.getPHits());
        suggestionContent.setQueryDecayedImpressions(ctrObj.getStateHits());
        suggestionContent.setQueryImpressions(Integer.toString(ctrObj.getImpressions()));
        autoSuggestSuggestion.setSuggestionContent(suggestionContent);
        autoSuggestSuggestions.add(autoSuggestSuggestion);
    }

    private void setQuerySuggestion (List<QuerySuggestion> querySuggestions, ArrayList<AutoSuggestSuggestion> autoSuggestSuggestions, List<AutoSuggestDoc> autoSuggestDocs) {
        int autoSuggestDocIndex = 0;
        for (QuerySuggestion querySuggestion : querySuggestions) {
            AutoSuggestDoc autoSuggestDoc = autoSuggestDocs.get(autoSuggestDocIndex++);

            TransformedCtrObj ctrObj = autoSuggestDoc.getCtrObj();
            Double solrScore = autoSuggestDoc.getSolrScore().doubleValue();
            Double wilsonCtr = autoSuggestDoc.getWilsonCTR();

            if ((querySuggestion.getStores() != null) && (querySuggestion.getStores().size() > 0)) {
                setAutoSuggestSuggestionsHavingStore(querySuggestion, ctrObj, wilsonCtr, solrScore, autoSuggestSuggestions);
            } else {
                setAutoSuggestSuggestionsHavingNoStore(querySuggestion, ctrObj, wilsonCtr, solrScore, autoSuggestSuggestions);
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
