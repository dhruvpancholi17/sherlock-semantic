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
public class DecayedProductObjTest extends BaseModelTest {

    private List<DecayedProductObj> decayedProductObjs;

    private static final Set<String> DECAYED_PRODUCT_OBJECT_FIELDS = ImmutableSet.of("ddContrib", "contrib", "productId", "count", "ddCount", "leafPaths", "store");

    @Override
    public void setup() {
        super.setup();
        List<String> lines = IOUtils.openFromResource("decayed_product_object.txt").readLines();
        decayedProductObjs = new ArrayList<>();
        for (String line : lines) {
            decayedProductObjs.add(jsonSeDe.readValue(line, DecayedProductObj.class));
        }
    }

    @Test
    public void testSerialization() {
        DecayedProductObj referenceDecayedProductObj = decayedProductObjs.get(0);
        for (DecayedProductObj decayedProductObj : decayedProductObjs) {
            Assert.assertFalse(ObjectUtils.isAnyFieldNull(decayedProductObj));
            Assert.assertTrue(assertEqual(referenceDecayedProductObj, decayedProductObj));
        }
    }

    @Test
    public void testFields() {
        for (DecayedProductObj decayedProductObj : decayedProductObjs) {
            ObjectUtils.invokeGetters(DECAYED_PRODUCT_OBJECT_FIELDS, decayedProductObj);
            Assert.assertEquals(DECAYED_PRODUCT_OBJECT_FIELDS, new HashSet<>(ObjectUtils.getFieldNames(decayedProductObj)));
        }
    }

    // Please don't change the format, it is easier to have breakpoints this way
    private boolean assertEqual(DecayedProductObj expected, DecayedProductObj actual) {
        if (expected.getCount() - actual.getCount() != 0) return false;
        if (Math.abs(expected.getDdCount() - actual.getDdCount()) > 1) return false;
        if (Math.abs(expected.getContrib() - actual.getContrib()) > 1) return false;
        if (Math.abs(expected.getDdContrib() - actual.getDdContrib()) > 1) return false;
        if (!StringUtils.equals(expected.getStore(), actual.getStore())) return false;
        if (!StringUtils.equals(expected.getProductId(), actual.getProductId())) return false;
        if (!new HashSet<>(expected.getLeafPaths()).equals(new HashSet<>(actual.getLeafPaths()))) return false;
        return true;
    }
}