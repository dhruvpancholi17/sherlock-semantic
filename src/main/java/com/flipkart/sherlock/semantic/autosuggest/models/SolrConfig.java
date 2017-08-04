package com.flipkart.sherlock.semantic.autosuggest.models;

import lombok.Data;

/**
 * Created by dhruv.pancholi on 04/07/17.
 */
@Data
public class SolrConfig {
    private int rows = 10;
    private int numTokens = 0;
    private int solrPort = 25280;
    private int phraseBoost = 1;
    private int impressionsThreshold = 0;
    private int maxNumberOfStorePerQuery = 3;

    private boolean solrSpellCorrection = true;

    private double ctrThreshold = 0.05;
    private double wilsonCtrThreshold = 0;
    private double stateHitsThreshold = 0.0;

    private String solrHost = "localhost";
    private String ctrField = "ctr_float";
    private String fqsString = "";
    private String solrCore = "autosuggest";
    private String queryField = "text";
    private String prefixField = "prefix_edgytext";
    private String phraseField = "text_edgytext_phrase";
    private String boostFunction = "min(div(log(field(impressions_sint)),log(10)),10.0)^1";
    private String sortFunctionString = "score desc,ranking-score_sfloat desc,p-hits_sfloat desc,ctr_sfloat desc";
}
