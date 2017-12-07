package com.flipkart.sherlock.semantic.autosuggest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

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

    public static final String WILSON_CTR_FL = "wilson_ctr:field(wilson-ctr_float)";

    public static final String WILSON_CTR = "wilson_ctr";

    public static final String SOLR_SCORE = "score";

    public static final String STORE_CLASSIFIER = "store-classifier_sstring";

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

    @JsonProperty(SOLR_SCORE)
    private final Float solrScore;

    @JsonProperty(WILSON_CTR)
    private final Double wilsonCTR;

    @JsonProperty(STORE_CLASSIFIER)
    private final Map<String, Double> storeClassifier;

}
