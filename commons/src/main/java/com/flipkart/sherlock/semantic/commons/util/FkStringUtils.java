package com.flipkart.sherlock.semantic.commons.util;

import com.flipkart.sherlock.semantic.commons.Constants;
import com.google.common.base.Joiner;

/**
 * Created by anurag.laddha on 10/12/17.
 */
public class FkStringUtils {

    public static Joiner joinerOnDot = Joiner.on(Constants.DELIM_DOT).skipNulls();
    public static Joiner joinerOnSpace = Joiner.on(Constants.DELIM_SPACE).skipNulls();
}
