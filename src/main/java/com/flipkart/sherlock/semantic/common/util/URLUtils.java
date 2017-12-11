package com.flipkart.sherlock.semantic.common.util;

import lombok.extern.slf4j.Slf4j;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by dhruv.pancholi on 11/12/17.
 */
@Slf4j
public class URLUtils {
    public static final String UTF8 = "UTF-8";

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty() || str.trim().equalsIgnoreCase("null");
    }

    public static String decodeUrl(String url) {
        if (isNullOrEmpty(url)) {
            return "";
        }
        String decoded = url;
        try {
            decoded = URLDecoder.decode(url, UTF8);
        } catch (Exception e) {
            log.debug("Exception thrown while decoding URL = " + url, e);
        }
        return decoded;
    }

    public static String encodeUrl(String url) {
        if (isNullOrEmpty(url)) {
            return "";
        }
        String encoded = url;
        try {
            encoded = URLEncoder.encode(url, UTF8);
        } catch (Exception e) {
            log.debug("Exception thrown while encoding URL = " + url, e);
        }
        encoded = encoded.replaceAll("%7E", "~");
        return encoded;
    }
}
