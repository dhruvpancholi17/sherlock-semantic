package com.flipkart.sherlock.semantic.autosuggest.dataGovernance;


import com.flipkart.seraph.fkint.cp.discover_search.AutoSuggest;
import com.flipkart.seraph.fkint.cp.discover_search.AutoSuggestRequest;
import com.flipkart.seraph.fkint.cp.discover_search.AutoSuggestResponse;
import com.flipkart.sherlock.semantic.autosuggest.models.Params;
import com.flipkart.sherlock.semantic.autosuggest.models.ProductResponse;
import com.flipkart.sherlock.semantic.autosuggest.models.QueryResponse;
import com.flipkart.sherlock.semantic.autosuggest.utils.IOUtils;
import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import exception.TransformException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

import static com.flipkart.sherlock.semantic.autosuggest.dataGovernance.Constants.*;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Ingester.class)
public class IngesterTest {

    Ingester ingesterSpy;

    @InjectMocks
    Ingester ingester;

    //private Ingester.AutoSuggestResponseData autoSuggestResponseData = new Ingester.AutoSuggestResponseData();

    private String payloadId = "6323a204-b181-4f78-8330-5d2c2e32ed65";
    private JsonSeDe instance;
    private QueryResponse queryResponse;
    private ProductResponse productResponse;
    private Params params;

    String jsonQueryResponse;
    String jsonProductResponse;
    String jsonParams;
    String jsonAutoSuggestResponse;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        ingesterSpy = spy(ingester);
        instance = JsonSeDe.getInstance();
        jsonQueryResponse = IOUtils.openFromResource("AutoSuggestDG/QueryResponse.json").readAll();
        jsonProductResponse = IOUtils.openFromResource("AutoSuggestDG/ProductResponse.json").readAll();
        jsonParams = IOUtils.openFromResource("AutoSuggestDG/Params.json").readAll();
        jsonAutoSuggestResponse = IOUtils.openFromResource("AutoSuggestDG/IngesterAutoSuggestResponse.json").readAll();
        queryResponse = instance.readValue(jsonQueryResponse, QueryResponse.class);
        productResponse = instance.readValue(jsonProductResponse, ProductResponse.class);
        params = instance.readValue(jsonParams, Params.class);
    }

    @Test
    public void loadAutoSuggestDGTest() throws TransformException, IOException {
        AutoSuggestResponseData autoSuggestResponseData = new AutoSuggestResponseData(payloadId, params, queryResponse, productResponse, null, null, null);
        autoSuggestResponseData.setParams(params);
        autoSuggestResponseData.setProductResponse(productResponse);
        autoSuggestResponseData.setQueryResponse(queryResponse);
        AutoSuggest actualAutoSuggestOutput = (AutoSuggest) ingesterSpy.transform(autoSuggestResponseData);
        AutoSuggestResponse actualAutoSuggestResponse = actualAutoSuggestOutput.getAutoSuggestResponse();
        AutoSuggestResponse expectedAutoSuggestResponse = instance.readValue(jsonAutoSuggestResponse, AutoSuggestResponse.class);
        Assert.assertEquals(instance.writeValueAsString(expectedAutoSuggestResponse), instance.writeValueAsString(actualAutoSuggestResponse));
    }

    @Test
    public void testAutoSuggestRequestResponse() throws TransformException, IOException {
        MultivaluedMap<String, String> header = new MultivaluedHashMap<>();
        header.add(xDeviceId, "testXDeviceId");
        header.add(xRequestId, "xTestRequestId");
        header.add(xABIds, "testABId");
        header.add(xClientDeviceChannel, "testDeviceChannelId");
        header.add(xSearchSessionId, "testSearchSessionId");
        header.add(xSearchQueryId, "testSearchQueryId");
        AutoSuggestResponseData autoSuggestResponseData = new AutoSuggestResponseData(payloadId, params, queryResponse,productResponse, header, null, null);
        AutoSuggest actualAutoSuggestOutput = (AutoSuggest) ingesterSpy.transform(autoSuggestResponseData);
        AutoSuggestRequest autoSuggestRequest = actualAutoSuggestOutput.getAutoSuggestRequest();
        Assert.assertEquals(autoSuggestRequest.getXClientDeviceChannel(), "testDeviceChannelId");
        Assert.assertEquals(autoSuggestRequest.getXRequestId(), "xTestRequestId");
        Assert.assertEquals(autoSuggestRequest.getXSearchSessionId(), "testSearchSessionId");
        Assert.assertEquals(autoSuggestRequest.getXSearchQueryId(), "testSearchQueryId");
        Assert.assertEquals(autoSuggestRequest.getXABIdsList().get(0), "testABId");

    }
}
