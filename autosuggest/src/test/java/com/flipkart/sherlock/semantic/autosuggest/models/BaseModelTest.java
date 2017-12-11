package com.flipkart.sherlock.semantic.autosuggest.models;

import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import org.junit.Before;

/**
 * Created by dhruv.pancholi on 14/10/17.
 */
public class BaseModelTest {

    JsonSeDe jsonSeDe;

    @Before
    public void setup() {
        jsonSeDe = JsonSeDe.getInstance();
    }
}
