package com.flipkart.sherlock.semantic.cacherefresh;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.flipkart.irene.kamikaze.couchbase.CouchbaseCache;
import com.flipkart.irene.kamikaze.couchbase.CouchbaseConfig;
import com.flipkart.irene.kamikaze.couchbase.CouchbaseProperties;



/**
 * Created by anurag.laddha on 19/09/17.
 */
public class CouchbaseCacheProvider {

    private static CouchbaseCache instance = null;

    private static final Set<String> hosts = new HashSet<>(Arrays.asList("http://10.32.125.96:8091/pools",
        "http://10.33.37.185:8091/pools",
        "http://10.33.249.65:8091/pools",
        "http://10.32.209.165:8091/pools",
        "http://10.32.169.2:8091/pools",
        "http://10.32.237.233:8091/pools",
        "http://10.34.117.145:8091/pools",
        "http://10.34.77.173:8091/pools",
        "http://10.32.161.127:8091/pools",
        "http://10.34.129.178:8091/pools",
        "http://10.34.205.162:8091/pools",
        "http://10.33.97.245:8091/pools",
        "http://10.33.105.70:8091/pools",
        "http://10.33.123.109:8091/pools",
        "http://10.32.105.248:8091/pools",
        "http://10.32.13.19:8091/pools",
        "http://10.33.233.235:8091/pools",
        "http://10.34.45.194:8091/pools",
        "http://10.33.133.27:8091/pools",
        "http://10.33.93.160:8091/pools"));
    private static final String BUCKET = "semantic-service";
    private static final String PASSWORD = "";
    private static final int optTimeOut = 1000;

    public static CouchbaseCache getInstance() throws IOException {
        if (instance == null) {
            CouchbaseConfig couchbaseConfig = new CouchbaseConfig(hosts, BUCKET, PASSWORD);
            CouchbaseProperties couchbaseProperties = new CouchbaseProperties(optTimeOut);
            instance = new CouchbaseCache(couchbaseConfig, couchbaseProperties);
            return instance;
        }
        return instance;
    }
}
