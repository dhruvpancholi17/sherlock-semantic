package com.flipkart.sherlock.semantic.commons.config;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by anurag.laddha on 10/12/17.
 */

/**
 * Integration test for Fk config service wrapper
 */
public class FkConfigServiceWrapperTestIT {

    @Test
    public void testAutoSugegstConfig(){
        FkConfigServiceWrapper fkConfigServiceWrapper = new FkConfigServiceWrapper("sherlock-autosuggest", true);
        System.out.println(fkConfigServiceWrapper.getInt("mysql.pool.maxIdleTimeSec"));
        Assert.assertTrue(fkConfigServiceWrapper.getInt("mysql.pool.maxIdleTimeSec") > 0);
    }
}