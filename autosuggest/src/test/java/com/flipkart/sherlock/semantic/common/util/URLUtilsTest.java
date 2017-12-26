package com.flipkart.sherlock.semantic.common.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by dhruv.pancholi on 19/12/17.
 */
public class URLUtilsTest {
    @Test
    public void encodeUrl() throws Exception {
        System.out.println(URLUtils.encodeUrl("http://10.33.161.214:9007/sherlock/stores/search.flipkart.com/autosuggest?products.count=120&products.start=0&q=grafytees%20pr&types=query%2Cproduct"));
        String uri = String.format("/v1/%s/size_recommendation_for_user/", new Object[]{URLUtils.encodeUrl("search.flipkart.com")});
        System.out.println(uri);
    }

}