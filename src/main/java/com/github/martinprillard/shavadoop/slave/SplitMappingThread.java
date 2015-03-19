package com.github.martinprillard.shavadoop.slave;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.martinprillard.shavadoop.util.Constant;
import com.github.martinprillard.shavadoop.util.Util;

/**
 * 
 * @author martin prillard
 * 
 */
public class SplitMappingThread extends Thread {

    private List<ConcurrentHashMap<String, AtomicInteger>> unsortedMaps;
    private List<String> chunk;
    private int nbWorker;

    public SplitMappingThread(List<ConcurrentHashMap<String, AtomicInteger>> _unsortedMaps, List<String> _chunk, int _nbWorker) {
        unsortedMaps = _unsortedMaps;
        chunk = _chunk;
        nbWorker = _nbWorker;
    }

    public void run() {
        for (String line : chunk) {
            line = cleanLine(line);
            if (!line.equals("") || !line.isEmpty()) {
                wordCount(nbWorker, line);
            }
        }
    }

    /**
     * Clean the line
     * 
     * @param line
     * @return line clean
     */
    private String cleanLine(String line) {
        String clean = line;
        clean = clean.trim();
        // clean the non alpha numeric character or space
        clean = clean.replaceAll("[^a-zA-Z0-9\\s]", " ");
        // just one space beetween each words
        clean = clean.replaceAll("\\s+", " ");
        clean = clean.replaceAll("\\t+", " ");
        return clean;
    }

    /**
     * Count the occurence of each word in the sentence
     * 
     * @param nbWorker
     * @param line
     * @return res
     */
    private void wordCount(int nbWorker, String line) {
        // split the line word by word
        String words[] = line.split(Constant.SEP_WORD);

        for (int i = 0; i < words.length; i++) {

            String word = words[i];
            // add counter value for this word
            int idNextWorker = getIdNextWorker(word, nbWorker);

            // increment atomically like the hadoop combiner
            increment(idNextWorker, word);
        }
    }

    /**
     * Return the id next worker from the key
     * 
     * @param key
     * @param nbWorker
     * @return id next worker
     */
    private int getIdNextWorker(String key, int nbWorker) {
        return Math.abs((int) (Util.simpleHash(key) % nbWorker));
    }

    /**
     * Increment the value atomically
     * 
     * @param idHashMap
     * @param key
     */
    private void increment(int idHashMap, String key) {
        AtomicInteger value = unsortedMaps.get(idHashMap).get(key);
        if (value == null) {
            value = new AtomicInteger(0);
            AtomicInteger old = unsortedMaps.get(idHashMap).putIfAbsent(key, value);
            if (old != null) {
                value = old;
            }
        }
        value.incrementAndGet(); // increment the value atomically
    }

}
