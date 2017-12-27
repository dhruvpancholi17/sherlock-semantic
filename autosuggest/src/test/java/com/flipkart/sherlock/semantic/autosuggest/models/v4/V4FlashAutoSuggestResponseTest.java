package com.flipkart.sherlock.semantic.autosuggest.models.v4;

import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.flipkart.sherlock.semantic.v4.V4FlashContentType;
import com.flipkart.sherlock.semantic.v4.V4FlashMarketPlace;
import com.flipkart.sherlock.semantic.v4.cards.V4FlashQuery;
import com.flipkart.sherlock.semantic.v4.cards.V4FlashQueryStore;
import com.flipkart.sherlock.semantic.v4.request.V4FlashAutoSuggestRequest;
import com.flipkart.sherlock.semantic.v4.request.V4SearchBrowseRequest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.flipkart.sherlock.semantic.v4.V4FlashMarketPlace.FLIPKART;
import static com.flipkart.sherlock.semantic.v4.V4FlashMarketPlace.GROCERY;
import static com.flipkart.sherlock.semantic.v4.V4FlashSuggestionType.*;


/**
 * Created by dhruv.pancholi on 11/12/17.
 */
public class V4FlashAutoSuggestResponseTest {

    @Test
    public void testSerialization() {
        V4FlashAutoSuggestResponse v4FlashAutoSuggestResponse_ = new V4FlashAutoSuggestResponse();
        v4FlashAutoSuggestResponse_.setPayloadId("payloadId");
        v4FlashAutoSuggestResponse_.setColdStartVersion(17);
        v4FlashAutoSuggestResponse_.setSuggestions(new ArrayList<>());
        V4FlashQuery v4Query = new V4FlashQuery();
        v4Query.setType(QUERY);
        v4Query.setContentType(V4FlashContentType.RECENT);
        v4Query.setQuery("query");
        v4Query.setClickUrl("clickUrl");
        v4FlashAutoSuggestResponse_.getSuggestions().add(v4Query);

        V4FlashQueryStore v4QueryStore = new V4FlashQueryStore();
        v4QueryStore.setType(QUERY_STORE);
        v4QueryStore.setContentType(V4FlashContentType.RECENT);
        v4QueryStore.setQuery("query");
        v4QueryStore.setClickUrl("clickUrl");
        v4QueryStore.setStore("store");
        v4QueryStore.setMarketPlaceId(V4FlashMarketPlace.FLIPKART);
        v4FlashAutoSuggestResponse_.getSuggestions().add(v4QueryStore);

        V4FlashAutoSuggestResponse v4FlashAutoSuggestResponse_1 = JsonSeDe.getInstance().readValue(JsonSeDe.getInstance().writeValueAsString(v4FlashAutoSuggestResponse_), V4FlashAutoSuggestResponse.class);
        System.out.println(v4FlashAutoSuggestResponse_1);
    }

    @Test
    public void testRequest() {
        V4FlashAutoSuggestRequest v4FlashAutoSuggestRequest = new V4FlashAutoSuggestRequest();
        v4FlashAutoSuggestRequest.setQuery("m");
        v4FlashAutoSuggestRequest.setMarketPlaceId(GROCERY);
        v4FlashAutoSuggestRequest.setUserTimeStamp(System.currentTimeMillis());
        v4FlashAutoSuggestRequest.setContextUri("/search?q=shoes&sid=osp/cil/e1f&as=on&as-show=on&otracker=start&as-pos=1_1_ic_shoes");
        v4FlashAutoSuggestRequest.setContextQuery("shoes");
        v4FlashAutoSuggestRequest.setContextStore("osp/cil/e1f");
        v4FlashAutoSuggestRequest.setTypes(Arrays.asList(QUERY_STORE,
                QUERY,
                PRODUCT,
                RICH,
                CLP,
                PARTITION));

        List<V4SearchBrowseRequest> v4SearchBrowseRequests = new ArrayList<>();
        V4SearchBrowseRequest v4SearchBrowseRequest = new V4SearchBrowseRequest();
        v4SearchBrowseRequest.setQuery("samsung mobiles");
        v4SearchBrowseRequest.setStore("tyy/4io");
        v4SearchBrowseRequest.setUri("/search?q=samsung%20mobiles&otracker=start&as-show=on&as=off");
        v4SearchBrowseRequest.setTimestamp(System.currentTimeMillis() - 10000);
        v4SearchBrowseRequest.setMarketPlaceId(FLIPKART);
        v4SearchBrowseRequests.add(v4SearchBrowseRequest);

        v4SearchBrowseRequest = new V4SearchBrowseRequest();
        v4SearchBrowseRequest.setQuery("chana dal");
        v4SearchBrowseRequest.setStore("73z");
        v4SearchBrowseRequest.setUri("/search?q=urad%20dal&otracker=start&as-show=on&as=off");
        v4SearchBrowseRequest.setTimestamp(System.currentTimeMillis() - 32000);
        v4SearchBrowseRequest.setMarketPlaceId(GROCERY);
        v4SearchBrowseRequests.add(v4SearchBrowseRequest);

        v4SearchBrowseRequest = new V4SearchBrowseRequest();
        v4SearchBrowseRequest.setQuery("water bottle for kids below 12 years");
        v4SearchBrowseRequest.setStore("search.flipkart.com");
        v4SearchBrowseRequest.setUri("/search?q=water%20bottle%20for%20kids%20below%2012%20years&otracker=start&as-show=off&as=off");
        v4SearchBrowseRequest.setTimestamp(System.currentTimeMillis() - 54000);
        v4SearchBrowseRequest.setMarketPlaceId(FLIPKART);
        v4SearchBrowseRequests.add(v4SearchBrowseRequest);

        v4FlashAutoSuggestRequest.setSearchBrowseHistory(v4SearchBrowseRequests);
        System.out.println(JsonSeDe.getInstance().writeValueAsString(v4FlashAutoSuggestRequest));
    }
}
