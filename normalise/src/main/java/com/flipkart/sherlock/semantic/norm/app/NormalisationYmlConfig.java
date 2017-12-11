package com.flipkart.sherlock.semantic.norm.app;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by anurag.laddha on 28/11/17.
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class NormalisationYmlConfig extends Configuration {
    @JsonProperty
    private String testKey;
}
