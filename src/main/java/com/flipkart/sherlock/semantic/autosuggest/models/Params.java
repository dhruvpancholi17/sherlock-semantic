package com.flipkart.sherlock.semantic.autosuggest.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

import static com.flipkart.sherlock.semantic.autosuggest.helpers.MarketAnalyzer.DEFAULT_MARKET_PLACE_IDS;

/**
 * Created by dhruv.pancholi on 01/06/17.
 */
@Getter
@Builder
@AllArgsConstructor
public class Params {

    public static final List<String> DEFAULT_COMPLETION_TYPES = Arrays.asList("query", "product");
    public static final List<String> DEFAULT_STORE_NODES = Arrays.asList("search.flipkart.com");

    private boolean debug = false;
    private boolean queryDisabled = false;
    private boolean productDisabled = false;
    private boolean solrSpellCorrection = true;

    private int solrPort = 25280;
    private int phraseBoost = 1;
    private int rows = 28;
    private int maxNumberOfStorePerQuery = 3;

    private int numTokens = 0;
    private int impressionsThreshold = 100;
    private double stateHitsThreshold = 0;

    private double wilsonCtrThreshold = 0;
    private double ctrThreshold = 0.05;


    private String originalQuery = "";
    private String query = "";
    private String store = "search.flipkart.com";
    private String leafNode = "search.flipkart.com";
    private String bucket = "default";
    private String solrHost = "localhost";
    private String solrCore = "autosuggest";
    private String queryField = "text";
    private String prefixField = "prefix_edgytext";
    private String phraseField = "text_edgytext_phrase";
    private String boostFunction = "min(div(log(field(impressions_sint)),log(10)),10.0)^1";
    private String ctrField = "ctr_float";

    private List<String> completionTypes = DEFAULT_COMPLETION_TYPES;
    private List<String> storeNodes = DEFAULT_STORE_NODES;
    private List<String> marketPlaceIds = DEFAULT_MARKET_PLACE_IDS;
    private List<String> sortFunctions = Arrays.asList("score desc", "ranking-score_sfloat desc", "p-hits_sfloat desc", "ctr_sfloat desc");
}
