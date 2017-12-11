package com.flipkart.sherlock.semantic.autosuggest.helpers;

import com.flipkart.sherlock.semantic.mocks.autosuggest.helpers.MockMarketAnalyzer;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.flipkart.sherlock.semantic.autosuggest.helpers.MarketAnalyzer.FLIP_KART;
import static com.flipkart.sherlock.semantic.autosuggest.helpers.MarketAnalyzer.FLIP_MART;
import static org.junit.Assert.assertEquals;

/**
 * Created by dhruv.pancholi on 16/10/17.
 */
public class MarketAnalyzerTest {


    private MarketAnalyzer marketAnalyzer;

    @Before
    public void setUp() {
        marketAnalyzer = new MockMarketAnalyzer().getMarketAnalyzer();
    }

    @Test
    public void getMarketPlaceIds() throws Exception {
        Assert.assertEquals(ImmutableSet.of(FLIP_MART), ImmutableSet.copyOf(marketAnalyzer.getMarketPlaceIds(null, "true", "GROCERY")));
        Assert.assertEquals(ImmutableSet.of(FLIP_MART), ImmutableSet.copyOf(marketAnalyzer.getMarketPlaceIds(null, "true", "grocery")));

        Assert.assertEquals(ImmutableSet.of(FLIP_KART, FLIP_MART), ImmutableSet.copyOf(marketAnalyzer.getMarketPlaceIds(null, "false", "GROCERY")));
        Assert.assertEquals(ImmutableSet.of(FLIP_KART, FLIP_MART), ImmutableSet.copyOf(marketAnalyzer.getMarketPlaceIds(null, "false", "grocery")));

        Assert.assertEquals(ImmutableSet.of(FLIP_MART), ImmutableSet.copyOf(marketAnalyzer.getMarketPlaceIds(null, "true", "FLIPKART")));
        Assert.assertEquals(ImmutableSet.of(FLIP_MART), ImmutableSet.copyOf(marketAnalyzer.getMarketPlaceIds(null, "true", "flipkart")));

        Assert.assertEquals(ImmutableSet.of(FLIP_KART), ImmutableSet.copyOf(marketAnalyzer.getMarketPlaceIds(null, "false", "FLIPKART")));
        Assert.assertEquals(ImmutableSet.of(FLIP_KART), ImmutableSet.copyOf(marketAnalyzer.getMarketPlaceIds(null, "false", "flipkart")));

        Assert.assertEquals(ImmutableSet.of(FLIP_KART, FLIP_MART), ImmutableSet.copyOf(marketAnalyzer.getMarketPlaceIds(null, null, "GROCERY")));
        Assert.assertEquals(ImmutableSet.of(FLIP_KART, FLIP_MART), ImmutableSet.copyOf(marketAnalyzer.getMarketPlaceIds(null, null, "grocery")));

        Assert.assertEquals(ImmutableSet.of(FLIP_KART), ImmutableSet.copyOf(marketAnalyzer.getMarketPlaceIds(null, null, "FLIPKART")));
        Assert.assertEquals(ImmutableSet.of(FLIP_KART), ImmutableSet.copyOf(marketAnalyzer.getMarketPlaceIds(null, null, "flipkart")));

        Assert.assertEquals(ImmutableSet.of(FLIP_KART), ImmutableSet.copyOf(marketAnalyzer.getMarketPlaceIds(null, null, "")));
        Assert.assertEquals(ImmutableSet.of(FLIP_KART), ImmutableSet.copyOf(marketAnalyzer.getMarketPlaceIds(null, "", null)));
        Assert.assertEquals(ImmutableSet.of(FLIP_KART), ImmutableSet.copyOf(marketAnalyzer.getMarketPlaceIds(null, "", "")));
        Assert.assertEquals(ImmutableSet.of(FLIP_KART), ImmutableSet.copyOf(marketAnalyzer.getMarketPlaceIds(null, null, null)));

        Assert.assertEquals(ImmutableSet.of(FLIP_MART), ImmutableSet.copyOf(marketAnalyzer.getMarketPlaceIds(null, "true", null)));
        Assert.assertEquals(ImmutableSet.of(FLIP_KART), ImmutableSet.copyOf(marketAnalyzer.getMarketPlaceIds(null, "false", null)));
        Assert.assertEquals(ImmutableSet.of(FLIP_MART), ImmutableSet.copyOf(marketAnalyzer.getMarketPlaceIds(null, "true", "")));
        Assert.assertEquals(ImmutableSet.of(FLIP_KART), ImmutableSet.copyOf(marketAnalyzer.getMarketPlaceIds(null, "false", "")));
    }

    @Test
    public void getMarketPlaceId() throws Exception {
        assertEquals(FLIP_KART, marketAnalyzer.getMarketPlaceId(null));
        assertEquals(FLIP_KART, marketAnalyzer.getMarketPlaceId(""));
        assertEquals(FLIP_MART, marketAnalyzer.getMarketPlaceId("73z"));
        assertEquals(FLIP_MART, marketAnalyzer.getMarketPlaceId("73z/xyz"));
        assertEquals(FLIP_KART, marketAnalyzer.getMarketPlaceId("tyy"));
        assertEquals(FLIP_KART, marketAnalyzer.getMarketPlaceId("tyy/4io"));
    }

    @Test
    public void removeProducts() throws Exception {

    }

}