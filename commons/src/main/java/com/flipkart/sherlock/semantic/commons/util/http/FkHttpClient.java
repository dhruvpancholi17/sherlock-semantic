package com.flipkart.sherlock.semantic.commons.util.http;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import javax.annotation.concurrent.ThreadSafe;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Created by anurag.laddha on 05/12/17.
 */

/**
 * Flipkart wrapper for Apache Http client
 * Please ensure all methods remain threadsafe
 */

@Slf4j
@Singleton
@ThreadSafe
public class FkHttpClient implements Closeable {

    private CloseableHttpClient httpClient;
    private final Object syncObj = new Object();

    public FkHttpClient(FkHttpClientConfig clientConfig, MetricRegistry metricRegistry){

        //Configure timeouts
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(clientConfig.getConnectTimeoutMs())
            .setConnectionRequestTimeout(clientConfig.getConnectionRequestTimeoutMs())
            .setSocketTimeout(clientConfig.getSocketTimeoutMs()).build();

        //Configure connection pooling
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(clientConfig.getTimeToLiveMs(), TimeUnit.MILLISECONDS);
        connManager.setMaxTotal(clientConfig.getMaxConnections());
        connManager.setDefaultMaxPerRoute(clientConfig.getMaxConnectionsPerRoute());

        this.httpClient = HttpClients.custom()
            .setConnectionManager(connManager)
            .setDefaultRequestConfig(requestConfig).build();

        //TODO setup guage for connection pool stats
    }

    public <R> R getRequest(HttpGet httpGet, Function<CloseableHttpResponse, R> process) throws Exception{
        if (httpGet != null){
            CloseableHttpResponse httpResponse = null;
            try {
                httpResponse = this.httpClient.execute(httpGet);
                R output = process.apply(httpResponse);
                //Ensure entity is fully consumed so that underlying connection can be returned back to the pool for reuse
                EntityUtils.consumeQuietly(httpResponse.getEntity());
                return output;
            }
            catch(Exception ex){
                log.error("Error while executing GET request: " + httpGet.getURI().toString(), ex);
                closeHttpResponseQuietly(httpResponse);
                throw ex;
            }
        }
        return null;
    }


    public <R> R postRequest(HttpPost httpPost, Function<CloseableHttpResponse, R> process) throws Exception{
        if (httpPost != null){
            CloseableHttpResponse httpResponse = null;
            try {
                httpResponse = this.httpClient.execute(httpPost);
                R output = process.apply(httpResponse);
                //Ensure entity is fully consumed so that underlying connection can be returned back to the pool for reuse
                EntityUtils.consumeQuietly(httpResponse.getEntity());
                return output;
            }
            catch(Exception ex){
                log.error("Error while executing POST request: " + httpPost.getURI().toString(), ex);
                closeHttpResponseQuietly(httpResponse);
                throw ex;
            }
        }
        return null;
    }


    private void closeHttpResponseQuietly(CloseableHttpResponse httpResponse){
        if (httpResponse != null){
            try {
                httpResponse.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Shuts down http client.
     * Should be closed when application shutsdown
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        if (this.httpClient != null){
            synchronized (this.syncObj) {
                this.httpClient.close();
            }
        }
    }
}
