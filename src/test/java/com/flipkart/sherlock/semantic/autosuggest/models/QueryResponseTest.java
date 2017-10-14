package com.flipkart.sherlock.semantic.autosuggest.models;

import com.flipkart.sherlock.semantic.test.utils.ObjectUtils;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dhruv.pancholi on 14/10/17.
 */
public class QueryResponseTest extends BaseModelTest {

    private static final Set<String> QUERY_RESPONSE_FIELDS = ImmutableSet.of("autoSuggestSolrResponse", "querySuggestions");

    @Test
    public void testFields() {
        QueryResponse queryResponse = new QueryResponse(null, null);
        Assert.assertEquals(QUERY_RESPONSE_FIELDS, new HashSet<String>(ObjectUtils.getFieldNames(queryResponse)));
        ObjectUtils.invokeGetters(QUERY_RESPONSE_FIELDS, queryResponse);
    }

}