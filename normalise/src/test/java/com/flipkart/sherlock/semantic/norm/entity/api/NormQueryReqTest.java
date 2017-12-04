package com.flipkart.sherlock.semantic.norm.entity.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.junit.Test;

/**
 * Created by anurag.laddha on 28/11/17.
 */
public class NormQueryReqTest {

    @Test
    public void testQueryReqSerDe() throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        NormQueryReq queryReq = new NormQueryReq(Lists.newArrayList("s1", "s2", "s3"), "mobiles", null);
        System.out.println(objectMapper.writeValueAsString(queryReq));


        String deserialisedData = "{\"data\":[\"s1\",\"s2\",\"s3\"]}";
        queryReq = objectMapper.readValue(deserialisedData, NormQueryReq.class);
        System.out.println(queryReq.getData());
        System.out.println(queryReq.getStore());
    }
}