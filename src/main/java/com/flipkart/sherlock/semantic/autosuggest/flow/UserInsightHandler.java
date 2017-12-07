package com.flipkart.sherlock.semantic.autosuggest.flow;

import com.flipkart.sherlock.semantic.autosuggest.models.UserInsightResponse;
import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by dhruv.pancholi on 07/12/17.
 */
@Singleton
public class UserInsightHandler {

    @Inject
    private JsonSeDe jsonSeDe;

    public UserInsightResponse getUserInsight(String accountId) {
        return new UserInsightResponse(ImmutableMap.of("tyy/4io", 1));
    }
}
