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
public class ProductSuggestionTest extends BaseModelTest {

    private static final Set<String> PRODUCT_SUGGESTION_FIELDS = ImmutableSet.of("id");

    @Test
    public void testFields() {
        ProductSuggestion productSuggestion = new ProductSuggestion(null);
        Assert.assertEquals(PRODUCT_SUGGESTION_FIELDS, new HashSet<>(ObjectUtils.getFieldNames(productSuggestion)));
        ObjectUtils.invokeGetters(PRODUCT_SUGGESTION_FIELDS, productSuggestion);
        Assert.assertFalse(ObjectUtils.containsSetters(productSuggestion));
        Assert.assertTrue(ObjectUtils.areAllFieldsPrivate(productSuggestion));
    }
}