package com.flipkart.sherlock.semantic.norm.entity;

import junit.framework.Assert;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by anurag.laddha on 28/11/17.
 */
public class BrandInfoTest {

    @Test
    public void testBrandsInfo(){
        BrandInfo brandInfo1 = new BrandInfo("samsung", "");
        BrandInfo brandInfo2 = new BrandInfo("samsung", null);
        BrandInfo brandInfo3 = new BrandInfo("samsung", null);

        Assert.assertFalse(brandInfo1.equals(brandInfo2));
        Assert.assertTrue(brandInfo2.equals(brandInfo3));


        System.out.println(StringUtils.isNotBlank("   an  "));
        System.out.println(StringUtils.isNotBlank("     "));
        System.out.println(StringUtils.isNotBlank(""));
        System.out.println(StringUtils.isNotBlank(null));
    }
}