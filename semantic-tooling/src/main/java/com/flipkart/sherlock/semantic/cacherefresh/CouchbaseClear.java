package com.flipkart.sherlock.semantic.cacherefresh;

import com.flipkart.irene.kamikaze.couchbase.CouchbaseCache;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by anurag.laddha on 19/09/17.
 */
public class CouchbaseClear {

    public static void main(String[] args) throws Exception {
        if (args != null && args.length != 0) {
            String inputFile = args[0];
//            String output = args[1];
            System.out.println("Reading semantic URLs from file: " + inputFile);
            int linesCount = 0;
            int deletedKeysCount = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
                String line;
                CouchbaseCache couchbaseCache = CouchbaseCacheProvider.getInstance();
                while ((line = br.readLine()) != null) {
                    String key = getCacheKey(line);
                    if (couchbaseCache.get(key) != null) {
                        couchbaseCache.delete(key);
                        deletedKeysCount++;
                    }
                    linesCount++;
                }
                couchbaseCache.shutDown();
            }

            System.out.println(String.format("Total number of urls: %d, number of urls deleted: %d", linesCount, deletedKeysCount));
        }
    }

    public static String getCacheKey(String uri) {
        if (uri == null) {
            return null;
        }
        return DigestUtils.md5Hex(uri);
    }
}
