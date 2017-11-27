package com.flipkart.sherlock.semantic.autosuggest.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Created by dhruv.pancholi on 20/10/17.
 */
public class JsonSeDeTest {

    JsonSeDe jsonSeDe = JsonSeDe.getInstance();

    @Test(expected = RuntimeException.class)
    public void readValue() throws Exception {
        jsonSeDe.readValue("{}", List.class);
    }

    @Test(expected = RuntimeException.class)
    public void readValue1() throws Exception {
        jsonSeDe.readValue("[]", new TypeReference<Map<String, Object>>() {
        });
    }
}