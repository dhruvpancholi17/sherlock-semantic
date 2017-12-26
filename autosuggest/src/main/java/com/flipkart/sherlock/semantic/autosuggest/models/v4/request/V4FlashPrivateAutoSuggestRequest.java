package com.flipkart.sherlock.semantic.autosuggest.models.v4.request;

import com.flipkart.sherlock.semantic.v4.request.V4FlashAutoSuggestRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by dhruv.pancholi on 26/12/17.
 */
@Getter
@Setter
@NoArgsConstructor
public class V4FlashPrivateAutoSuggestRequest extends V4FlashAutoSuggestRequest {
    private boolean debug;
    private String asBucket;
}
