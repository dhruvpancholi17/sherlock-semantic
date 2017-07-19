package com.flipkart.sherlock.semantic.common.solr;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by dhruv.pancholi on 05/07/17.
 */

@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class Core {
    private String hostname;
    private int port;
    private String core;

    public String getSolrUrl() {
        return "http://" + hostname + ":" + port + "/solr/" + core;
    }
}
