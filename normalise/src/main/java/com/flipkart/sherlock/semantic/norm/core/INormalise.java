package com.flipkart.sherlock.semantic.norm.core;

import java.util.List;
import java.util.Map;

/**
 * Created by anurag.laddha on 03/12/17.
 */

public interface INormalise {

    enum Type {
        IndividualToken,
        FullContext
    }

    enum Context {
        Store, MarketplaceId
    }

    List<String> normalise(List<String> textList, Map<Context, String> context);
}
