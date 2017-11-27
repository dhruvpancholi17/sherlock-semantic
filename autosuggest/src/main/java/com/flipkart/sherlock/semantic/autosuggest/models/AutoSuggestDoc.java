package com.flipkart.sherlock.semantic.autosuggest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Created by dhruv.pancholi on 01/06/17.
 */
@Getter
@AllArgsConstructor
public class AutoSuggestDoc {

    public static final String LOGGED_QC_QUERY = "logged-qc-query_sstring";

    public static final String CORRECTED_QUERY = "corrected-query_sstring";
    
    public static final String CTR_OBJ = "ctr-obj_sstring";

    public static final String PRODUCT_OBJECT = "product-obj_sstring";

    public static final String PRODUCT_STORE = "product-store-obj_sstring";

    @JsonProperty(LOGGED_QC_QUERY)
    private final String loggedQuery;

    @JsonProperty(CORRECTED_QUERY)
    private final String correctedQuery;

    @JsonProperty(CTR_OBJ)
    private final TransformedCtrObj ctrObj;

    @JsonProperty(PRODUCT_OBJECT)
    private final List<DecayedProductObj> decayedProductObjs;

    @JsonProperty(PRODUCT_STORE)
    private final List<ProductStore> productStores;
}
