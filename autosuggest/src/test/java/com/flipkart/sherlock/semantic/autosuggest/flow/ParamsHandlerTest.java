package com.flipkart.sherlock.semantic.autosuggest.flow;

import com.flipkart.sherlock.semantic.autosuggest.models.Params;
import com.flipkart.sherlock.semantic.autosuggest.outer.MockAutoSuggestAppInjector;
import com.google.inject.Injector;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by dhruv.pancholi on 20/10/17.
 */
public class ParamsHandlerTest {

    private static final Map<String, Object> KEY_VALUE = new HashMap<>();

    static {
        KEY_VALUE.put("phraseField", "phraseFieldValue");
        KEY_VALUE.put("ctrField", "ctrFieldValue");
        KEY_VALUE.put("rows", 28);
        KEY_VALUE.put("sortFunction", "sortFunctionValue");
        KEY_VALUE.put("boostFunction", "boostFunctionValue");
        KEY_VALUE.put("phraseBoost", 10);
        KEY_VALUE.put("prefixField", "prefixFieldValue");
        KEY_VALUE.put("queryField", "queryFieldValue");
        KEY_VALUE.put("maxNumberOfStorePerQuery", 30);
        KEY_VALUE.put("solrSpellCorrection", false);
        KEY_VALUE.put("types", "abc");
        KEY_VALUE.put("numTokens", 10);
        KEY_VALUE.put("impressionsThreshold", 101);
        KEY_VALUE.put("stateHitsThreshold", 102);
        KEY_VALUE.put("wilsonCtrThreshold", 0.9);
        KEY_VALUE.put("ctrThreshold", 0.09);
        KEY_VALUE.put("minCharsForIncorrectPrefix", 4);
        KEY_VALUE.put("pristinePrefixField", "prefix_edgytext");
    }

    @Test
    public void getParamsTest() throws Exception {
        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        MultivaluedMap<String, String> multivaluedMap = new MultivaluedHashMap<>();
        for (Map.Entry<String, Object> stringObjectEntry : KEY_VALUE.entrySet()) {
            multivaluedMap.put(stringObjectEntry.getKey(), Collections.singletonList(String.valueOf(stringObjectEntry.getValue())));
        }
        Mockito.when(uriInfo.getQueryParameters()).thenReturn(multivaluedMap);
        Injector autoSuggestAppInjector = new MockAutoSuggestAppInjector().getAutoSuggestAppInjector();
        ParamsHandler paramsHandler = autoSuggestAppInjector.getInstance(ParamsHandler.class);
        Params params = paramsHandler.getParams("search.flipkart.com", uriInfo);
        assertEquals("phraseFieldValue", params.getPhraseField());
        assertEquals("ctrFieldValue", params.getCtrField());
        assertEquals(28, params.getRows());
        assertEquals(Collections.singletonList("sortFunctionValue"), params.getSortFunctions());
        assertEquals("boostFunctionValue", params.getBoostFunction());
        assertEquals(10, params.getPhraseBoost());
        assertEquals("prefixFieldValue", params.getPrefixField());
        assertEquals("queryFieldValue", params.getQueryField());
        assertEquals(30, params.getMaxNumberOfStorePerQuery());
        assertEquals(false, params.isSolrSpellCorrection());
        assertEquals(Collections.singletonList("abc"), params.getCompletionTypes());
        assertEquals(10, params.getNumTokens());
        assertEquals(101, params.getImpressionsThreshold());
        assertEquals(102, params.getStateHitsThreshold(), 0);
        assertEquals(0.9, params.getWilsonCtrThreshold(), 0.01);
        assertEquals(0.09, params.getCtrThreshold(), 0.01);
        assertEquals(4, params.getMinCharsForIncorrectPrefix());
        assertEquals("prefix_edgytext", params.getPristinePrefixField());
    }
}
