package com.flipkart.sherlock.semantic.autosuggest.providers;

import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.flipkart.sherlock.semantic.common.util.FkConfigServiceWrapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

/**
 * Created by dhruv.pancholi on 16/10/17.
 */
public class WrapperProviderTest {

    @Mock
    private FkConfigServiceWrapper fkConfigServiceWrapper;

    private WrapperProvider wrapperProvider;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        wrapperProvider = new WrapperProvider(fkConfigServiceWrapper);
    }

    @Test
    public void configure() throws Exception {
        wrapperProvider.configure();
    }

    @Test
    public void jsonSeDeProvider() throws Exception {
        assertEquals(JsonSeDe.getInstance(), wrapperProvider.jsonSeDeProvider());
    }

    @Test
    public void getFkConfigServiceWrapper() throws Exception {
        assertEquals(fkConfigServiceWrapper, wrapperProvider.getFkConfigServiceWrapper());
    }

}