package com.flipkart.sherlock.semantic.commons.cache.couchbase;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by anurag.laddha on 19/12/17.
 */

/**
 * Integration test for Fk couchbase client
 * File name must end in IT
 */
public class FkCouchbaseClientTestIT {

    @Test
    public void testCouchbaseClient() throws Exception {
        CouchbaseConfig couchbaseConfig = new CouchbaseConfig.CouchbaseConfigBuilder(Sets.newHashSet("http://127.0.0.1:8091/pools"),
            "default", "", 5).build();
        FkCouchbaseClient<String, List<String>> couchbaseClient = new FkCouchbaseClient<>(couchbaseConfig);

        //Set key in couchbase, access the same to validate its not null
        List<String> strList = Lists.newArrayList("str1", "str2");
        couchbaseClient.put("abc", strList);
        Assert.assertNotNull(couchbaseClient.get("abc"));
        Assert.assertEquals(strList, couchbaseClient.get("abc"));
        System.out.println(couchbaseClient.get("abc"));

        //Sleep till key expiry and evaluate for data not present in couchbase
        Thread.sleep(6000);
        Assert.assertNull(couchbaseClient.get("abc"));
        couchbaseClient.shutdown();
    }
}