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
public class ProductResponseTest extends BaseModelTest {

    private static final Set<String> PRODUCT_RESPONSE_FIELDS = ImmutableSet.of("autoSuggestSolrResponse", "productSuggestions");

    @Test
    public void testFields() {
        ProductResponse productResponse = new ProductResponse(null, null);
        Assert.assertEquals(PRODUCT_RESPONSE_FIELDS, new HashSet<>(ObjectUtils.getFieldNames(productResponse)));
        ObjectUtils.invokeGetters(PRODUCT_RESPONSE_FIELDS, productResponse);
    }
}
