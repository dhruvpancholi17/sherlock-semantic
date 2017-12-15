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
public class AutoSuggestDocTest extends BaseModelTest {

    private static final Set<String> AUTOSUGGEST_DOC_FIELDS = ImmutableSet.of("loggedQuery", "correctedQuery", "ctrObj", "decayedProductObjs", "productStores", "solrScore", "wilsonCTR");

    @Test
    public void testFields() {
        AutoSuggestDoc autoSuggestDoc = new AutoSuggestDoc(null, null, null, null, null, null, null);
        Assert.assertEquals(AUTOSUGGEST_DOC_FIELDS, new HashSet<>(ObjectUtils.getFieldNames(autoSuggestDoc)));
        ObjectUtils.invokeGetters(AUTOSUGGEST_DOC_FIELDS, autoSuggestDoc);

        Assert.assertFalse(ObjectUtils.containsSetters(autoSuggestDoc));
        Assert.assertTrue(ObjectUtils.areAllFieldsPrivate(autoSuggestDoc));
    }
}