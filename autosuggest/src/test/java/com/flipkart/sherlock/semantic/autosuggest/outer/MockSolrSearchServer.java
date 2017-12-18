package com.flipkart.sherlock.semantic.autosuggest.outer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.flipkart.sherlock.semantic.autosuggest.utils.IOUtils;
import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.flipkart.sherlock.semantic.core.search.SearchRequest;
import com.flipkart.sherlock.semantic.core.search.SearchRequest.Param;
import com.flipkart.sherlock.semantic.core.search.SearchResponse;
import com.flipkart.sherlock.semantic.core.search.SolrSearchServer;
import com.flipkart.sherlock.semantic.core.search.SpellResponse;
import com.flipkart.sherlock.semantic.core.search.SpellResponse.SpellSuggestion;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static com.flipkart.sherlock.semantic.autosuggest.models.AutoSuggestDoc.*;
import static org.mockito.Matchers.any;

/**
 * Created by dhruv.pancholi on 19/10/17.
 */
public class MockSolrSearchServer {

    private Map<SearchRequest, SearchResponse> requestSearchResponseMap;

    private Map<SearchRequest, SpellResponse> requestSpellResponseMap;

    private static Map<String, Param> paramNameMap = new HashMap<>();

    static {
        Param[] values = Param.values();
        for (Param value : values) {
            paramNameMap.put(value.getParamName(), value);
        }
    }

    private JsonSeDe jsonSeDe = JsonSeDe.getInstance();

    @Mock
    private SolrSearchServer solrSearchServer;

    @Test
    public void simpleTest() {
        SolrSearchServer solrSearchServer = new MockSolrSearchServer().getSolrSearchServer();
    }

    public SolrSearchServer getSolrSearchServer() {
        MockitoAnnotations.initMocks(this);
        updateRequestSearchResponseMap();
        updateRequestSpellResponseMap();
        Mockito.when(solrSearchServer.query(any(), any())).thenAnswer(invocationOnMock -> {
            Object[] arguments = invocationOnMock.getArguments();
            SearchRequest searchRequest = (SearchRequest) arguments[0];
            return requestSearchResponseMap.get(getSanitizedSearchRequest(searchRequest));
        });
        Mockito.when(solrSearchServer.spellQuery(any(), any())).thenAnswer(invocationOnMock -> {
            Object[] arguments = invocationOnMock.getArguments();
            SearchRequest searchRequest = (SearchRequest) arguments[0];
            return requestSpellResponseMap.get(getSanitizedSearchRequest(searchRequest));
        });
        return solrSearchServer;
    }

    private static final String[] DEFAULT_FL = {
            LOGGED_QC_QUERY,CORRECTED_QUERY,CTR_OBJ,PRODUCT_OBJECT,PRODUCT_STORE,WILSON_CTR_FL,SOLR_SCORE};

    private static final String[] DEFAULT_SORT = {
            "ctr_sfloat desc",
            "p-hits_sfloat desc",
            "ranking-score_sfloat desc",
            "score desc"};

    private SearchRequest getSanitizedSearchRequest(SearchRequest searchRequest) {
        Map<Param, ArrayList<String>> requestParams = searchRequest.getRequestParams();
        SearchRequest searchRequest_ = new SearchRequest();
        for (Map.Entry<Param, ArrayList<String>> paramArrayListEntry : requestParams.entrySet()) {
            if (paramArrayListEntry.getKey() == Param.SORT) {
                for (String value : DEFAULT_SORT) {
                    searchRequest_.addParam(paramArrayListEntry.getKey(), value);
                }
            } else if (paramArrayListEntry.getKey() == Param.FL) {
                for (String value : DEFAULT_FL) {
                    searchRequest_.addParam(paramArrayListEntry.getKey(), value);
                }
            } else {
                Collections.sort(paramArrayListEntry.getValue());
                for (String value : paramArrayListEntry.getValue()) {
                    searchRequest_.addParam(paramArrayListEntry.getKey(), value);
                }
            }
        }
        return searchRequest_;
    }

    private void updateRequestSpellResponseMap() {
        requestSpellResponseMap = new LinkedHashMap<>();
        IOUtils ioUtils = IOUtils.openFromResource("solr_spell_docs.txt");
        List<String> lines = ioUtils.readLines();
        for (String line : lines) {
            line = line.replaceAll("\\+", " ");
            String[] split = line.split("\t");
            Map<String, Object> response = jsonSeDe.readValue(split[1], new TypeReference<Map<String, Object>>() {
            });

            List<Object> suggestions = (List<Object>) ((Map<String, Object>) response.get("spellcheck")).get("suggestions");
            String query = (String) suggestions.get(0);
            List<String> suggestion = (List<String>) ((Map<String, Object>) suggestions.get(1)).get("suggestion");
            SpellResponse spellResponse = new SpellResponse(split[0], Arrays.asList(new SpellSuggestion(query, suggestion)));
            SearchRequest searchRequest = getSanitizedSearchRequest(getSearchRequest(split[0]));
            requestSpellResponseMap.put(searchRequest, spellResponse);
        }
    }

    private void updateRequestSearchResponseMap() {
        requestSearchResponseMap = new LinkedHashMap<>();
        IOUtils ioUtils = IOUtils.openFromResource("solr_query_docs.txt");
        List<String> lines = ioUtils.readLines();
        for (String line : lines) {
            line = line.replaceAll("\\+", " ");
            String[] split = line.split("\t");
            Map<String, Object> response = jsonSeDe.readValue(split[1], new TypeReference<Map<String, Object>>() {
            });
            List<Map<String, Object>> docs = (List<Map<String, Object>>) ((Map<String, Object>) response.get("response")).get("docs");
            SearchRequest searchRequest = getSanitizedSearchRequest(getSearchRequest(split[0]));
            SearchResponse searchResponse = new SearchResponse(split[0], docs);
            requestSearchResponseMap.put(searchRequest, searchResponse);
        }
    }

    private static SearchRequest getSearchRequest(String encodedParams) {
        Map<Param, List<String>> searchRequestMap = new HashMap<>();
        SearchRequest searchRequest = new SearchRequest();
        String[] params = encodedParams.split("&");

        for (String encodedParam : params) {
            String[] keyVal = encodedParam.split("=");
            Param param = paramNameMap.get(keyVal[0]);
            if (keyVal[1].contains("type_sstring")) continue;
            if (!searchRequestMap.containsKey(param)) searchRequestMap.put(param, new ArrayList<>());
            searchRequestMap.get(param).add(keyVal[1]);
        }

        for (Map.Entry<Param, List<String>> paramListEntry : searchRequestMap.entrySet()) {
            Collections.sort(paramListEntry.getValue());
            for (String value : paramListEntry.getValue()) {
                searchRequest.addParam(paramListEntry.getKey(), value);
            }
        }

        return searchRequest;
    }
}
