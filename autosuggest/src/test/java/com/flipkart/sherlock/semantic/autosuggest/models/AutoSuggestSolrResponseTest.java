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
public class AutoSuggestSolrResponseTest extends BaseModelTest {

    private static final Set<String> AUTOSUGGEST_SOLR_RESPONSE_FIELDS = ImmutableSet.of("solrQuery", "autoSuggestDocs");

    @Test
    public void testFields() {
        AutoSuggestSolrResponse autoSuggestSolrResponse = new AutoSuggestSolrResponse(null, null);
        Assert.assertEquals(AUTOSUGGEST_SOLR_RESPONSE_FIELDS, new HashSet<>(ObjectUtils.getFieldNames(autoSuggestSolrResponse)));
        ObjectUtils.invokeGetters(AUTOSUGGEST_SOLR_RESPONSE_FIELDS, autoSuggestSolrResponse);
        Assert.assertFalse(ObjectUtils.containsSetters(autoSuggestSolrResponse));
        Assert.assertTrue(ObjectUtils.areAllFieldsPrivate(autoSuggestSolrResponse));
    }
}