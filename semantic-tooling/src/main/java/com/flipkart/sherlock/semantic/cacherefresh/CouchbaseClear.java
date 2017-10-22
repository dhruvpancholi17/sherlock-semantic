package com.flipkart.sherlock.semantic.cacherefresh;

import com.flipkart.irene.kamikaze.couchbase.CouchbaseCache;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by anurag.laddha on 19/09/17.
 */

@Slf4j
public class CouchbaseClear {

    static class Work implements Runnable{

        private List<String> urlList;
        private AtomicInteger urlTotalCount;
        private AtomicInteger urlDeletedCount;
        private int chunkId;
        private String context;
        private CouchbaseCache couchbaseCache;

        public Work(List<String> urlList, AtomicInteger urlTotalCount, AtomicInteger urlDeletedCount, int chunkId,
                    String context, CouchbaseCache couchbaseCache) {
            this.urlList = urlList;
            this.urlTotalCount = urlTotalCount;
            this.urlDeletedCount = urlDeletedCount;
            this.chunkId = chunkId;
            this.context = context;
            this.couchbaseCache = couchbaseCache;
        }

        @Override
        public void run() {

            try {
                //System.out.println(String.format("Processing chunk %d for context: %s", this.chunkId, this.context));
                for (String url : this.urlList) {
                    String key = getCacheKey(url);
                    if (couchbaseCache.get(key) != null) {
                        couchbaseCache.delete(key);
                        this.urlDeletedCount.incrementAndGet();
                    }
                    this.urlTotalCount.incrementAndGet();
                }
            }
            catch(Exception ex){
                log.error("Error while clearing entries from couchbase", ex);
                System.out.println(ex.getMessage());
            }
        }
    }


    public static void main(String[] args) throws Exception {
        if (args != null && args.length != 0) {

            String inputFile = args[0];  //path to url file
            String metaData = args.length >= 2 ? args[1] : ""; //additional context for logging
            int parallelism = args.length >= 3 ? Integer.parseInt(args[2]) : 20;

            //Tracking total number of urls processed and number of couchbase entries deleted across threads
            AtomicInteger urlTotalCount = new AtomicInteger(0);
            AtomicInteger urlDeletedCount = new AtomicInteger(0);

            //Split queries into equal parts based on parallelism
            System.out.println("Reading semantic URLs from file: " + inputFile);
            List<String> lines = Files.readAllLines(Paths.get(inputFile), StandardCharsets.UTF_8);
            parallelism = Math.min(parallelism, lines.size());
            List<List<String>> splitLists = Lists.partition(lines, (int) Math.ceil((float)lines.size()/parallelism));
            CouchbaseCache couchbaseCache = CouchbaseCacheProvider.getInstance();

            System.out.println(String.format("Number of lines: %d, number of chunks: %d, parallelism: %d",
                lines.size(), splitLists.size(), parallelism));

            //Assign part of urls to each thread
            ExecutorService es = Executors.newFixedThreadPool(parallelism);
            for(int i=0; i< splitLists.size(); i++){
                es.submit(new Work(splitLists.get(i), urlTotalCount, urlDeletedCount, i, metaData, couchbaseCache));
            }

            //Wait for all threads to finish work
            es.shutdown();
            System.out.println("Awaiting clearing couchbase entries for context: " + metaData);
            while(!es.isTerminated()){
            }
            couchbaseCache.shutDown();

            System.out.println(String.format("Total number of urls: %d, number of urls deleted: %d, context (query or store): %s",
                urlTotalCount.get(), urlDeletedCount.get(), metaData));

            System.exit(0);
        }
    }

    private static String getCacheKey(String uri) {
        if (uri == null) {
            return null;
        }
        return DigestUtils.md5Hex(uri);
    }
}
