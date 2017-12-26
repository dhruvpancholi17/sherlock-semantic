package com.flipkart.sherlock.semantic.commons.hystrix;

import com.flipkart.sherlock.semantic.commons.CommonITContext;
import com.flipkart.sherlock.semantic.commons.cache.couchbase.CouchbaseConfig;
import com.flipkart.sherlock.semantic.commons.cache.couchbase.FkCouchbaseClient;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by anurag.laddha on 19/12/17.
 */

/**
 * Integration test for Hystrix executor
 * File name must end in IT
 */
public class HystrixExecutorTestIT {

    @Test
    public void testHystrixExecutor(){
        HystrixExecutor hystrixExecutor = CommonITContext.getInstance(HystrixExecutor.class);
        CouchbaseConfig couchbaseConfig = new CouchbaseConfig.CouchbaseConfigBuilder(Sets.newHashSet("http://127.0.0.1:8091/pools"),
            "default", "", 5).build();
        FkCouchbaseClient<String, List<String>> couchbaseClient = new FkCouchbaseClient<>(couchbaseConfig);

        //Set value for some key in couchbase
        List<String> strList = Lists.newArrayList("str1", "str2");
        hystrixExecutor.executeSync("searchEngine.search", () -> {
            couchbaseClient.put("abc", strList);
            return null;
        });

        //Retrieve that value from couchbase
        List<String> cbValue = hystrixExecutor.executeSync("searchEngine.search", () -> couchbaseClient.get("abc"));
        Assert.assertNotNull(cbValue);
        Assert.assertEquals(strList, cbValue);
        System.out.println(cbValue);

        couchbaseClient.shutdown();
    }

}