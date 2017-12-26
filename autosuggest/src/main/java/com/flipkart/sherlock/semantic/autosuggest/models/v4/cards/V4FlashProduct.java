package com.flipkart.sherlock.semantic.autosuggest.models.v4.cards;

import com.flipkart.sherlock.semantic.autosuggest.models.v4.V4FlashContentType;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.V4FlashSuggestion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by dhruv.pancholi on 11/12/17.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class V4FlashProduct extends V4FlashSuggestion {
    private String clickUrl;
    private V4FlashContentType contentType;
    private String title;
    private String subTitle;
    private String imageUrl;

    private String pid;
    private String lid;
}
