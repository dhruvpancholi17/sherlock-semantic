package com.flipkart.sherlock.semantic.autosuggest.models;

import com.flipkart.sherlock.semantic.test.utils.ObjectUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dhruv.pancholi on 20/10/17.
 */
public class ParamsTest {

    @Test
    public void testFields() {
        Params params = Params.builder().build();
        Assert.assertFalse(ObjectUtils.containsSetters(params));
        Assert.assertTrue(ObjectUtils.areAllFieldsPrivate(params));
    }
}