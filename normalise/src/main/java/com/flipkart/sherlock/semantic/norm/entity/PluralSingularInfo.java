package com.flipkart.sherlock.semantic.norm.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by anurag.laddha on 03/12/17.
 */

/**
 * Plural singular tuple informatino
 */
@AllArgsConstructor
@Getter
public class PluralSingularInfo {
    /**
     * Plural term
     */
    private String plural;

    /**
     * Singular form of plural term
     */
    private String singular;
}
