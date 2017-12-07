package com.flipkart.sherlock.semantic.commons.util.http;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
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

/**
 * Created by anurag.laddha on 05/12/17.
 */

/**
 * Flipkart wrapper for Apache Http client
 * Ensures resource clean up after executing http requests
 * Please ensure all methods remain threadsafe
 */

@Slf4j
@Singleton
@ThreadSafe
public class FkHttpClient implements Closeable {

    private CloseableHttpClient httpClient;
    private ObjectMapper objectMapper;
    private final Object syncObj = new Object();

    public FkHttpClient(FkHttpClientConfig clientConfig, ObjectMapper objectMapper, MetricRegistry metricRegistry){

        this.objectMapper = objectMapper;

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

    /**
     * Executes given GET request, and casts response to given type
     * @return: returns null in case of error
     * @throws Exception
     */
    public <T> T executeGetRequest(HttpGet httpGet, TypeReference<T> typeReference) throws Exception{
        if (httpGet != null && typeReference != null){
            T returnValue = null;
            CloseableHttpResponse httpResponse = null;
            try {
                httpResponse = this.httpClient.execute(httpGet);
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                    returnValue = this.objectMapper.readValue(httpResponse.getEntity().getContent(), typeReference);
                }
                //Ensure entity is fully consumed so that underlying connection can be returned back to the pool for reuse
                EntityUtils.consumeQuietly(httpResponse.getEntity());
                return returnValue;
            }
            catch(Exception ex){
                log.error("Error while executing GET request: " + httpGet.getURI().toString(), ex);
                closeHttpResponseQuietly(httpResponse);
                throw ex;
            }
        }
        return null;
    }

    /**
     * Executes given POST request and casts response to given type
     * @return: returns null in case of error
     * @throws Exception
     */
    public <T> T executePostRequest(HttpPost httpPost, TypeReference<T> typeReference) throws Exception{
        if (httpPost != null && typeReference != null){
            T returnValue = null;
            CloseableHttpResponse httpResponse = null;
            try {
                httpResponse = this.httpClient.execute(httpPost);
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                    returnValue = this.objectMapper.readValue(httpResponse.getEntity().getContent(), typeReference);
                }
                //Ensure entity is fully consumed so that underlying connection can be returned back to the pool for reuse
                EntityUtils.consumeQuietly(httpResponse.getEntity());
                return returnValue;
            }
            catch(Exception ex){
                log.error("Error while executing GET request: " + httpPost.getURI().toString(), ex);
                closeHttpResponseQuietly(httpResponse);
                throw ex;
            }
        }
        return null;
    }


    /**
     * Executes given POST request and casts response to given type
     * @return: status code
     * @throws Exception
     */
    public int executePostRequestNoResponse(HttpPost httpPost) throws Exception{
        if (httpPost != null){
            CloseableHttpResponse httpResponse = null;
            try {
                httpResponse = this.httpClient.execute(httpPost);
                int statusCode = httpResponse.getStatusLine().getStatusCode();;
                EntityUtils.consumeQuietly(httpResponse.getEntity());
                return statusCode;
            }
            catch(Exception ex){
                log.error("Error while executing POST request: " + httpPost.getURI().toString(), ex);
                closeHttpResponseQuietly(httpResponse);
                throw ex;
            }
        }
        return HttpStatus.SC_BAD_REQUEST;
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
