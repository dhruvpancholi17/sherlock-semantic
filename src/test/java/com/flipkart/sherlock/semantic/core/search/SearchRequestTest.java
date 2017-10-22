package com.flipkart.sherlock.semantic.core.search;

import com.flipkart.sherlock.semantic.autosuggest.utils.IOUtils;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bharat.thakarar on 31/05/17.
 */
public class SearchRequestTest {

    @Test
    public void getRequestParams() throws Exception {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.addParam(SearchRequest.Param.FQ, "impressionsPerNumInputs:[9 TO *] OR impressions:[50 TO *]");
        searchRequest.addParam(SearchRequest.Param.FQ, "ctr:[0.03 TO *]");
        searchRequest.addParam(SearchRequest.Param.FQ, "query:samsung");
        Map<SearchRequest.Param, ArrayList<String>> reqParams = searchRequest.getRequestParams();
        System.out.println("reqParams = " + reqParams);
    }

    private static Map<String, SearchRequest.Param> paramNameMap = new HashMap<>();

    static {
        SearchRequest.Param[] values = SearchRequest.Param.values();
        for (SearchRequest.Param value : values) {
            paramNameMap.put(value.getParamName(), value);
        }
    }

    @Test
    public void testEquals() {
        List<String> lines = IOUtils.openFromResource("solr_query_docs.txt").readLines();
        SearchRequest searchRequest1 = getSearchRequest(lines.get(0).split("\t")[0]);
        SearchRequest searchRequest2 = getSearchRequest(lines.get(0).split("\t")[0]);
        Assert.assertEquals(searchRequest1, searchRequest2);

        //        for (String line : lines) {
//            String[] split = line.split("\t");
//            SearchRequest searchRequest = getSearchRequest(split[0]);
//        }
    }

    private static SearchRequest getSearchRequest(String encodedParams) {
        SearchRequest searchRequest = new SearchRequest();
        String[] params = encodedParams.split("&");
        for (String param : params) {
            String[] keyVal = param.split("=");
            searchRequest.addParam(paramNameMap.get(keyVal[0]), keyVal[1]);
        }
        return searchRequest;
    }
}