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
public class QuerySuggestionTest extends BaseModelTest {

    private static final Set<String> QUERY_SUGGESTION_FIELDS = ImmutableSet.of("query", "stores");

    @Test
    public void testFields() {
        QuerySuggestion querySuggestion = new QuerySuggestion(null, null);
        Assert.assertEquals(QUERY_SUGGESTION_FIELDS, new HashSet<String>(ObjectUtils.getFieldNames(querySuggestion)));
        ObjectUtils.invokeGetters(QUERY_SUGGESTION_FIELDS, querySuggestion);
        Assert.assertFalse(ObjectUtils.containsSetters(querySuggestion));
        Assert.assertTrue(ObjectUtils.areAllFieldsPrivate(querySuggestion));
    }
}
