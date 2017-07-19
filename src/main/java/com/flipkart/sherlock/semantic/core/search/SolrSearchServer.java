package com.flipkart.sherlock.semantic.core.search;

import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.flipkart.sherlock.semantic.common.solr.Core;
import com.flipkart.sherlock.semantic.common.solr.SolrServerProvider;
import com.flipkart.sherlock.semantic.core.search.SearchRequest.Param;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Suggestion;
import org.apache.solr.common.SolrDocumentList;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.Map.Entry;

/**
 * Created by anurag.laddha on 23/05/17.
 */
@Slf4j
@Singleton
public class SolrSearchServer implements ISearchEngine {

    @Inject
    private JsonSeDe jsonSeDe;

    @Inject
    private SolrServerProvider solrServerProvider;

    @Override
    public SearchResponse query(SearchRequest request, Map<SearchParam, String> params){
        if (request != null && params != null && params.containsKey(SearchParam.CORE)){
            SolrQuery solrQuery = getSolrQueryFromSearchReq(request);
            SolrServer solrServer = getSolrServerFromParams(params);
            try{
                QueryResponse queryResponse = solrServer.query(solrQuery);
                return getSearchResponseFromSolrResponse(jsonSeDe.writeValueAsString(solrQuery), queryResponse);
            } catch (Exception e) {
                log.error("Error in fetching response from solr", e);
            }
        }
        return null;
    }


    @Override
    public SpellResponse spellQuery(SearchRequest request, Map<SearchParam, String> params){
        if (request != null && params != null && params.containsKey(SearchParam.CORE)) {
            SolrQuery solrQuery = getSolrQueryFromSearchReq(request);
            String solrQueryString = getSolrQueryString(solrQuery);
            SolrServer solrServer = getSolrServerFromParams(params);

            try {
                QueryResponse queryResponse = solrServer.query(solrQuery);
                return getSpellResponseFromSolrResponse(solrQueryString, queryResponse);
            } catch (Exception e) {
                log.error("Error in fetching response from solr", e);
            }
        }
        return null;
    }

    private SolrServer getSolrServerFromParams(Map<SearchParam, String> params){
        String solrCoreStr = params.get(SearchParam.CORE).trim();
        String experiment = params.containsKey(SearchParam.EXPERIMENT) ? params.get(SearchParam.EXPERIMENT).trim() : "";

        Core solrCoreInfo = null;
        if (params.containsKey(SearchParam.HOST) && params.containsKey(SearchParam.PORT)){
            try {
                solrCoreInfo = new Core(params.get(SearchParam.HOST), Integer.parseInt(params.get(SearchParam.PORT)), solrCoreStr);
            }
            catch (Exception ex){
                log.error("Exception in fetching core details from params: {}", params);
            }
        }
        return solrCoreInfo != null ? solrServerProvider.getSolrServer(solrCoreInfo)
            : solrServerProvider.getSolrServer(solrCoreStr, experiment);
    }

    private SpellResponse getSpellResponseFromSolrResponse(String solrQueryString, QueryResponse queryResponse) {

        if (queryResponse != null && queryResponse.getSpellCheckResponse() != null){
            List<Suggestion> solrSpellSuggestions = queryResponse.getSpellCheckResponse().getSuggestions();
            List<SpellResponse.SpellSuggestion> spellSuggestions = new ArrayList<>();
            solrSpellSuggestions.forEach(s -> spellSuggestions.add(new SpellResponse.SpellSuggestion(s.getToken(),
                s.getAlternatives())));

            return new SpellResponse(solrQueryString, spellSuggestions);
        }

        return null;
    }

    private String getSolrQueryString(SolrQuery solrQuery) {
        String solrQueryString = solrQuery.toString();
        try {
            solrQueryString = URLDecoder.decode(solrQuery.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("Can't decode the solr request: {}", solrQuery.toString());
        }
        return solrQueryString;
    }


    private SearchResponse getSearchResponseFromSolrResponse(String solrQuery, QueryResponse queryResponse) {
        if (queryResponse != null) {
            List<Map<String, Object>> searchResults = new ArrayList<>();
            SolrDocumentList solrDocs = queryResponse.getResults();
            if (solrDocs != null && solrDocs.size() > 0) {
                solrDocs.forEach(currResult -> searchResults.add(currResult.getFieldValueMap()));
            }
            return new SearchResponse(solrQuery, searchResults);
        }
        return null;
    }


    private SolrQuery getSolrQueryFromSearchReq(SearchRequest request) {
        SolrQuery solrQuery = new SolrQuery();
        if (request.getRequestParams().size() > 0) {
            for (Entry<Param, ArrayList<String>> entry : request.getRequestParams().entrySet()) {
                solrQuery.setParam(entry.getKey().getParamName(), entry.getValue().toArray(new String[entry.getValue().size()]));
            }
        }
        return solrQuery;
    }

}
