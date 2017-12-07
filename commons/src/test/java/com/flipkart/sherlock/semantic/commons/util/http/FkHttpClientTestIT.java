package com.flipkart.sherlock.semantic.commons.util.http;

import com.codahale.metrics.MetricRegistry;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

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
        FkHttpClient fkHttpClient = new FkHttpClient(fkHttpClientConfig, metricRegistry);

        HttpGet httpGet = new HttpGet("http://10.33.205.247:9007/sherlock/stores/search.flipkart.com/autosuggest?q=mob");

        try {
            String responseData = fkHttpClient.getRequest(httpGet, (resp) -> {
                String content = null;
                try {
                    content = EntityUtils.toString(resp.getEntity());
                    System.out.println(content);
                } catch (IOException e) {
                }
                return content;
            });
            Assert.assertTrue(responseData.trim().length() > 0);
        } catch (Exception e) {
            Assert.fail("Get request failed");
        }

        fkHttpClient.close();
    }
}