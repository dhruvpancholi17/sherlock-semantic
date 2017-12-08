package com.flipkart.sherlock.semantic.autosuggest.flow;

import com.fasterxml.jackson.core.type.TypeReference;
import com.flipkart.sherlock.semantic.autosuggest.models.UserInsightResponse;
import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.flipkart.sherlock.semantic.common.hystrix.HystrixCommandConfig;
import com.flipkart.sherlock.semantic.common.hystrix.HystrixCommandHelper;
import com.flipkart.sherlock.semantic.common.hystrix.HystrixCommandWrapper;
import com.flipkart.sherlock.semantic.common.util.FkConfigServiceWrapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dhruv.pancholi on 07/12/17.
 */
@Slf4j
@Singleton
public class UserInsightHandler {

    @Inject
    private JsonSeDe jsonSeDe;

    @Inject
    private FkConfigServiceWrapper fkConfigServiceWrapper;

    private static final String HYSTRIX_PREFIX = "hystrix";
    private static final String HYSTRIX_GROUP_SOLR = "UIEngine";
    private static final String HYSTRIX_COMMAND_SEARCH = "insight";

    private static final String URL_REGEX = "http://10.32.113.153:9092/athena/api/uie/Signals?id=%s&tableName=searchQuery_hourly&partitionTime=hourly&partitionCount=1&signalClassName=com.flipkart.uie.athena.domainobjects.signals.sessionInsights.SearchQuerySignal";

    public UserInsightResponse getUserInsight(String accountId) {

        log.error("Account id : {}", accountId);

        if(accountId == null || accountId.isEmpty()) return new UserInsightResponse(ImmutableMap.of());

        HystrixCommandConfig commandConfig = getHystrixCommandConfig(HYSTRIX_GROUP_SOLR, HYSTRIX_COMMAND_SEARCH);
        String url = String.format(URL_REGEX, accountId);
        List<Map<String,Object>> uieResponse = null;

        if (commandConfig != null) {
            HystrixCommandWrapper<List<Map<String,Object>>> insightCommand = new HystrixCommandWrapper<>(commandConfig,
                    () -> executeUIERequest(url));

            uieResponse = HystrixCommandHelper.executeSync(insightCommand, commandConfig.getGroupKey(),
                    commandConfig.getCommandKey());
        } else {
            log.error("Could not find config key for group: {}, command: {}", HYSTRIX_GROUP_SOLR, HYSTRIX_COMMAND_SEARCH);
        }
        return convertResponse(uieResponse);
    }

    private UserInsightResponse convertResponse(List<Map<String, Object>> uieResponse) {
        Map<String,Integer> storeWeightDistribution = Maps.newHashMap();

        for (Map<String, Object> insightMap : uieResponse) {
            Map<String,Object> storeInsight = (Map<String,Object>) insightMap.get("_2");
            if(storeInsight.containsKey("storeId") && storeInsight.get("storeId") != null && storeInsight.get("storeId") != "") {
                String storeId = storeInsight.get("storeId").toString();
                Integer count = Integer.valueOf(storeInsight.get("count").toString());
                if(storeWeightDistribution.containsKey(storeId)){
                    Integer occurrenceCount = storeWeightDistribution.get(storeId);
                    storeWeightDistribution.put(storeId, occurrenceCount + count);
                }
                else {
                    storeWeightDistribution.put(storeId, count);
                }
            }
        }
        return new UserInsightResponse(storeWeightDistribution);
    }

    private List<Map<String,Object>> executeUIERequest(String url) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        try {
            HttpResponse response = client.execute(request);
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            return JsonSeDe.getInstance().readValue(result.toString(), new TypeReference<List<Map<String,Object>>>() {});
        } catch (IOException e) {
            log.error("UIE Failure : {}", e);
        }
        return new ArrayList<>();
    }

    private HystrixCommandConfig getHystrixCommandConfig(String group, String command) {
        return jsonSeDe.readValue(
                fkConfigServiceWrapper.getString(HYSTRIX_PREFIX + "." + group + "." + command),
                HystrixCommandConfig.class);
    }
}
