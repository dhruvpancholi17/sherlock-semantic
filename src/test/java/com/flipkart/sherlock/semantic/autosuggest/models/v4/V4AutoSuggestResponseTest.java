package com.flipkart.sherlock.semantic.autosuggest.models.v4;

import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by dhruv.pancholi on 06/12/17.
 */
public class V4AutoSuggestResponseTest {

    @Test
    public void testSeDe() {
        String response = "{\"payloadId\":\"bfe5785f-cd10-4aa0-ae43-cbe1521d5df2\",\"coldStartVersion\":0,\"suggestions\":[{\"type\":\"QUERY_STORE\",\"content\":{\"clickUrl\":\"/search?q=quechua&as=on&as-show=on&sid=reh/4d7/ak9\",\"contentType\":\"RECENT\",\"query\":\"quechua\",\"store\":\"Backpacks\",\"marketplaceId\":\"FLIPKART\"}},{\"type\":\"QUERY_STORE\",\"content\":{\"clickUrl\":\"/search?q=quechua&as=on&as-show=on&sid=2oq/s9b/qgu/8cd\",\"contentType\":\"RECENT\",\"query\":\"quechua\",\"store\":\"Men's Jackets\",\"marketplaceId\":\"FLIPKART\"}},{\"type\":\"QUERY_STORE\",\"content\":{\"clickUrl\":\"/search?q=quechua&as=on&as-show=on&sid=2oq/s9b/6gr/8cd\",\"contentType\":\"RECENT\",\"query\":\"quechua\",\"store\":\"Men's Sports Jackets\",\"marketplaceId\":\"FLIPKART\"}},{\"type\":\"QUERY_STORE\",\"content\":{\"clickUrl\":\"/search?q=quick heal total security&as=on&as-show=on&sid=6bo/5hp/lwb/n3u\",\"contentType\":\"RECENT\",\"query\":\"quick heal total security\",\"store\":\"Security Software\",\"marketplaceId\":\"FLIPKART\"}},{\"type\":\"QUERY\",\"content\":{\"clickUrl\":\"/search?q=quilt&as=on&as-show=on\",\"contentType\":\"RECENT\",\"query\":\"quilt\"}},{\"type\":\"QUERY\",\"content\":{\"clickUrl\":\"/search?q=quechua bags&as=on&as-show=on\",\"contentType\":\"RECENT\",\"query\":\"quechua bags\"}},{\"type\":\"QUERY\",\"content\":{\"clickUrl\":\"/search?q=quartz watch&as=on&as-show=on\",\"contentType\":\"RECENT\",\"query\":\"quartz watch\"}},{\"type\":\"QUERY\",\"content\":{\"clickUrl\":\"/search?q=queen size bed&as=on&as-show=on\",\"contentType\":\"RECENT\",\"query\":\"queen size bed\"}},{\"type\":\"QUERY\",\"content\":{\"clickUrl\":\"/search?q=q&q&as=on&as-show=on\",\"contentType\":\"RECENT\",\"query\":\"q&q\"}},{\"type\":\"QUERY\",\"content\":{\"clickUrl\":\"/search?q=quilts for double bed&as=on&as-show=on\",\"contentType\":\"RECENT\",\"query\":\"quilts for double bed\"}},{\"type\":\"PRODUCT\",\"content\":{\"clickUrl\":\"https://www.flipkart.com/a/p/a?pid=SECEP6PSD9H3RFFB\",\"contentType\":\"RECENT\",\"title\":\"\",\"imageUrl\":\"\",\"pid\":\"SECEP6PSD9H3RFFB\"}}]}";
        V4AutoSuggestResponse v4AutoSuggestResponse = JsonSeDe.getInstance().readValue(response, V4AutoSuggestResponse.class);
        System.out.println(v4AutoSuggestResponse);
    }
}