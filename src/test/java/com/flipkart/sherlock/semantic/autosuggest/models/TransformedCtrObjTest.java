package com.flipkart.sherlock.semantic.autosuggest.models;

import com.flipkart.sherlock.semantic.autosuggest.utils.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by dhruv.pancholi on 14/10/17.
 */
public class TransformedCtrObjTest extends BaseModelTest {

    @Test
    public void testSerialization() {
        List<String> lines = IOUtils.openFromResource("transformed_ctr_obj.txt").readLines();
        String reference = lines.get(0);

        TransformedCtrObj referenceTransformedCtrObj = jsonSeDe.readValue(reference, TransformedCtrObj.class);
        Assert.assertEquals(referenceTransformedCtrObj, referenceTransformedCtrObj);

        for (String line : lines) {
            TransformedCtrObj transformedCtrObj = jsonSeDe.readValue(line, TransformedCtrObj.class);
            boolean result = assertEqual(referenceTransformedCtrObj, transformedCtrObj);
            Assert.assertTrue(result);
        }
    }

    // Please don't change the format, it is easier to have breakpoints this way
    private boolean assertEqual(TransformedCtrObj expected, TransformedCtrObj actual) {
        if (expected.getImpressions() - actual.getImpressions() != 0) return false;
        if (Math.abs(expected.getPHits() - actual.getPHits()) > 1) return false;
        if (Math.abs(expected.getStateHits() - actual.getStateHits()) > 1) return false;
        if (Math.abs(expected.getCtr() - actual.getCtr()) > 0.01) return false;
        return true;
    }
}
