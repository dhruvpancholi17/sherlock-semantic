package com.flipkart.sherlock.semantic.autosuggest.models;

import lombok.Getter;

/**
 * Created by dhruv.pancholi on 04/07/17.
 */
@Getter
public class SolrConfig {

    private String solrHost = "localhost";
    private int solrPort = 25280;
    private String solrCore = "autosuggest";

    private String queryField = "text";
    private String prefixField = "prefix_edgytext";
    private String phraseField = "text_edgytext_phrase";
    private int phraseBoost = 1;
    private String boostFunction = "min(div(log(field(impressions_sint)),log(10)),10.0)^1";
    private String sortFunctionString = "score desc,ranking-score_sfloat desc,p-hits_sfloat desc,ctr_sfloat desc";
    private int rows = 10;
    private double ctrThreshold = 0.05;
    private String ctrField = "ctr_float";
    private String fqsString = "";
    private int maxNumberOfStorePerQuery = 3;
    private boolean solrSpellCorrection = true;

    private double wilsonCtrThreshold = 0;
    private double numTokens = 0;
    private double impressionsThreshold = 0;
    private double stateHitsThreshold = 0.0;
}
