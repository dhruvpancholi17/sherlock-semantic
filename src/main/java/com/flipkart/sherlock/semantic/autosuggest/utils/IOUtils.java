package com.flipkart.sherlock.semantic.autosuggest.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhruv.pancholi on 24/09/16.
 */
public class IOUtils {

    private String filePath;
    private FileReader fileReader;

    public IOUtils(String filePath) throws FileNotFoundException {
        this.filePath = filePath;
        this.fileReader = new FileReader(filePath);
    }

    public static IOUtils open(String filePath) {
        IOUtils ioUtils = null;
        try {
            ioUtils = new IOUtils(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return ioUtils;
    }

    public <T> T readObject(Class<T> tClass) {
        return JsonSeDe.getInstance().readValue(readAll(), tClass);
    }

    public String readAll() {
        List<String> lines = readLines();
        StringBuffer sb = new StringBuffer();
        for (String line : lines) {
            sb.append(line);
        }
        return sb.toString();
    }


    public List<String> readLines() {
        try {
            return readLines_();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<String> readLines_() throws IOException {
        List<String> list = new ArrayList<String>();
        String line = null;
        BufferedReader br = new BufferedReader(fileReader);
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty())
                list.add(line);
        }
        return list;
    }


    public static void main(String[] args) {

    }
}
