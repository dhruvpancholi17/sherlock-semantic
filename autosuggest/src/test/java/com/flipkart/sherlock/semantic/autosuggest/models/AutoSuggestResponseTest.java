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
public class AutoSuggestResponseTest extends BaseModelTest {

    private static final Set<String> AUTOSUGGEST_RESPONSE_FIELDS = ImmutableSet.of("payloadId", "querySuggestions", "productSuggestions", "params", "querySolrQuery", "productSolrQuery", "autoSuggestDocs");

    @Test
    public void testFields() {
        AutoSuggestResponse autoSuggestResponse = new AutoSuggestResponse(null, null, null, null, null, null, null);
        Assert.assertEquals(AUTOSUGGEST_RESPONSE_FIELDS, new HashSet<>(ObjectUtils.getFieldNames(autoSuggestResponse)));
        ObjectUtils.invokeGetters(AUTOSUGGEST_RESPONSE_FIELDS, autoSuggestResponse);
        Assert.assertFalse(ObjectUtils.containsSetters(autoSuggestResponse));
        Assert.assertTrue(ObjectUtils.areAllFieldsPrivate(autoSuggestResponse));
    }
}