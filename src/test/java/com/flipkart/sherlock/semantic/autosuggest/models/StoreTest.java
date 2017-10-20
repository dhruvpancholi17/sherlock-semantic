package com.flipkart.sherlock.semantic.autosuggest.models;

import com.flipkart.sherlock.semantic.autosuggest.utils.IOUtils;
import com.flipkart.sherlock.semantic.test.utils.ObjectUtils;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by dhruv.pancholi on 14/10/17.
 */
public class StoreTest extends BaseModelTest {

    private List<Store> stores;

    private static final Set<String> STORE_FIELDS = ImmutableSet.of("store", "title", "marketPlaceId");

    @Override
    public void setup() {
        super.setup();
        List<String> lines = IOUtils.openFromResource("store.txt").readLines();
        stores = new ArrayList<>();
        for (String line : lines) {
            stores.add(jsonSeDe.readValue(line, Store.class));
        }
    }

    @Test
    public void testSerialization() {
        for (Store store : stores) {
            Assert.assertFalse(ObjectUtils.isAnyFieldNull(store));
        }
    }

    @Test
    public void testFields() {
        for (Store store : stores) {
            Assert.assertEquals(STORE_FIELDS, new HashSet<>(ObjectUtils.getFieldNames(store)));
            ObjectUtils.invokeGetters(STORE_FIELDS, store);
            Assert.assertFalse(ObjectUtils.containsSetters(store));
            Assert.assertTrue(ObjectUtils.areAllFieldsPrivate(store));
        }
    }
}
