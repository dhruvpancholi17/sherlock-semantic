package com.flipkart.sherlock.semantic.common.util;

import junit.framework.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by anurag.laddha on 24/07/17.
 */

/**
 * Integration test for config service wrapper
 */
public class FkConfigServiceWrapperTestIT {

    @Test
    public void testConfigService(){
        FkConfigServiceWrapper fkConfigServiceWrapper = new FkConfigServiceWrapper("fk-ad-at-userprofile-mapper", true);
        Assert.assertNotNull(fkConfigServiceWrapper.getString("mysql.master.user"));
        Assert.assertNull(fkConfigServiceWrapper.getString("abc"));
    }
}