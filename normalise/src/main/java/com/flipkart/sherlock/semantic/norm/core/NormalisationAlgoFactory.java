package com.flipkart.sherlock.semantic.norm.core;

import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Map;

/**
 * Created by anurag.laddha on 03/12/17.
 */

@Singleton
public class NormalisationAlgoFactory {

    @Inject
    private Map<INormalise.Type, INormalise> normalisationTypeToImplMap;

    public INormalise getNormaliseImpl(Multimap<String, String> context){
        /**
         * Flexibility to choose impl based on context (request params or config in future)
         */
       return this.normalisationTypeToImplMap.get(INormalise.Type.IndividualToken);
    }
}
