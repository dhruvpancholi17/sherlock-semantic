package com.flipkart.sherlock.semantic.commons.cache.couchbase;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.CouchbaseConnectionFactory;
import com.couchbase.client.CouchbaseConnectionFactoryBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.flipkart.sherlock.semantic.commons.cache.ICache;
import com.flipkart.sherlock.semantic.commons.util.SerDeUtils;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.concurrent.ThreadSafe;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by anurag.laddha on 19/12/17.
 */

/**
 * Couchbase client
 * Wrapper is useful for following: key hashing, ttl validations, serialisation/deserialisation, etc
 */
@Slf4j
@ThreadSafe
public class FkCouchbaseClient<K,V> implements ICache<K,V> {

    private static final HashFunction HASH_FUNCTION = Hashing.md5();      //configurable reqd?
    private static final Charset utfCharset = Charset.forName("UTF-8");

    private final TypeReference<V> typeReference;
    private CouchbaseClient couchbaseClient;
    private int defaultTTLSec;
    private boolean hashKey = false;

    public FkCouchbaseClient(CouchbaseConfig couchbaseConfig){
        try {
            this.typeReference = new TypeReference<V>() {};
            this.defaultTTLSec = couchbaseConfig.getTimeToLiveSec();
            this.hashKey = couchbaseConfig.isHashKey();
            List<URI> cbURIs = getURIs(couchbaseConfig.getHostsUri());
            if (couchbaseConfig.getOperationTimeoutMs() > 0) {
                CouchbaseConnectionFactoryBuilder builder = new CouchbaseConnectionFactoryBuilder();
                builder.setOpTimeout(couchbaseConfig.getOperationTimeoutMs());
                CouchbaseConnectionFactory connectionFactory = builder.buildCouchbaseConnection(
                    cbURIs, couchbaseConfig.getBucket(), couchbaseConfig.getPassword());
                this.couchbaseClient = new CouchbaseClient(connectionFactory);
            }
            else {
                this.couchbaseClient = new CouchbaseClient(cbURIs, couchbaseConfig.getBucket(), couchbaseConfig.getPassword());
            }
        }
        catch(Exception ex){
            log.error("Error while initialising couchbase client", ex);
            throw new RuntimeException("Error while initialising couchbase client", ex);
        }
    }


    @Override
    public V get(K key) {
        String keyStr = getKeyStr(key);
        try {
            Object value = this.couchbaseClient.get(keyStr);
            if (value != null) {
                log.debug("Retrived from couchbase. Key: {}, value: {}", key, value);
                return SerDeUtils.castToGeneric(value.toString(), this.typeReference);
            }
        }
        catch (Exception ex){
            log.error("Error while fetching key:{} from couchbase", keyStr, ex);
        }
        return null;
    }


    @Override
    public void put(K key, V value) {
        put(key, value, -1);
    }


    @Override
    public void put(K key, V value, int timeToLiveSec) {
        String keyStr = getKeyStr(key);
        int ttl = getTTL(this.defaultTTLSec, timeToLiveSec);
        try {
            String valueStr =  SerDeUtils.valueAsString(value);
            log.debug("Adding to couchbase. Key: {}, value: {}", key, value);
            this.couchbaseClient.set(keyStr, ttl, valueStr);
        }
        catch (Exception ex){
            log.error("Error while setting key:{} in couchbase", keyStr, ex);
        }
    }


    @Override
    public Map<K, V> getBulk(Iterable<K> keys) {

        if (keys != null){
            List<String> keyList = new ArrayList<>();
            try {
                keys.forEach(k -> keyList.add(getKeyStr(k)));

                Map<String, Object> strKeyToStrValMap = this.couchbaseClient.getBulk(keyList);
                if (strKeyToStrValMap != null && strKeyToStrValMap.size() > 0) {
                    Map<String, V> strKeyToTypedValMap = new HashMap<>();

                    //Cast value to desired type
                    for (Map.Entry<String, Object> entry : strKeyToStrValMap.entrySet()) {
                        strKeyToTypedValMap.put(entry.getKey(),
                            SerDeUtils.castToGeneric(entry.getValue().toString(), this.typeReference));
                    }

                    Map<K, V> typedKeyToTypedValMap = new HashMap<>();
                    keys.forEach(key -> typedKeyToTypedValMap.put(key, strKeyToTypedValMap.get(getKeyStr(key))));
                    return typedKeyToTypedValMap;
                }
            }
            catch(Exception ex){
                log.error("Error while fetching values in bulk for keys: {}", keyList, ex);

            }
        }
        return null;
    }

    @Override
    public boolean remove(K key) {
        if (key != null){
            try {
                log.debug("Removing key: {} from couchbase", key);
                this.couchbaseClient.delete(getKeyStr(key));
                return true;
            }
            catch(Exception ex){
                log.error("Error while removing key: {}", getKeyStr(key), ex);
            }
        }
        return false;
    }

    @Override
    public void shutdown(){
        this.couchbaseClient.shutdown();
    }


    private int getTTL(int defaultTTLSec, int perKeyTTLSec){
        //Ensure per key TTL is not too high (30 days)
        return (perKeyTTLSec > 0 && perKeyTTLSec < TimeUnit.DAYS.toSeconds(30)) ?
            perKeyTTLSec :
            defaultTTLSec;
    }


    private String getKeyStr(K key){
        if (this.hashKey){
            return HASH_FUNCTION.hashString(key.toString(), utfCharset).toString();
        }
        else{
            return key.toString();
        }
    }


    private List<URI> getURIs(Set<String> hostUris) throws Exception {
        if (hostUris != null) {
            return hostUris.stream().map(URI::create).collect(Collectors.toList());
        }
        return null;
    }

}
