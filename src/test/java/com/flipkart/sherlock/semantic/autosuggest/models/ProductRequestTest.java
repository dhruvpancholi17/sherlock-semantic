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
public class ProductRequestTest extends BaseModelTest {

    private static final Set<String> PRODUCT_REQUEST_FIELDS = ImmutableSet.of("params", "autoSuggestSolrResponse");

    @Test
    public void testFields() {
        ProductRequest productRequest = new ProductRequest(null, null);
        Assert.assertEquals(PRODUCT_REQUEST_FIELDS, new HashSet<>(ObjectUtils.getFieldNames(productRequest)));
        ObjectUtils.invokeGetters(PRODUCT_REQUEST_FIELDS, productRequest);
    }
}
