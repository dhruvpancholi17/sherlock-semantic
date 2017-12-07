package com.flipkart.sherlock.semantic.commons.util.http;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Created by anurag.laddha on 07/12/17.
 */


/**
 * Integration test for Http client
 * Class name must end in IT
 */
public class FkHttpClientTestIT {

    @Test
    public void testHttpClient() throws Exception{
        FkHttpClientConfig fkHttpClientConfig = new FkHttpClientConfig.FkHttpClientConfigBuilder()
            .setConnectTimeoutMs(1000)
            .setConnectionRequestTimeoutMs(1000)
            .setSocketTimeoutMs(2000)
            .setMaxConnections(50)
            .setMaxConnectionsPerRoute(5)
            .build();

        MetricRegistry metricRegistry = new MetricRegistry();
        ObjectMapper objectMapper = new ObjectMapper();
        FkHttpClient fkHttpClient = new FkHttpClient(fkHttpClientConfig, objectMapper, metricRegistry);

        HttpGet httpGet = new HttpGet("http://10.33.205.247:9007/sherlock/stores/search.flipkart.com/autosuggest?q=mob");

        Map<String, Object> content = fkHttpClient.executeGetRequest(httpGet, new TypeReference<Map<String, Object>>() {});
        System.out.println("Content received: ");
        System.out.println(content);
        Assert.assertNotNull(content);

        fkHttpClient.close();
    }
}