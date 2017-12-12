package com.flipkart.sherlock.semantic.autosuggest.dataGovernance;


import com.flipkart.sherlock.semantic.autosuggest.models.Params;
import com.flipkart.sherlock.semantic.autosuggest.models.ProductResponse;
import com.flipkart.sherlock.semantic.autosuggest.models.QueryResponse;
import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.core.MultivaluedMap;

@Getter
@Setter
public class AutoSuggestResponseData {
    private Params params;
    private ProductResponse productResponse;
    private String payLoadId;
    private QueryResponse queryResponse;
    private MultivaluedMap<String, String> header;
    private MultivaluedMap<String, String> queryParam;
    private MultivaluedMap<String, String> pathParam;

    public AutoSuggestResponseData(String payLoadId, Params params, QueryResponse queryResponse, ProductResponse productResponse, MultivaluedMap<String, String> header, MultivaluedMap<String, String> queryParam, MultivaluedMap<String, String> pathParam) {
        this.params = params;
        this.productResponse = productResponse;
        this.queryResponse = queryResponse;
        this.header = header;
        this.payLoadId = payLoadId;
        this.queryParam = queryParam;
        this.pathParam = pathParam;
    }
}
