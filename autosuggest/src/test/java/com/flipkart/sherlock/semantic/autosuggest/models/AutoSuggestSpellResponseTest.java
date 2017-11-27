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
public class AutoSuggestSpellResponseTest extends BaseModelTest {

    private static final Set<String> AUTOSUGGEST_SPELL_RESPONSE_FIELDS = ImmutableSet.of("solrQuery", "correctedQuery");

    @Test
    public void testFields() {
        AutoSuggestSpellResponse autoSuggestSpellResponse = new AutoSuggestSpellResponse(null, null);
        Assert.assertEquals(AUTOSUGGEST_SPELL_RESPONSE_FIELDS, new HashSet<>(ObjectUtils.getFieldNames(autoSuggestSpellResponse)));
        ObjectUtils.invokeGetters(AUTOSUGGEST_SPELL_RESPONSE_FIELDS, autoSuggestSpellResponse);
        Assert.assertFalse(ObjectUtils.containsSetters(autoSuggestSpellResponse));
        Assert.assertTrue(ObjectUtils.areAllFieldsPrivate(autoSuggestSpellResponse));
    }
}