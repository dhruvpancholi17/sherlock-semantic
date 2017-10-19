package com.flipkart.sherlock.semantic.autosuggest.views;

import com.flipkart.sherlock.semantic.autosuggest.outer.MockAutoSuggestAppInjector;
import com.flipkart.sherlock.semantic.common.metrics.MetricsManager;
import com.google.inject.Injector;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.Arrays;

import static com.flipkart.sherlock.semantic.app.AutoSuggestApp.getTracedItems;

/**
 * Created by dhruv.pancholi on 19/10/17.
 */
public class AutoSuggestViewTest {

    @Mock
    UriInfo uriInfo;

    @Test
    public void testAutoSuggestApp() throws Exception {
        MockitoAnnotations.initMocks(this);
        MultivaluedMap<String, String> multivaluedMap = new MultivaluedHashMap<>();
        multivaluedMap.put("q", Arrays.asList("m"));
        multivaluedMap.put("types", Arrays.asList("query,product"));
        Mockito.when(uriInfo.getQueryParameters()).thenReturn(multivaluedMap);

        MetricsManager.init(getTracedItems());
        Injector autoSuggestAppInjector = new MockAutoSuggestAppInjector().getAutoSuggestAppInjector();
        AutoSuggestView autoSuggestView = autoSuggestAppInjector.getInstance(AutoSuggestView.class);
        System.out.println(autoSuggestView.pathMethod("search.flipkart.com", uriInfo).getEntity());
    }
}