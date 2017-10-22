package com.flipkart.sherlock.semantic.autosuggest.outer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.flipkart.kloud.config.DynamicBucket;
import com.flipkart.sherlock.semantic.autosuggest.utils.IOUtils;
import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.flipkart.sherlock.semantic.common.util.FkConfigServiceWrapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.util.Map;

/**
 * Created by dhruv.pancholi on 18/10/17.
 */
public class MockFkConfigServiceWrapper {

    @Mock
    private DynamicBucket dynamicBucket;

    @InjectMocks
    private FkConfigServiceWrapper fkConfigServiceWrapper;

    @Before
    public void setUp() {
        fkConfigServiceWrapper = getFkConfigServiceWrapper();
    }

    @Test
    public void testNotNull() {
        Assert.assertNotNull(fkConfigServiceWrapper);
    }

    public FkConfigServiceWrapper getFkConfigServiceWrapper() {
        MockitoAnnotations.initMocks(this);
        IOUtils ioUtils = IOUtils.openFromResource("sherlock_autosuggest_prod.txt");
        if (ioUtils == null) throw new RuntimeException("Unable to load the config from resource");
        Map<String, Object> configMap = JsonSeDe.getInstance().readValue(ioUtils.readAll(), new TypeReference<Map<String, Object>>() {
        });

        Mockito.when(dynamicBucket.getString(Mockito.anyString())).thenAnswer(answerProvider(configMap));
        Mockito.when(dynamicBucket.getBoolean(Mockito.anyString())).thenAnswer(answerProvider(configMap));
        Mockito.when(dynamicBucket.getInt(Mockito.anyString())).thenAnswer(answerProvider(configMap));
        Mockito.when(dynamicBucket.getDouble(Mockito.anyString())).thenAnswer(answerProvider(configMap));

        return fkConfigServiceWrapper;
    }

    private static <T> Answer<T> answerProvider(Map<String, Object> configMap) {
        return invocationOnMock -> {
            Object[] arguments = invocationOnMock.getArguments();
            String key = (String) arguments[0];
            return (T) configMap.get(key);
        };
    }
}
