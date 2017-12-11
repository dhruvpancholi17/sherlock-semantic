package com.flipkart.sherlock.semantic.autosuggest.models.v4;

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
public class V4Product extends V4Suggestion {
    private String clickUrl;
    private V4ContentType contentType;
    private String title;
    private String subTitle;
    private String imageUrl;

    private String pid;
    private String lid;
}
