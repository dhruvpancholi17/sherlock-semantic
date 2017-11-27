package com.flipkart.sherlock.semantic.autosuggest.models;

import com.flipkart.sherlock.semantic.autosuggest.utils.IOUtils;
import com.flipkart.sherlock.semantic.test.utils.ObjectUtils;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by dhruv.pancholi on 14/10/17.
 */
public class ProductStoreTest extends BaseModelTest {

    private List<ProductStore> productStores;

    private static final Set<String> PRODUCT_STORE_FIELDS = ImmutableSet.of("count", "ddCount", "contrib", "ddContrib", "store");

    @Override
    public void setup() {
        super.setup();
        List<String> lines = IOUtils.openFromResource("product_store.txt").readLines();
        productStores = new ArrayList<>();
        for (String line : lines) {
            productStores.add(jsonSeDe.readValue(line, ProductStore.class));
        }
    }

    @Test
    public void testSerialization() {
        ProductStore referenceProductStore = productStores.get(0);
        for (ProductStore productStore : productStores) {
            Assert.assertFalse(ObjectUtils.isAnyFieldNull(productStore));
            Assert.assertTrue(assertEqual(referenceProductStore, productStore));
        }
    }

    @Test
    public void testFields() {
        for (ProductStore productStore : productStores) {
            ObjectUtils.invokeGetters(PRODUCT_STORE_FIELDS, productStore);
            Assert.assertEquals(PRODUCT_STORE_FIELDS, new HashSet<>(ObjectUtils.getFieldNames(productStore)));
            Assert.assertFalse(ObjectUtils.containsSetters(productStore));
            Assert.assertTrue(ObjectUtils.areAllFieldsPrivate(productStore));
        }
    }

    // Please don't change the format, it is easier to have breakpoints this way
    private boolean assertEqual(ProductStore expected, ProductStore actual) {
        if (expected.getCount() - actual.getCount() != 0) return false;
        if (Math.abs(expected.getDdCount() - actual.getDdCount()) > 1) return false;
        if (Math.abs(expected.getContrib() - actual.getContrib()) > 1) return false;
        if (Math.abs(expected.getDdContrib() - actual.getDdContrib()) > 1) return false;
        if (!StringUtils.equals(expected.getStore(), actual.getStore())) return false;
        return true;
    }

}