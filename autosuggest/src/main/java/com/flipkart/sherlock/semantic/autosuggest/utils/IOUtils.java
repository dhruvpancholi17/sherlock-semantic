package com.flipkart.sherlock.semantic.autosuggest.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhruv.pancholi on 24/09/16.
 */
@Slf4j
public class IOUtils {

    private BufferedReader bufferedReader;

    public IOUtils(String filePath) throws FileNotFoundException {
        this.bufferedReader = new BufferedReader(new FileReader(filePath));
    }

    public IOUtils(BufferedReader bufferedReader) throws FileNotFoundException {
        this.bufferedReader = bufferedReader;
    }

    public static IOUtils open(String filePath) {
        try {
            return new IOUtils(filePath);
        } catch (FileNotFoundException e) {
            log.error("{}", e);
        }
        return null;
    }

    public static IOUtils openFromResource(String filePath) {
        try {
            return new IOUtils(getFromResource(filePath));
        } catch (Exception e) {
            log.error("{}", e);
        }
        return null;
    }


    public List<String> readLines() {
        try {
            return readLines_();
        } catch (IOException e) {
            log.error("{}", e);
        }
        return null;
    }

    private List<String> readLines_() throws IOException {
        List<String> list = new ArrayList<String>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty())
                list.add(line);
        }
        return list;
    }

    public <T> T readObject(Class<T> tClass) {
        return JsonSeDe.getInstance().readValue(readAll(), tClass);
    }

    public String readAll() {
        List<String> lines = readLines();
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line);
        }
        return sb.toString();
    }

    private static BufferedReader getFromResource(String fileName) throws IOException {
        return new BufferedReader(new InputStreamReader(IOUtils.class.getClassLoader().getResourceAsStream(fileName)));
    }
}
