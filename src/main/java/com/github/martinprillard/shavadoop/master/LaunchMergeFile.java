package com.github.martinprillard.shavadoop.master;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.ConcurrentHashMap;

import com.github.martinprillard.shavadoop.util.Constant;

/**
 * 
 * @author martin prillard
 * 
 */
public class LaunchMergeFile extends Thread {

    String file;
    ConcurrentHashMap<String, Integer> finalResult;

    public LaunchMergeFile(String _file, ConcurrentHashMap<String, Integer> _finalResult) {
        file = _file;
        finalResult = _finalResult;
    }

    public void run() {
        File f = new File(file);
        FileReader fic;
        try {
            fic = new FileReader(f);
            BufferedReader read = new BufferedReader(fic);
            String line = null;

            // for each lines of the file
            while ((line = read.readLine()) != null) {
                String words[] = line.split(Constant.SEP_CONTAINS_FILE);
                // add each line to our hashmap
                String word = words[0];
                int counter = Integer.parseInt(words[1]);
                if (finalResult.keySet().contains(word)) {
                    finalResult.put(word, finalResult.get(word) + counter);
                } else {
                    finalResult.put(word, counter);
                }
            }
            fic.close();
            read.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
