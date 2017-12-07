package com.flipkart.sherlock.semantic.norm.core;

import com.flipkart.sherlock.semantic.commons.Constants;
import com.flipkart.sherlock.semantic.norm.entity.BrandInfo;
import com.flipkart.sherlock.semantic.norm.util.NormConstants;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by anurag.laddha on 29/11/17.
 */

@Slf4j
@Singleton
public class IndividualTokenNormalisationService implements INormalise {

    private BrandDataSource brandDataSource;
    private SingulariseResourcesDataSource singulariseResourcesDataSource;
    private static final String spaceDelim = "\\s+";
    private final int maxBrandTokenLength;
    private Joiner spaceJoiner = Joiner.on(Constants.DELIM_SPACE).skipNulls();


    @Inject
    public IndividualTokenNormalisationService(BrandDataSource brandDataSource,
                                               SingulariseResourcesDataSource singulariseResourcesDataSource,
                                               @Named(NormConstants.GUICE_LENGTH_BRAND_CONSIDERATION) int maxBrandTokenLength) {
        this.brandDataSource = brandDataSource;
        this.singulariseResourcesDataSource = singulariseResourcesDataSource;
        this.maxBrandTokenLength = maxBrandTokenLength;
    }


    @Override
    public List<String> normalise(List<String> textList, Map<Context, String> context) {

        if (textList != null) {
            List<String> normalisedList = textList.stream()
                .map(text -> getNormalisedString(text, context))
                .collect(Collectors.toList());
            return normalisedList;
        }
        return textList;
    }

    private String getNormalisedString(String text, Map<INormalise.Context, String> context){

        if (StringUtils.isNotBlank(text)) {
            Set<Integer> skipNormIndexes = new HashSet<>();
            int endIndexInclusive;

            String[] tokensArr = text.split(spaceDelim);
            List<String> tokensList = Lists.newArrayList(tokensArr);
            int strLen = tokensArr.length;

            /**
             * Starting from each token, evaluate for increasing length of string till max brand length to check for substring being brand
             * Track brand substring index to prevent normalisation of tokens at those index
             */
            for (int startIndex = 0; startIndex < tokensArr.length; startIndex++) {
                for (int len = 1; len <= maxBrandTokenLength; len++) {
                    endIndexInclusive = startIndex + len - 1;
                    if (endIndexInclusive < strLen) {
                        List<String> subList = tokensList.subList(startIndex, endIndexInclusive + 1);
                        String partialString = spaceJoiner.join(subList);
                        if (this.brandDataSource.isBrand(new BrandInfo(partialString, context.get(INormalise.Context.Store)))) {
                            //we should not normalise tokens between start and end index inclusive
                            skipNormIndexes.addAll(IntStream.rangeClosed(startIndex, endIndexInclusive).boxed().collect(Collectors.toSet()));
                            startIndex = endIndexInclusive; //skip evaluation till end index
                            break;
                        }
                    } else {//gone past str len. Skip rest of evaluation.
                        break;
                    }
                }
            }

            /**
             * Get singular form of plural tokens
             * Skip tokens that are part of brand name
             */
            List<String> normalisedToken = new ArrayList<>();
            int currIndex = 0;
            for (String currToken : tokensList) {
                if (!skipNormIndexes.contains(currIndex)) {
                    String plural = this.singulariseResourcesDataSource.singularise(currToken);
                    normalisedToken.add(plural != null ? plural : currToken);
                } else {//add current token as is
                    normalisedToken.add(currToken);
                }
                currIndex++;
            }

            return spaceJoiner.join(normalisedToken);
        }
        return text;
    }
}
