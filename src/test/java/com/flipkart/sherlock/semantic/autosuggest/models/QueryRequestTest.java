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
public class QueryRequestTest extends BaseModelTest {

    private static final Set<String> QUERY_REQUEST_FIELDS = ImmutableSet.of("params", "autoSuggestSolrResponse");

    @Test
    public void testFields() {
        QueryRequest queryRequest = new QueryRequest(null, null);
        Assert.assertEquals(QUERY_REQUEST_FIELDS, new HashSet<>(ObjectUtils.getFieldNames(queryRequest)));
        ObjectUtils.invokeGetters(QUERY_REQUEST_FIELDS, queryRequest);
    }
}
