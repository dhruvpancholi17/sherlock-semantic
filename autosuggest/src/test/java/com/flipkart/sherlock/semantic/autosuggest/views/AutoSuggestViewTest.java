package com.flipkart.sherlock.semantic.autosuggest.views;

import com.flipkart.sherlock.semantic.autosuggest.models.AutoSuggestResponse;
import com.flipkart.sherlock.semantic.autosuggest.outer.MockAutoSuggestAppInjector;
import com.flipkart.sherlock.semantic.autosuggest.utils.IOUtils;
import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.google.inject.Injector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.Collections;
import java.util.List;

/**
 * Created by dhruv.pancholi on 19/10/17.
 */
public class AutoSuggestViewTest {

    private AutoSuggestView autoSuggestView;

    private Injector autoSuggestAppInjector;

    private JsonSeDe jsonSeDe;

    private UriInfo uriInfo;

    @Before
    public void setUp() throws Exception {
        autoSuggestAppInjector = new MockAutoSuggestAppInjector().getAutoSuggestAppInjector();
        autoSuggestView = autoSuggestAppInjector.getInstance(AutoSuggestView.class);
        jsonSeDe = autoSuggestAppInjector.getInstance(JsonSeDe.class);
        uriInfo = Mockito.mock(UriInfo.class);
    }

    @Test
    public void testAnnotations() throws NoSuchMethodException {
        Assert.assertEquals("/", AutoSuggestView.class.getAnnotation(Path.class).value());
        Assert.assertEquals(new String[]{MediaType.TEXT_PLAIN}, AutoSuggestView.class.getMethod("serverRunning").getAnnotation(Produces.class).value());

        Assert.assertEquals("/sherlock/stores/{store : .+}/autosuggest", AutoSuggestView.class.getMethod("pathMethod", String.class, UriInfo.class).getAnnotation(Path.class).value());
        Assert.assertEquals(new String[]{MediaType.APPLICATION_JSON}, AutoSuggestView.class.getMethod("pathMethod", String.class, UriInfo.class).getAnnotation(Produces.class).value());

        Assert.assertEquals("sherlock/cacherefresh", AutoSuggestView.class.getMethod("cacheRefresh", String.class).getAnnotation(Path.class).value());
        Assert.assertEquals(new String[]{MediaType.APPLICATION_JSON}, AutoSuggestView.class.getMethod("cacheRefresh", String.class).getAnnotation(Produces.class).value());

        Assert.assertEquals("sherlock/cacheget", AutoSuggestView.class.getMethod("cacheGet", String.class, String.class).getAnnotation(Path.class).value());
        Assert.assertEquals(new String[]{MediaType.APPLICATION_JSON}, AutoSuggestView.class.getMethod("cacheGet", String.class, String.class).getAnnotation(Produces.class).value());
    }

    @Test
    public void testAPIs() {
        Assert.assertNotNull(autoSuggestView.serverRunning());
        Assert.assertNotNull(autoSuggestView.cacheRefresh(null));
        Assert.assertNotNull(autoSuggestView.cacheGet("AutoSuggestDisabledQueriesDao", ""));
    }

    @Test
    public void testAutoSuggestApp() {

        MockitoAnnotations.initMocks(this);
        List<String> lines = IOUtils.openFromResource("autosuggest.txt").readLines();
        for (String line : lines) {
            String[] split = line.split("\t");

            UriInfo uriInfo = Mockito.mock(UriInfo.class);
            MultivaluedMap<String, String> multivaluedMap = new MultivaluedHashMap<>();
            multivaluedMap.put("q", Collections.singletonList(split[0]));
            multivaluedMap.put("types", Collections.singletonList("query,product"));
            Mockito.when(uriInfo.getQueryParameters()).thenReturn(multivaluedMap);

            Assert.assertTrue(isEquivalent(split[1], String.valueOf(autoSuggestView.pathMethod("search.flipkart.com", uriInfo).getEntity())));
        }
    }

    private boolean isEquivalent(String expected_, String actual_) {
        AutoSuggestResponse expected = jsonSeDe.readValue(expected_, AutoSuggestResponse.class);
        AutoSuggestResponse actual = jsonSeDe.readValue(actual_, AutoSuggestResponse.class);

        if (expected.getPayloadId() == null || expected.getPayloadId().isEmpty()) return false;
        if (expected.getQuerySuggestions() == null || expected.getQuerySuggestions().isEmpty()) return false;
        if (expected.getProductSuggestions() == null || expected.getProductSuggestions().isEmpty()) return false;

        if (actual.getPayloadId() == null || actual.getPayloadId().isEmpty()) return false;
        if (actual.getQuerySuggestions() == null || actual.getQuerySuggestions().isEmpty()) return false;
        if (actual.getProductSuggestions() == null || actual.getProductSuggestions().isEmpty()) return false;

        if (expected.getPayloadId().equals(actual.getPayloadId())) return false;
        if (!expected.getQuerySuggestions().equals(actual.getQuerySuggestions())) return false;
        if (!expected.getProductSuggestions().equals(actual.getProductSuggestions())) return false;

        return true;
    }
}
