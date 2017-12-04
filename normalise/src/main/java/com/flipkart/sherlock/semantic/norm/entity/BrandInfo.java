package com.flipkart.sherlock.semantic.norm.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by anurag.laddha on 28/11/17.
 */

@EqualsAndHashCode
@AllArgsConstructor
@Getter
public class BrandInfo {
    /**
     * Brand name
     */
    private String name;

    /**
     * Store/Category that brand belongs to
     * Can be comma separated list
     */
    private String store;
}
