package com.flipkart.sherlock.semantic.autosuggest.models.v4;

import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import org.junit.Test;

import java.util.ArrayList;

import static com.flipkart.sherlock.semantic.autosuggest.models.v4.V4SuggestionType.QUERY;
import static com.flipkart.sherlock.semantic.autosuggest.models.v4.V4SuggestionType.QUERY_STORE;

/**
 * Created by dhruv.pancholi on 11/12/17.
 */
public class V4AutoSuggestResponseTest {

    @Test
    public void testSerialization() {
        V4AutoSuggestResponse v4AutoSuggestResponse_ = new V4AutoSuggestResponse();
        v4AutoSuggestResponse_.setPayloadId("payloadId");
        v4AutoSuggestResponse_.setColdStartVersion(17);
        v4AutoSuggestResponse_.setSuggestions(new ArrayList<>());
        V4Query v4Query = new V4Query();
        v4Query.setType(QUERY);
        v4Query.setContentType(V4ContentType.RECENT);
        v4Query.setQuery("query");
        v4Query.setClickUrl("clickUrl");
        v4AutoSuggestResponse_.getSuggestions().add(v4Query);

        V4QueryStore v4QueryStore = new V4QueryStore();
        v4QueryStore.setType(QUERY_STORE);
        v4QueryStore.setContentType(V4ContentType.RECENT);
        v4QueryStore.setQuery("query");
        v4QueryStore.setClickUrl("clickUrl");
        v4QueryStore.setStore("store");
        v4QueryStore.setMarketPlaceId(V4MarketPlace.FLIPKART);
        v4AutoSuggestResponse_.getSuggestions().add(v4QueryStore);

        V4AutoSuggestResponse v4AutoSuggestResponse_1 = JsonSeDe.getInstance().readValue(JsonSeDe.getInstance().writeValueAsString(v4AutoSuggestResponse_), V4AutoSuggestResponse.class);
    }
}
